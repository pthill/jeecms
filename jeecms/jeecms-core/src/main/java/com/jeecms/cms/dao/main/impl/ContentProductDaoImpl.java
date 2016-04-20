package com.jeecms.cms.dao.main.impl;

import org.springframework.stereotype.Repository;

import com.jeecms.cms.dao.main.ContentProductDao;
import com.jeecms.cms.entity.main.ContentProduct;
import com.jeecms.common.hibernate3.HibernateBaseDao;

@Repository
public class ContentProductDaoImpl extends HibernateBaseDao<ContentProduct, Integer>
		implements ContentProductDao {
	public ContentProduct findById(Integer id) {
		ContentProduct entity = get(id);
		return entity;
	}

	public ContentProduct save(ContentProduct bean) {
		getSession().save(bean);
		return bean;
	}

	@Override
	protected Class<ContentProduct> getEntityClass() {
		return ContentProduct.class;
	}
}