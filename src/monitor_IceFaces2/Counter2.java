package monitor_IceFaces2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
//import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
//import java.net.UnknownHostException;




import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

//import jdk.internal.org.xml.sax.InputSource;




import org.icefaces.application.PortableRenderer;
import org.icefaces.application.PushRenderer;
//import org.icepush.client.HttpResponse;
//import org.icepush.client.HttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
//import org.apache.http.client.HttpClient;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.client.HttpClients;
//import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.methods.HttpGet;


@ManagedBean
@SessionScoped
public class Counter2{
	
	public FacesContext facesContext;
	public HttpSession session;
	public String m_sessionId;
	public PortableRenderer m_renderer;
	public int increment = 31;
	private long eTime;
	
	boolean node1 = false;
	String state = "RUN1";
	ping runPing = null; 
	
	//Prepare session communication	
	 	@PostConstruct
	public void postConstruct(){		
	 	System.out.println("here from postConstruct");	
	 	facesContext = FacesContext.getCurrentInstance();         
	 	session = (HttpSession)facesContext.getExternalContext().getSession(false);
	 	m_sessionId = session.getId();
	 	PushRenderer.addCurrentSession(m_sessionId);
	 	m_renderer = PushRenderer.getPortableRenderer();
	}
	 	
	public void refresh(){
		System.out.println("here from refresh");		
		m_renderer.render(m_sessionId);
	} 	
		
	public int getIncrement(){
        return increment;
    }	
	public String getState(){
        return state;
    }
	public long getTime(){
        return eTime;
    }
	
	public void start(String url, String node){								
		if(!node1){
			node1 = true;
			int interval = 5;
			Runnable runPing = new pingOLD(url, 200, 80, node, interval);					
			this.state = "STOP";
			new Thread(runPing).start();

			System.out.println("started " + node1);
		}else{
			this.node1 = false;
			this.state = "RUN";
					 
			System.out.println("stopped");
			}
	}		
	
////PING CLASS	 
	 public class pingOLD implements Runnable{
		 String node, url;		
		 int timeout, port;		
		 
		 public pingOLD(String url, int timeout, int port, String node, int interval){
			 this.url = url;
			 this.timeout = timeout;
			 this.port = port;
			 this.node = node;
		}
		 
		 int httpURL_code;
//METHOD HTTPURLCONNECTION 1		 
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
					httpURL_code = connection.getResponseCode();
					temp.close();
					connection.disconnect();
				} catch (IOException e) {					
					elapsedTime = 5;
					System.out.println("testBy_HttpURLConnection : fail");
				}			 
			 return elapsedTime;
		 }
		 
		 String inetSCKaddrr_code;
//METHOD INETSOCKETADDRESS 2		 		 
		 public long testBy_InetSocketAddress(String url, int timeout){
			 long elapsedTime = 0;
//			 String response;
			 String hostname = url.contains("http://") ? url.replaceAll("http://","") : url;
			 
			 try {
				 Socket soc = new Socket();
			     long starTime = System.currentTimeMillis();
			     soc.connect(new InetSocketAddress(hostname, 80), 5000);
			     elapsedTime = System.currentTimeMillis() - starTime;
//			        	
//			     BufferedReader in = new BufferedReader(new InputStreamReader(soc.getInputStream()));
//			     while(true) {
//               	if (in.readLine() != null || in.readLine() != "" || in.readLine() != "null") {
//               		inetSCKaddrr_code = in.readLine();  
//               		System.out.println("output : " + in.readLine());
//               	}else{
//               		break;
//               	}
//			     }
			       soc.close();			        
			    } catch (IOException ex) {
			    	elapsedTime = 5;
			    	System.out.println("testBy_InetSocketAddress : fail");
			    }			 
			 return elapsedTime;
		 }
		 
//METHOD SOCKET 3			 
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
		 int httpCl_code;
		 
//METHOD HTTPCLIENT 4			 
		 public long testBy_HttpClient(String url, int timeout){
			 url = url.replaceFirst("^https", "http"); 	
			 url = url.contains("http://") ? url : "http://" + url;
			 long elapsedTime = 0;
			 long starTime = 0;
				 	
			 HttpClient client = new DefaultHttpClient();						 
				 	
				 	HttpGet request = new HttpGet(url);
				 	try {
				 		starTime = System.currentTimeMillis();
						HttpResponse response = client.execute(request);
						httpCl_code = response.getStatusLine().getStatusCode();
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
		 	     		
			 long time = 0;
			 long initialTest_method1 = testBy_HttpURLConnection(url, 2000);
			 long initialTest_method2 = testBy_InetSocketAddress(url, 2000);	
			 long initialTest_method3 = testBy_Socket(url, 2000);
			 long initialTest_method4 = testBy_HttpClient(url, 2000);
			 
		    while(node1){			    	
		    	if(initialTest_method1 !=5 ){
		    		time = testBy_HttpURLConnection(url, 2000);
		    		System.out.println("testBy_HttpURLConnection response time : " + time + "ms and code : " + httpURL_code);
		    	}else if(initialTest_method2 !=5 ){
		    		time = testBy_InetSocketAddress(url, 2000);   
		    		System.out.println("testBy_InetSocketAddress response time : " + time + "ms and code : " + inetSCKaddrr_code);
		    	}else if(initialTest_method3 !=5 ){
		    		time = testBy_InetSocketAddress(url, 2000);   
		    		System.out.println("testBy_Socket response time : " + time);
		    	}else if(initialTest_method4 != 5){
		    		time = testBy_HttpClient(url, 2000);   
		    		System.out.println("testBy_HttpClient response time : " + time + "ms and code : " + httpCl_code);
		    	}else{
		    		System.out.println("ALL METHODS FAILED TO CONNECT");
		    	}
//		    	System.out.println("tested URL : " + url);
					try{
						Thread.sleep(1000);
						}catch(InterruptedException e){							
							e.printStackTrace();
						}
						if(increment==1){
							increment=20;					
							break;
						}
					increment--;
					m_renderer.render(m_sessionId);
					eTime = time;
			 }		
		 }		 
	 }	
}
