package cn.hollo.www.upyun;

import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * upyun的基础服务类
 * @author orson
 *
 */
public abstract class UpYunBaseClass {
	protected static String GET   = "GET";
	protected static String POST  = "POST";
	protected static String PUT   = "PUT";
	protected static String DELETE  = "DELETE";
	protected static String http = "http://";					//请求协议
	protected static String upYun_userName = "hollogo";		    //又拍云用户空间名称
	protected static String upYun_password = "2wsx3edc";		//又拍云用户空间的密码
	protected String host = "v0.api.upyun.com";					//请求域名
	protected String upYun_bucket; //又拍云空间名称
	protected String uriName;	   //请求的资源名称
	protected static HttpParams httpParams;
	
	protected UpYunBaseClass(){
		if (httpParams == null)
			httpParams = getHttpParams();
	}
	
	/**
     * @return
     */
    protected String getCurrentTimeStamp(){
    	SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		TimeZone gmtTime 		= TimeZone.getTimeZone("GMT");
		format.setTimeZone(gmtTime);			
		String timeStamp = format.format(new Date());
		
		return timeStamp;
    }
    
    /**
     * 获取请求对象
     * @param length	： 数据的长度
     * @return
     */
    protected HttpUriRequest getHttpUriRequest(String url, String methodType, long length){
    	HttpUriRequest request = null;
    	
    	if (PUT.equals(methodType))
    		request = new HttpPut(url);
    	else if (GET.equals(methodType))
    		request = new HttpGet(url);
    	else if (POST.equals(methodType))
    		request = new HttpPost(url);
    	else if (DELETE.equals(methodType))
    		request = new HttpDelete(url);
    	
    	//当前的时间戳
		String date = getCurrentTimeStamp();
		
		//请求的uri地址
		String uri = upYun_bucket + uriName;
		
		//签名字符串
		String sign = sign(methodType, uri, length, date);
		
		request.setHeader("Method", methodType);
		request.setHeader("Expect", "");
		request.setHeader("Host", host);
		request.setHeader("Authorization", sign);
		request.setHeader("Date", date);
		request.setHeader("Mkdir", "true");
		
		return request;
    }
    
    /**
	 * 获取http请求参数
	 * @return 
	 * @throws Exception
	 */
	public HttpParams getHttpParams(){
        // 创建 HttpParams 以用来设置 HTTP 参数（这一部分不是必需的）  
        httpParams = new BasicHttpParams();
        // 设置连接超时和 Socket 超时，以及 Socket 缓存大小  
        HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);
        HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
        // 设置重定向，缺省为 true  
        HttpClientParams.setRedirecting(httpParams, true);
        // 设置 user agent  
        //String userAgent = "Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2) Gecko/20100115 Firefox/3.6"; 
       // PinChe/1.3.1 (iPad; iOS 7.0.2; Scale/2.00)
        //String userAgent =  GeneralUtil.getUserAgent();
        //HttpProtocolParams.setUserAgent(httpParams, "/pinche");
        // 创建一个 HttpClient 实例  
        // 注意 HttpClient httpClient = new HttpClient(); 是Commons HttpClient  
        // 中的用法，在 Android 1.5 中我们需要使用 Apache 的缺省实现 DefaultHttpClient  
        //httpClient = new DefaultHttpClient(httpParams);  
        //return httpClient;  
        return httpParams;
    }

    /**
	 * 
	 * @param method
	 * @param uri
	 * @param length
	 * @return
	 */
    protected String sign(String method, String uri, long length, String date){
		String sign = method + "&" + uri + "&"
				+ date + "&" + length + "&" + md5(upYun_password);

		return "UpYun " + upYun_userName + ":" + md5(sign);
	}
	
    /**
     * md5加密
     * @param str
     * @return
     */
	private static String md5(String str) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		byte[] encodedValue = md5.digest();
		int j = encodedValue.length;
		char finalValue[] = new char[j * 2];
		int k = 0;
		for (int i = 0; i < j; i++) {
			byte encoded = encodedValue[i];
			finalValue[k++] = hexDigits[encoded >> 4 & 0xf];
			finalValue[k++] = hexDigits[encoded & 0xf];
		}

		return new String(finalValue);
	}

	/**
	 * 执行请求操作
	 */
	public void excuteRequest(){

	}
}
