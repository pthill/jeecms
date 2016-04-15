package com.jeecms.dfcf.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import com.jeecms.cms.entity.main.Channel;
import com.jeecms.cms.entity.main.Content;
import com.jeecms.cms.entity.main.Content.ContentStatus;
import com.jeecms.dfcf.model.FundNewsBean;
public class FundNewsJob extends CollectJob {

	private final static String[] CHANNELS_PATH = {"S888009009", "S888013001", "S888013002", "S888013003", "S888013004", "S888013005", "S888013006", "S888013007", "S888013008", "S888013009"};
	
	@SuppressWarnings("unchecked")
	public void runJob() {
		final List<Channel> cList = channelMng.findByPathsAndSitedId(CHANNELS_PATH, siteId);
		final Map<String, List<FundNewsBean>> map = new HashMap<String, List<FundNewsBean>>();
		for (final Channel channel : cList) {
			final List<FundNewsBean> fnbList = new ArrayList<FundNewsBean>();

			List<Content> contents = (List<Content>) contentMng.getMaxReleaseDate(user.getId(), user.getId(), ContentStatus.all, contentMng.getCheckStep(), siteId, channel.getId(), 1, 1).getList();
			String title = "";
			Long time = 0L;
			if (contents.size() != 0) {
				title = contents.get(0).getTitle();
				time = contents.get(0).getReleaseDate().getTime();
			}
			for (int j = 1; j < PAGE; j++) {
				try {
					final String json = this.loadJson("EMInfoCNewsList", channel.getPath(), j, SIZE, "date", "desc");
					if (json != "" && json != null) {
						for (FundNewsBean fnb : this.getFundNewsBeanIds(json, "records")) {
							if (!title.equals(fnb.getTitle()) && ((fnb.getDate() != null && time != fnb.getDate().getTime()) || fnb.getDate() == null)) {
								String s = this.loadJsonText("EMInfoContent", "C", "H1", fnb.getId());
								if (s.contains("{") && s.indexOf("{") != -1) {
									s = s.substring(s.indexOf("{"), s.length());
									JSONObject jsonObject = new JSONObject(s);
									FundNewsBean fundNewsBean = this.getFundNewsBean(jsonObject);
									fnbList.add(fundNewsBean);
								} else {
									j = PAGE;
									break;
								}

							} else {
								j = PAGE;
								break;
							}
						}
					}
				} catch (Exception e) {
					j = PAGE;
					break;
				}
			}
			map.put(String.valueOf(channel.getId()), fnbList);
		}
		contentMng.saveFundNews(map);
	}

}
