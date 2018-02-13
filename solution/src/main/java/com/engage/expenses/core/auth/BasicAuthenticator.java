package com.engage.expenses.core.auth;
 
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
 
import java.util.Map;
import java.util.Optional;
import java.util.Set;
 
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import jdk.nashorn.internal.ir.annotations.Immutable;

/**
 * Basic authenticator
 *
 * @author N/A
 * @since 2018-02-10
 */
@Immutable
public final class BasicAuthenticator implements Authenticator<BasicCredentials, User>
{
    private static final Map<String, Set<String>> ROLES = ImmutableMap.of(
        "guest", ImmutableSet.of(),
        "user", ImmutableSet.of("USER"),
        "admin", ImmutableSet.of("ADMIN", "USER")
    );

    private final String m_username;
    private final String m_password;

    public BasicAuthenticator(String login, String password)
    {
        m_username = login;
        m_password = password;
    }

    @Override
    public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException
    {
        if (m_username.equals(credentials.getUsername()) && m_password.equals(credentials.getPassword()))
        {
            return Optional.of(new User(credentials.getUsername(), ROLES.get(credentials.getUsername())));
        }
        return Optional.empty();
    }
}
