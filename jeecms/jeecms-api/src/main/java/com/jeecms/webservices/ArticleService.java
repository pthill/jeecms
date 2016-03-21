/** TOM */
package com.jeecms.webservices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.common.page.Pagination;
import com.jeecms.model.ArticleModel;
@Service
@Transactional(readOnly=true)
public class ArticleService extends SpringBeanAutowiringSupport {

	@Autowired
	private ContentMng ContentMng;

	public ArticleModel getByContentId(final Long contentId) {
		final Content c=ContentMng.findById(contentId.intValue());
		final ArticleModel am = new ArticleModel();
		am.setTitle(c.getTitle());
		am.setContent(c.getDesc());
		am.setPicture(c.getTypeImg());
		am.setCreateDate(c.getDate());
		return am;
	}

	@SuppressWarnings("unchecked")
	public ArticleModel[] findAllContent(final Long pageNo,final Long pageSize) {
		final Pagination p = ContentMng.getPageByChannelIdsForTag(
				new Integer[] { 11, 12 }, null, null, null, null, 2,
				new HashMap<String, String[]>(), 1, 2, pageNo.intValue(), pageSize.intValue());

		final List<Content> cList = (List<Content>) p.getList();
		final List<ArticleModel> amList = new ArrayList<ArticleModel>();

		for (final Content c : cList) {
			final ArticleModel am = new ArticleModel();
			am.setTitle(c.getTitle());
			am.setContent(c.getDesc());
			am.setPicture(c.getTypeImg());
			am.setCreateDate(c.getDate());
//			am.setUrl("");// c.getUrl()==null? "":c.getUrl()
			amList.add(am);
			am.setArticleId(511l);
		}
		return amList.toArray(new ArticleModel[amList.size()]);
	}

}
