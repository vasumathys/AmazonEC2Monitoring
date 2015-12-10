import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Properties;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import javax.mail.Session;

import org.joda.time.DateTime;

import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.ec2.model.Instance;
import com.twilio.sdk.TwilioRestException;


/**
 * @author Bhavana Bhasker	
 *
 */
public class MetricCalculator {

	MetricCalculator(Instance _instance)
	{
		instance = _instance;
	}
	
	void Display(String metricName) 
	{
		if(result != null)
		{
			for(Datapoint dataPoint:result.getDatapoints())
			{
				 System.out.printf("%s instance's Time %s : %s%n", metricName, instance.getInstanceId(), dataPoint.getTimestamp());
				 System.out.printf("%s instance's average %s : %s%n", metricName, instance.getInstanceId(), dataPoint.getAverage());      
		         System.out.printf("%s instance's max %s : %s%n", metricName, instance.getInstanceId(), dataPoint.getMaximum());
		            Date d = dataPoint.getTimestamp();    
		            Long unix = d.getTime()*1000000;		            
		            DecimalFormat df = new DecimalFormat("#.####");
		            df.setRoundingMode(RoundingMode.CEILING);
		            String instanc = instance.getInstanceId().toString();
		            if (metricName == "RequestCount"){
		              Double value = dataPoint.getSum();
		              
			          AWSDashBoard.Filecontents += metricName + ","+ "Instance=" + instanc
			        		  + " value=" +df.format(value).toString() + " " + unix.toString() + "\n" ;	
			          // Sum for the Request count reaches 5 
			          if(value > 5)
			          {
			        	try {
							new SendMessages().PrintMessage(metricName,instanc);
						} catch (TwilioRestException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
			          }
		            }
		            else if(metricName == "VolumeReadBytes" || metricName == "VolumeWriteBytes"){
			              Double value = dataPoint.getAverage();
			              
				          AWSDashBoard.Filecontents += metricName + ","+ "Instance=" + instanc
				        		  + " value=" +df.format(value).toString() + " " + unix.toString() + "\n" ;		
		            }
		            else {
		             Double value = dataPoint.getMaximum();	
		            AWSDashBoard.Filecontents += metricName + ","+ "Instance=" + instanc
		            		+ " value=" +df.format(value).toString() + " " + unix.toString() + "\n" ;
		            
		            }
			} 
			   
			 
		}
		else
		{
			System.out.printf("Result unavailable for %s\n", metricName);
		}
	}
	
	public void CpuUtilization() {
		GetMetrics("CPUUtilization","");
		Display("CPUUtilization");
	}

	public void DiskRead(String volumeid) {
		GetMetrics("VolumeReadBytes",volumeid);
		Display("VolumeReadBytes");
	}
	
	public void BandwidthIn() {
		GetMetrics("NetworkIn","");
		Display("NetworkIn");
	}
	
	public void DiskWrite(String volumeid) {
		GetMetrics("VolumeWriteBytes",volumeid);
		Display("VolumeWriteBytes");
	}
	
	public void BandwidthOut() {
		GetMetrics("NetworkOut","");
		Display("NetworkOut");
	}
	public void MemoryUtilization(){
		GetMetrics("MemoryUtilization","");
		Display("MemoryUtilization");
	}
	public void HttpCount() {
		GetMetrics("RequestCount","");
		Display("RequestCount");
	}
	
	Instance instance;
	GetMetricStatisticsRequest request;
	GetMetricStatisticsResult result;
	
	static final long 	twentyFourHrs = 1000 * 60 * 60 * 24;
    static final int 	oneHour = 60 * 60;

    private void GetMetrics(String metricName,String volumeid)
    {
    	String namespace;
    	String name = "InstanceId";
    	String value = instance.getInstanceId();
    	String statistics = "Maximum";
 
        if(metricName.equals("MemoryUtilization")){
    		namespace = "System/Linux";
    	}
    	else if(metricName.equals("RequestCount")){
    		namespace = "AWS/ELB";
    		name = "LoadBalancerName";
    		value = "Linux-LoadBalancer";
    		statistics = "Sum";
    	}
    	else if (metricName.equals("VolumeReadBytes") || metricName.equals("VolumeWriteBytes")){
    		namespace = "AWS/EBS";
    		name = "VolumeId";
    		statistics = "Average";
    		value = volumeid;
    	}
    	else {
    		namespace = "AWS/EC2";
    	}
    	
    	if(AWSDashBoard.cloudwatchClient!=null)
		{
			request = new GetMetricStatisticsRequest()
					.withStartTime(new Date(new Date().getTime()- twentyFourHrs))
		            .withNamespace(namespace)
		            .withPeriod(oneHour)
		            .withDimensions(new Dimension().withName(name).withValue(value))
		            .withMetricName(metricName)
		            .withStatistics(statistics)
		            .withEndTime(new Date());
			
			if(request != null)
			{
				result = AWSDashBoard.cloudwatchClient.getMetricStatistics(request);
			}
		}
    }
	
}
