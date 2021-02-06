package iter1;
import java.io.PrintWriter;
public class elevator extends Thread{
	
	// Scheduler Packet Data
	private SchedulerData scheDat;

	private boolean movingUp;
	private boolean movingDown;
	private int currDirection;
	private final int elevatorNum;
	private int currFloor;

	public elevator(int elevatorNum) {
		this.elevatorNum = elevatorNum;
	}
	public synchronized ElevatorData getElevatorData() {
		return new ElevatorData(elevatorNum, currFloor, movingUp, movingDown,
			currDirection);
	}
	/**
     * Notify this thread
     */
	 public synchronized void wake() {
	    	try {
	    		this.notify();
	    	} catch (Exception e) {}
	    }
	 /**
		 * Process the received scheduler packet
		 */
		public void processPacket(SchedulerData s) {
			scheDat = s;
			int mode = s.getMode();
		}
			@Override
			public void run() {

				print("this" + getElevatorData());
}
			public static void print(String string) {
				// TODO Auto-generated method stub
				
			}
