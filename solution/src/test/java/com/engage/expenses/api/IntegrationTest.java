package com.engage.expenses.api;

import com.engage.expenses.ExpensesApplication;
import com.engage.expenses.ExpensesConfiguration;
import com.engage.expenses.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.http.HttpStatus;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Expenses service integration tests
 *
 * @author jklarica
 * @since 2018-02-10
 */
@RunWith(DataProviderRunner.class)
public class IntegrationTest
{
    @ClassRule
    public static final DropwizardAppRule<ExpensesConfiguration> RULE = new DropwizardAppRule<>(
        ExpensesApplication.class, ResourceHelpers.resourceFilePath("profiles/test.yml")
    );
    private static ObjectMapper MAPPER = CommonUtils.getObjectMapper();
    private static Client CLIENT;
    private static String RESOURCE_URI;

    @BeforeClass
    public static void setUp()
    {
        CLIENT = new JerseyClientBuilder(RULE.getEnvironment()).using(MAPPER).build("Test client");
        RESOURCE_URI = String.format("http://localhost:%d/app/expenses", RULE.getLocalPort());
    }

    @Test
    public void testExpenses()
    {
        Response saveExpenseResponse = _saveExpense("{\"date\":\"01/01/18\", \"amount\":10, \"reason\":\"Test\", \"country\": \"GBR\"}");
        int id = saveExpenseResponse.readEntity(Integer.class);
        Assert.assertTrue(id > 0);

        Response getExpensesResponse = _getExpenses();
        List<ExpenseRecordTax> expenses = Arrays.asList(getExpensesResponse.readEntity(ExpenseRecordTax[].class));
        Assert.assertTrue(getExpensesResponse.getStatus() == HttpStatus.SC_OK);
        Assert.assertTrue(expenses.stream().map(ExpenseRecordTax::getId).collect(Collectors.toList()).contains(id));
    }

    @DataProvider
    public static Object[][] invalidInputDataProvider()
    {
        return new Object[][]
        {
            {
                "Missing country code and amount should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"reason\":\"Test\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Invalid country code should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":10, \"reason\":\"Test\", \"country\": \"BAD_COUNTRY_CODE\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Negative amount should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":-1, \"reason\":\"Test\", \"country\":\"GBR\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Amount containing more than 2 decimal points should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":10.234, \"reason\":\"Test\", \"country\":\"GBR\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Amount exceeding one million should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":1000001, \"reason\":\"Test\", \"country\":\"GBR\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Empty country code should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":100, \"reason\":\"Test\", \"country\":\"\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Empty reason should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":10, \"reason\":\"\", \"country\":\"GBR\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Reason longer than 800 characters should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":1000, \"reason\":\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?\", \"country\":\"GBR\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Empty amount should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":\"\", \"reason\":\"Test\", \"country\":\"GBR\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Empty date should result in HTTP 400 status code",
                "{\"date\":\"\", \"amount\":10, \"reason\":\"Test\", \"country\":\"GBR\"}",
                HttpStatus.SC_BAD_REQUEST
            },
            {
                "Invalid date should result in HTTP 400 status code",
                "{\"date\":\"2020-01-XZY\", \"amount\":10, \"reason\":\"Test\", \"country\":\"GBR\"}",
                HttpStatus.SC_BAD_REQUEST
            },
            {
                "Null reason should result in HTTP 400 status code",
                "{\"date\":\"01/01/18\", \"amount\":10, \"reason\":, \"country\":\"GBR\"}",
                HttpStatus.SC_BAD_REQUEST
            },
            {
                "Null amount should result in HTTP 400 status code",
                "{\"date\":\"01/01/18\", \"amount\":, \"reason\":\"Test\", \"country\":\"GBR\"}",
                HttpStatus.SC_BAD_REQUEST
            },
            {
                "Null date should result in HTTP 400 status code",
                "{\"date\":, \"amount\":10, \"reason\":\"Test\", \"country\":\"GBR\"}",
                HttpStatus.SC_BAD_REQUEST
            },
            {
                "Null country code should result in HTTP 400 status code",
                "{\"date\":\"01/01/18\", \"amount\":10, \"reason\":\"Test\", \"country\":}",
                HttpStatus.SC_BAD_REQUEST
            },
        };
    }

    @Test
    @UseDataProvider("invalidInputDataProvider")
    public void testInvalidInput(String message, String jsonString, int expectedStatusCode)
    {
        Assert.assertEquals(message, expectedStatusCode, _saveExpense(jsonString).getStatus());
    }

    @DataProvider
    public static Object[][] saveDeleteExpensesProvider()
    {
        return new Object[][]
            {
                {
                    "For total amount of 10.2 standard UK VAT should be 1.70",
                    "{\"date\":\"01/01/18\", \"amount\":10.2, \"reason\":\"Test\", \"country\": \"GBR\"}",
                    new BigDecimal("1.70")
                },
                {
                    "For total amount of 15000 standard Isle of Man VAT should be 2500.00",
                    "{\"date\":\"01/01/18\", \"amount\":15000, \"reason\":\"Test\", \"country\": \"IMN\"}",
                    new BigDecimal("2500.00")
                },
                {
                    "For total amount of 150.43 standard Guernsey VAT should be 0.00",
                    "{\"date\":\"01/01/18\", \"amount\":15000, \"reason\":\"Test\", \"country\": \"GGY\"}",
                    new BigDecimal("0.00")
                }
            };
    }

    @Test
    @UseDataProvider("saveDeleteExpensesProvider")
    public void saveDeleteExpenses(String message, String jsonString, BigDecimal expectedVat)
    {
        int id = _saveExpense(jsonString).readEntity(Integer.class);
        ExpenseRecordTax expenseRecordTax = _getExpense(id).readEntity(ExpenseRecordTax.class);
        Assert.assertEquals(message, expectedVat, expenseRecordTax.getVat());

        int status = _deleteExpense(id).readEntity(Integer.class);
        Assert.assertEquals(status, 1);
    }

    private static Response _saveExpense(String jsonString)
    {
        return CLIENT.target(RESOURCE_URI)
            .request()
            .post(Entity.json(jsonString));
    }

    private static Response _getExpenses()
    {
        return CLIENT.target(RESOURCE_URI)
            .request()
            .get();
    }

    private static Response _getExpense(int id)
    {
        return CLIENT.target(RESOURCE_URI)
            .path(String.valueOf(id))
            .request()
            .get();
    }

    private static Response _deleteExpense(int id)
    {
        return CLIENT.target(RESOURCE_URI)
            .path(String.valueOf(id))
            .request()
            .delete();
    }
}
