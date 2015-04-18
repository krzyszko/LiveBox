package dom;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.log4j.*;

import java.util.*;

import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.impl.Log4JLogger;
import org.eclipse.jetty.util.log.Log;


public class LiveBox {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	private String login;
	private String password;
	private String url;
	private WebClient webClient;
	private int retries;
	public LiveBox(String login,String password,String url){
		this.login=login;
		this.password=password;
		this.url=url;
		logger.log(Level.DEBUG,this.login+ " "+Base64.encodeBase64String(this.password.getBytes())+" "+this.url);
		
	}
	public void setRetries(int retries){
		this.retries=retries;
	}
	public int getRetries(){
		return this.retries;
	}
	public void reboot(int retries){
		boolean status=false;
		int i=1;
		System.out.println(retries);
		while (i<=retries){
			logger.info(String.format("Reboot %d attempt",i++));
			status=this.reboot();
			if (status) break;
		}
		if (!status) logger.fatal(String.format("Could not restart device in %d retries",retries));
	}
	public boolean reboot(){
		boolean status=false;
		try {
			logger.log(Level.INFO,"Reboot process started");
			HtmlPage mainPage = this.login();
			HtmlAnchor supportLink = (HtmlAnchor) mainPage.getByXPath("id('hmenu-support')/a").get(0);
			HtmlPage supportPage=supportLink.click();
			HtmlAnchor restartLink = (HtmlAnchor) supportPage.getByXPath("id('left-restart-link')/a").get(0);
			System.out.println(restartLink.asXml());
			HtmlPage restartQuestion=restartLink.click();
			HtmlElement button1 = (HtmlElement) restartQuestion.getByXPath("id('content-center')/form/div[3]/div/input[2]").get(0);
			
			HtmlPage restartPage = button1.click();
			HtmlSpan span = (HtmlSpan) restartPage.getByXPath("id('content-center')/form/div[4]/p/span").get(0);
			if (span.getTextContent().contains("Trwa restart Liveboxa…")){
				logger.info("Device is beeing restarted");
				status=true;
			}
			else {
				logger.warn("Could not restart livebox");
				
			}
			return status;
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			logger.log(Level.ERROR,e.getMessage());
			return status;
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			logger.log(Level.ERROR,e.getMessage());
			return status;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.log(Level.ERROR,e.getMessage());
			return status;
		} catch (NullPointerException e){
			logger.log(Level.ERROR,e.getMessage());
			return status;
		}
		finally{
			this.Logout();
		}
	}
private HtmlPage login(){
	HtmlPage loggedPage=null;
	try{
		this.webClient=new WebClient(BrowserVersion.FIREFOX_24);
		//Login.webClient.waitForBackgroundJavaScript(1000);
		this.webClient.getOptions().setActiveXNative(true);
		this.webClient.getCookieManager().setCookiesEnabled(true);
		this.webClient.setAjaxController(new NicelyResynchronizingAjaxController());

		HtmlPage mainpage=this.webClient.getPage(this.url);
		logger.info("Connected to "+this.url);
		HtmlForm form = mainpage.getFormByName("Login");
		HtmlTextInput usernameInput = (HtmlTextInput) mainpage.getElementById("PopupUsername");
	    HtmlPasswordInput passwordInput = (HtmlPasswordInput) mainpage.getElementById("PopupPassword");
	    HtmlSubmitInput button = (HtmlSubmitInput) mainpage.getElementById("bt_authenticate");
		//Login.webClient.waitForBackgroundJavaScript(1000);
	
	    //usernameInput.setValueAttribute(this.user);
	    
	    passwordInput.setValueAttribute("admin");
	    loggedPage=button.click();
	    return loggedPage;
    }
	catch (IOException e){
		logger.log(Level.ERROR,e.getStackTrace());
		this.Logout();
		return null;
	}
}

private void Logout()
	{
		if (this.webClient!=null){
			this.webClient.closeAllWindows();
			logger.info("Successfully close browser");
		}
		else{
			logger.info("Browser already closed");
		}
		this.webClient=null;
	}
	

}
