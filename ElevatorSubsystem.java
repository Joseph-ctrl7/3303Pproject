/**
 *
 */

/**
 * @authors: Joseph Anyia, Amith Kumar Das Orko, Tolu Ajisola,
 *          Israel Okonkwo, Mehdi Khan
 *
 */

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;



public class ElevatorSubsystem implements Runnable{

    private int currentElevatorFloor;
    private int previousElevatorFloor;
    private int destinationFloor;
    private int directionButton;
    private int floorButton;
    private String elevatorData;
    private Scheduler scheduler;
    private Elevator elevator;
    private ArrayList<Integer> elevatorInfo;
    private ArrayList<FloorData> requests;
    private List<Integer> floorRequests;
    private List<Integer> destinationRequests;
    private List<Integer> floors;
    public ElevatorMovingState state;
    private ElevatorDoorState doorState;
    private boolean operateDoors = false;
    private boolean isMoving = false;
    private boolean noRequests = false;
    private int port;
    private int numOfFloors;
    private boolean stopRequested = false;
    DatagramPacket sendPacket, receivePacket;
    DatagramSocket receiveSocket, sendSocket;


    public enum ElevatorMovingState {UP, DOWN, IDLE};
    public enum ElevatorDoorState {OPEN, CLOSE};


    public ElevatorSubsystem(){}

    public ElevatorSubsystem(int port, int numOfFloors) throws SocketException {
        //this.scheduler = scheduler;
        elevatorData = "";
        this.port = port;
        elevator = new Elevator(this, numOfFloors);
        elevatorInfo = new ArrayList<>();
        floorRequests = new ArrayList<>();
        destinationRequests = new ArrayList<>();
        state = ElevatorMovingState.IDLE;
        doorState = ElevatorDoorState.CLOSE;
        Random rand = new Random();
        currentElevatorFloor = rand.nextInt(6); //elevator goes up to the 6th floor
        this.numOfFloors = numOfFloors;
        floors = new ArrayList<>();
        for(int i=0; i<numOfFloors; i++){
            floors.add(i);
        }
        receiveSocket = new DatagramSocket(this.port);
        sendSocket = new DatagramSocket();

    }


    /**
     * this method sends and receives data packets from the scheduler
     * @throws UnknownHostException
     * @throws InterruptedException
     */
    public synchronized void receiveAndSend() throws IOException, InterruptedException {
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

        // Block until a datagram packet is received from receiveSocket.
        //while (true){
            try {
                receiveSocket.receive(receivePacket);
            } catch (IOException e) {
                System.out.print("IO Exception: likely:");
                System.out.println("Receive Socket Timed Out.\n" + e);
                e.printStackTrace();
                System.exit(1);
            }

            // Process the received datagram.
            System.out.println("\nElevatorSubsystem: Packet received:");
            System.out.println("From host: " + receivePacket.getAddress());
            System.out.println("Host port: " + receivePacket.getPort());
            int len = receivePacket.getLength();
            System.out.println("Length: " + len);
            // Form a String from the byte array.
            String received = new String(data, 0, len);
            System.out.println("Containing: " + received + "\n");
            String arr[] = received.split(" ");// split the received packet into a String array
            System.out.println(Arrays.toString(arr));
            if (arr[0].equals("Door")) {
                this.doorsStateMachine(elevator);
            }
            if (arr[0].equals("NA")) {
                noRequests = true;
                System.out.println("no pending request");
                return;
            }
            if (arr[0].equals("FLOOR")) {
                if (elevatorInfo.isEmpty()) {//if there are no requests being attended to
                    this.receiveSchedulerInfo(arr[4], arr[2], arr[3]);//process the received data from the packet
                    elevator.addRequests(new FloorData(arr[1], Integer.parseInt(arr[2]), arr[3], Integer.parseInt(arr[4])));
                    elevator.addFloorRequests(Integer.parseInt(arr[2]));
                    elevator.addDestinationRequests(Integer.parseInt(arr[4]));
                    startElevatorSM(elevator, directionButton, currentElevatorFloor); //start the elevator statemachine
                } else {
                    elevator.addRequests(new FloorData(arr[1], Integer.parseInt(arr[2]), arr[3], Integer.parseInt(arr[4])));
                    elevator.addFloorRequests(Integer.parseInt(arr[2]));
                    elevator.addDestinationRequests(Integer.parseInt(arr[4]));
                    //return;
                }
                notifyAll();

        }

    }



    //getters and setters for the fields in the elevatorSubsystem class-----------------------------------------------------------------------
    public int getPort(){
        return port;
    }


    public void setFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }

    public int getFloor() {
        return this.destinationFloor;
    }

    public void setDirectionButton(int directionButton){
        this.directionButton = directionButton;
    }

    public int getDirectionButton() {
        return this.directionButton;
    }

    public void setElevatorButton(int floorButton){
        this.floorButton = floorButton;
    }

    public int getElevatorButton() {
        return this.floorButton;
    }
    public Elevator getElevator(){
        return elevator;
    }

    public int getCurrentFloor() {
        return this.currentElevatorFloor;
    }

    public int getElevatorInfo(int index) {
        return elevatorInfo.get(index);
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentElevatorFloor = currentFloor;
        System.out.println(currentElevatorFloor);
    }

    public boolean doorsOperational(){
        return operateDoors;
    }

    //-------------------------------------------------------------------------------------------------------------------


    /**
     *this method is responsible for sending all elevator locations to the scheduler
     */
    public void sendElevatorData() throws UnknownHostException {
        String elevatorData = "ElevatorData "+state.toString()+" "+currentElevatorFloor+ " "+port; //store the data gotten from the elevator
        byte elevatorStringArr[] = elevatorData.getBytes();
        byte[] dataArray = new byte[22];
        System.arraycopy(elevatorStringArr, 0, dataArray, 0, elevatorStringArr.length);
        //send packet back to scheduler
        sendPacket = new DatagramPacket(dataArray, dataArray.length, InetAddress.getLocalHost(), 22);
        System.out.println("Sending data from elevator:");
        System.out.println("To: " + sendPacket.getAddress());
        System.out.println("host port: " + sendPacket.getPort());
        System.out.print("Data: ");
        System.out.println(new String(sendPacket.getData(),0,sendPacket.getLength()));
        try {
            receiveSocket.send(sendPacket);//send notification packet to scheduler
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("sent\n");

    }




    /**
     * checks if the elevator info is available
     * @return true if it is
     */
    public boolean checkIfEmpty() {
        if(elevatorInfo.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * this method gets the information from the scheduler
     */
    public void receiveSchedulerInfo(String elevatorButton, String floorButton, String direction) {
        this.destinationFloor = Integer.parseInt(elevatorButton);
        this.floorButton = Integer.parseInt(floorButton);
        if(direction.equals("Up")){
            this.directionButton = 1;
        }else{
            this.directionButton = 0;
        }

        elevatorInfo.add(this.floorButton);
        elevatorInfo.add(this.directionButton);
        elevatorInfo.add(this.destinationFloor);
    }



    /**
     * this method is the state machine responsible for elevator movement
     * @param e elevator to be notified
     */
    public synchronized void startElevatorSM(Elevator e, int direction, int currentElevatorFloor) throws InterruptedException, IOException {
        switch(state){
            case IDLE: //when elevator is idle and a request is made
                if(this.elevatorInfo.contains(direction)) {
                    if (direction == 0) {
                        state = ElevatorMovingState.DOWN;
                        isMoving = true;
                    }
                    if (direction == 1) {
                        state = ElevatorMovingState.UP;
                        isMoving = true;
                    }
                    //e.turnOnLamps(this.destinationFloor, direction); //notify elevator to turn on lamps
                    Thread et = new Thread(e);
                    et.start();//start elevator thread
                }
            case UP: //when elevator is going up and arrives at its destination
                if(e.checkIfArrived()==true){
                    this.previousElevatorFloor = this.currentElevatorFloor;
                    this.currentElevatorFloor = this.elevator.getCurrentFloor();
                    state = ElevatorMovingState.IDLE;
                    isMoving = false;
                }
                break;
            case DOWN: //when elevator is going down and arrives at its destination
                if(e.checkIfArrived()==true){
                    this.previousElevatorFloor = this.currentElevatorFloor;
                    this.currentElevatorFloor = this.elevator.getCurrentFloor();
                    //scheduler.notifyScheduler(true); //notify scheduler that elevator has arrived
                    state = ElevatorMovingState.IDLE;
                    isMoving = false;
                }
                break;
        }
    }



    /**
     * state machine for door operations
     * @param elevator elevator whose door will be opened or closed
     * @throws InterruptedException
     */
    public synchronized void doorsStateMachine(Elevator elevator) throws InterruptedException {
        switch(doorState){
            case CLOSE:
                operateDoors = false;
                //Thread.sleep(2000); //wait for elevator to be notified
                //if(scheduler.notifyAboutFloor(true) == true){
                    doorState = ElevatorDoorState.OPEN;
                    elevator.openDoors();
                //}
            case OPEN:
                operateDoors = false;
                Thread.sleep(2000); // wait a while before doors close
                doorState = ElevatorDoorState.CLOSE;
                elevator.closeDoors();
        }
        operateDoors = true;

        notifyAll();
    }

    @Override
    public void run() {
        try {
            this.sendElevatorData();//send the elevator locations to the scheduler
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(2000);//wait a while before receiving packets
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                this.receiveAndSend();//continually receive packets
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SocketException {
       //ElevatorSubsystem es = new ElevatorSubsystem(28, 6);
       //ElevatorSubsystem e = new ElevatorSubsystem(30, 6);
       //Thread el = new Thread(es);
       // Thread elevatorSystem = new Thread(e);
       //el.start();
       for(int i = 0; i < 2; i++){
           ElevatorSubsystem e = new ElevatorSubsystem(28+i, 6);
           Thread t = new Thread(e);
           t.start();
       }
/*
        Scheduler s = new Scheduler(2, 6, 22);
        Thread schedulerThread = new Thread(s);
        schedulerThread.start();
        FloorSubsystem f = new FloorSubsystem("elevatorInputs.txt", 21);
        Thread floorSubsystem = new Thread(f);
        floorSubsystem.start();
        //elevatorSystem.start();

 */
    }
}

