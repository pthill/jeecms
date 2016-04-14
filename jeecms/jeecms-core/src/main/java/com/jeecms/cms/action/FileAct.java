package com.jeecms.cms.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.ServletContextAware;

import com.jeecms.core.entity.CmsSite;
import com.jeecms.core.web.util.CmsUtils;
@Controller
public class FileAct implements ServletContextAware{
	
	@RequestMapping(value = "content/attachment/{date}/{filename}.{ext}")
	public void getAttachment(@PathVariable(value = "date") String date, @PathVariable(value = "filename") String filename, @PathVariable(value = "ext") String ext, HttpServletRequest request, HttpServletResponse response) throws Exception {
		FileInputStream fis = null;
		if ("jpg".equals(ext) || "png".equals(ext) || "gif".equals(ext)) {
			response.setContentType("image/jpg");
		} else if ("doc".equals(ext) || "docx".equals(ext)) {
			response.setContentType("application/msword");
		} else if ("pdf".equals(ext)) {
			response.setContentType("application/pdf");
		} else if ("ppt".equals(ext)||"pptx".equals(ext)) {
			response.setContentType("application/x-ppt");
		} else if ("xls".equals(ext)||"xlsx".equals(ext)) {
			response.setContentType("application/x-xls");
		}
		try {
			CmsSite site = CmsUtils.getSite(request);
			OutputStream out = response.getOutputStream();
			File file = new File(getFilePath().getProperty("jeecms.workspace") + File.separator+getFilePath().getProperty("jeecms.article.attachment.path") + File.separator + date + File.separator + filename + "." + ext);
			fis = new FileInputStream(file);
			byte[] b = new byte[fis.available()];
			fis.read(b);
			out.write(b);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private ServletContext ctx;

	public void setServletContext(ServletContext servletContext) {
		this.ctx = servletContext;
	}
	
	private Properties getFilePath() {
		String dir = "";
		Properties propertie = new Properties();  
	     try {
	    	 String path = ctx.getRealPath("/")+"WEB-INF\\classes\\application.properties";
	    	 InputStream in = new BufferedInputStream(new FileInputStream(path));
	    	 propertie.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	     return propertie;
	}
	
}
