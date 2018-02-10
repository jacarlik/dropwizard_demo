package com.engage.expenses.api;

import com.engage.expenses.ExpensesApplication;
import com.engage.expenses.ExpensesConfiguration;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
        ObjectMapper MAPPER = Jackson.newObjectMapper();
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        MAPPER.configure(MapperFeature.USE_ANNOTATIONS, false);

        Client client = new JerseyClientBuilder(RULE.getEnvironment()).using(MAPPER).build("Test client");

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
