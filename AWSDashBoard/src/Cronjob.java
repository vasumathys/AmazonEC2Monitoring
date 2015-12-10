import java.util.Timer;
public class Cronjob {
	   public static void main(String[] args){

	     Timer t = new Timer();
	     AWSDashBoard aws = new AWSDashBoard();
	     // This task is scheduled to run every 5 mins
	     t.scheduleAtFixedRate(aws, 0, 300000);
	   }
}
