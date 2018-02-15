package com.engagetech.expenses;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.client.JerseyClientConfiguration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Expenses application configuration class
 *
 * @author N/A
 * @since 2018-02-09
 */
public class ExpensesConfiguration extends Configuration
{
    @JsonProperty("jerseyClient")
    @Valid
    @NotNull
    private JerseyClientConfiguration m_jerseyClient = new JerseyClientConfiguration();

    @JsonProperty("database")
    @Valid
    @NotNull
    private DataSourceFactory m_database = new DataSourceFactory();

    @JsonProperty("dateFormat")
    @NotBlank
    private String m_dateFormat;

    @JsonProperty("username")
    @NotBlank
    private String m_userName;

    @JsonProperty("password")
    @NotBlank
    private String m_password;

    public JerseyClientConfiguration getJerseyClientConfiguration()
    {
        return m_jerseyClient;
    }

    public void setJerseyClientConfiguration(JerseyClientConfiguration jerseyClient)
    {
        m_jerseyClient = jerseyClient;
    }

    public DataSourceFactory getDataSourceFactory()
    {
        return m_database;
    }

    public void setDataSourceFactory(DataSourceFactory factory)
    {
        m_database = factory;
    }

    public String getDateFormat()
    {
        return m_dateFormat;
    }

    public void setDateFormat(String dateFormat)
    {
        this.m_dateFormat = dateFormat;
    }

    public String getUserName()
    {
        return m_userName;
    }

    public void setUserName(String userName)
    {
        m_userName = userName;
    }

    public String getPassword()
    {
        return m_password;
    }

    public void setPassword(String password)
    {
        m_password = password;
    }
}
