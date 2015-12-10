import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
public class SendMessages {
	  // Find your Account Sid and Token at twilio.com/user/account
	  public static final String ACCOUNT_SID = "AC0a23952dc53df0fde6667b272439b41b";
	  public static final String AUTH_TOKEN = "2f2e7aa877cc3e5969cd859cdc1fbfc2";
	 
	  public static void PrintMessage(String metricName,String instance) throws TwilioRestException {
	    TwilioRestClient client = new TwilioRestClient(ACCOUNT_SID, AUTH_TOKEN);
	 
	    // Build a filter for the MessageList
	    List<NameValuePair> params = new ArrayList<NameValuePair>();
	    String messag = "Max Alert!Check your instance ";
	           messag += instance;
	           messag += " .The metric ";
	    	   messag += metricName;
	    	   messag += "reached its maximum value";
	    params.add(new BasicNameValuePair("Body", messag));
	    params.add(new BasicNameValuePair("To", "+14088096461"));
	    params.add(new BasicNameValuePair("From", "+12057915894"));
	 
	    MessageFactory messageFactory = client.getAccount().getMessageFactory();
	    Message message = messageFactory.create(params);
	    System.out.println(message.getSid());
	  }
}
