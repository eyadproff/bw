package sa.gov.nic.bio.bw.client.login.workflow;

import java.util.Objects;

public class WebServiceResponse<T> extends ServiceResponse<T>
{
	private String url;
	private int httpCode;
	
	public WebServiceResponse(boolean success, String errorCode, Exception exception, T result, String url, int httpCode)
	{
		super(success, errorCode, exception, result);
		
		this.url = url;
		this.httpCode = httpCode;
	}
	
	public static <T> WebServiceResponse<T> success(T result, String url, int httpCode)
	{
		return new WebServiceResponse<>(true, null, null, result, url, httpCode);
	}
	
	public static <T> WebServiceResponse<T> failure(String errorCode, Exception exception, String url, int httpCode)
	{
		return new WebServiceResponse<>(false, errorCode, exception, null, url, httpCode);
	}
	
	public static <T> WebServiceResponse<T> failure(String errorCode, String url, int httpCode)
	{
		return new WebServiceResponse<>(false, errorCode, null, null, url, httpCode);
	}
	
	public static <T> WebServiceResponse<T> failure(String errorCode, Exception exception)
	{
		return new WebServiceResponse<>(false, errorCode, exception, null, null, 0);
	}
	
	public static <T> WebServiceResponse<T> failure(String errorCode)
	{
		return new WebServiceResponse<>(false, errorCode, null, null, null, 0);
	}
	
	public String getUrl(){return url;}
	public void setUrl(String url){this.url = url;}
	
	public int getHttpCode(){return httpCode;}
	public void setHttpCode(int httpCode){this.httpCode = httpCode;}
	
	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		if(!super.equals(o)) return false;
		WebServiceResponse<?> that = (WebServiceResponse<?>) o;
		return httpCode == that.httpCode && Objects.equals(url, that.url);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), url, httpCode);
	}
	
	@Override
	public String toString()
	{
		return "WebServiceResponse{" + "url='" + url + '\'' + ", httpCode=" + httpCode + '}';
	}
}