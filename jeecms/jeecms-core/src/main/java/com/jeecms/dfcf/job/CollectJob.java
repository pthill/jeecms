package com.jeecms.dfcf.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.jeecms.cms.dao.main.impl.ContentDaoImpl;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.Content.ContentStatus;
import com.jeecms.cms.entity.main.ContentDoc;
import com.jeecms.cms.entity.main.ContentExt;
import com.jeecms.cms.entity.main.ContentTxt;
import com.jeecms.cms.manager.assist.CmsFileMng;
import com.jeecms.cms.manager.main.CmsModelMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsSiteMng;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.dfcf.model.AttachBean;
import com.jeecms.dfcf.model.AuthorBean;
import com.jeecms.dfcf.model.FundNewsBean;
import com.jeecms.dfcf.model.ResearchBean;

@Controller
public class CollectJob{

	@Autowired
	private CmsUserMng cmsUserMng;
	@Autowired
	private CmsSiteMng cmsSiteMng;
	@Autowired
	private ContentMng manager;
	@Autowired
	private CmsModelMng cmsModelMng;
	@Autowired
	private CmsFileMng fileMng;
	@Autowired
	private ContentMng contentMng;
	@Autowired
	private ContentDaoImpl contentDaoImpl;

	/**
	 * @param interfaceType 请求接口URL类型
	 * @param id  栏目id
	 * @param pageNo   请求页码  最大值5
	 * @param pageSize  请求单页记录   最大值100
	 * @param sort  排序字段  
	 * @param order  排序方式
	 * @return
	 */
	private static String loadJson(String interfaceType,String id,int pageNo,int pageSize,String sort,String order) {
		String url = "http://open.eastmoney.com/data/API/"+interfaceType+"/token/"+id+"/"+pageNo+"/"+pageSize+"/"+sort+"/"+order;
		StringBuilder json = new StringBuilder();
		try {
			URL urlObject = new URL(url);
			URLConnection uc = urlObject.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				json.append(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	/**
	 * 获取正文
	 * @param interfaceType  请求接口URL类型
	 * @param type (CE，去掉html标签后的内容。C，资讯内容)
	 * @param h  (H1:新闻，H2：公告，H3：研报，H4：法律法规)
	 * @param id
	 * @return
	 */
	private static String loadJsonText(String interfaceType,String type,String h,String id) {
		String url = "http://open.eastmoney.com/data/API/"+interfaceType+"/token/"+type+"/"+h+"/"+id;
		StringBuilder json = new StringBuilder();
		try {
			URL urlObject = new URL(url);
			URLConnection uc = urlObject.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					uc.getInputStream()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				json.append(inputLine);
			}
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return json.toString();
	}
	
	private static List<FundNewsBean> getFundNewsBeanIds(String jsonStr,String key) {
		List<FundNewsBean> list = new ArrayList<FundNewsBean>();
		try {// 将json字符串转换为json对象
			JSONObject jsonObj = new JSONObject(jsonStr);
			String idKey = "";
			if("H1".equals(jsonObj.getString("H"))){
				idKey = "Id";
			}else if("H3".equals(jsonObj.getString("H"))){
				idKey = "id";
			}
			// 得到指定json key对象的value对象
			JSONArray jsonList = jsonObj.getJSONArray(key);
			// 遍历jsonArray
			for (int i = 0; i < jsonList.length(); i++) {
				FundNewsBean fundNewsBean = new FundNewsBean();
				// 获取每一个json对象
				JSONObject jsonItem = jsonList.getJSONObject(i);
				String id = jsonItem.getString(idKey);
				fundNewsBean.setId(id);
				if(jsonItem.getString("Date")!=null&&jsonItem.getString("Date")!=""){
					fundNewsBean.setDate(getDateTime(jsonItem.getString("Date"),"HH:mm"));
				}
				String title = jsonItem.getString("Title");
				fundNewsBean.setTitle(title);
				list.add(fundNewsBean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	private static List<ResearchBean> getResearchBeanIds(String jsonStr,String key) {
		List<ResearchBean> list = new ArrayList<ResearchBean>();
		try {// 将json字符串转换为json对象
			JSONObject jsonObj = new JSONObject(jsonStr);
			String idKey = "";
			if("H1".equals(jsonObj.getString("H"))){
				idKey = "Id";
			}else if("H3".equals(jsonObj.getString("H"))){
				idKey = "id";
			}
			// 得到指定json key对象的value对象
			JSONArray jsonList = jsonObj.getJSONArray(key);
			// 遍历jsonArray
			for (int i = 0; i < jsonList.length(); i++) {
				ResearchBean researchBean = new ResearchBean();
				// 获取每一个json对象
				JSONObject jsonItem = jsonList.getJSONObject(i);
				String id = jsonItem.getString(idKey);
				researchBean.setId(id);
				if (jsonItem.getString("date")!=null&&jsonItem.getString("date")!="") {
					researchBean.setDate(getDateTime(jsonItem.getString("date"),"yyyy-MM-dd"));
				}
				String title = jsonItem.getString("title");
				researchBean.setTitle(title);
				list.add(researchBean);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	/**
	 * 日期转换
	 * @param dateString  日期格式字符串
	 * @param format   日期格式
	 * @return
	 */
	private static Date getDateTime(final String dateString, final String format) {
		Date date = null;
		try {
			if("HH:mm".equals(format)){
				SimpleDateFormat time = new SimpleDateFormat(format);
				date = time.parse(dateString);
				
				Calendar c2=Calendar.getInstance();
				c2.setTime(date);
				
				Calendar c=Calendar.getInstance();
				c=Calendar.getInstance();
				c.set(Calendar.HOUR, c2.get(Calendar.HOUR));
				c.set(Calendar.MINUTE, c2.get(Calendar.MINUTE));
				c.set(Calendar.SECOND, c2.get(Calendar.SECOND));
				c.set(Calendar.MILLISECOND, c2.get(Calendar.MILLISECOND));
				Date date2 = c.getTime();
				date = date2;
			}else if("yyyy-MM-dd".equals(format)){
				SimpleDateFormat time = new SimpleDateFormat(format);
				date = time.parse(dateString);
			}
		} catch (ParseException e) {
			getDateTime(dateString, "YYYY-MM-DD");
		}
		
		return date;
	}
	
	@SuppressWarnings("static-access")
	private static FundNewsBean getFundNewsBean(JSONObject jsonObject) {
		FundNewsBean fundNewsBean = new FundNewsBean();
		if(jsonObject.getNames("Author").length>0){
			fundNewsBean.setAuthor(jsonObject.getNames("Author")[0]);
		}
		if(jsonObject.getNames("reletecodes").length>0){
			fundNewsBean.setReletecodes(jsonObject.getNames("reletecodes")[0]);
		}
		if(jsonObject.getString("Date")!=null&&jsonObject.getString("Date")!=""){
			final Date date=getDateTime(jsonObject.getString("Date"),"HH:mm");
			fundNewsBean.setDate(date);
		}
		fundNewsBean.setNewsId(jsonObject.getString("newsid"));
		fundNewsBean.setDocreader(jsonObject.getString("docreader"));
		fundNewsBean.setFrom(jsonObject.getString("From"));
		fundNewsBean.setId(jsonObject.getString("Id"));
		fundNewsBean.setTitle(jsonObject.getString("Title"));
		fundNewsBean.setUrl(jsonObject.getString("url"));
		fundNewsBean.setText(jsonObject.getString("text"));
		return fundNewsBean;
	}
	
	private static ResearchBean getResearchBean(String s,AttachBean attachBean,AuthorBean authorBean) {
		ResearchBean researchBean = new ResearchBean();
		JSONObject jsonObject = new JSONObject(s);
		researchBean.setAttach(attachBean);
		researchBean.setAuthorList(authorBean);
		researchBean.setChange(jsonObject.getString("change"));
		researchBean.setCode(jsonObject.getString("code"));
		researchBean.setCodename(jsonObject.getString("codename"));
		researchBean.setCprice(jsonObject.getString("cprice"));
		if (jsonObject.getString("date")!=null&&jsonObject.getString("date")!="") {
			researchBean.setDate(getDateTime(jsonObject.getString("date"),"yyyy-MM-dd"));
		}
		researchBean.setDprice(jsonObject.getString("dprice"));
		researchBean.setId(jsonObject.getString("id"));
		researchBean.setIndustry(jsonObject.getString("industry"));
		researchBean.setIndustrycode(jsonObject.getString("industry"));
		researchBean.setKcode(jsonObject.getString("kcode"));
		researchBean.setKname(jsonObject.getString("kname"));
		researchBean.setKtype(jsonObject.getString("ktype"));
		researchBean.setOrg(jsonObject.getString("org"));
		researchBean.setOrgcode(jsonObject.getString("orgcode"));
		researchBean.setOrgprizeinfo(jsonObject.getString("orgprizeinfo"));
		researchBean.setRate(jsonObject.getString("rate"));
		if (jsonObject.getString("reportDate")!=null&&jsonObject.getString("reportDate")!="") {
			researchBean.setReportDate(getDateTime(jsonObject.getString("reportDate"),"yyyy-MM-dd HH:mm:ss"));
		}
		researchBean.setRtype(jsonObject.getString("rtype"));
		researchBean.setRtypecode(jsonObject.getString("rtypecode"));
		researchBean.setSpace(jsonObject.getString("space"));
		researchBean.setText(jsonObject.getString("text"));
		researchBean.setTitle(jsonObject.getString("title"));
		return researchBean;
	}
	
	private static AttachBean getAttachBean(String jsonStr,String key) {
		AttachBean attachBean = new AttachBean();

		try {// 将json字符串转换为json对象
			JSONObject jsonObj = new JSONObject(jsonStr);
			JSONArray jsonList = jsonObj.getJSONArray(key);
			if (jsonList.length()>0) {
				// 得到指定json key对象的value对象
				attachBean.setFileSize(jsonList.getJSONObject(0).getString(
						"fileSize"));
				attachBean.setFiletype(jsonList.getJSONObject(0).getString(
						"filetype"));
				attachBean.setName(jsonList.getJSONObject(0).getString("name"));
				attachBean.setPagenum(jsonList.getJSONObject(0).getString(
						"pagenum"));
				attachBean.setSeq(jsonList.getJSONObject(0).getInt("seq"));
				attachBean.setUrl(jsonList.getJSONObject(0).getString("url"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return attachBean;
	}
	
	private static AuthorBean getAuthorBean(String jsonStr,String key) {
		AuthorBean authorBean = new AuthorBean();

		try {// 将json字符串转换为json对象
			JSONObject jsonObj = new JSONObject(jsonStr);
			JSONArray jsonList = jsonObj.getJSONArray(key);
			if (jsonList.length()>0) {
				// 得到指定json key对象的value对象
				authorBean.setAuth(jsonList.getJSONObject(0).getString("auth"));
				authorBean.setAuthcode(jsonList.getJSONObject(0).getString(
						"authcode"));
				authorBean.setAuthprizeinfo(jsonList.getJSONObject(0)
						.getString("authprizeinfo"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return authorBean;
	}
	public void getResearchJson() {
		CmsUser user = cmsUserMng.findById(1);
		CmsSite site = cmsSiteMng.findById(1);
		String[] str = {"T004005001","T004005002"};
		for (int i = 0; i < str.length; i++) {
			for (int j = 1; j < 100; j++) {
				String json = loadJson("EMInfoCResearchList", str[i], j, 50,"datetime", "desc");
				if (json != "" && json != null) {
					Integer channelId = 0;
					if ("T004005001".equals(str[i])) {
						channelId = 78;
					} else if ("T004005002".equals(str[i])) {
						channelId = 79;
					}
					for (ResearchBean rb : getResearchBeanIds(json, "records")) {
						List<Content> contents=(List<Content>) contentMng.getPageByRight(null, null, user.getId(), user.getId(), false, false, ContentStatus.checked, contentMng.getCheckStep(), site.getId(), channelId, user.getId(),2, 1, 1).getList();
						if (!contents.get(0).getTitle().equals(rb.getTitle())&&contents.get(0).getReleaseDate().getTime()!=rb.getDate().getTime()) {
							String s = loadJsonText("EMInfoContent", "C", "H3",rb.getId());
							s=s.substring(14, s.length());
							AttachBean attachBean = getAttachBean(s, "attach");
							AuthorBean authorBean = getAuthorBean(s,"authorList");
							ResearchBean researchBean = getResearchBean(s,attachBean, authorBean);
							saveResearchs(channelId, researchBean);
						} else {
							j=100;
							break;
						}
					}
				}
			}
		}
	}
	private void saveResearchs(Integer channelId, ResearchBean researchBean){
		Content bean = new Content();
		ContentExt ext = new ContentExt();
		ext.setAuthor(researchBean.getAuthorList().getAuth());
		ext.setTitle(researchBean.getTitle());
		ext.setOrigin("东方财富");
		ext.setReleaseDate(researchBean.getDate());
		ContentTxt txt = new ContentTxt();
		txt.setTxt(researchBean.getText());
		ContentDoc doc = new ContentDoc();
		Integer[] topicIds={0};
		Integer typeId = 1;
		CmsUser user = cmsUserMng.findById(1);
		CmsSite site = cmsSiteMng.findById(1);
		bean.setSite(site);
		bean.setModel(cmsModelMng.findById(1));
		String[] tagArr = {};
		String[] attachmentPaths= {researchBean.getAttach().getName()};
		String[] attachmentNames = {researchBean.getAttach().getUrl()};
		String[] attachmentFilenames = {researchBean.getAttach().getName()};
		manager.save(bean, ext, txt, doc,null, topicIds, null,
				tagArr, attachmentPaths, attachmentNames, attachmentFilenames,
				null, null, channelId, typeId, false,false, user, true);
		//处理附件
		fileMng.updateFileByPaths(attachmentPaths,null,ext.getMediaPath(),ext.getTitleImg(),ext.getTypeImg(),ext.getContentImg(),true,bean);
	}
	
	public void getfundNewsJson() {
		CmsUser user = cmsUserMng.findById(1);
		CmsSite site = cmsSiteMng.findById(1);
		String[] str = {"S888009009","S888013001","S888013002","S888013003","S888013004","S888013005","S888013006","S888013007","S888013008","S888013009"};
		for (int i = 0; i < str.length; i++) {
			for (int j = 1; j < 100; j++) {
				String json = loadJson("EMInfoCNewsList", str[i], j, 50,"date", "desc");
				if (json != "" && json != null) {
					Integer channelId = 0;
					if ("S888009009".equals(str[i])) {
						channelId = 11;
					} else if ("S888013001".equals(str[i])) {
						channelId = 12;
					} else if ("S888013002".equals(str[i])) {
						channelId = 13;
					} else if ("S888013003".equals(str[i])) {
						channelId = 14;
					} else if ("S888013004".equals(str[i])) {
						channelId = 15;
					} else if ("S888013005".equals(str[i])) {
						channelId = 40;
					} else if ("S888013006".equals(str[i])) {
						channelId = 41;
					} else if ("S888013007".equals(str[i])) {
						channelId = 70;
					} else if ("S888013008".equals(str[i])) {
						channelId = 75;
					} else if ("S888013009".equals(str[i])) {
						channelId = 75;
					}
					for (FundNewsBean fnb : getFundNewsBeanIds(json, "records")) {
//						Content content = contentMng.getByMaxReleaseDate(user.getId(), channelId);
						List<Content> contents=(List<Content>) contentMng.getPageByRight(null, null, user.getId(), user.getId(), false, false, ContentStatus.checked,contentMng.getCheckStep(), site.getId(), channelId, user.getId(), 2,1, 1).getList();
						if (!contents.get(0).getTitle().equals(fnb.getTitle())&&contents.get(0).getReleaseDate().getTime()!=fnb.getDate().getTime()) {
							String s = loadJsonText("EMInfoContent", "C", "H1",fnb.getId());
							s=s.substring(14, s.length());
							JSONObject jsonObject = new JSONObject(s);
							FundNewsBean fundNewsBean = getFundNewsBean(jsonObject);
							saveFundNews(channelId, fundNewsBean);
						} else {
							j=100;
							break;
						}

					}
				}
			}
		}
	}
	
	
	private void saveFundNews(Integer channelId, FundNewsBean fundNewsBean) {
		Content bean = new Content();
		ContentExt ext = new ContentExt();
		ext.setAuthor(fundNewsBean.getAuthor());
		ext.setOrigin(fundNewsBean.getFrom());
		ext.setTitle(fundNewsBean.getTitle());
		ext.setReleaseDate(fundNewsBean.getDate());
		ext.setDescription(fundNewsBean.getDocreader());
		ContentTxt txt = new ContentTxt();
		txt.setTxt(fundNewsBean.getText());
		ContentDoc doc = new ContentDoc();
		Integer[] topicIds={0};
		Integer typeId = 1;
		CmsUser user = cmsUserMng.findById(1);
		CmsSite site = cmsSiteMng.findById(1);
		bean.setSite(site);
		bean.setModel(cmsModelMng.findById(1));
		String[] tagArr = {};
		manager.save(bean, ext, txt, doc,null, topicIds, null,
				tagArr, null, null, null,
				null, null, channelId, typeId, false,false, user, true);
	}
	
	
}
