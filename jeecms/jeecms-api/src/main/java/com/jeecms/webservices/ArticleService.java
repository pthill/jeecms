/** TOM */
package com.jeecms.webservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.model.ArticleModel;

public class ArticleService extends SpringBeanAutowiringSupport {

	@Autowired
	private ContentMng contentMng;

	public ArticleModel getByArticleId(final Integer articleId) {
		return contentMng.getByArticleId(articleId);
	}

	public ArticleModel[] findAllContent(final Integer pageNo,final Integer pageSize) {
		return contentMng.findAllContent(pageNo, pageSize);
	}

}
