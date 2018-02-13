package com.engage.expenses.db;

import com.engage.expenses.api.ExpenseRecordTax;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple JDBI mapper used to wire result-set entries to new ExpenseRecordTax instance
 *
 * @author
 * @since 2018-02-10
 */
public class ExpenseRecordTaxMapper implements ResultSetMapper<ExpenseRecordTax>
{
    private static final String VAT = "vat";

    public ExpenseRecordTax map(int i,
                                ResultSet rs,
                                StatementContext context) throws SQLException
    {
        return new ExpenseRecordTax(rs.getDate(ExpenseMapper.DATE).toLocalDate(),
                                    rs.getBigDecimal(ExpenseMapper.AMOUNT),
                                    rs.getBigDecimal(VAT),
                                    rs.getString(ExpenseMapper.REASON));
    }
}
