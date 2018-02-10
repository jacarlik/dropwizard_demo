package com.engage.expenses;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Configuration class for the expenses application
 *
 * @author jklarica
 * @since 2018-02-09
 */
public class ExpensesConfiguration extends Configuration
{
    @Valid
    @NotNull
    private DataSourceFactory m_database = new DataSourceFactory();

    @NotEmpty
    private String m_dateFormat;

    @JsonProperty
    public String getDateFormat()
    {
        return m_dateFormat;
    }

    @JsonProperty
    public void setDateFormat(String dateFormat)
    {
        this.m_dateFormat = dateFormat;
    }

    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory factory) {
        m_database = factory;
    }

    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory()
    {
        return m_database;
    }
}
