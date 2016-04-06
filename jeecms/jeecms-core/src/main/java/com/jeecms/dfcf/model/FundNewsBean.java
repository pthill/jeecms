package com.jeecms.dfcf.model;

import java.util.Date;

/**
 * 新闻
 * @author TengWei
 */
public class FundNewsBean {
	
	private String newsId;
	private String Id;
	private String author;
	private Date date;
	private String from;
	private String order;
	private String title;
	private String url;
	private String reletecodes;
	private String docreader;
	private String text;
	
	public String getNewsId() {
		return newsId;
	}
	public void setNewsId(String newsId) {
		this.newsId = newsId;
	}
	public String getId() {
		return Id;
	}
	public void setId(String id) {
		Id = id;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getOrder() {
		return order;
	}
	public void setOrder(String order) {
		this.order = order;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getReletecodes() {
		return reletecodes;
	}
	public void setReletecodes(String reletecodes) {
		this.reletecodes = reletecodes;
	}
	public String getDocreader() {
		return docreader;
	}
	public void setDocreader(String docreader) {
		this.docreader = docreader;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
