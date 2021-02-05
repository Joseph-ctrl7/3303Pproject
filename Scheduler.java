import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Scheduler implements Runnable {

    //private Elevator elevator;
    private Timestamp time;
    private int direction;
    private int floorNumber;
    private int elevatorButton;
    private Map<String, Integer> inputInfo;

    public Scheduler() {
        inputInfo = new HashMap();
    }


    /**
     * This method receives the input from the Floorsubsystem and sends it to the ElevatorSubsystem
     */
    public synchronized void receiveInfo(Timestamp time, int floorNumber, int direction, int elevatorButton) {
        this.time = time;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.elevatorButton = elevatorButton;

        inputInfo.put("floorNumber", floorNumber);
        inputInfo.put("direction", direction);
        inputInfo.put("elevatorButton", elevatorButton);

        while (inputInfo.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        System.out.println("Passenger would like to go " + this.direction + " to " + this.elevatorButton + " from " + floorNumber);
        notifyAll();
    }

    @Override
    public void run() {


    }
}
