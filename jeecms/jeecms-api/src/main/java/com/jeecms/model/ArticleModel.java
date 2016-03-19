package com.jeecms.model;

import java.io.Serializable;

public class ArticleModel implements Serializable {

	private static final long serialVersionUID = -1325453597739580402L;

	private String title;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
