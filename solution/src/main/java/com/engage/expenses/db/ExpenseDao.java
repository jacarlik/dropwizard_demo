package com.engage.expenses.db;

import com.engage.expenses.api.ExpenseRecord;
import com.engage.expenses.api.ExpenseRecordTax;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * A DAO class containing expense-related queries
 *
 * @author
 * @since 2018-02-10
 */
@RegisterMapper({ExpenseMapper.class, ExpenseRecordTaxMapper.class})
public interface ExpenseDao
{
    @SqlQuery("SELECT id, date, amount, ROUND(amount - (amount / 1.2), 2) AS vat, reason FROM t_expense;")
    List<ExpenseRecordTax> getExpenses();

    @SqlQuery("SELECT id, date, amount, ROUND(amount - (amount / 1.2), 2) AS vat, reason FROM t_expense WHERE id = :id;")
    ExpenseRecordTax getExpense(@Bind("id") final int id);

    @Transaction
    @SqlUpdate("INSERT INTO t_expense(date, amount, reason) VALUES(:date, :amount, :reason)")
    @GetGeneratedKeys
    int saveExpense(@BindBean final ExpenseRecord expense);

    @SqlUpdate("DELETE FROM t_expense WHERE id = :id")
    int deleteExpense(@Bind("id") final int id);
}
