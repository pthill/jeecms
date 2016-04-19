package com.jeecms.dfcf.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.Content.ContentStatus;
import com.jeecms.dfcf.model.AttachBean;
import com.jeecms.dfcf.model.AuthorBean;
import com.jeecms.dfcf.model.ResearchBean;

public class ResearchJob extends CollectJob {

	private static final String[] CHANNELS_PATH = {"T004005001", "T004005002"};
	private static final Logger logger = LoggerFactory.getLogger(ResearchJob.class);
	@SuppressWarnings({"unchecked"})
	public void runJob() {
		final List<Channel> cList = channelMng.findByPathsAndSitedId(CHANNELS_PATH, siteId);

		final Map<String, List<ResearchBean>> map = new HashMap<String, List<ResearchBean>>();
		for (final Channel channel : cList) {
			List<ResearchBean> researchBeans = new ArrayList<ResearchBean>();
			List<Content> contents = (List<Content>) contentMng.getMaxReleaseDate(user.getId(), user.getId(), ContentStatus.all, contentMng.getCheckStep(), siteId, channel.getId(), 1, 1).getList();
			String title = "";
			Long time = 0L;
			if (contents.size() > 0) {
				title = contents.get(0).getTitle();
				time = contents.get(0).getReleaseDate().getTime();
			}
			for (int j = 1; j < PAGE; j++) {
				try {
					String json = this.loadJson("EMInfoCResearchList", channel.getPath(), j, SIZE, "datetime", "desc");
					if (json != "" && json != null&&json.contains("{") && json.indexOf("{") != -1) {
						for (ResearchBean rb : this.getResearchBeanIds(json, "records")) {
							if (!title.equals(rb.getTitle()) && ((rb.getDate() != null && time != rb.getDate().getTime()) || rb.getDate() == null)) {
								String s = this.loadJsonText("EMInfoContent", "C", "H3", rb.getId());
								if (s.contains("{") && s.indexOf("{") != -1) {
									s = s.substring(s.indexOf("{"), s.length());
									AttachBean attachBean = this.getAttachBean(s, "attach");
									AuthorBean authorBean = this.getAuthorBean(s, "authorList");
									ResearchBean researchBean = this.getResearchBean(s, attachBean, authorBean);
									researchBeans.add(researchBean);
								} else {
									j = PAGE;
									if(logger.isWarnEnabled()){
										logger.warn("EMInfoContent:"+rb.getId()+"\t"+s);
									}
									break;
								}

							} else {
								j = PAGE;
								break;
							}
						}
					}else{
						j = PAGE;
						if(logger.isWarnEnabled()){
							logger.warn("EMInfoCResearchList:"+ channel.getPath()+"\t"+json);
						}
						break;
					}
				} catch (Exception e) {
					j = PAGE;
					break;
				}
			}
			map.put(String.valueOf(channel.getId()), researchBeans);
		}
		contentMng.saveResearchs(map);
	}

}
