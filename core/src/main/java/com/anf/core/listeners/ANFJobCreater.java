package com.anf.core.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.JobManager;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.day.cq.wcm.api.PageEvent;
import com.day.cq.wcm.api.PageModification;
import com.day.cq.wcm.api.PageModification.ModificationType;

@Component(service = {EventHandler.class}, immediate = true,
        property = {EventConstants.EVENT_TOPIC + "=" + PageEvent.EVENT_TOPIC})
public class ANFJobCreater implements EventHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ANFJobCreater.class);
    public static final String PROPERTY_TOPIC = "anf/job";
    private static final String FILTER_PATH = "/content/anf-code-challenge/us/en";// Ideally we
                                                                                  // should get this
                                                                                  // path from
                                                                                  // configurable
                                                                                  // path.

    @Reference
    JobManager jobManager;

    @Override
    public void handleEvent(final Event event) {
        try {
            final Iterator<PageModification> pageIter =
                    PageEvent.fromEvent(event).getModifications();
            while (pageIter.hasNext()) {
                final PageModification pageModification = pageIter.next();
                if ((pageModification.getType().equals(ModificationType.CREATED))
                        && (pageModification.getPath().startsWith(FILTER_PATH))) {
                    final Map<String, Object> jobProperties = new HashMap<>();
                    jobProperties.put("event", event.getTopic());
                    jobProperties.put("path", pageModification.getPath());
                    final Job job = this.jobManager.addJob(PROPERTY_TOPIC, jobProperties);
                }
            }

        } catch (final Exception e) {
            LOG.error("\n Exception is : {} ", e.getMessage());
        }

    }

}
