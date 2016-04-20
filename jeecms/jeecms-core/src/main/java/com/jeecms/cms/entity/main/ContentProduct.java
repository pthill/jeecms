package com.jeecms.cms.entity.main;

import org.apache.commons.lang3.StringUtils;

import com.jeecms.cms.entity.main.base.BaseContentProduct;

public class ContentProduct extends BaseContentProduct {

	private static final long serialVersionUID = 1L;

	public void init(){
		blankToNull();
	}
	
	public void blankToNull() {
		if (StringUtils.isBlank(getProductCode())) {
			setProductCode(null);
		}
	}
	
	/* [CONSTRUCTOR MARKER BEGIN] */
	public ContentProduct () {
		super();
	}

	/**
	 * Constructor for primary key
	 */
	public ContentProduct (java.lang.Integer id) {
		super(id);
	}

	/**
	 * Constructor for required fields
	 */
	public ContentProduct (
		java.lang.Integer id,
		java.lang.String productCode) {

		super (
			id,
			productCode);
	}

	/* [CONSTRUCTOR MARKER END] */

	
}
