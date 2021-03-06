package com.engagetech.expenses.health;

import com.codahale.metrics.health.HealthCheck;
import com.engagetech.expenses.core.ExpensesService;

/**
 * Basic endpoint health check
 *
 * @author N/A
 * @since 2018-02-09
 */
public class ExpenseResourceHealthCheck extends HealthCheck
{
    private static final String HEALTHY_MESSAGE = "Expenses service is healthy";
    private static final String UNHEALTHY_MESSAGE = "Expenses service is not healthy";

    private final ExpensesService m_expensesService;

    public ExpenseResourceHealthCheck(ExpensesService expensesService)
    {
        m_expensesService = expensesService;
    }

    @Override
    public Result check()
    {
        String healthStatus = m_expensesService.performHealthCheck();

        if (healthStatus == null)
        {
            return Result.healthy(HEALTHY_MESSAGE);
        }
        else
        {
            return Result.unhealthy(UNHEALTHY_MESSAGE , healthStatus);
        }
    }
}