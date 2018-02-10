package com.engage.expenses.db;

import com.engage.expenses.api.Expense;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ExpenseMapper implements ResultSetMapper<Expense>
{
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String AMOUNT = "amount";
    private static final String REASON = "reason";
    private static final String COUNTRY = "country";
    private static final String VAT = "vat";

    public Expense map(int i,
                       ResultSet resultSet,
                       StatementContext context) throws SQLException
    {
        Expense expense = new Expense(resultSet.getDate(DATE).toLocalDate(),
                                      resultSet.getBigDecimal(AMOUNT),
                                      resultSet.getString(REASON),
                                      resultSet.getString(COUNTRY));
        expense.setId(resultSet.getInt(ID));
        expense.setVat(resultSet.getBigDecimal(VAT));

        return expense;
    }
}