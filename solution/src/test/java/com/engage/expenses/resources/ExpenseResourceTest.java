package com.engage.expenses.resources;

import com.engage.expenses.api.ExpenseRecord;
import com.engage.expenses.api.ExpenseRecordTax;
import com.engage.expenses.service.ExpensesService;
import com.engage.expenses.util.CommonUtils;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableList;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.junit.ResourceTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

/**
 * Expenses service resource tests
 *
 * @author jklarica
 * @since 2018-02-10
 */
public class ExpenseResourceTest
{
    private static final ExpensesService EXPENSES_SERVICE = mock(ExpensesService.class);
    private final ExpenseRecord EXPENSE_RECORD = new ExpenseRecord(LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), "Test", "GBR");
    private final ExpenseRecordTax EXPENSE_RECORD_TAX = new ExpenseRecordTax(0, LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), new BigDecimal("1.7"), "Test");
    private static final ObjectMapper MAPPER = CommonUtils.getObjectMapper();

    @ClassRule
    public static ResourceTestRule RESOURCES = ResourceTestRule.builder()
        .addResource(new ExpenseResource(EXPENSES_SERVICE))
        .setMapper(MAPPER)
        .build();

    @Before
    public void setup()
    {
        when(EXPENSES_SERVICE.getExpenses()).thenReturn(ImmutableList.of(EXPENSE_RECORD_TAX));
        when(EXPENSES_SERVICE.saveExpense(EXPENSE_RECORD)).thenReturn(0);
    }

    @After
    public void tearDown()
    {
        reset(EXPENSES_SERVICE);
    }

    @Test
    public void testGetExpenses()
    {
        List<ExpenseRecordTax> expenses = Arrays.asList(RESOURCES.target("/expenses").request().get().readEntity(ExpenseRecordTax[].class));
        assertThat(expenses.get(0)).isEqualTo(EXPENSE_RECORD_TAX);
        verify(EXPENSES_SERVICE).getExpenses();
    }

    @Test
    public void testSaveExpense()
    {
        assertThat(RESOURCES.target("/expenses").request().post(Entity.entity(EXPENSE_RECORD, MediaType.APPLICATION_JSON)).readEntity(Integer.class)).isEqualTo(0);
        verify(EXPENSES_SERVICE).saveExpense(EXPENSE_RECORD);
    }
}
