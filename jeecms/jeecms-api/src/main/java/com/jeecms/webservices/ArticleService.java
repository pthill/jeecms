/** TOM */
package com.jeecms.webservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.model.ArticleModel;

public class ArticleService extends SpringBeanAutowiringSupport {

	@Autowired
	private ContentMng ContentMng;

	public ArticleModel getByContentId(Integer contentId) {
		ContentMng.findById(contentId);
		final ArticleModel am = new ArticleModel();
		return am;
	}

	@SuppressWarnings("unchecked")
	public ArticleModel[] findAllContent() {
		final Pagination p = ContentMng.getPageByChannelIdsForTag(
				new Integer[] { 11 }, null, null, null, null, 2,
				new HashMap<String, String[]>(), 1, 2, 1, 10);

		final List<Content> cList = (List<Content>) p.getList();
		final List<ArticleModel> amList = new ArrayList<ArticleModel>();

		for (final Content c : cList) {
			final ArticleModel am = new ArticleModel();
			am.setTitle(c.getAuthor());
			amList.add(am);
		}
		return amList.toArray(new ArticleModel[amList.size()]);
	}

}
