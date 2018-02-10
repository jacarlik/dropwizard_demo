package com.engage.expenses.db;

import com.engage.expenses.api.ExpenseRecordTax;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple JDBI mapper used to wire result-set entries to new ExpenseRecordTax instance
 *
 * @author jklarica
 * @since 2018-02-10
 */
public class ExpenseRecordTaxMapper implements ResultSetMapper<ExpenseRecordTax>
{
    private static final String ID = "id";
    private static final String DATE = "date";
    private static final String VAT = "vat";
    private static final String REASON = "reason";

    public ExpenseRecordTax map(int i,
                                ResultSet rs,
                                StatementContext context) throws SQLException
    {
        return new ExpenseRecordTax(rs.getInt(ID), rs.getDate(DATE).toLocalDate(), rs.getBigDecimal(VAT), rs.getString(REASON));
    }
}
