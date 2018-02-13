package com.engage.expenses.resources;

import com.codahale.metrics.annotation.Timed;
import com.engage.expenses.api.ExpenseRecord;
import com.engage.expenses.api.ExpenseRecordTax;
import com.engage.expenses.core.ExpensesService;
import com.engage.expenses.core.auth.User;
import com.google.common.collect.ImmutableList;
import io.dropwizard.auth.Auth;
import org.hibernate.validator.constraints.Range;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Expenses resource
 *
 * Response codes:
 * 1.) BAD_REQUEST (400)           - Unexpected fields found in the request, missing values and JSON parsing exceptions in general
 * 2.) UNPROCESSABLE_ENTITY (422)  - Validation failures (contains explanation in the payload)
 * 3.) INTERNAL_SERVER_ERROR (500) - Indicates a failure such as DB not being accessible
 *
 * TODO: Consider using cache (i.e. Guava's LoadingCache) for this resource, especially when calling getExpenses()
 *
 * @author N/A
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
     * @param user User
     * @param offset Query offset used for pagination purposes
     * @param limit How many records to retrieve starting from the "offset"
     * @return A list of expenses or an empty list if none are found
     */
    @RolesAllowed({ "ADMIN" })
    @GET
    @Timed
    public Response getExpenses(@Auth User user,
                                @QueryParam("offset") @Range(min = 0, message = "Offset has to be greater or equal to 0") int offset,
                                @QueryParam("limit") @Range(min = 0, max = 1000, message = "Maximum number of records is limited to range [1, 1000]") int limit)
    {
        List<ExpenseRecordTax> expenses = m_expensesService.getExpenses(offset, limit);
        if (expenses.isEmpty())
        {
            Response.ok(ImmutableList.of()).build();
        }
        return Response.ok(expenses).build();
    }

    /**
     * Retrieve a single expense tax record
     *
     * @param user User
     * @param id Expense ID
     * @return Expense tax record or an empty response if the matching record hasn't been found
     */
    @RolesAllowed({ "ADMIN" })
    @GET
    @Timed
    @Path("{id}")
    public Response getExpense(@Auth User user,
                               @PathParam("id") final int id)
    {
        return Response.ok(m_expensesService.getExpense(id)).build();
    }

    /**
     * Saves new expense to the DB
     *
     * @param user User
     * @param expense Expense record
     * @return Expense ID for the newly created record
     */
    @RolesAllowed({ "ADMIN" })
    @POST
    @Timed
    public Response saveExpense(@Auth User user,
                                @NotNull @Valid final ExpenseRecord expense)
    {
        return Response.ok(m_expensesService.saveExpense(expense)).build();
    }

    /**
     * Remove an expense record by referencing its ID
     *
     * @param user User
     * @param id Expense record ID
     * @return Status 1 in case of success, otherwise 0
     */
    @RolesAllowed({ "ADMIN" })
    @DELETE
    @Timed
    @Path("{id}")
    public Response deleteExpense(@Auth User user,
                                  @PathParam("id") final int id)
    {
        return Response.ok(m_expensesService.deleteExpense(id)).build();
    }
}
