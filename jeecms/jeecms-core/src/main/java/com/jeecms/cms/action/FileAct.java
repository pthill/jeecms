package com.jeecms.cms.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
@Controller
public class FileAct{
	
	@Value(value="${com.fcn.phoenix.workspace}")
	private String workspace;
	
	@Value(value="${jeecms.article.content.attachment.path}")
	private String articleContentAttachmentPath;
	
	@RequestMapping(value = "content/attachment/{date}/{filename}.{ext}",method=RequestMethod.GET)
	public void getAttachment(@PathVariable(value = "date") String date, @PathVariable(value = "filename") String filename, @PathVariable(value = "ext") String ext, HttpServletRequest request, HttpServletResponse response) throws Exception {
		FileInputStream fis = null;
		if ("jpg".equals(ext) || "png".equals(ext) || "gif".equals(ext)) {
			response.setContentType("image/jpg");
		} else if ("doc".equals(ext) || "docx".equals(ext)) {
			response.setContentType("application/msword");
		} else if ("pdf".equals(ext)) {
			response.setContentType("application/pdf");
		} else if ("ppt".equals(ext) || "pptx".equals(ext)) {
			response.setContentType("application/x-ppt");
		} else if ("xls".equals(ext) || "xlsx".equals(ext)) {
			response.setContentType("application/x-xls");
		}
		try {
			OutputStream out = response.getOutputStream();
			File file = new File(workspace + File.separator + articleContentAttachmentPath + File.separator + date + File.separator + filename + "." + ext);
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
	
}
