package com.engagetech.expenses.api;

import com.engagetech.expenses.ExpensesApplication;
import com.engagetech.expenses.ExpensesConfiguration;
import com.engagetech.expenses.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.http.HttpHeaders;
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

import static com.engagetech.expenses.util.CommonUtils.SCALE;

/**
 * Expenses application integration tests
 *
 * @author N/A
 * @since 2018-02-10
 */
@RunWith(DataProviderRunner.class)
public class IntegrationTest
{
    @ClassRule
    public static final DropwizardAppRule<ExpensesConfiguration> RULE = new DropwizardAppRule<>(
        ExpensesApplication.class, "src/main/resources/profiles/mainline.yml"
    );
    private static ObjectMapper MAPPER = CommonUtils.getObjectMapper();
    private static Client CLIENT;
    private static String RESOURCE_URI;
    private static final String BASIC_AUTH_HEADER = "Basic YWRtaW46YWRtaW4=";

    @BeforeClass
    public static void setUp()
    {
        CLIENT = new JerseyClientBuilder(RULE.getEnvironment())
            .using(RULE.getConfiguration().getJerseyClientConfiguration())
            .using(MAPPER)
            .build("Test client");

        RESOURCE_URI = String.format("https://localhost:%d/app/expenses", RULE.getLocalPort());
    }

    @DataProvider
    public static Object[][] invalidInputDataProvider()
    {
        return new Object[][]
        {
            {
                "Negative amount should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":-1, \"reason\":\"Test\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Amount containing more than 2 decimal points should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":10.234, \"reason\":\"Test\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Amount exceeding one million should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":1000001, \"reason\":\"Test\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Empty reason should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":10, \"reason\":\"\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Reason longer than 800 characters should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":1000, \"reason\":\"Sed ut perspiciatis unde omnis iste natus error sit voluptatem accusantium doloremque laudantium, totam rem aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia voluptas sit aspernatur aut odit aut fugit, sed quia consequuntur magni dolores eos qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in ea voluptate velit esse quam nihil molestiae consequatur, vel illum qui dolorem eum fugiat quo voluptas nulla pariatur?\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Empty amount should result in HTTP 422 status code",
                "{\"date\":\"01/01/18\", \"amount\":\"\", \"reason\":\"Test\"}",
                HttpStatus.SC_UNPROCESSABLE_ENTITY
            },
            {
                "Empty date should result in HTTP 400 status code",
                "{\"date\":\"\", \"amount\":10, \"reason\":\"Test\"}",
                HttpStatus.SC_BAD_REQUEST
            },
            {
                "Invalid date should result in HTTP 400 status code",
                "{\"date\":\"2020-01-XZY\", \"amount\":10, \"reason\":\"Test\"}",
                HttpStatus.SC_BAD_REQUEST
            },
            {
                "Missing reason should result in HTTP 400 status code",
                "{\"date\":\"01/01/18\", \"amount\":10, \"reason\":}",
                HttpStatus.SC_BAD_REQUEST
            },
            {
                "Missing amount should result in HTTP 400 status code",
                "{\"date\":\"01/01/18\", \"amount\":, \"reason\":\"Test\"}",
                HttpStatus.SC_BAD_REQUEST
            },
            {
                "Null date should result in HTTP 400 status code",
                "{\"date\":, \"amount\":10, \"reason\":\"Test\"}",
                HttpStatus.SC_BAD_REQUEST
            }
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
                    "For total amount of 10.2 UK VAT should amount to 1.70",
                    "{\"date\":\"01/01/18\", \"amount\":10.2, \"reason\":\"Test\"}",
                    BigDecimal.valueOf(1.70).setScale(SCALE, BigDecimal.ROUND_HALF_UP)
                },
                {
                    "For total amount of 15000 UK VAT should amount to 2500.00",
                    "{\"date\":\"01/01/18\", \"amount\":15000, \"reason\":\"Test\"}",
                    BigDecimal.valueOf(2500.00).setScale(SCALE, BigDecimal.ROUND_HALF_UP)
                },
                {
                    "For total amount of 150.43 UK VAT should amount to 25.07",
                    "{\"date\":\"01/01/18\", \"amount\":150.43, \"reason\":\"Test\"}",
                    BigDecimal.valueOf(25.07).setScale(SCALE, BigDecimal.ROUND_HALF_UP)
                },
                {
                    "For total amount of 100EUR UK VAT should amount to 14.79",
                    "{\"date\":\"01/01/18\", \"amount\":100, \"reason\":\"Test\", \"currency\":\"EUR\"}",
                    BigDecimal.valueOf(14.79).setScale(SCALE, BigDecimal.ROUND_HALF_UP)
                },
                {
                    "For total amount of 100USD UK VAT should amount to 12.09",
                    "{\"date\":\"15/01/18\", \"amount\":100, \"reason\":\"Test\", \"currency\":\"USD\"}",
                    BigDecimal.valueOf(12.09).setScale(SCALE, BigDecimal.ROUND_HALF_UP)
                },
                {
                    "For total amount of 15000HUF UK VAT should amount to 7.21",
                    "{\"date\":\"15/01/18\", \"amount\":15000, \"reason\":\"Test\", \"currency\":\"HUF\"}",
                    BigDecimal.valueOf(7.21).setScale(SCALE, BigDecimal.ROUND_HALF_UP)
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

    @DataProvider
    public static Object[][] expensesQueryParamsProvider()
    {
        return new Object[][]
            {
                {
                    "Expecting HTTP 400 status code with offset of 0 and limit of 0", 0, 0, HttpStatus.SC_BAD_REQUEST
                },
                {
                    "Expecting HTTP 400 status code with offset of -1 and limit of 0", -1, 0, HttpStatus.SC_BAD_REQUEST
                },
                {
                    "Expecting HTTP 400 status code with offset of 0 and limit of -1", 0, -1, HttpStatus.SC_BAD_REQUEST
                },
                {
                    "Expecting HTTP 400 status code with offset of 0 and limit of 100000", 0, 100000, HttpStatus.SC_BAD_REQUEST
                },
                {
                    "Expecting HTTP 200 status code with offset of 0 and limit of 1", 0, 1, HttpStatus.SC_OK
                },
                {
                    "Expecting HTTP 200 status code with offset of 0 and limit of 1", 10, 100, HttpStatus.SC_OK
                },
            };
    }

    @Test
    @UseDataProvider("expensesQueryParamsProvider")
    public void testGetExpensesQueryParams(String message, int offset, int limit, int expectedStatusCode)
    {
        Assert.assertEquals(message, expectedStatusCode, _getExpenses(offset, limit).getStatus());
    }

    private static Response _saveExpense(String jsonString)
    {
        return CLIENT.target(RESOURCE_URI)
            .request()
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .post(Entity.json(jsonString));
    }

    private static Response _getExpenses(int offset, int limit)
    {
        return CLIENT.target(RESOURCE_URI)
            .queryParam("offset", offset)
            .queryParam("limit", limit)
            .request()
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .get();
    }

    private static Response _getExpense(int id)
    {
        return CLIENT.target(RESOURCE_URI)
            .path(String.valueOf(id))
            .request()
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .get();
    }

    private static Response _deleteExpense(int id)
    {
        return CLIENT.target(RESOURCE_URI)
            .path(String.valueOf(id))
            .request()
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .delete();
    }
}
