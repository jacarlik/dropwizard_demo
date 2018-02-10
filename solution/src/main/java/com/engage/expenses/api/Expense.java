package com.engage.expenses.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

public class Expense
{
    @JsonProperty("id") private int m_id;
    @JsonProperty("date") @NotNull private LocalDate m_date;
    @JsonIgnore @NotNull @Range(min = 1) private BigDecimal m_amount;
    @JsonIgnore private BigDecimal m_vat;
    @JsonProperty("reason") @NotNull private String m_reason;
    @JsonIgnore @NotNull private String m_country;

    public Expense() {
    }

    public Expense(LocalDate date, BigDecimal amount, String reason, String country)
    {
        m_date = date;
        m_amount = amount;
        m_reason = reason;
        m_country = country;
    }

    public Expense(LocalDate date, BigDecimal amount, BigDecimal vat, String reason, String country)
    {
        m_date = date;
        m_amount = amount;
        m_vat = vat;
        m_reason = reason;
        m_country = country;
    }

    public int getId()
    {
        return m_id;
    }

    public void setId(int id)
    {
        m_id = id;
    }

    public LocalDate getDate()
    {
        return m_date;
    }

    public void setDate(final LocalDate date)
    {
        m_date = date;
    }

    @JsonIgnore
    public BigDecimal getAmount()
    {
        return m_amount;
    }

    @JsonProperty
    public void setAmount(final BigDecimal amount)
    {
        m_amount = amount;
    }

    @JsonProperty
    public BigDecimal getVat()
    {
        return m_vat;
    }

    @JsonIgnore
    public void setVat(final BigDecimal vat)
    {
        m_vat = vat;
    }

    public String getReason()
    {
        return m_reason;
    }

    public void setReason(final String reason)
    {
        m_reason = reason;
    }

    @JsonIgnore
    public String getCountry()
    {
        return m_country;
    }

    @JsonProperty
    public void setCountry(String country)
    {
        m_country = country;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Expense expense = (Expense) o;
        return Objects.equals(m_date, expense.m_date) &&
            Objects.equals(m_amount, expense.m_amount) &&
            Objects.equals(m_vat, expense.m_vat) &&
            Objects.equals(m_reason, expense.m_reason);
    }

    @Override public int hashCode()
    {
        return Objects.hash(m_id, m_date, m_amount, m_vat, m_reason, m_country);
    }

    @Override public String toString()
    {
        return "Expense{" +
            "m_id=" + m_id +
            ", m_date=" + m_date +
            ", m_amount=" + m_amount +
            ", m_vat=" + m_vat +
            ", m_reason='" + m_reason + '\'' +
            ", m_country='" + m_country + '\'' +
            '}';
    }
}
