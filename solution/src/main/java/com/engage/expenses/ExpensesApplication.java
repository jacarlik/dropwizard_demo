package com.engage.expenses;

import com.engage.expenses.health.ExpenseResourceHealthCheck;
import com.engage.expenses.resources.ExpenseResource;
import com.engage.expenses.service.ExpensesService;
import com.engage.expenses.util.LocalDateDeserializer;
import com.engage.expenses.util.LocalDateSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.flywaydb.core.Flyway;
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
 * @author jklarica
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
        // Enable CORS headers
        final FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter("allowedOrigins", "*");
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin");
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,POST,DELETE");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");

        // DO not pass a preflight request to down-stream auth filters unauthenticated preflight requests should be permitted by spec
        cors.setInitParameter(CrossOriginFilter.CHAIN_PREFLIGHT_PARAM, Boolean.FALSE.toString());

        // Set up date format for (de)serialization
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(configuration.getDateFormat());
        environment.getObjectMapper()
            .registerModule(new SimpleModule().addDeserializer(LocalDate.class, new LocalDateDeserializer(dateTimeFormatter)))
            .registerModule(new SimpleModule().addSerializer(LocalDate.class, new LocalDateSerializer(dateTimeFormatter)));

        // Retrieve datasource from the configuration file
        DataSource dataSource = configuration.getDataSourceFactory().build(environment.metrics(), DATABASE);
        DBI dbi = new DBI(dataSource);

        // Initiate flyway migrations
        LOGGER.info("Initiating flyway migrations...");
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();

        // Register health check(s)
        LOGGER.info("Registering health checks...");
        environment.healthChecks().register(EXPENSES_APPLICATION, new ExpenseResourceHealthCheck(dbi.onDemand(ExpensesService.class)));

        // Register resource(s)
        LOGGER.info("Registering resources...");
        environment.jersey().register(new ExpenseResource(dbi.onDemand(ExpensesService.class)));
    }
}
