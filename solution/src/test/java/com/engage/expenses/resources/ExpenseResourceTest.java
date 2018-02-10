package com.engage.expenses.resources;

import com.engage.expenses.api.Expense;
import com.engage.expenses.db.ExpenseDao;
import com.engage.expenses.service.ExpensesService;
import com.google.common.collect.ImmutableList;
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

public class ExpenseResourceTest
{
    private static final ExpenseDao EXPENSE_DAO = mock(ExpenseDao.class);
    private static final ExpensesService EXPENSES_SERVICE = mock(ExpensesService.class);
    private final Expense EXPENSE = new Expense(LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), "Test", "GBR");

    @ClassRule
    public static final ResourceTestRule RESOURCES = ResourceTestRule.builder()
        .addResource(new ExpenseResource(EXPENSES_SERVICE))
        .build();

    @Before
    public void setup()
    {
        when(EXPENSES_SERVICE.getExpenses()).thenReturn(ImmutableList.of(EXPENSE));
        when(EXPENSES_SERVICE.saveExpense(EXPENSE)).thenReturn(0);
    }

    @After
    public void tearDown()
    {
        reset(EXPENSES_SERVICE);
    }

    @Test
    public void testGetExpenses()
    {
        List<Expense> expenses = Arrays.asList(RESOURCES.target("/expenses").request().get().readEntity(Expense[].class));
        assertThat(expenses.get(0)).isEqualTo(EXPENSE);
        verify(EXPENSES_SERVICE).getExpenses();
    }

    @Test
    public void testSaveExpense()
    {
        assertThat(RESOURCES.target("/expenses").request().post(Entity.entity(EXPENSE, MediaType.APPLICATION_JSON)).readEntity(Integer.class)).isEqualTo(0);
        verify(EXPENSES_SERVICE).saveExpense(EXPENSE);
    }
}
