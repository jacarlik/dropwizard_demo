package com.engage.expenses.api;

import static io.dropwizard.testing.FixtureHelpers.*;
import static org.hamcrest.Matchers.*;

import com.engage.expenses.util.CommonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.MatcherAssert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expenses service (de)serialization tests
 *
 * @author jklarica
 * @since 2018-02-10
 */
public class ExpenseTest
{
    private static final ObjectMapper MAPPER = CommonUtils.getObjectMapper();
    private static final ExpenseRecord EXPENSE_RECORD = new ExpenseRecord(
        LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), "Test"
    );

    private static final ExpenseRecordTax EXPENSE_RECORD_TAX = new ExpenseRecordTax(
        LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), new BigDecimal("1.7"), "Test"
    );

    @BeforeClass
    public static void setUp()
    {
        EXPENSE_RECORD.setId(1001);
    }

    @Test
    public void expenseRecordSerializesToJSON() throws Exception
    {
        MatcherAssert.assertThat(
            "Assert that ExpenseRecord can be serialized to JSON",
            MAPPER.writeValueAsString(EXPENSE_RECORD),
            is(equalTo(fixture("fixtures/expense_record.json")))
        );
    }

    @Test
    public void expenseRecordDeserializesFromJSON() throws Exception
    {
        MatcherAssert.assertThat(
            "Assert that ExpenseRecord can be deserialized from JSON",
            MAPPER.readValue(fixture("fixtures/expense_record.json"), ExpenseRecord.class),
            is(EXPENSE_RECORD)
        );
    }

    @Test
    public void expenseRecordTaxSerializesToJSON() throws Exception
    {
        MatcherAssert.assertThat(
            "Assert that ExpenseRecordTax can be serialized to JSON",
            MAPPER.writeValueAsString(EXPENSE_RECORD_TAX),
            is(equalTo(fixture("fixtures/expense_record_tax.json")))
        );
    }

    @Test
    public void expenseRecordTaxDeserializesFromJSON() throws Exception
    {
        MatcherAssert.assertThat(
            "Assert that ExpenseRecordTax can be deserialized from JSON",
            MAPPER.readValue(fixture("fixtures/expense_record_tax.json"), ExpenseRecordTax.class),
            is(EXPENSE_RECORD_TAX)
        );
    }
}
