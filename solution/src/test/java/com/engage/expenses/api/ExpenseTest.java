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
    private static final ExpenseRecord EXPENSE = new ExpenseRecord(
        LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), "Test", "GBR"
    );


    @BeforeClass
    public static void setUp()
    {
        EXPENSE.setId(1001);
    }

    @Test
    public void serializesToJSON() throws Exception {
        MatcherAssert.assertThat(
            "Assert that expense can be serialized to JSON",
            MAPPER.writeValueAsString(EXPENSE),
            is(equalTo(fixture("fixtures/expense.json")))
        );
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        MatcherAssert.assertThat(
            "Assert that expense can be deserialized from JSON",
            MAPPER.readValue(fixture("fixtures/expense.json"), ExpenseRecord.class),
            is(EXPENSE)
        );
    }
}
