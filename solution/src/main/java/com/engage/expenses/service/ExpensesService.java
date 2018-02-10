package com.engage.expenses.service;

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
 * @author jklarica
 * @since 2018-02-10
 */
public abstract class ExpensesService
{
    private static final String DATABASE_ACCESS_ERROR = "Could not reach the PostgreSQL database. The database may be down or there may be network connectivity issues. Details: ";
    private static final String DATABASE_CONNECTION_ERROR = "Could not create a connection to the MySQL database. The database configurations are likely incorrect. Details: ";
    private static final String UNEXPECTED_DATABASE_ERROR = "Unexpected error occurred while attempting to reach the database. Details: ";

    @CreateSqlObject
    abstract ExpenseDao expenseDao();

    public List<ExpenseRecordTax> getExpenses()
    {
        return expenseDao().getExpenses();
    }

    public int saveExpense(ExpenseRecord expense)
    {
        // Strip potential XSS from the "reason" field; an alternative would be return non-200 response to the client, indicating a bad input
        return expenseDao().saveExpense(
            new ExpenseRecord(expense.getDate(), expense.getAmount(), CommonUtils.stripXSS(expense.getReason()), expense.getCountry())
        );
    }

    /**
     * Attempt to retrieve expenses in order to determine the health status of the service
     */
    public String performHealthCheck()
    {
        try
        {
            expenseDao().getExpenses();
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
