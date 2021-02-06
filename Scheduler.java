import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Scheduler implements Runnable {

    //private Elevator elevator;
    private Timestamp time;
    private boolean dataReceived = false;
    private int direction;
    private int floorNumber;
    private int elevatorButton;
    private int currentFloor;
    private int numberOfElevators;
    private ElevatorSubsystem subsystem;
    private elevator.Elevator elevator;
    private Map<String, Integer> inputInfo;
    private Map<String, Integer> elevatorData;
    private boolean schedulerNotified = false;

    public Scheduler(elevator.Elevator elevator) {
        inputInfo = new HashMap();
        elevatorData = new HashMap();
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

    public int getCurrentFloor(){
        return currentFloor;
    }

    public synchronized boolean checkData(){
        while (schedulerNotified == false) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        notifyAll();
        return true;
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
        dataReceived = true;

        System.out.println("File input data -------------------------------------------------------------");

        while (inputInfo.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        if(this.direction == 1) {
            System.out.println("Passenger would like to go UP to floor " + this.elevatorButton + " from floor " + floorNumber);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("Passenger would like to go DOWN to floor " + this.elevatorButton + " from floor " + floorNumber);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        notifyAll();
    }

    public synchronized void receiveElevatorData(int currentFloor, int direction) {
        this.currentFloor = currentFloor;
        this.direction = direction;

        elevatorData.put("currentFloor", currentFloor);
        elevatorData.put("direction", direction);

        while (elevatorData.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        if (this.direction == 1) {
            System.out.println("Elevator is coming DOWN from floor " + this.floorNumber);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Elevator is going up to floor " + this.floorNumber);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

    public synchronized boolean askForElevatorData(){
        while(elevatorData.isEmpty()){
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        notifyAll();
        return true;
    }

    public boolean notifyScheduler(boolean b){
        this.schedulerNotified = b;
        return schedulerNotified;
    }

    @Override
    public void run() {
        this.checkData();
        //System.out.println("done");


       // subsystem

    }
}
