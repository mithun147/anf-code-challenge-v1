package com.anf.core.models.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import com.anf.core.beans.NewsArticle;
import com.anf.core.models.NewsFeed;

@Model(adaptables = SlingHttpServletRequest.class, adapters = NewsFeed.class,
        resourceType = {NewsFeedImpl.RESOURCE_TYPE},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class NewsFeedImpl implements NewsFeed {

    protected static final String RESOURCE_TYPE = "anf-code-challenge/components/newsfeed";

    @ValueMapValue
    @Default(values = "/var/commerce/products/anf-code-challenge")
    private String sourcepath;

    @SlingObject
    private ResourceResolver resourceResolver;

    private List<NewsArticle> newsList;
    String currentDate = this.getCurrentDate();

    @Override
    public List<NewsArticle> getNewsList() {
        return this.newsList;
    }

    @Override
    public String getSourcepath() {
        return this.sourcepath;
    }

    private String getCurrentDate() {

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return simpleDateFormat.format(new Date());
    }

    @PostConstruct
    private void init() {
        this.newsList = new ArrayList<>();
        final Resource newsResource =
                this.resourceResolver.getResource(this.sourcepath + "/newsData");
        final Iterator<Resource> newsIterator = newsResource.listChildren();
        while (newsIterator.hasNext()) {
            final Resource resource = newsIterator.next();
            final ValueMap valueMap = resource.getValueMap();
            final NewsArticle newsArticle = new NewsArticle();
            newsArticle.setAuthor(valueMap.get("author", StringUtils.EMPTY));
            newsArticle.setTitle(valueMap.get("title", StringUtils.EMPTY));
            newsArticle.setDescription(valueMap.get("description", StringUtils.EMPTY));
            newsArticle.setContent(valueMap.get("content", StringUtils.EMPTY));
            newsArticle.setUrlImage(valueMap.get("urlImage", StringUtils.EMPTY));
            newsArticle.setUrl(valueMap.get("url", StringUtils.EMPTY));
            newsArticle.setCurrentDate(this.currentDate);
            this.newsList.add(newsArticle);
        }
    }

}
