package com.engage.expenses.db;

import com.engage.expenses.api.Expense;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

@RegisterMapper(ExpenseMapper.class)
public interface ExpenseDao
{
    @SqlQuery("SELECT e.id, e.date, e.amount, e.reason, ROUND(e.amount - (e.amount / (1 + r.rate)), 2) AS vat, e.country FROM t_expense e INNER JOIN t_standard_vat_rate r ON r.country = e.country;")
    List<Expense> getExpenses();

    @SqlUpdate("INSERT INTO t_expense(date, amount, reason, country) VALUES(:date, :amount, :reason, :country)")
    @GetGeneratedKeys
    int saveExpense(@BindBean final Expense expense);
}
