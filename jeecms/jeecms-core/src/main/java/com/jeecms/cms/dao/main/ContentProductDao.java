package com.jeecms.cms.dao.main;

import com.jeecms.cms.entity.main.ContentProduct;
import com.jeecms.common.hibernate3.Updater;

public interface ContentProductDao {

	public ContentProduct findById(Integer id);
	
	public ContentProduct save(ContentProduct bean);

	public ContentProduct updateByUpdater(Updater<ContentProduct> updater);
	
}
