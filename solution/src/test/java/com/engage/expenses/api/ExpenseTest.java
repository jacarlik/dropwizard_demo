package com.engage.expenses.api;

import static io.dropwizard.testing.FixtureHelpers.*;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.dropwizard.jackson.Jackson;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseTest
{
    private static final ObjectMapper MAPPER = Jackson.newObjectMapper();
    private static final ExpenseRecord EXPENSE = new ExpenseRecord(
        LocalDate.parse("2018-02-10"), new BigDecimal("10.2"), "Test", "GBR"
    );

    static
    {
        EXPENSE.setId(1001);
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.configure(MapperFeature.USE_ANNOTATIONS, false);
    }

    @Test
    public void serializesToJSON() throws Exception {
        MatcherAssert.assertThat(
            "Expense can be serialized to JSON",
            MAPPER.writeValueAsString(EXPENSE),
            is(equalTo(fixture("fixtures/expense.json")))
        );
    }

    @Test
    public void deserializesFromJSON() throws Exception {
        MatcherAssert.assertThat(
            "Expense can be deserialized from JSON",
            MAPPER.readValue(fixture("fixtures/expense.json"), ExpenseRecord.class),
            is(EXPENSE)
        );
    }
}
