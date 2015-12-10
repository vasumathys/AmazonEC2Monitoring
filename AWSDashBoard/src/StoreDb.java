import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class StoreDb {
	public void post(String Filecontents){
	  	String data = Filecontents;
    	//String urlstring ="http://localhost:8086/write?db=EC2Monitoring";
	  	String urlstring = "https://gigawatt-mcfly-77.c.influxdb.com:8086/write?db=EC2Monitoring&u=influxdb&p=5712f0fc21d52b61";
    	URL url = null;
		try {
			url = new URL(urlstring);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        URLConnection conn = null;
		try {
			conn = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        conn.setDoOutput(true);
        conn.setRequestProperty ("Authorization","Basic");

        OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(conn.getOutputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

        try {
			writer.write(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String line;
        BufferedReader reader = null;
		try {
			reader = new BufferedReader(new 
			InputStreamReader(conn.getInputStream()));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			while ((line = reader.readLine()) != null) {
			  System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


	}


