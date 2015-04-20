package yunior.core;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
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

import yunior.model.Course;

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
		/*
		 * 构建client开始
		 */
		CookieStore cookieStore = new BasicCookieStore();  
		BasicClientCookie cookie = new BasicClientCookie("JSESSIONID", "V0QE9hlyqbqUOHiCcIWkYAeGS4rmSyJXfO45nfGzH5I23WmrjnZW!-255119862");  

		String stuNum = "20124743";
		String password = "632601";
//		String stuNum = "20124746";
//		String password = "z32c66y08";
		cookie.setVersion(0);  
		cookie.setDomain("202.118.31.197");  
		cookie.setPath("/");  
		cookieStore.addCookie(cookie);  
		CloseableHttpClient client = HttpClients.custom()  
		        .setDefaultCookieStore(cookieStore)  
		        .build();  
		/*
		 * 构建client结束
		 */
		
		 ArrayList<Course> courses = new ArrayList<Course>();//课程数组
		 HttpHost aaoHost = new HttpHost("202.118.31.197");
		 HttpPost loginReq = new HttpPost(
	                "http://202.118.31.197/ACTIONLOGON.APPPROCESS?mode=4");
		 

         UrlEncodedFormEntity entity;
		try {
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("WebUserNO", stuNum));
	         formparams
	                 .add(new BasicNameValuePair("Password", password));
//	         formparams.add(new BasicNameValuePair("WebUserNO", "20124746"));
//	         formparams
//	                 .add(new BasicNameValuePair("Password", "z32c66y08"));
	         formparams.add(new BasicNameValuePair("Agnomen", "CqZc"));
			 entity = new UrlEncodedFormEntity(formparams,
			         "UTF-8");
			loginReq.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
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
       
         /*
          * 判断学期模块开始
          * 根据学号判断应查询哪几个学期
          */
		 
		 String gradeString = (String) stuNum.subSequence(0,4);
		 int grade = Integer.parseInt(gradeString);
		 System.out.println(grade);
		 int turn = (2015-grade)*2;
		 /*
		  * 判断学期模块结束
		  */
		 
		 
		 
		 /*
		  * 分数获取模块开始
		  */
		 for(int  term = 0; term < turn;term++) {
			 HttpGet queryReq = new HttpGet
					  ("http://202.118.31.197/ACTIONQUERYSTUDENTSCORE.APPPROCESS?YearTermNO="+(14-term));
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
			    Elements trs = doc.getElementsByClass("color-rowNext");//奇数行
			    Elements trs2 = doc.getElementsByClass("color-row");//偶数行
			    
			    /*
				 * 奇数行分数获取模块开始
				 */
			    for(Element tr:trs) {
			    	Elements tds = tr.getElementsByTag("td");
			    	int j = 0;//为空标识符
			    	Course course = new Course();
			    	for(int i = 0;i < tds.size();i++) {
			    		
			    		Element td = tds.get(i);
			    		String s = td.text();
			    		if(s.equals(" ")) {
			    			j = 1;//若为空，将此条目标为1;
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
			    				course.setCredit(Float.parseFloat(s));
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
//			    			System.out.println(s);
			    			
			    		}
			    	}
			    	if(j == 0) {
			    		courses.add(course);
			    	}
			    }
			    /*
				 * 奇数行分数获取模块结束
				 */
			    
			    
			    /*
				 * 偶数行分数获取模块开始
				 */
			    for(Element tr:trs2) {
			    	Elements tds = tr.getElementsByTag("td");
			    	Course course = new Course();
			    	int j = 0;//为空标识符
			    	for(int i = 0;i < tds.size();i++) {
			    		Element td = tds.get(i);
			    		String s = td.text();
			    		if(s.equals(" ")) {
			    			j = 1;
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
			    				course.setCredit(Float.parseFloat(s));
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
//			    			System.out.println(s);
			    			
			    		}
			    	}
			    	if(j == 0) {
			    		courses.add(course);
			    	}
			    	
			    }
			    /*
				 * 偶数行分数获取模块结束
				 */
			    
		        
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
		  * 分数获取模块结束
		  */
		 System.out.println(courses.size());
		 Calculation c = new Calculation();
		 c.gpaCalc(courses);
	}
	
	/*
	 * 标准GPA计算模块
	 */
	public void gpaCalc(ArrayList<Course> courses) {
		float credits = 0,gpa = 0;
		Iterator<Course> it = courses.iterator();
		while(it.hasNext()) {
			Course course = it.next();
			int score = course.getScore();
			float credit = course.getCredit();
			credits += credit;
			System.out.println(score);
			if(score >= 90) {
				gpa += 4.0*credit;
//				System.out.println(4.0);
			}else if(score >=80) {
				gpa += 3.0*credit;
//				System.out.println(3.0);
			}else if(score >= 70) {
				gpa += 2.0*credit;
//				System.out.println(2.0);
			}else if(score >= 60) {
				gpa += 1.0*credit;
//				System.out.println(1.0);
			}
		}
		float avgGpa = gpa/credits;
		System.out.println("绩点为："+avgGpa);
	}
 
  
  
}
