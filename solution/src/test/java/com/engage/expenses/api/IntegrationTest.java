package com.engage.expenses.api;

import com.engage.expenses.ExpensesApplication;
import com.engage.expenses.ExpensesConfiguration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.dropwizard.client.JerseyClientBuilder;
import io.dropwizard.jackson.Jackson;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.apache.http.HttpStatus;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTest
{
    @ClassRule
    public static final DropwizardAppRule<ExpensesConfiguration> RULE =
        new DropwizardAppRule<>(ExpensesApplication.class, ResourceHelpers.resourceFilePath("test.yml"));

    @Test
    public void testExpenses()
    {
        Client client = new JerseyClientBuilder(RULE.getEnvironment()).build("Test client");

        ObjectMapper MAPPER = Jackson.newObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        Response saveExpenseResponse = client.target(
            String.format("http://localhost:%d/api/expenses", RULE.getLocalPort()))
            .request()
            .post(Entity.json("{\"date\":\"2020-01-01\", \"amount\": 10, \"reason\":\"Test\", \"country\": \"GBR\"}"));

        int id = saveExpenseResponse.readEntity(Integer.class);
        assertThat(id).isGreaterThan(0);

        Response getExpensesResponse = client.target(
            String.format("http://localhost:%d/api/expenses", RULE.getLocalPort()))
            .request()
            .get();

        List<Expense> expenses = Arrays.asList(getExpensesResponse.readEntity(Expense[].class));

        assertThat(getExpensesResponse.getStatus()).isEqualTo(HttpStatus.SC_OK);
        assertThat(expenses.stream().map(Expense::getId).collect(Collectors.toList())).contains(id);
    }
}
