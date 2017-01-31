package monitor_IceFaces2;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

//import javax.annotation.PostConstruct;
//import javax.faces.bean.ManagedBean;
//import javax.faces.bean.SessionScoped;
//import javax.faces.context.FacesContext;
//import javax.servlet.http.HttpSession;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.icefaces.application.PortableRenderer;
//import org.icefaces.application.PushRenderer;

//PING CLASS	 
	 public class ping implements Runnable{
		 String node, url;		
		 int timeout, port;		
		 private int increment = 5;
		 private String m_sessionId;
		 //private PortableRenderer m_renderer;
		 boolean node1 = true;
		 
		 Counter2 obj = new Counter2();
		 		 
		 public ping(String url, int timeout, int port, String node, int interval){
			 this.url = url;
			 this.timeout = timeout;
			 this.port = port;
			 this.node = node;
			 
			 		 }
//METHOD HTTPURLCONNECTION		 
		 public long testBy_HttpURLConnection(String url, int timeout){
			 url = url.replaceFirst("^https", "http"); 	
			 url = url.contains("http://") ? url : "http://" + url;
			 HttpURLConnection connection = null;
			 long elapsedTime = 0;
			 
			 try{
		        connection = (HttpURLConnection) new URL(url).openConnection();
		        connection.setConnectTimeout(timeout);
		        connection.setReadTimeout(timeout);
		        connection.setRequestMethod("HEAD");		 
			    }catch(IOException exception){		
		   		    						 System.out.println("connection could not be established !!!");
			    							 }
			 	
				try {
					long starTime = System.currentTimeMillis();	
					InputStream temp = connection.getInputStream();
					elapsedTime = System.currentTimeMillis() - starTime;
					temp.close();
					connection.disconnect();
				} catch (IOException e) {					
					elapsedTime = 5;
					System.out.println("testBy_HttpURLConnection : fail");
				}			 
			 return elapsedTime;
		 }
		 
//METHOD INETSOCKETADDRESS		 		 
		 public long testBy_InetSocketAddress(String url, int timeout){
			 long elapsedTime = 0;			 
			 String hostname = url.contains("http://") ? url.replaceAll("http://","") : url;
			 
			 try {
			        Socket soc = new Socket();
			        long starTime = System.currentTimeMillis();
			        	soc.connect(new InetSocketAddress(hostname, 80), 5000);
			        	elapsedTime = System.currentTimeMillis() - starTime;
			            soc.close();			        
			    } catch (IOException ex) {
			    	elapsedTime = 5;
			    	System.out.println("testBy_InetSocketAddress : fail");
			    }			 
			 return elapsedTime;
		 }
		 
//METHOD SOCKET			 
		 public long testBy_Socket(String url, int timeout){
			 long elapsedTime = 0;			 
			 String hostname = url.contains("http://") ? url.replaceAll("http://","") : url;
			 
			 try {
				 	long starTime = System.currentTimeMillis();
			        Socket soc = new Socket(hostname, 80);		        		        	
			        elapsedTime = System.currentTimeMillis() - starTime;
			            soc.close();			        
			    } catch (IOException ex) {
			    	elapsedTime = 5;
			    	System.out.println("testBy_Socket : fail");
			    }			 
			 return elapsedTime;
		 }		 
		 
//METHOD HTTPCLIENT			 
		 public long testBy_HttpClient(String url, int timeout){
			 long elapsedTime = 0;
			 long starTime = 0;
				 	
			 HttpClient client = new DefaultHttpClient();						 
				 	
				 	HttpGet request = new HttpGet(url);
				 	try {
				 		starTime = System.currentTimeMillis();
						HttpResponse response = client.execute(request);
						elapsedTime = System.currentTimeMillis() - starTime;
					} catch (ClientProtocolException e) {
						elapsedTime = 5;
				    	System.out.println("testBy_HttpClient : fail");
						e.printStackTrace();
					} catch (IOException e) {
						elapsedTime = 5;
				    	System.out.println("testBy_HttpClient : fail");
						e.printStackTrace();
					}				 	 						 		                  	    	
			 return elapsedTime;
		 }	
		 
		 
		 public void run(){			 
		 	     		
		 System.out.println("received URL : " + url);
			 long time = 0;
			 long initialTest_method1 = testBy_HttpURLConnection(url, 2000);
			 long initialTest_method2 = testBy_InetSocketAddress(url, 2000);	
			 long initialTest_method3 = testBy_Socket(url, 2000);
			 long initialTest_method4 = testBy_HttpClient(url, 2000);
			 
		    while(node1){	   	
		    	
System.out.println("here in while");		    	
		    	if(initialTest_method1 !=5 ){
		    			   time = testBy_HttpURLConnection(url, 2000);
		    			   System.out.println("testBy_HttpURLConnection response time : " + time);
		    			   }else if(initialTest_method2 !=5 ){
		    				    time = testBy_InetSocketAddress(url, 2000);   
		    				    System.out.println("testBy_InetSocketAddress response time : " + time);
		    			   }else if(initialTest_method3 !=5 ){
		    				    time = testBy_InetSocketAddress(url, 2000);   
		    				    System.out.println("testBy_Socket response time : " + time);
		    			   }else if(initialTest_method4 != 5){
		    				   	time = testBy_HttpClient(url, 2000);   
		    				   	System.out.println("testBy_HttpClient response time : " + time);		    				   	
		    			   }else{
		    				   System.out.println("ALL METHODS FAILED TO CONNECT");
		    			   }
		    	
						 try{
							Thread.sleep(1000);
							}catch(InterruptedException e){							
							e.printStackTrace();
							}
						if(obj.increment==1)
							{
							obj.increment=5;					
							break;
							}
						obj.increment--;
						obj.refresh();
				//		obj.m_renderer.render(obj.m_sessionId);
			 			}		
		 }
	
		 
	     }	 