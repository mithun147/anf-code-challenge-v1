package com.anf.core.models.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.factory.ModelFactory;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import com.anf.core.beans.NewsArticle;
import com.anf.core.models.NewsFeed;
import com.day.cq.wcm.api.Page;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

/**
 * Simple JUnit test verifying the NewsFeedImpl
 */
@ExtendWith(AemContextExtension.class)
public class NewsFeedImplTest {

    private final AemContext context = new AemContext(ResourceResolverType.RESOURCERESOLVER_MOCK);
    @Mock
    private Resource resource;
    @Mock
    private NewsFeed newsFeed;
    @Mock
    private Page page;
    @Mock
    private ModelFactory modelFactory;
    @Mock
    private ResourceResolver resourceResolver;

    @BeforeEach
    public void setup() throws Exception {

        this.context.addModelsForClasses(NewsFeedImpl.class);
        this.page = this.context.create().page("/content/mypage");
        this.context.load().json("/com/anf/core/models/impl/newsfeed.json",
                "/var/commerce/products/anf-code-challenge");
        this.resource = this.context.create().resource(this.page, "newsfeed", "sling:resourceType",
                "anf-code-challenge/components/newsfeed", "sourcepath",
                "/var/commerce/products/anf-code-challenge");
        this.newsFeed = this.context.request().adaptTo(NewsFeed.class);

    }

    @Test
    void testGetNewsList() {
        final List<NewsArticle> newsList = this.newsFeed.getNewsList();
        assertNotNull(newsList);
        assertTrue(newsList.size() == 10);
        assertTrue(newsList.get(1).getTitle()
                .equals("Foxes and D.C. Politicians Don’t Mix, As This Week and History Show"));
        assertTrue(newsList.get(1).getAuthor().equals("Leah Askarinam"));
        assertTrue(newsList.get(1).getUrl().equals(
                "https://www.nytimes.com/2022/04/08/us/politics/capitol-hill-fox-history.html"));
        assertTrue(newsList.get(1).getUrlImage().equals(
                "https://static01.nyt.com/images/2022/04/06/us/politics/-06onpolitics-pm-newsletter-fox/-06onpolitics-pm-newsletter-fox-facebookJumbo-v2.jpg"));
    }

    @Test
    void testGetSourcepath() {
        assertTrue(
                this.newsFeed.getSourcepath().equals("/var/commerce/products/anf-code-challenge"));
    }

}
