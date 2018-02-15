package com.engagetech.expenses.db;

import com.engagetech.expenses.api.ExpenseRecord;
import com.engagetech.expenses.api.ExpenseRecordTax;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.Transaction;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Data access class containing expense-related queries
 *
 * @author N/A
 * @since 2018-02-10
 */
@RegisterMapper({ExpenseMapper.class, ExpenseRecordTaxMapper.class})
public interface ExpenseDao
{
    @SqlQuery("SELECT id, date, amount, reason FROM t_expense OFFSET :offset LIMIT :limit;")
    List<ExpenseRecordTax> getExpenses(@Bind("offset") final int offset, @Bind("limit") final int limit);

    @SqlQuery("SELECT id, date, amount, reason FROM t_expense WHERE id = :id;")
    ExpenseRecordTax getExpense(@Bind("id") final int id);

    @Transaction
    @SqlUpdate("INSERT INTO t_expense(date, amount, reason) VALUES(:date, :amount, :reason)")
    @GetGeneratedKeys
    int saveExpense(@BindBean final ExpenseRecord expense);

    @Transaction
    @SqlUpdate("DELETE FROM t_expense WHERE id = :id")
    int deleteExpense(@Bind("id") final int id);
}
