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

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.fcn.phoenix.util.CalendarUtil;
import com.jeecms.cms.entity.assist.CmsTask;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.dfcf.model.AttachBean;
import com.jeecms.dfcf.model.AuthorBean;
import com.jeecms.dfcf.model.FundNewsBean;
import com.jeecms.dfcf.model.ResearchBean;

public abstract class CollectJob extends QuartzJobBean {

	private static final Logger logger = LoggerFactory.getLogger(CollectJob.class);
	@Autowired
	protected CmsUserMng cmsUserMng;
	@Autowired
	protected ContentMng contentMng;
	@Autowired
	protected ChannelMng channelMng;
	
	
	protected final static String USER_NAME = "system";
	protected static final Integer PAGE = 5;
	protected static final Integer SIZE = 20;
	protected Integer siteId;
	protected CmsUser user;
	public abstract void runJob();
	
	@Override
	protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
		// getSiteId
		final JobDataMap jdm = context.getJobDetail().getJobDataMap();
		final String siteIdStr = (String) jdm.get(CmsTask.TASK_PARAM_SITE_ID);
		if (StringUtils.isNotBlank(siteIdStr)) {
			this.siteId = Integer.parseInt(siteIdStr);
		} else {
			this.siteId = 1;
		}

		// getUser
		user = cmsUserMng.findByUsername(USER_NAME);

		// run job
		runJob();
	}
	
	protected void initJob(){
		
	}
	/**
	 * @param interfaceType 请求接口URL类型
	 * @param id  栏目id
	 * @param pageNo   请求页码  最大值5
	 * @param pageSize  请求单页记录   最大值100
	 * @param sort  排序字段  
	 * @param order  排序方式
	 * @return
	 */
	protected String loadJson(String interfaceType, String id, int pageNo, int pageSize, String sort, String order) {
		final String url = "http://open.eastmoney.com/data/API/" + interfaceType + "/token/" + id + "/" + pageNo + "/" + pageSize + "/" + sort + "/" + order;

		if (logger.isDebugEnabled()) {
			logger.debug(url);
		}

		final StringBuilder json = new StringBuilder();
		try {
			URL urlObject = new URL(url);
			URLConnection uc = urlObject.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
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
	protected String loadJsonText(final String interfaceType, final String type, final String h, final String id) {
		final String url = "http://open.eastmoney.com/data/API/" + interfaceType + "/token/" + type + "/" + h + "/" + id;
		final StringBuilder json = new StringBuilder();
		try {
			final URL urlObject = new URL(url);
			final URLConnection uc = urlObject.openConnection();
			final BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "UTF-8"));
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
	
	protected List<FundNewsBean> getFundNewsBeanIds(final String jsonStr,final String key) {
		final List<FundNewsBean> list = new ArrayList<FundNewsBean>();
		try {// 将json字符串转换为json对象
			final JSONObject jsonObj = new JSONObject(jsonStr);
			String idKey = "";
			if("H1".equals(jsonObj.getString("H"))){
				idKey = "Id";
			}else if("H3".equals(jsonObj.getString("H"))){
				idKey = "id";
			}
			// 得到指定json key对象的value对象
			final JSONArray jsonList = jsonObj.getJSONArray(key);
			// 遍历jsonArray
			for (int i = 0; i < jsonList.length(); i++) {
				FundNewsBean fundNewsBean = new FundNewsBean();
				// 获取每一个json对象
				JSONObject jsonItem = jsonList.getJSONObject(i);
				String id = jsonItem.getString(idKey);
				fundNewsBean.setId(id);
				if(jsonItem.getString("Date")!=null&&jsonItem.getString("Date")!=""){
					fundNewsBean.setDate(getDateTime(jsonItem.getString("Date")));
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
	protected List<ResearchBean> getResearchBeanIds(String jsonStr,String key) {
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
					researchBean.setDate(getDateTime(jsonItem.getString("date")));
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
	 * @return
	 * @throws ParseException 
	 */
	private Date getDateTime(final String dateString) {
		Date date = null;
		String[] formats = { "HH:mm","yyyy-MM-dd HH:mm", CalendarUtil.Format.DATE.toString(), CalendarUtil.Format.DATETIME.toString() };
		for (String format : formats) {
			try {
				if ("HH:mm".equals(format)) {
					SimpleDateFormat time = new SimpleDateFormat(format);
					date = time.parse(dateString);
					Calendar c2 = Calendar.getInstance();
					c2.setTime(date);
					Calendar c = Calendar.getInstance();
					c = Calendar.getInstance();
					c.set(Calendar.HOUR, c2.get(Calendar.HOUR));
					c.set(Calendar.MINUTE, c2.get(Calendar.MINUTE));
					c.set(Calendar.SECOND, c2.get(Calendar.SECOND));
					c.set(Calendar.MILLISECOND, c2.get(Calendar.MILLISECOND));
					Date date2 = c.getTime();
					date = date2;
					break;
				} else if (CalendarUtil.Format.DATE.toString().equals(format)||"yyyy-MM-dd HH:mm".equals(format)||CalendarUtil.Format.DATETIME.toString().equals(format)) {
					SimpleDateFormat time = new SimpleDateFormat(format);
					date = time.parse(dateString);
					break;
				}
			} catch (ParseException e) {
				e.printStackTrace();
				continue;
			}
		}
		return date;
	}
	
	@SuppressWarnings("static-access")
	protected FundNewsBean getFundNewsBean(JSONObject jsonObject) {
		FundNewsBean fundNewsBean = new FundNewsBean();
		if(jsonObject.getNames("Author").length>0){
			fundNewsBean.setAuthor(jsonObject.getNames("Author")[0]);
		}
		if(jsonObject.getNames("reletecodes").length>0){
			fundNewsBean.setReletecodes(jsonObject.getNames("reletecodes")[0]);
		}
		if(jsonObject.getString("Date")!=null&&jsonObject.getString("Date")!=""){
			Date date = getDateTime(jsonObject.getString("Date"));
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
	
	protected ResearchBean getResearchBean(String s,AttachBean attachBean,AuthorBean authorBean){
		ResearchBean researchBean = new ResearchBean();
		JSONObject jsonObject = new JSONObject(s);
		researchBean.setAttach(attachBean);
		researchBean.setAuthorList(authorBean);
		researchBean.setChange(jsonObject.getString("change"));
		researchBean.setCode(jsonObject.getString("code"));
		researchBean.setCodename(jsonObject.getString("codename"));
		researchBean.setCprice(jsonObject.getString("cprice"));
		if (jsonObject.getString("date")!=null&&jsonObject.getString("date")!="") {
			researchBean.setDate(getDateTime(jsonObject.getString("date")));
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
			researchBean.setReportDate(getDateTime(jsonObject.getString("reportDate")));
		}
		researchBean.setRtype(jsonObject.getString("rtype"));
		researchBean.setRtypecode(jsonObject.getString("rtypecode"));
		researchBean.setSpace(jsonObject.getString("space"));
		researchBean.setText(jsonObject.getString("text"));
		researchBean.setTitle(jsonObject.getString("title"));
		return researchBean;
	}
	
	protected AttachBean getAttachBean(String jsonStr,String key) {
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
	
	protected AuthorBean getAuthorBean(String jsonStr,String key) {
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

}
