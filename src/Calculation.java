



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Calculation {
	String loginRequestString = //登录请求链接
			"http://202.118.31.197/ACTIONLOGON.APPPROCESS?mode=&WebUserNO=20124730&Password=123456&Agnomen=z33j";
	String queryRequstString =  //成绩查询链接
			"http://202.118.31.197/ACTIONQUERYSTUDENTSCORE.APPPROCESS";
	String verifyCodeRequestString = //验证码请求链接
			"http://202.118.31.197/ACTIONVALIDATERANDOMPICTURE.APPPROCESS";
	String queryScoreString = 
			"http://202.118.31.197/ACTIONQUERYSTUDENTSCORE.APPPROCESS?YearTermNO=3";
	
	public static void main (String[] args) {
		CookieStore cookieStore = new BasicCookieStore();  
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "Vu21aXIxK1woH8GlyK2RCb64YBGn0bmV3ogvxDfaFv7ZfcsdiF1L!-255119862");  
		cookie.setVersion(0);  
		cookie.setDomain("202.118.31.197");  
		cookie.setPath("/");  
		cookieStore.addCookie(cookie);  
		CloseableHttpClient client = HttpClients.custom()  
		        .setDefaultCookieStore(cookieStore)  
		        .build();  
		 HttpHost aaoHost = new HttpHost("202.118.31.197");
		 HttpPost loginReq = new HttpPost(
	                "http://202.118.31.197/ACTIONLOGON.APPPROCESS?mode=4");
		 

         UrlEncodedFormEntity entity;
		try {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	         formparams.add(new BasicNameValuePair("WebUserNO", "20124743"));
	         formparams
	                 .add(new BasicNameValuePair("Password", "632601"));
	         formparams.add(new BasicNameValuePair("Agnomen", "7kGR"));
	         formparams.add(new BasicNameValuePair("submit.x", "43"));
	         formparams.add(new BasicNameValuePair("submit.y", "7"));
			entity = new UrlEncodedFormEntity(formparams,
			         "UTF-8");
			loginReq.setEntity(entity);
//			System.out.println(loginReq.getEntity());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
//			String result = null;
			CloseableHttpResponse  response = client.execute(aaoHost, loginReq);

	        StringBuilder builder = new StringBuilder();
	        BufferedReader br;
			br = new BufferedReader(new InputStreamReader(
			        response.getEntity().getContent(),"GBK"));
			 String line;
		        while ((line = br.readLine()) != null) {
		            builder.append(line);
		        }
		    
	        System.out.println(builder.toString());
	        response.close();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
         
		 
		  HttpGet queryReq = new HttpGet
				  ("http://202.118.31.197/ACTIONQUERYSTUDENTSCORE.APPPROCESS");
		  try {
			CloseableHttpResponse  response = client.execute(aaoHost, queryReq);
			StringBuilder builder = new StringBuilder();
	        BufferedReader br;
			br = new BufferedReader(new InputStreamReader(
			        response.getEntity().getContent(),"GBK"));
			String line;
	        while ((line = br.readLine()) != null) {
	            builder.append(line);
	        }
	        String result = builder.toString();
		    Document doc = Jsoup.parse(result);
		    System.out.println(doc.title());
		    Elements trs = doc.getElementsByClass("color-rowNext");
		    Elements trs2 = doc.getElementsByClass("color-row");
		    
		    ArrayList<Course> courses = new ArrayList<Course>();
		    for(Element tr:trs) {
		    	Elements tds = tr.getElementsByTag("td");
		    	Course course = new Course();
		    	for(int i = 0;i < tds.size();i++) {
		    		Element td = tds.get(i);
		    		String s = td.text();
		    		if(s.equals(" ")) {
		    			break;
		    		}else {
		    			switch(i){
		    			case 0:
		    				course.setProperty(s);
		    				break;
		    			case 1:
		    				course.setNumber(s);
		    				break;
		    			case 2:
		    				course.setName(s);
		    				break;
		    			case 3:
		    				course.setType(s);
		    				break;
		    			case 4:
		    				course.setTime(s);
		    				break;
		    			case 5:
		    				course.setCredit(s);
		    				break;
		    			case 6:
		    				course.setScoreType(s);
		    				break;
		    			case 7:
		    				if(s.equals("优")) course.setScore(95);
		    				else if(s.equals("良")) course.setScore(85);
		    				else if(s.equals("中")) course.setScore(75);
		    				else if(s.equals("及格")) course.setScore(65);
		    				else 
		    				try {
		    				 course.setScore(Integer.parseInt(s));
		    				}catch(NumberFormatException e) {
		    					course.setScore(0);
		    				}
		    				break;
		    			default:
		    				System.out.println(course.toString());
		    			}
		    			System.out.println(s);
		    			
		    		}
		    	}
		    	courses.add(course);
		    }
		    for(Element tr:trs2) {
		    	Elements tds = tr.getElementsByTag("td");
		    	Course course = new Course();
		    	for(int i = 0;i < tds.size();i++) {
		    		Element td = tds.get(i);
		    		String s = td.text();
		    		if(s.equals(" ")) {
		    			continue;
		    		}else {
		    			switch(i){
		    			case 0:
		    				course.setProperty(s);
		    				break;
		    			case 1:
		    				course.setNumber(s);
		    				break;
		    			case 2:
		    				course.setName(s);
		    				break;
		    			case 3:
		    				course.setType(s);
		    				break;
		    			case 4:
		    				course.setTime(s);
		    				break;
		    			case 5:
		    				course.setCredit(s);
		    				break;
		    			case 6:
		    				course.setScoreType(s);
		    				break;
		    			case 7:
		    				if(s.equals("优")) course.setScore(95);
		    				else if(s.equals("良")) course.setScore(85);
		    				else if(s.equals("中")) course.setScore(75);
		    				else if(s.equals("及格")) course.setScore(65);
		    				else 
		    				try {
		    				 course.setScore(Integer.parseInt(s));
		    				}catch(NumberFormatException e) {
		    					course.setScore(0);
		    				}
		    				break;
		    			default:
		    				System.out.println(course.toString());
		    			}
		    			System.out.println(s);
		    			
		    		}
		    	}
		    	courses.add(course);
		    }
		    
	        System.out.println(courses.size());
	        response.close();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  
	}
	/*
	 * 将每个条目存储为course
	 */
	
 
  
  
}
