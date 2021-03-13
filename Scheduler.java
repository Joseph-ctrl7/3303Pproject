/**
 *
 */

/**
 * @author: Joseph Anyia, Amith Kumar Das Orko, Tolu Ajisola,
 *          Israel Okonkwo, Mehdi Khan
 *
 */


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.*;

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
    private int numberOfFloors;
    private ElevatorSubsystem subsystem;
    private ElevatorSubsystem s;
    private Elevator elevator;
    private SchedulerStates state;
    private boolean floorNotified = false;
    public ArrayList numbers;
    private Map<Integer, ElevatorSubsystem> elevators;

    private Queue<DatagramPacket> queue;
    private int portNumber;
    private DatagramSocket receiveSocket;
    private DatagramSocket socket;
    private InetAddress local;
    private DatagramPacket receivePacket;
    private DatagramPacket ackPacket;

    Thread sThread;

    private Map<String, Integer> inputInfo;
    private Map<String, Integer> elevatorData;
    private boolean schedulerNotified = false;


    public enum SchedulerStates {LISTENING, RECEIVED, ELEVATORINFO, FLOORINFO}


    public Scheduler(int numberOfElevators, int numberOfFloors) {
        this.numberOfElevators = numberOfElevators;
        this.numberOfFloors = numberOfFloors;
        elevators = new HashMap();
        inputInfo = new HashMap();
        elevatorData = new HashMap();
        this.elevator = elevator;
        state = SchedulerStates.LISTENING;
        s = new ElevatorSubsystem(this);
        sThread = new Thread(s);

        numbers = new ArrayList();
        for (int i=0; i<numberOfFloors; i++){
            numbers.add(i);
        }

        for(int i = 1; i < numberOfElevators+1; i++){
            subsystem = new ElevatorSubsystem(this);
            subsystem.setCurrentFloor(this.getSpecificLocation());
            elevators.put(i, subsystem);
        }

    }

    public synchronized void sendReceivePacket(){
        byte data[] = new byte[100];
        byte validationMessage[] = new byte[100];
        validationMessage = "Message received".getBytes();
        receivePacket = new DatagramPacket(data, data.length);
        System.out.println("Scheduler: LISTENING");

        try {
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

    }


















    public void getLocations(){
            for(Map.Entry<Integer, ElevatorSubsystem> e : elevators.entrySet()){
                System.out.println( e.getValue().getCurrentFloor());
        }
    }

    public int getSpecificLocation(){
        int i = 0;
        Collections.shuffle(numbers);
        return (int) numbers.get(i);
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

    public void selectBestElevator(){

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

    public SchedulerStates getState() {
        return state;
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
    public synchronized boolean startSchedulerSM(){
        switch (state){
            case LISTENING:// scheduler listens for info from both elevator and floor subsystems
                if(this.dataReceived == true){
                    state = SchedulerStates.RECEIVED;
                    this.dataReceived = false;
                }
                break;
            case RECEIVED:
                if(this.floorInfoReceived == true){
                    state = SchedulerStates.FLOORINFO;
                    this.floorInfoReceived = false; //clear flag
                }

                if(this.elevatorInfoReceived == true){
                    state = SchedulerStates.ELEVATORINFO;
                    this.elevatorInfoReceived = false; //clear flag
                }
                break;
            case FLOORINFO:
                //if(this.askForInput() == true){  //updates elevator tasks if there is input in the scheduler
                  //  s.receiveSchedulerInfo();
                //}
                state = SchedulerStates.LISTENING; //returns back to listening for info
                break;
            case ELEVATORINFO:
                checkData(); //checks to see if the scheduler has been notified by the ElevatorSubsystem(elevator subsystem thread will then start in order to receive scheduler info)
                if(!elevatorData.isEmpty()){
                    state = SchedulerStates.LISTENING;
                }
                break;
        }
        return true;
    }

/*
    private Elevator getBestElevator(SchedulerRequest request) {
        Elevator tempElevator = null;
        for (int i = 1; i <= numberOfElevators; i++) {
            Elevator elevator = elevatorStatus.get(i);
            if (elevator.getNumRequests() == 0) {
                return elevator;
            } else {
                if (elevator.getRequestDirection().equals(request.getRequestDirection())) {
                    if(tempElevator == null) {
                        tempElevator = elevator;
                    }
                    if (elevator.getRequestDirection().equals(Direction.DOWN)
                            && elevator.getCurrentFloor() > request.getSourceFloor()) {
                        if(elevator.getNumRequests() < tempElevator.getNumRequests()) {
                            tempElevator = elevator;
                        }
                    } else if (elevator.getRequestDirection().equals(Direction.UP)
                            && elevator.getCurrentFloor() < request.getDestFloor()) {
                        if(elevator.getNumRequests() < tempElevator.getNumRequests()) {
                            tempElevator = elevator;
                        }
                    }
                }
            }
        }
        return tempElevator;
    }*/


    @Override
    public void run() {
        sendReceivePacket();
        //this.startSchedulerSM();
        //sThread.start();
    }


    public static void main(String[] args) {
        Scheduler s = new Scheduler(2, 6);
        s.getLocations();
    }
}

