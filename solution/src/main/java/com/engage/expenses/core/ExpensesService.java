package com.engage.expenses.core;

import com.engage.expenses.api.ExpenseRecord;
import com.engage.expenses.api.ExpenseRecordTax;
import com.engage.expenses.db.ExpenseDao;
import com.engage.expenses.util.CommonUtils;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;

import java.util.List;

/**
 * Expense service layer acting as a link between resource and DAO layers
 *
 * @author N/A
 * @since 2018-02-10
 */
public abstract class ExpensesService
{
    // Maximum number of records to retrieve within a single page
    private static final int RESULTSET_LIMIT = 1000;

    // Errors
    private static final String DATABASE_ACCESS_ERROR = "Could not reach the database. The database may be down or there may be network connectivity issues. Details: ";
    private static final String DATABASE_CONNECTION_ERROR = "Could not create a connection to the database. The database configurations are likely incorrect. Details: ";
    private static final String UNEXPECTED_DATABASE_ERROR = "Unexpected error occurred while attempting to reach the database. Details: ";
    private static final String RESULTSET_SIZE_EXCEEDED = String.format("Maximum result-set size has been exceeded, please request less than %d records", RESULTSET_LIMIT);

    @CreateSqlObject
    abstract ExpenseDao expenseDao();

    public List<ExpenseRecordTax> getExpenses(int offset, int limit)
    {
        return expenseDao().getExpenses(offset, limit);
    }

    public ExpenseRecordTax getExpense(int id)
    {
        return expenseDao().getExpense(id);
    }

    /**
     * Saves new expense to the DB
     *
     * Also, it strips away potential XSS characters from the "reason" field, since
     * it's the only loose text field, allowing users to plug-in anything.
     *
     * @param expense Expense record
     * @return Expense ID for the newly created record
     */
    public int saveExpense(ExpenseRecord expense)
    {
        return expenseDao().saveExpense(
            new ExpenseRecord(
                expense.getDate(),
                expense.getAmount(),
                CommonUtils.stripXSS(expense.getReason())
            )
        );
    }

    /**
     * Remove an expense record by referencing its ID
     *
     * @param id Expense record ID
     * @return Status 1 in case of success, otherwise 0
     */
    public int deleteExpense(final int id)
    {
        return expenseDao().deleteExpense(id);
    }

    /**
     * Attempt to retrieve expenses in order to determine the health status of the service
     *
     * @return Exception details if health check failed; otherwise null
     */
    public String performHealthCheck()
    {
        try
        {
            expenseDao().getExpenses(0, 10);
        }
        catch (UnableToObtainConnectionException e)
        {
            return _checkUnableToObtainConnectionException(e);
        }
        catch (UnableToExecuteStatementException e)
        {
            return _checkUnableToExecuteStatementException(e);
        }
        catch (Exception e)
        {
            return UNEXPECTED_DATABASE_ERROR + e.getCause().getLocalizedMessage();
        }

        return null;
    }

    private String _checkUnableToObtainConnectionException(UnableToObtainConnectionException e)
    {
        if (e.getCause() instanceof java.sql.SQLNonTransientConnectionException)
        {
            return DATABASE_ACCESS_ERROR + e.getCause().getLocalizedMessage();
        }
        else if (e.getCause() instanceof java.sql.SQLException)
        {
            return DATABASE_CONNECTION_ERROR + e.getCause().getLocalizedMessage();
        }
        else
        {
            return UNEXPECTED_DATABASE_ERROR + e.getCause().getLocalizedMessage();
        }
    }

    private String _checkUnableToExecuteStatementException(UnableToExecuteStatementException e)
    {
        if (e.getCause() instanceof java.sql.SQLSyntaxErrorException)
        {
            return DATABASE_CONNECTION_ERROR + e.getCause().getLocalizedMessage();
        }
        else
        {
            return UNEXPECTED_DATABASE_ERROR + e.getCause().getLocalizedMessage();
        }
    }
}
