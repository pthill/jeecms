package com.jeecms.cms.manager.main.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jeecms.cms.dao.main.ContentProductDao;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentProduct;
import com.jeecms.cms.manager.main.ContentProductMng;
import com.jeecms.common.hibernate3.Updater;

@Service
@Transactional
public class ContentProductMngImpl implements ContentProductMng {
	public ContentProduct save(ContentProduct product, Content content) {
		content.setContentProduct(product);
		product.setContent(content);
		product.init();
		dao.save(product);
		content.setContentProduct(product);
		return product;
	}

	public ContentProduct update(ContentProduct bean) {
		Updater<ContentProduct> updater = new Updater<ContentProduct>(bean);
		bean = dao.updateByUpdater(updater);
		bean.blankToNull();
		return bean;
	}

	private ContentProductDao dao;

	@Autowired
	public void setDao(ContentProductDao dao) {
		this.dao = dao;
	}
}