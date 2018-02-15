package com.engagetech.expenses.resources;

import com.engagetech.expenses.api.ExpenseRecord;
import com.engagetech.expenses.api.ExpenseRecordTax;
import com.engagetech.expenses.client.fixer.FixerIOClient;
import com.engagetech.expenses.core.ExpensesService;
import com.engagetech.expenses.core.auth.BasicAuthenticator;
import com.engagetech.expenses.core.auth.BasicAuthorizer;
import com.engagetech.expenses.core.auth.User;
import com.engagetech.expenses.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.apache.http.HttpHeaders;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.glassfish.jersey.test.grizzly.GrizzlyWebTestContainerFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Expenses service resource tests
 *
 * @author N/A
 * @since 2018-02-10
 */
public class ExpenseResourceTest
{
    private static final ExpensesService EXPENSES_SERVICE = mock(ExpensesService.class);
    private static final FixerIOClient FIXER_IO_CLIENT = mock(FixerIOClient.class);

    // Expense amount using GBP
    private final ExpenseRecord EXPENSE_RECORD_GBP = new ExpenseRecord(LocalDate.parse("2018-02-10"), BigDecimal.valueOf(10.2), "Test");

    // Expense amount using EUR
    private final ExpenseRecord EXPENSE_RECORD_EUR = new ExpenseRecord(LocalDate.parse("2018-02-10"), BigDecimal.valueOf(100), "EUR", "Test");

    // Expense amount converted from EUR to GBP
    private final ExpenseRecord EXPENSE_RECORD_CONVERTED_EUR_GBP = new ExpenseRecord(LocalDate.parse("2018-02-10"), BigDecimal.valueOf(88.77), "Test");

    private final ExpenseRecordTax EXPENSE_RECORD_TAX = new ExpenseRecordTax(1, LocalDate.parse("2018-02-10"), BigDecimal.valueOf(10.2), BigDecimal.valueOf(1.7), "Test");
    private static final ObjectMapper MAPPER = CommonUtils.getObjectMapper();

    // How many records to request from the service
    private static final int OFFSET = 0;
    private static final int LIMIT = 5;

    // Credentials
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String BASIC_AUTH_HEADER = "Basic YWRtaW46YWRtaW4=";

    @ClassRule
    public static ResourceTestRule RESOURCES = ResourceTestRule.builder()
        .setTestContainerFactory(new GrizzlyWebTestContainerFactory())
        .addProvider(new AuthDynamicFeature(new BasicCredentialAuthFilter.Builder<User>()
                                                .setAuthenticator(new BasicAuthenticator(USERNAME, PASSWORD))
                                                .setAuthorizer(new BasicAuthorizer())
                                                .setRealm("BASIC-AUTH-REALM")
                                                .buildAuthFilter()))
        .addProvider(RolesAllowedDynamicFeature.class)
        .addProvider(new AuthValueFactoryProvider.Binder<>(User.class))
        .addResource(new ExpenseResource(EXPENSES_SERVICE, FIXER_IO_CLIENT))
        .setMapper(MAPPER)
        .build();

    @Before
    public void setup()
    {
        when(EXPENSES_SERVICE.getExpenses(OFFSET, LIMIT)).thenReturn(ImmutableList.of(EXPENSE_RECORD_TAX));
        when(EXPENSES_SERVICE.getExpense(1)).thenReturn(EXPENSE_RECORD_TAX);
        when(EXPENSES_SERVICE.saveExpense(EXPENSE_RECORD_GBP)).thenReturn(0);
        when(EXPENSES_SERVICE.saveExpense(EXPENSE_RECORD_CONVERTED_EUR_GBP)).thenReturn(0);
        when(EXPENSES_SERVICE.deleteExpense(1)).thenReturn(1);
        when(FIXER_IO_CLIENT.convertCurrency(LocalDate.of(2018, 2, 10), "EUR", "GBP", BigDecimal.valueOf(100))).thenReturn(BigDecimal.valueOf(88.77));
    }

    @Test
    public void testGetExpenses()
    {
        List<ExpenseRecordTax> expenses = Arrays.asList(
            RESOURCES.target("/expenses")
                .queryParam("offset", OFFSET)
                .queryParam("limit", LIMIT)
                .request()
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                .get()
                .readEntity(ExpenseRecordTax[].class)
        );
        Assert.assertEquals("Requested expense records correspond to the list retrieved from the resource", EXPENSE_RECORD_TAX, expenses.get(0));
        verify(EXPENSES_SERVICE).getExpenses(OFFSET, LIMIT);
    }

    @Test
    public void testGetExpense()
    {
        ExpenseRecordTax expense = RESOURCES.target("/expenses")
            .path(String.valueOf(1))
            .request()
            .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
            .get()
            .readEntity(ExpenseRecordTax.class);
        Assert.assertEquals("Requested expense record corresponds to the retrieved one", EXPENSE_RECORD_TAX, expense);
        verify(EXPENSES_SERVICE).getExpense(1);
    }

    @Test
    public void testSaveExpenseGBP()
    {
        Assert.assertEquals(
            "Expense record can be saved",
            new Integer(0),
            RESOURCES.target("/expenses")
                .request()
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                .post(Entity.entity(EXPENSE_RECORD_GBP, MediaType.APPLICATION_JSON))
                .readEntity(Integer.class)
        );

        verify(EXPENSES_SERVICE).saveExpense(EXPENSE_RECORD_GBP);
    }

    @Test
    public void testSaveExpenseEUR()
    {
        Assert.assertEquals(
            "Expense record can be saved",
            new Integer(0),
            RESOURCES.target("/expenses")
                .request()
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                .post(Entity.entity(EXPENSE_RECORD_EUR, MediaType.APPLICATION_JSON))
                .readEntity(Integer.class)
        );
        verify(EXPENSES_SERVICE).saveExpense(EXPENSE_RECORD_CONVERTED_EUR_GBP);
    }

    @Test
    public void testDeleteExpense()
    {
        Assert.assertEquals(
            "Expense record can be deleted",
            new Integer(1),
            RESOURCES.target("/expenses")
                .path(String.valueOf(1))
                .request()
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                .delete()
                .readEntity(Integer.class)
        );
        verify(EXPENSES_SERVICE).deleteExpense(1);
    }
}
