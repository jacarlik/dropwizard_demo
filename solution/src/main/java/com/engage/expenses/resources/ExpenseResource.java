package com.engage.expenses.resources;

import com.codahale.metrics.annotation.Timed;
import com.engage.expenses.api.ExpenseRecord;
import com.engage.expenses.service.ExpensesService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Expenses resource
 *
 * Response codes:
 *
 * 1.) BAD_REQUEST (400)           - Unexpected fields found in the request, missing values and JSON parsing exceptions in general
 * 2.) UNPROCESSABLE_ENTITY (422)  - Validation failures (contains explanation in the payload)
 * 3.) INTERNAL_SERVER_ERROR (500) - Indicates a failure such as DB not being accessible
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
     * Retrieve a single expense tax record
     *
     * @param id Expense ID
     * @return Expense tax record
     */
    @GET
    @Timed
    @Path("{id}")
    public Response getExpense(@PathParam("id") final int id)
    {
        return Response.ok(m_expensesService.getExpense(id)).build();
    }

    /**
     * Save a new expense record to the DB
     *
     * @param expense Expense object
     * @return Response object
     */
    @POST
    @Timed
    public Response saveExpense(@NotNull @Valid final ExpenseRecord expense)
    {
        return Response.ok(m_expensesService.saveExpense(expense)).build();
    }

    /**
     * Delete a single expense record from the DB
     *
     * @param id Expense record ID
     * @return Status 1 indicates success, 0 that no records were deleted
     */
    @DELETE
    @Timed
    @Path("{id}")
    public Response deleteExpense(@PathParam("id") final int id)
    {
        return Response.ok(m_expensesService.deleteExpense(id)).build();
    }
}
