package com.engage.expenses.db;

import com.engage.expenses.api.ExpenseRecord;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple JDBI mapper used to wire result-set entries to new ExpenseRecord instance
 *
 * @author jklarica
 * @since 2018-02-10
 */
public class ExpenseMapper implements ResultSetMapper<ExpenseRecord>
{
    private static final String DATE = "date";
    private static final String AMOUNT = "amount";
    private static final String REASON = "reason";
    private static final String COUNTRY = "country";

    public ExpenseRecord map(int i,
                             ResultSet resultSet,
                             StatementContext context) throws SQLException
    {
        return new ExpenseRecord(resultSet.getDate(DATE).toLocalDate(),
                                 resultSet.getBigDecimal(AMOUNT),
                                 resultSet.getString(REASON),
                                 resultSet.getString(COUNTRY));
    }
}
