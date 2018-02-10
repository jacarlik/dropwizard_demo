package com.engage.expenses.service;

import com.engage.expenses.api.Expense;
import com.engage.expenses.db.ExpenseDao;
import com.engage.expenses.util.XSSUtils;
import org.skife.jdbi.v2.exceptions.UnableToExecuteStatementException;
import org.skife.jdbi.v2.exceptions.UnableToObtainConnectionException;
import org.skife.jdbi.v2.sqlobject.CreateSqlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

public abstract class ExpensesService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpensesService.class);
    private static final String DATABASE_ACCESS_ERROR = "Could not reach the PostgreSQL database. The database may be down or there may be network connectivity issues. Details: ";
    private static final String DATABASE_CONNECTION_ERROR = "Could not create a connection to the MySQL database. The database configurations are likely incorrect. Details: ";
    private static final String UNEXPECTED_DATABASE_ERROR = "Unexpected error occurred while attempting to reach the database. Details: ";
    private static final String SUCCESS = "Success";
    private static final String EXPENSE_NOT_FOUND = "Expense ID %d not found.";

    @CreateSqlObject
    abstract ExpenseDao expenseDao();

    public List<Expense> getExpenses()
    {
        return expenseDao().getExpenses();
    }

    public int saveExpense(Expense expense)
    {
        // Script potential XSS from the "reason" field; alternative would return non-200 response to the client, indicating a bad input
        return expenseDao().saveExpense(
            new Expense(expense.getDate(), expense.getAmount(), XSSUtils.stripXSS(expense.getReason()), expense.getCountry())
        );
    }

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
