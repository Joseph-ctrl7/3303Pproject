import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class Scheduler implements Runnable {

    //private Elevator elevator;
    private Timestamp time;
    private int direction;
    private int floorNumber;
    private int elevatorButton;
    private int numberOfElevators;
    private ElevatorSubsystem subsystem;
    private elevator.Elevator elevator;
    private Map<String, Integer> inputInfo;

    public Scheduler(elevator.Elevator elevator) {
        inputInfo = new HashMap();
        this.elevator = elevator;

    }


    public int getDirection(){
        return this.direction;
    }

    public int getFloorNumber(){
        return this.floorNumber;
    }

    public int getElevatorButton(){
        return this.elevatorButton;
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

    public synchronized boolean askForInput(){
        while(inputInfo.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        notifyAll();
        return true;
    }

    @Override
    public void run() {
       // subsystem

    }
}
