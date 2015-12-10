import org.bson.BsonDocument;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.bson.*;

public class AccessMongolab {
	public  void http() {
        int counter = 0;
        String instance = null;
        Date d = null ;
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df2.setTimeZone(TimeZone.getTimeZone("UTC"));
		MongoClientURI mongoClientURI = new MongoClientURI("mongodb://bhavana:bhavana@ds037244.mongolab.com:37244/tests");
		MongoClient mongoClient = new MongoClient(mongoClientURI);
		
		MongoDatabase amazonEc2DB = mongoClient.getDatabase(mongoClientURI.getDatabase());
		MongoCollection testCollection =  amazonEc2DB.getCollection("ctr");
		
		
		
		MongoCursor testCursor = testCollection.find().iterator();
		
			
		while (testCursor.hasNext()){
			Document obj = (Document)testCursor.next();
			counter = (int) obj.get("ctr");
			instance = (String) obj.get("instance");
		    String formdat = (String) obj.get("time");
			try {
				d = df2.parse(formdat);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		mongoClient.close();
        Long unix = d.getTime()*1000000;	
		 String Filecontents = "HTTP" + ","+ "Instance=" + instance
       		  + " value=" +counter + " " + unix.toString() + "\n" ;	
		 System.out.println(Filecontents);
		 new StoreDb().post(Filecontents); 
		
	}
	public void memory() {
        Long memory = null;
        String instance = null;
        Date d = null ;
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df2.setTimeZone(TimeZone.getTimeZone("UTC"));
		MongoClientURI mongoClientURI = new MongoClientURI("mongodb://bhavana:bhavana@ds037244.mongolab.com:37244/tests");
		MongoClient mongoClient = new MongoClient(mongoClientURI);
		
		MongoDatabase amazonEc2DB = mongoClient.getDatabase(mongoClientURI.getDatabase());
		MongoCollection testCollection =  amazonEc2DB.getCollection("memoryctr");
		
		
		
		MongoCursor testCursor = testCollection.find().iterator();
		
			
		while (testCursor.hasNext()){
			Document obj = (Document)testCursor.next();
			memory = (Long)obj.get("mem");
			instance = (String) obj.get("instance");
		    String formdat = (String) obj.get("time");
			try {
				d = df2.parse(formdat);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		mongoClient.close();
        Long unix = d.getTime()*1000000;	
		 String Filecontents = "Memory" + ","+ "Instance=" + instance
       		  + " value=" +memory + " " + unix.toString() + "\n" ;	
		 System.out.println(Filecontents);
		 new StoreDb().post(Filecontents); 
		
	}
}
