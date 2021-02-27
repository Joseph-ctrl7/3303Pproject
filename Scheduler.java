/**
 *
 */

/**
 * @author: Joseph Anyia, Amith Kumar Das Orko, Tolu Ajisola,
 *          Israel Okonkwo, Mehdi Khan
 *
 */


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Scheduler implements Runnable {

    private Timestamp time;
    private boolean dataReceived = false; // if any piece of data either from elevator or floor subsystem is received
    private boolean elevatorInfoReceived = false; //info from ElevatorSubsystem
    private boolean floorInfoReceived = false; //info from FloorSubsystem
    private int direction;
    private int floorNumber;
    private int elevatorButton;
    private int currentFloor;
    private int numberOfElevators;
    private ElevatorSubsystem subsystem;
    private Elevator elevator;
    private SchedulerStates state;
    private boolean floorNotified = false;

    private Map<String, Integer> inputInfo;
    private Map<String, Integer> elevatorData;
    private boolean schedulerNotified = false;


    public enum SchedulerStates {LISTENING, RECEIVED, ELEVATORINFO, FLOORINFO}


    public Scheduler(Elevator elevator) {
        inputInfo = new HashMap();
        elevatorData = new HashMap();
        this.elevator = elevator;
        state = SchedulerStates.LISTENING;


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


    /**
     * lets scheduler know that floor has succesfully been notified about the elevator arrival. Can now commence to open doors
     *
     */
    public synchronized boolean notifyAboutFloor(boolean b){
        this.floorNotified = b;
        return this.floorNotified;
    }


    /**
     * checks if floor subsystem info was received
     * @return false if the hashmap storing the floor subsystem info is not empty
     */
    public boolean checkIfEmpty() {
        if(inputInfo.isEmpty()) {
            return true;
        }
        return false;
    }


    /**
     * checks if elevator data is available
     * @return
     */
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
        floorInfoReceived = true;

        System.out.println("\nScheduler data from File input -------------------------------------------------------------");

        while (inputInfo.isEmpty()) { //waits until input hashmap is updated
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


    /**
     * this method gets the data in the elevator subsystem
     * @param currentFloor
     * @param direction
     */
    public synchronized void receiveElevatorData(int currentFloor, int direction) {
        this.currentFloor = currentFloor;
        this.direction = direction;

        elevatorData.put("currentFloor", currentFloor);
        elevatorData.put("direction", direction);
        dataReceived = true;
        elevatorInfoReceived = true;

        while (elevatorData.isEmpty()) {  //waits until elevator data hashmap is updated
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        System.out.println("\nScheduler data from ElevatorSubsystem---------------------------------------------");

        if(schedulerNotified == true){
            System.out.println("Elevator was at floor "+this.currentFloor+" and has arrived at floor "+ this.floorNumber);
        }

        notifyAll();
    }


    /**
     * this method returns true if the scheduler has the input files
     * @return
     */
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

    /**
     * returns true if scheduler contains the elevator data
     * @return
     */
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

    /**
     * notifies the scheduler when the elevator files have successfully been received
     * @param b
     * @return
     */
    public boolean notifyScheduler(boolean b){
        this.schedulerNotified = b;
        return schedulerNotified;
    }


    /**
     * scheduler state machine. Responsible for receiving and sending info to both
     * the floorsubsystem and elevatorsubsystem
     *
     *
     */
    public synchronized void startSchedulerSM(){
        switch (state){
            case LISTENING:// scheduler listens for info from both elevator and floor subsystems
                if(this.dataReceived == true){
                    state = SchedulerStates.RECEIVED;
                }
            case RECEIVED:
                if(this.floorInfoReceived == true){
                    state = SchedulerStates.FLOORINFO;
                    this.floorInfoReceived = false; //clear flag
                }
                if(this.elevatorInfoReceived == true){
                    state = SchedulerStates.ELEVATORINFO;
                    this.elevatorInfoReceived = false; //clear flag
                }
            case FLOORINFO:
                checkData(); //checks to see if the scheduler has been notified by the ElevatorSubsystem(elevator subsystem thread will then start in order to receive scheduler info)
                state = SchedulerStates.LISTENING; //returns back to listening for info
            case ELEVATORINFO:
                if(!elevatorData.isEmpty()){
                    state = SchedulerStates.LISTENING;
                }

        }
    }


    @Override
    public void run() {
        this.startSchedulerSM();
    }
}

