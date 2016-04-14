package com.jeecms.dfcf.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
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
import com.jeecms.dfcf.model.FundNewsBean;

public class FundNewsJob extends QuartzJobBean{

	private CmsUserMng cmsUserMng;
	private CmsSiteMng cmsSiteMng;
	private ContentMng contentMng;
	private ChannelMng manager;
	
	private final static String USER_NAME = "system";
	private static final Integer PATH = 10;
	
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
		        getfundNewsJson();
		    } catch (JSONException  | SchedulerException e1) {  
		        e1.printStackTrace();  
		    }
		
	}
	
	public void getfundNewsJson() {
		CmsSite site = cmsSiteMng.findById(1);
		CmsUser user = cmsUserMng.findByUsername(USER_NAME);
		String[] str = { "S888009009", "S888013001", "S888013002","S888013003","S888013004","S888013005","S888013006","S888013007","S888013008","S888013009"};
		Map<String, List<FundNewsBean>> map = new HashMap<String, List<FundNewsBean>>();
		for (int i = 0; i < str.length; i++) {
			List<FundNewsBean> fnbList = new ArrayList<FundNewsBean>();
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
			if (contents.size() != 0) {
				title = contents.get(0).getTitle();
				time = contents.get(0).getReleaseDate().getTime();
			}
			for (int j = 1; j < PATH; j++) {
				String json = CollectJobUtil.loadJson("EMInfoCNewsList", str[i], j, 100,"date", "desc");
				if (json != "" && json != null) {
					for (FundNewsBean fnb : CollectJobUtil.getFundNewsBeanIds(json, "records")) {
						if (!title.equals(fnb.getTitle())&& ((fnb.getDate() != null&& time != fnb.getDate().getTime())||fnb.getDate()==null)) {
							String s = CollectJobUtil.loadJsonText("EMInfoContent", "C", "H1",fnb.getId());
							if (s.contains("{")&&s.indexOf("{")!=-1) {
								s = s.substring(s.indexOf("{"), s.length());
								JSONObject jsonObject = new JSONObject(s);
								FundNewsBean fundNewsBean = CollectJobUtil.getFundNewsBean(jsonObject);
								fnbList.add(fundNewsBean);
							}else{
								j+=PATH;
								break;
							}
						} else {
							j=100;
							break;
						}

					}
				}
			}
			map.put(String.valueOf(channelId), fnbList);
		}

		contentMng.saveFundNews(map);
	}

}
