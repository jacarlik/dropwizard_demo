package com.engage.expenses.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A class representing a single expense record
 *
 * @author
 * @since 2018-02-10
 */
@JsonPropertyOrder({ "id", "date", "amount", "reason" })
public class ExpenseRecord
{
    @JsonProperty("id")
    private int m_id;

    @JsonProperty("date")
    @NotNull(message = "Date should not be null")
    private LocalDate m_date;

    @JsonProperty("amount")
    @NotNull(message = "Amount should not be null")
    @Range(min = 1, max = 1000000, message = "Amount should be in range [1, 1000000]")
    @Digits(integer = 1000000, fraction = 2, message = "Amount should contain not more than 2 decimal places")
    private BigDecimal m_amount;

    @JsonProperty("reason")
    @NotBlank(message = "Reason should not be empty or null")
    @Length(max = 800, message = "Maximum reason length is limited to 800 characters")
    private String m_reason;

    ExpenseRecord() {
    }

    public ExpenseRecord(LocalDate date, BigDecimal amount, String reason)
    {
        m_date = date;
        m_amount = amount;
        m_reason = reason;
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

    public BigDecimal getAmount()
    {
        return m_amount;
    }

    public void setAmount(final BigDecimal amount)
    {
        m_amount = amount;
    }

    public String getReason()
    {
        return m_reason;
    }

    public void setReason(final String reason)
    {
        m_reason = reason;
    }

    @Override public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ExpenseRecord that = (ExpenseRecord) o;
        return m_id == that.m_id &&
            Objects.equals(m_date, that.m_date) &&
            Objects.equals(m_amount, that.m_amount) &&
            Objects.equals(m_reason, that.m_reason);
    }

    @Override public int hashCode()
    {
        return Objects.hash(m_id, m_date, m_amount, m_reason);
    }

    @Override public String toString()
    {
        return "ExpenseRecord{" +
            "m_id=" + m_id +
            ", m_date=" + m_date +
            ", m_amount=" + m_amount +
            ", m_reason='" + m_reason + '\'' +
            '}';
    }
}
