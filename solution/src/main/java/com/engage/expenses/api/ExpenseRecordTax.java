package com.engage.expenses.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A class which represents expense-related taxes
 *
 * @author jklarica
 * @since 2018-02-10
 */
@JsonIgnoreProperties({ "amount", "country" })
@JsonPropertyOrder({ "id", "date", "vat", "reason" })
public class ExpenseRecordTax extends ExpenseRecord
{
    private BigDecimal m_vat;

    @JsonCreator
    public ExpenseRecordTax(@JsonProperty("id") int id,
                            @JsonProperty("date") LocalDate date,
                            @JsonProperty("vat") BigDecimal vat,
                            @JsonProperty("reason") String reason)
    {
        super(id, date, BigDecimal.ZERO, reason);
        m_vat = vat;
    }

    public ExpenseRecordTax() {
    }

    public BigDecimal getVat()
    {
        return m_vat;
    }

    public void setVat(BigDecimal vat)
    {
        m_vat = vat;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        if (!super.equals(o))
            return false;
        ExpenseRecordTax that = (ExpenseRecordTax) o;
        return Objects.equals(m_vat, that.m_vat);
    }

    @Override public int hashCode()
    {
        return Objects.hash(super.hashCode(), m_vat);
    }

    @Override public String toString()
    {
        return "ExpenseRecordTax{" +
            "m_vat=" + m_vat +
            '}';
    }
}
