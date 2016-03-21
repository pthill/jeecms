package com.jeecms;

import java.net.URL;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.ser.BeanDeserializerFactory;
import org.apache.axis.encoding.ser.BeanSerializerFactory;

import com.jeecms.model.ArticleModel;

public class AxisClient {

	public static void main(String[] agrs) {

		Service service = new Service();
		String url = "http://localhost:8080/jeecms/services/ArticleService";
		String methodName = "findAllContent";

		try {
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new URL(url));
			call.setOperationName(new QName(url, methodName));
			QName qn = new QName("com.jeecms.model", "ArticleModel");
			call.registerTypeMapping(ArticleModel.class, qn, new BeanSerializerFactory(ArticleModel.class, qn), new BeanDeserializerFactory(ArticleModel.class, qn));

			//设置要传入参数,如果没有要传入的参数，则不要写这个  
			//			call.addParameter("name", XMLType.XSD_STRING, ParameterMode.IN);
			//设置返回的类型  
			call.setReturnType(qn, ArticleModel[].class);

			//执行，调用webservice  
			ArticleModel[] p = (ArticleModel[]) call.invoke(new Object[]{});
			for(final ArticleModel am:p){
				System.out.println(am.getTitle());
			}
			System.out.println(p);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
