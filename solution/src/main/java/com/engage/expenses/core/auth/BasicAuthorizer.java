package com.engage.expenses.core.auth;
 
import io.dropwizard.auth.Authorizer;

/**
 * Basic authorizer
 *
 * @author N/A
 * @since 2018-02-10
 */
public class BasicAuthorizer implements Authorizer<User>
{
    @Override
    public boolean authorize(User user, String role)
    {
        return user.getRoles() != null && user.getRoles().contains(role);
    }
}
