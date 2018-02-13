package com.engage.expenses.api;

import static io.dropwizard.testing.FixtureHelpers.*;

import com.engage.expenses.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expenses service (de)serialization tests
 *
 * @author N/A
 * @since 2018-02-10
 */
public class ExpenseTest
{
    private static final ObjectMapper MAPPER = CommonUtils.getObjectMapper();
    private static final ExpenseRecord EXPENSE_RECORD = new ExpenseRecord(
        LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), "Test"
    );

    private static final ExpenseRecordTax EXPENSE_RECORD_TAX = new ExpenseRecordTax(
        1, LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), new BigDecimal("1.7"), "Test"
    );

    @BeforeClass
    public static void setUp()
    {
        EXPENSE_RECORD.setId(1001);
    }

    @Test
    public void expenseRecordSerializesToJSON() throws Exception
    {
        Assert.assertEquals(
            "ExpenseRecord can be serialized to JSON",
            fixture("fixtures/expense_record.json"),
            MAPPER.writeValueAsString(EXPENSE_RECORD)
        );
    }

    @Test
    public void expenseRecordDeserializesFromJSON() throws Exception
    {
        Assert.assertEquals(
            "ExpenseRecord can be deserialized from JSON",
            EXPENSE_RECORD,
            MAPPER.readValue(fixture("fixtures/expense_record.json"), ExpenseRecord.class)
        );
    }

    @Test
    public void expenseRecordTaxSerializesToJSON() throws Exception
    {
        Assert.assertEquals(
            "ExpenseRecordTax can be serialized to JSON",
            fixture("fixtures/expense_record_tax.json"),
            MAPPER.writeValueAsString(EXPENSE_RECORD_TAX)
        );
    }

    @Test
    public void expenseRecordTaxDeserializesFromJSON() throws Exception
    {
        Assert.assertEquals(
            "ExpenseRecordTax can be deserialized from JSON",
            EXPENSE_RECORD_TAX,
            MAPPER.readValue(fixture("fixtures/expense_record_tax.json"), ExpenseRecordTax.class)
        );
    }
}
