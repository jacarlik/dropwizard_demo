package com.engage.expenses;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Expenses application configuration class
 *
 * @author jklarica
 * @since 2018-02-09
 */
public class ExpensesConfiguration extends Configuration
{
    @JsonProperty("database")
    @Valid
    @NotNull
    private DataSourceFactory m_database = new DataSourceFactory();

    @JsonProperty("dateFormat")
    @NotBlank
    private String m_dateFormat;

    public String getDateFormat()
    {
        return m_dateFormat;
    }

    public void setDateFormat(String dateFormat)
    {
        this.m_dateFormat = dateFormat;
    }

    public void setDataSourceFactory(DataSourceFactory factory) {
        m_database = factory;
    }

    public DataSourceFactory getDataSourceFactory()
    {
        return m_database;
    }
}
