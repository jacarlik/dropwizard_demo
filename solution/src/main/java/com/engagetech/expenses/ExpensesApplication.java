package com.engagetech.expenses;

import com.engagetech.expenses.client.fixer.FixerIOClient;
import com.engagetech.expenses.core.auth.BasicAuthenticator;
import com.engagetech.expenses.core.auth.BasicAuthorizer;
import com.engagetech.expenses.core.auth.User;
import com.engagetech.expenses.health.ExpenseResourceHealthCheck;
import com.engagetech.expenses.resources.ExpenseResource;
import com.engagetech.expenses.core.ExpensesService;
import com.engagetech.expenses.util.LocalDateDeserializer;
import com.engagetech.expenses.util.LocalDateSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.flywaydb.core.Flyway;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;

/**
 * Expenses application main class
 *
 * @author N/A
 * @since 2018-02-09
 */
public class ExpensesApplication extends Application<ExpensesConfiguration>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpensesApplication.class);
    private static final String DATABASE = "postgresql";
    private static final String EXPENSES_APPLICATION = "Expenses Application";

    public static void main(final String[] args) throws Exception
    {
        new ExpensesApplication().run(args);
    }

    @Override
    public String getName()
    {
        return EXPENSES_APPLICATION;
    }

    @Override
    public void initialize(final Bootstrap<ExpensesConfiguration> bootstrap)
    {
        // Set up a flyway bundle
        bootstrap.addBundle(new FlywayBundle<ExpensesConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(ExpensesConfiguration configuration)
            {
                return configuration.getDataSourceFactory();
            }
        });
    }

    @Override
    public void run(final ExpensesConfiguration configuration, final Environment environment)
    {
        LOGGER.info("Enabling CORS...");
        _enableCORS(environment);

        LOGGER.info("Registering custom modules...");
        _registerAdditionalModules(configuration, environment);

        LOGGER.info("Setting up basic authentication...");
        _setUpSecurity(configuration, environment);

        LOGGER.info("Retrieving the data source...");
        DataSource dataSource = configuration.getDataSourceFactory().build(environment.metrics(), DATABASE);
        DBI dbi = new DBI(dataSource);

        LOGGER.info("Initiating flyway migrations...");
        _initiateFlywayMigrations(dataSource);

        LOGGER.info("Registering health checks...");
        environment.healthChecks().register(EXPENSES_APPLICATION, new ExpenseResourceHealthCheck(dbi.onDemand(ExpensesService.class)));

        // Register resource(s)
        LOGGER.info("Registering resources...");
        environment.jersey().register(new ExpenseResource(dbi.onDemand(ExpensesService.class), new FixerIOClient()));
    }

    /**
     * Enable cross-origin resource sharing
     *
     * Example pre-flight request:
     *
     *      Request Method: OPTIONS
     *      Request URL: https://localhost:8443/app/expenses?limit=500&offset=0
     *      Access-Control-Request-Headers: authorization
     *      Access-Control-Request-Method: GET
     *      Origin: http://localhost:8080
     *
     * Example pre-flight response:
     *      Access-Control-Allow-Credentials: true
     *      Access-Control-Allow-Headers: X-Requested-With,Content-Type,Accept,Origin,Authorization
     *      Access-Control-Allow-Methods: OPTIONS,GET,POST,DELETE
     *      Access-Control-Allow-Origin: http://localhost:8080
     *      Access-Control-Max-Age: 1800
     */
    private static void _enableCORS(final Environment environment)
    {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // The Access-Control-Allow-Origin response header indicates whether the response can be shared with resources with the given origin
        cors.setInitParameter("allowedOrigins", "*");

        // The Access-Control-Allow-Headers response header is used in response to a pre-flight request to indicate which HTTP headers can be used during the actual request
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin,Authorization");

        // The Access-Control-Allow-Methods response header specifies the method or methods allowed when accessing the resource in response to a preflight request
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,POST,DELETE");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // This will prevent pre-flight requests (that don't have authorization headers on them) from getting filtered by your down-stream authentication / authorization filters which would result in a 401~403 instead of a 200
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());
    }

    /**
     * Register custom modules
     */
    private static void _registerAdditionalModules(final ExpensesConfiguration configuration,
                                                   final Environment environment)
    {
        // We're using date intentionally, although having datetime would allow us to check whether the expense date is in the future
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(configuration.getDateFormat());

        // Register additional
        environment.getObjectMapper()
            .registerModule(new SimpleModule().addDeserializer(LocalDate.class, new LocalDateDeserializer(dateTimeFormatter)))
            .registerModule(new SimpleModule().addSerializer(LocalDate.class, new LocalDateSerializer(dateTimeFormatter)));
    }

    /**
     * Set up security-related config
     */
    private static void _setUpSecurity(final ExpensesConfiguration configuration,
                                       final Environment environment)
    {
        // The AuthDynamicFeature with the BasicCredentialAuthFilter and RolesAllowedDynamicFeature enables HTTP Basic authentication and authorization; requires an authenticator which takes instances of BasicCredentials
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                                                                 .setAuthenticator(new BasicAuthenticator(configuration.getUserName(), configuration.getPassword()))
                                                                 .setAuthorizer(new BasicAuthorizer())
                                                                 .setRealm("BASIC-AUTH-REALM")
                                                                 .buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);

        // If we want to use @Auth annotation to inject a custom Principal type into your resource
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    }

    /**
     * Initiate flyway migrations
     *
     * This can also be executed from the CLI, for example:
     * java -jar target/expenses-1.0-SNAPSHOT.jar db migrate src/main/resources/profiles/mainline.yml
     *
     * Other available commands: clean, info, validate, init, repair
     */
    private static void _initiateFlywayMigrations(final DataSource dataSource)
    {

        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }
}
