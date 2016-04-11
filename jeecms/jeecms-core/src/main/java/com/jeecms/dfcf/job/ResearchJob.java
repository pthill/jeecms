package com.jeecms.dfcf.job;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.Content.ContentStatus;
import com.jeecms.cms.manager.main.ChannelMng;
import com.jeecms.cms.manager.main.ContentMng;
import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.entity.CmsUser;
import com.jeecms.core.manager.CmsSiteMng;
import com.jeecms.core.manager.CmsUserMng;
import com.jeecms.dfcf.model.AttachBean;
import com.jeecms.dfcf.model.AuthorBean;
import com.jeecms.dfcf.model.ResearchBean;

public class ResearchJob extends QuartzJobBean{

	private CmsUserMng cmsUserMng;
	private CmsSiteMng cmsSiteMng;
	private ContentMng contentMng;
	private ChannelMng manager;
	
	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		 try {  
			 
			 	SchedulerContext schCtx = context.getScheduler().getContext();  
	            //获取Spring中的上下文    
		        ApplicationContext appCtx = (ApplicationContext)schCtx.get("applicationContext"); 
		        this.cmsSiteMng = (CmsSiteMng) appCtx.getBean("cmsSiteMng");
		        this.cmsUserMng = (CmsUserMng) appCtx.getBean("cmsUserMng");
		        this.contentMng = (ContentMng) appCtx.getBean("contentMng");
		        this.manager = (ChannelMng) appCtx.getBean("channelMng");
		        getResearchJson();
		    } catch (JSONException | UnsupportedEncodingException | ParseException | SchedulerException e1) {  
		        e1.printStackTrace();  
		    }
	}
	
	public void getResearchJson() throws JSONException, ParseException, UnsupportedEncodingException {
		CmsUser user = cmsUserMng.findById(1);
		CmsSite site = cmsSiteMng.findById(1);
		String[] str = { "T004005001", "T004005002" };
		Map<String, List<ResearchBean>> map = new HashMap<String, List<ResearchBean>>();
		for (int i = 0; i < str.length; i++) {
			List<ResearchBean> researchBeans = new ArrayList<ResearchBean>();
			Integer channelId = 0;
			Channel channel = manager.findByPath(str[i], site.getId());
			if (channel!=null) {
				channelId = channel.getId();
			}
			List<Content> contents = (List<Content>) contentMng
					.getMaxReleaseDate(user.getId(), user.getId(),
							ContentStatus.all, contentMng.getCheckStep(),
							site.getId(), channelId, 1, 1).getList();
			String title = "";
			Long time = 0L;
			if (contents.size() > 0) {
				title = contents.get(0).getTitle();
				time = contents.get(0).getReleaseDate().getTime();
			}
			for (int j = 1; j < 10; j++) {
				String json = CollectJobUtil.loadJson("EMInfoCResearchList", str[i], j, 100,
						"datetime", "desc");
				if (json != "" && json != null) {
					for (ResearchBean rb : CollectJobUtil.getResearchBeanIds(json, "records")) {
						if (!title.equals(rb.getTitle())&& ((rb.getDate() != null && time != rb.getDate().getTime()) || rb.getDate() == null)) {
							String s = CollectJobUtil.loadJsonText("EMInfoContent", "C", "H3",rb.getId());
							s = s.substring(s.indexOf("{"), s.length());
							AttachBean attachBean = CollectJobUtil.getAttachBean(s, "attach");
							AuthorBean authorBean = CollectJobUtil.getAuthorBean(s,"authorList");
							ResearchBean researchBean = CollectJobUtil.getResearchBean(s,attachBean, authorBean);
							researchBeans.add(researchBean);
						} else {
							j = 100;
							break;
						}
					}
				}
			}
			map.put(String.valueOf(channelId), researchBeans);
		}
		contentMng.saveResearchs(map);
	}
	

}
