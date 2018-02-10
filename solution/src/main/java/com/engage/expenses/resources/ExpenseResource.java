package com.engage.expenses.resources;

import com.codahale.metrics.annotation.Timed;
import com.engage.expenses.ExpensesConfiguration;
import com.engage.expenses.api.Expense;
import com.engage.expenses.service.ExpensesService;
import com.google.common.annotations.VisibleForTesting;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Expenses resource
 *
 * @author jklarica
 * @since 2018-02-09
 */
@Path("expenses")
@Produces(MediaType.APPLICATION_JSON)
public class ExpenseResource
{
    private final ExpensesService m_expensesService;

    public ExpenseResource(ExpensesService expensesService)
    {
        m_expensesService = expensesService;
    }

    /**
     * Retrieve a list of all expenses
     *
     * @return Response object
     */
    @GET
    @Timed
    public Response getExpenses()
    {
        return Response.ok(m_expensesService.getExpenses()).build();
    }

    /**
     * Save a new expense record to the DB
     *
     * @param expense Expense object
     * @return Response object
     */
    @POST
    @Timed
    public Response saveExpense(@NotNull @Valid final Expense expense)
    {
        return Response.ok(m_expensesService.saveExpense(expense)).build();
    }
}
