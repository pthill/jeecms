package com.jeecms.cms.manager.main;

import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.ContentProduct;

public interface ContentProductMng {
	public ContentProduct save(ContentProduct product, Content content);

	public ContentProduct update(ContentProduct product);
}