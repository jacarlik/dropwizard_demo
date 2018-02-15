package com.engagetech.expenses.core.auth;
 
import jdk.nashorn.internal.ir.annotations.Immutable;

import java.security.Principal;
import java.util.Set;

/**
 * User representation used for basic authentication/authorization purposes
 *
 * @author N/A
 * @since 2018-02-11
 */
@Immutable
public final class User implements Principal
{
    private final int m_id;
    private final String m_name;
    private final Set<String> m_roles;
 
    public User(String name, Set<String> roles)
    {
        m_id = (int) (Math.random() * 100);
        m_name = name;
        m_roles = roles;
    }

    public int getId()
    {
        return m_id;
    }

    @Override public String getName()
    {
        return m_name;
    }

    public Set<String> getRoles()
    {
        return m_roles;
    }
}
