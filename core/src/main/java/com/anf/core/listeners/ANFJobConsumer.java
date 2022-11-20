package com.anf.core.listeners;

import javax.jcr.Node;
import javax.jcr.Session;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.anf.core.util.ResolverUtil;

@Component(service = JobConsumer.class, immediate = true,
        property = {JobConsumer.PROPERTY_TOPICS + "=" + ANFJobCreater.PROPERTY_TOPIC})
public class ANFJobConsumer implements JobConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ANFJobConsumer.class);

    @Reference
    ResourceResolverFactory resourceResolverFactory;

    @Override
    public JobResult process(final Job job) {
        try {
            final ResourceResolver resourceResolver =
                    ResolverUtil.getResourceResolver(this.resourceResolverFactory);
            final String path = (String) job.getProperty("path");
            final Session session = resourceResolver.adaptTo(Session.class);
            final Node node = session.getNode(path + "/jcr:content");
            node.setProperty("pageCreated", true);
            session.save();
            LOG.info("\n Job executing for  : {} ", resourceResolver.getResource(path).getName());
            return JobResult.OK;
        } catch (final Exception e) {
            LOG.info("\n Error in Job Consumer : {}  ", e.getMessage());
            return JobResult.FAILED;
        }
    }

}
