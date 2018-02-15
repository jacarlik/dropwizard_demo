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
        // Set up flyway
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
     */
    private static void _enableCORS(final Environment environment)
    {
        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,POST,DELETE");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // Do not pass a pre-flight request to down-stream auth filters; unauthenticated preflight requests should be permitted by spec
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());
    }

    /**
     * Register custom modules
     */
    private static void _registerAdditionalModules(final ExpensesConfiguration configuration,
                                                   final Environment environment)
    {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(configuration.getDateFormat());
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
        environment.jersey().register(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                                                                 .setAuthenticator(new BasicAuthenticator(configuration.getUserName(), configuration.getPassword()))
                                                                 .setAuthorizer(new BasicAuthorizer())
                                                                 .setRealm("BASIC-AUTH-REALM")
                                                                 .buildAuthFilter()));
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    }

    /**
     * Initiate flyway migrations
     */
    private static void _initiateFlywayMigrations(final DataSource dataSource)
    {
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }
}
