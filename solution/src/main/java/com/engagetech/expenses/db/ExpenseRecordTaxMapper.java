package com.engagetech.expenses.db;

import com.engagetech.expenses.api.ExpenseRecordTax;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.engagetech.expenses.util.CommonUtils.STANDARD_UK_VAT;
import static com.engagetech.expenses.util.CommonUtils.calculateVat;

/**
 * Simple JDBI mapper used to wire result-set entries to ExpenseRecordTax instance
 *
 * @author N/A
 * @since 2018-02-10
 */
public class ExpenseRecordTaxMapper implements ResultSetMapper<ExpenseRecordTax>
{
    public ExpenseRecordTax map(int i,
                                ResultSet rs,
                                StatementContext context) throws SQLException
    {
        return new ExpenseRecordTax(rs.getInt(ExpenseMapper.ID),
                                    rs.getDate(ExpenseMapper.DATE).toLocalDate(),
                                    rs.getBigDecimal(ExpenseMapper.AMOUNT),
                                    calculateVat(rs.getBigDecimal(ExpenseMapper.AMOUNT), STANDARD_UK_VAT),
                                    rs.getString(ExpenseMapper.REASON));
    }
}
