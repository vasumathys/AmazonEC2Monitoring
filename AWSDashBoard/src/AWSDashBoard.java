import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.InstanceBlockDeviceMapping;
import com.amazonaws.services.ec2.model.Reservation;


/**
 *	Author: Bhavana Bhasker
 *	Class:  AWSDashBoard, gets Amazon EC2 instances and calculates the following:  
 *			CPU Utilization.
 *			Memory Utilization
 *			Network Bandwidth Usage
 *	Dependency:  
*/
public class AWSDashBoard extends TimerTask{

	static AmazonEC2      ec2;
	static AmazonCloudWatchClient cloudwatchClient;
	public static String Filecontents = "";
	private static Map<Integer,Instance> GetInstances() throws Exception {
	
        AWSCredentials credentials = null;
        
        try {
            credentials = new ProfileCredentialsProvider("default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException("Credentials creation failed",e);
        }
        ec2 = new AmazonEC2Client(credentials);
        ec2.setRegion(Region.getRegion(Regions.US_WEST_1));
        cloudwatchClient = new AmazonCloudWatchClient(credentials);
        cloudwatchClient.setRegion(Region.getRegion(Regions.US_WEST_1));
        cloudwatchClient.setEndpoint("http://monitoring.us-west-1.amazonaws.com");
        try {
            List<Reservation> reservations = ec2.describeInstances().getReservations();
            Map<Integer,Instance> instances = new HashMap<Integer,Instance>();

            Integer i = 0 ;
            for (Reservation reservation : reservations) {
            	List<Instance> insts = reservation.getInstances();
            	for(Instance inst : insts)
            		instances.put((++i),inst);
            }

            System.out.println("Found " + instances.size() + " Amazon EC2 instance(s) running.");
            
            return instances;
        } catch (AmazonServiceException ase) {
                System.out.println("Caught Exception: " + ase.getMessage());
                System.out.println("Reponse Status Code: " + ase.getStatusCode());
                System.out.println("Error Code: " + ase.getErrorCode());
                System.out.println("Request ID: " + ase.getRequestId());
        }
		return null;
    }


    public static void main(String[] args) throws Exception {
      new AWSDashBoard().run();

    }

   @Override 
	public  void run() {
	   String volumeid ="";
	   String platform = "";
        System.out.println("Amazon EC2 Metric DashBoard");
        

        Map<Integer, Instance> instMap = null;
		try {
			instMap = GetInstances();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if( instMap.size() == 0 )
        {
        	System.out.println("No Instances returned, exising");
        	return;
        }
        
        	for (Map.Entry<Integer, Instance> entry : instMap.entrySet()) {
        		String name = "No Tag";
        		String[] splits = entry.getValue().getTags().get(0).toString().split("Value:");
        		if (splits.length > 1 )
        			name = splits[1].replace('}', ' ');
        	    System.out.println(entry.getKey().toString() + " : " + name );
        	Instance inst = instMap.get(new Integer(entry.getKey().toString()));
        	
        	if(inst == null)
        	{
        		System.out.println("Invalid Selection!\n");
        		continue;
        	}
        	if(inst.getState() != null && !inst.getState().getName().equals("running"))
        	continue;
        	if(inst.getBlockDeviceMappings()!=null)
                for(InstanceBlockDeviceMapping objBlock:inst.getBlockDeviceMappings()){
                volumeid = objBlock.getEbs().getVolumeId();
            }
        	MetricCalculator metrics = new MetricCalculator(inst);
        	metrics.CpuUtilization();
            metrics.DiskRead(volumeid);
            metrics.DiskWrite(volumeid);
            metrics.BandwidthIn();
            metrics.BandwidthOut();
            metrics.MemoryUtilization();
            metrics.HttpCount();
        }
        
        new StoreDb().post(Filecontents); 
        new AccessMongolab().http();
        new AccessMongolab().memory();
        System.out.println("\n\nEnding Dashboard Program");
        System.out.println("===========================================");
	}
}
