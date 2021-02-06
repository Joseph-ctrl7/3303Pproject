package iter1;

public class ElevatorSubsystem extends Thread {


	
	public void print(String message) {
		System.out.println("ELEVATOR SUBSYSTEM: " + message);
		}
	public void wait(int ms) {
	try {
		Thread.sleep(ms);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	}
	public static void main(String args[]) {
		ElevatorSubsystem c = new ElevatorSubsystem();
	}
	
	
}
