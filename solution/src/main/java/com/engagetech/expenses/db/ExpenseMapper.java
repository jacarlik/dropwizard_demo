package com.engagetech.expenses.db;

import com.engagetech.expenses.api.ExpenseRecord;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple JDBI mapper used to wire result-set entries to ExpenseRecord instance
 *
 * @author N/A
 * @since 2018-02-10
 */
public class ExpenseMapper implements ResultSetMapper<ExpenseRecord>
{
    static final String ID = "id";
    static final String DATE = "date";
    static final String AMOUNT = "amount";
    static final String REASON = "reason";

    public ExpenseRecord map(int i,
                             ResultSet resultSet,
                             StatementContext context) throws SQLException
    {
        return new ExpenseRecord(resultSet.getDate(DATE).toLocalDate(),
                                 resultSet.getBigDecimal(AMOUNT),
                                 resultSet.getString(REASON));
    }
}
