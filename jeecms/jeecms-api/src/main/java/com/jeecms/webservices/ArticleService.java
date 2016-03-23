/** TOM */
package com.jeecms.webservices;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.model.ArticleModel;
import com.jeecms.model.CategoryModel;

public class ArticleService extends SpringBeanAutowiringSupport {

	@Autowired
	private ContentMng contentMng;
	@Autowired
	private ChannelMng channelMng;

	public ArticleModel getByArticleId(final Integer articleId) {
		return contentMng.getByArticleId(articleId);
	}

	public ArticleModel[] findAllContent(final String search, final Integer categoryId, final Date releaseStartDate, final Date releaseEndDate, final Integer pageNo, final Integer pageSize) {
		return contentMng.findAllContent(search, categoryId, releaseStartDate, releaseEndDate, pageNo, pageSize);
	}

	public CategoryModel[] findAllCategory() {
		return contentMng.findAllCategory();
	}
	
	public ArticleModel[] findByHotspots() {
		return contentMng.findByHotspots();
	}
}
