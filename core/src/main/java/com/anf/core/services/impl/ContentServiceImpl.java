package com.anf.core.services.impl;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import com.anf.core.beans.UserDetails;
import com.anf.core.services.ContentService;
import com.anf.core.util.ResolverUtil;

/**
 * @author mithun
 *
 */
@Component(immediate = true, service = ContentService.class)
@Designate(ocd = ContentServiceImpl.Config.class)
public class ContentServiceImpl implements ContentService {

    // OSGI configurations
    @ObjectClassDefinition(name = "Content Service Configuration",
            description = "OSGI service providing configuration options for Content Service")
    @interface Config {

        @AttributeDefinition(name = "Path where maximum and minimum age is configured",
                description = "Node path where max and min age is configured for validation")
        String getAgeConfigPath() default "/etc/age";

        @AttributeDefinition(name = "Users Root Path",
                description = "Root path under which user details should be saved")
        String getuserPath() default "/var/anf-code-challenge";
    }

    @Reference
    private ResourceResolverFactory resolverFactory;

    private String ageMaxMinPath;
    private String usersPath;
    Log log = LogFactory.getLog(ContentServiceImpl.class);

    /**
     * save user details to under "/var/anf-code-challenge"
     */
    @Override
    public void commitUserDetails(final UserDetails userDetails)
            throws RepositoryException, LoginException {

        final Session session =
                ResolverUtil.getResourceResolver(this.resolverFactory).adaptTo(Session.class);

        final String firstName = userDetails.getFirstName().toLowerCase();
        final String lastName = userDetails.getLastName().toLowerCase();
        final String fullName = new StringBuilder(firstName).append(lastName).toString();
        final Node usersRootNode =
                JcrUtils.getOrCreateByPath(this.usersPath, "sling:Folder", session);
        final Node userDetailsNode =
                JcrUtils.getOrCreateUniqueByPath(usersRootNode, fullName, "nt:unstructured");

        userDetailsNode.setProperty("firstName", userDetails.getFirstName());
        userDetailsNode.setProperty("lastName", userDetails.getLastName());
        userDetailsNode.setProperty("country", userDetails.getCountry());
        userDetailsNode.setProperty("age", userDetails.getAge());

        // save changes
        session.save();
    }

    @Override
    public boolean validateAge(final int age) {

        boolean validAge = false;
        try {

            if ((age < this.getMinAge()) || (age > this.getMaxAge())) {
                return validAge;
            } else {
                validAge = true;
            }

        } catch (RepositoryException | LoginException e) {
            this.log.error("Error while reading age limit", e);
            throw new RuntimeException("Error while reading age limit.");
        }
        return validAge;
    }

    /**
     * @param config
     */
    @Activate
    protected void activate(final Config config) {
        this.ageMaxMinPath = config.getAgeConfigPath();
        this.usersPath = config.getuserPath();
    }

    private Node getAgeConfigNode() throws LoginException {

        final ResourceResolver resolver = ResolverUtil.getResourceResolver(this.resolverFactory);
        Node ageConfigNode = null;
        if (resolver != null) {
            final Resource resource = resolver.getResource(this.ageMaxMinPath);
            if (resource != null) {
                ageConfigNode = resource.adaptTo(Node.class);
            }
        }

        return ageConfigNode;

    }

    /**
     * @return
     * @throws NumberFormatException
     * @throws RepositoryException
     * @throws LoginException
     */
    private int getMaxAge() throws NumberFormatException, RepositoryException, LoginException {
        final Node ageConfigNode = this.getAgeConfigNode();
        int maxAge = 0;
        if (ageConfigNode != null) {
            maxAge = Integer.valueOf(ageConfigNode.getProperty("maxAge").getString());
        }
        return maxAge;
    }

    private int getMinAge() throws NumberFormatException, RepositoryException, LoginException {
        final Node ageConfigNode = this.getAgeConfigNode();
        int minAge = 0;
        if (ageConfigNode != null) {
            minAge = Integer.valueOf(ageConfigNode.getProperty("minAge").getString());
        }
        return minAge;
    }


}
