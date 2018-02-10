package com.engage.expenses;

import com.engage.expenses.resources.ExpenseResourceHealthCheck;
import com.engage.expenses.resources.ExpenseResource;
import com.engage.expenses.service.ExpensesService;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.flyway.FlywayBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.flywaydb.core.Flyway;
import org.skife.jdbi.v2.DBI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Main class for the expenses application
 *
 * @author jklarica
 * @since 2018-02-09
 */
public class ExpensesApplication extends Application<ExpensesConfiguration>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpensesApplication.class);
    private static final String POSTGRESQL = "postgresql";
    private static final String EXPENSES_SERVICE = "Expenses Service";

    public static void main(final String[] args) throws Exception
    {
        new ExpensesApplication().run(args);
    }

    @Override
    public String getName()
    {
        return "Expenses";
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
        // Set up date format for (de)serialization
        DateFormat expenseDateFormat = new SimpleDateFormat(configuration.getDateFormat());
        environment.getObjectMapper().setDateFormat(expenseDateFormat);

        // Retrieve datasource from the configuration file
        DataSource dataSource = configuration.getDataSourceFactory().build(environment.metrics(), POSTGRESQL);
        DBI dbi = new DBI(dataSource);

        // Initiate flyway migrations
        LOGGER.info("Initiating flyway migrations...");
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();

        // Register health check(s)
        LOGGER.info("Registering health checks...");
        environment.healthChecks().register(EXPENSES_SERVICE, new ExpenseResourceHealthCheck(dbi.onDemand(ExpensesService.class)));

        // Register resource(s)
        LOGGER.info("Registering resources...");
        environment.jersey().register(new ExpenseResource(dbi.onDemand(ExpensesService.class)));
    }
}
