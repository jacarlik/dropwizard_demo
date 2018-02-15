package com.engagetech.expenses.client.fixer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Fixer.io result representation
 *
 * Example:
 *
 * GET https://api.fixer.io/latest?symbols=USD,GBP
 * {
 *     "base": "EUR",
 *     "date": "2018-02-14",
 *     "rates": {
 *         "GBP": 0.8904,
 *         "USD": 1.2348
 *     }
 * }
 */
public class FixerIOExchangeRates
{
    @JsonProperty("base")
    private String m_base;

    @JsonProperty("date")
    private String m_date;

    @JsonProperty("rates")
    private Map<String, BigDecimal> m_rates;

    public String getBase()
    {
        return m_base;
    }

    public void setBase(String base)
    {
        this.m_base = base;
    }

    public String getDate()
    {
        return m_date;
    }

    public void setDate(String date)
    {
        m_date = date;
    }

    public Map<String, BigDecimal> getRates()
    {
        return m_rates;
    }

    public void setRates(Map<String, BigDecimal> rates)
    {
        m_rates = rates;
    }
}
