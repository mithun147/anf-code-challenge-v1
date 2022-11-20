package com.anf.core.models;

import java.util.List;

import com.anf.core.beans.NewsArticle;

public interface NewsFeed {
	public String getSourcepath();

	public List<NewsArticle> getNewsList();
}
