package com.engage.expenses.resources;

import com.engage.expenses.api.ExpenseRecord;
import com.engage.expenses.api.ExpenseRecordTax;
import com.engage.expenses.core.ExpensesService;
import com.engage.expenses.core.auth.BasicAuthenticator;
import com.engage.expenses.core.auth.BasicAuthorizer;
import com.engage.expenses.core.auth.User;
import com.engage.expenses.util.CommonUtils;
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
    private final ExpenseRecord EXPENSE_RECORD = new ExpenseRecord(LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), "Test");
    private final ExpenseRecordTax EXPENSE_RECORD_TAX = new ExpenseRecordTax(LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), new BigDecimal("1.7"), "Test");
    private static final ObjectMapper MAPPER = CommonUtils.getObjectMapper();
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
        .addResource(new ExpenseResource(EXPENSES_SERVICE))
        .setMapper(MAPPER)
        .build();

    @Before
    public void setup()
    {
        when(EXPENSES_SERVICE.getExpenses()).thenReturn(ImmutableList.of(EXPENSE_RECORD_TAX));
        when(EXPENSES_SERVICE.getExpense(1)).thenReturn(EXPENSE_RECORD_TAX);
        when(EXPENSES_SERVICE.saveExpense(EXPENSE_RECORD)).thenReturn(0);
        when(EXPENSES_SERVICE.deleteExpense(1)).thenReturn(1);
    }

    @After
    public void tearDown()
    {
        reset(EXPENSES_SERVICE);
    }

    @Test
    public void testGetExpenses()
    {
        List<ExpenseRecordTax> expenses = Arrays.asList(
            RESOURCES.target("/expenses")
                .request()
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                .get()
                .readEntity(ExpenseRecordTax[].class)
        );
        Assert.assertEquals("Requested expense records correspond to the list retrieved from the resource", EXPENSE_RECORD_TAX, expenses.get(0));
        verify(EXPENSES_SERVICE).getExpenses();
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
    public void testSaveExpense()
    {
        Assert.assertEquals(
            "Expense record can be saved",
            new Integer(0),
            RESOURCES.target("/expenses")
                .request()
                .header(HttpHeaders.AUTHORIZATION, BASIC_AUTH_HEADER)
                .post(Entity.entity(EXPENSE_RECORD, MediaType.APPLICATION_JSON))
                .readEntity(Integer.class)
        );

        verify(EXPENSES_SERVICE).saveExpense(EXPENSE_RECORD);
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
