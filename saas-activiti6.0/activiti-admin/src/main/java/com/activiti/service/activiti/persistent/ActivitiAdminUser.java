package com.activiti.service.activiti.persistent;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * A {@link UserDetails} implementation that exposes the {@link com.activiti.domain.idm.User.User} object
 * the logged in user represents.
 * 
 * @author Frederik Heremans
 * @author Joram Barrez
 */
public class ActivitiAdminUser extends User {

    private static final long serialVersionUID = 1L;
    
    protected org.activiti.engine.identity.User userObject;
    
    /**
     * The userId needs to be passed explicitly. It can be the email, but also the external id when eg LDAP is being used. 
     */
    public ActivitiAdminUser(org.activiti.engine.identity.User user, String userId, Collection<? extends GrantedAuthority> authorities) {
        super(userId, user.getPassword() != null ? user.getPassword() : "", authorities); // Passwords needs to be non-null. Even if it's not there (eg LDAP auth)
        this.userObject = user;
    }
    
    public org.activiti.engine.identity.User getUserObject() {
        return userObject;
    }
}
