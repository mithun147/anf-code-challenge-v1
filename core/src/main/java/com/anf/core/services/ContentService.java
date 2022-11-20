package com.anf.core.services;

import javax.jcr.RepositoryException;
import org.apache.sling.api.resource.LoginException;
import com.anf.core.beans.UserDetails;

/**
 * @author Mithun Halder
 *
 */

public interface ContentService {
    /**
     * @param userDetails
     * @throws RepositoryException
     * @throws LoginException
     */
    void commitUserDetails(UserDetails userDetails) throws RepositoryException, LoginException;

    /**
     * @param age
     * @return boolean
     */
    boolean validateAge(int age);
}
