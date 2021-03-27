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
    public ElevatorMovingState state;
    private ElevatorDoorState doorState;
    private boolean operateDoors = false;
    private boolean isMoving = false;
    private int port;
    DatagramPacket sendPacket, receivePacket;
    DatagramSocket receiveSocket, sendSocket;


    public enum ElevatorMovingState {UP, DOWN, IDLE};
    public enum ElevatorDoorState {OPEN, CLOSE};

    public ElevatorSubsystem(int port, Scheduler scheduler) throws SocketException {
        this.scheduler = scheduler;
        elevatorData = "";
        this.port = port;
        elevator = new Elevator(this);
        elevatorInfo = new ArrayList<>();
        state = ElevatorMovingState.IDLE;
        doorState = ElevatorDoorState.CLOSE;
        Random rand = new Random();
        currentElevatorFloor = rand.nextInt(6); //elevator goes up to the 6th floor
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
        String received = new String(data,0,len);
        System.out.println("Containing: "+received + "\n");
        if(received.equals("Door")){
            this.doorsStateMachine(elevator);
        }
        String arr[] = received.split(" ");// split the received packet into a String array
        System.out.println(Arrays.toString(arr));
        if (arr[0].equals("NOTIFICATION")){

        }
        this.receiveSchedulerInfo(arr[4], arr[2], arr[3]);//process the received data from the packet

        startElevatorSM(elevator, directionButton, currentElevatorFloor, floorButton); //start the elevator statemachine


        elevatorData = elevatorData+previousElevatorFloor+" "+directionButton; //store the data gotten from the elevator
        byte elevatorStringArr[] = elevatorData.getBytes();
        byte[] dataArray = new byte[6];
        System.arraycopy(elevatorStringArr, 0, dataArray, 0, elevatorStringArr.length);
        //send packet back to scheduler
        sendPacket = new DatagramPacket(dataArray, dataArray.length, InetAddress.getLocalHost(), 22);
        System.out.println("\nElevatorSubsystem: Sending packet:");
        System.out.println("To: " + sendPacket.getAddress());
        System.out.println("host port: " + sendPacket.getPort());
        System.out.print("Data: ");
        System.out.println(new String(sendPacket.getData(),0,sendPacket.getLength()));
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("sent\n");

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

    //-------------------------------------------------------------------------------------------------------------------


    /**
     * notifies the scheduler about the arrival of an elevator
     */
    public synchronized void notifyOnArrival() throws InterruptedException, IOException {
        String notificationData = "ArrivalNotification"; //store the data gotten from the elevator
        byte elevatorStringArr[] = notificationData.getBytes();
        byte[] dataArray = new byte[19];
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


        byte[] data = new byte[100];
        sendPacket = new DatagramPacket(data, data.length);
        receiveSocket.receive(sendPacket);
        System.out.println("\nElevatorSubsystem: Packet received:");
        System.out.println("From host: " + sendPacket.getAddress());
        System.out.println("Host port: " + sendPacket.getPort());
        int len = sendPacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: " );

        // Form a String from the byte array.
        String received = new String(data,0,len);
        System.out.println(received + "\n");
        if(received.equals("Door")){// if a door request is received from the scheduler, open or close doors
            this.doorsStateMachine(elevator);
        }
        else{
            notifyAll();
        }
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
    public synchronized void startElevatorSM(Elevator e, int direction, int currentElevatorFloor, int floorNumber) throws InterruptedException, IOException {
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
                    e.turnOnLamps(this.destinationFloor, direction); //notify elevator to turn on lamps
                    e.startMotor(direction, currentElevatorFloor, floorNumber);//notify elevator to start motors
                }
            case UP: //when elevator is going up and arrives at its destination
                if(e.checkIfArrived()==true){
                    this.previousElevatorFloor = this.currentElevatorFloor;
                    this.currentElevatorFloor = this.elevator.getCurrentFloor();
                    scheduler.notifyScheduler(true); //notify scheduler that elevator has arrived
                    state = ElevatorMovingState.IDLE;
                    isMoving = false;
                }
                break;
            case DOWN: //when elevator is going down and arrives at its destination
                if(e.checkIfArrived()==true){
                    this.previousElevatorFloor = this.currentElevatorFloor;
                    this.currentElevatorFloor = this.elevator.getCurrentFloor();
                    scheduler.notifyScheduler(true); //notify scheduler that elevator has arrived
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
                Thread.sleep(2000); //wait for elevator to be notified
                if(scheduler.notifyAboutFloor(true) == true){
                    doorState = ElevatorDoorState.OPEN;
                    elevator.openDoors();
                }
            case OPEN:
                Thread.sleep(2000); // wait a while before doors close
                doorState = ElevatorDoorState.CLOSE;
                elevator.closeDoors();
        }

        notifyAll();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            this.receiveAndSend();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        try {
            this.doorsStateMachine(elevator); //starts door operation
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nElevator now heading to floor "+ this.destinationFloor+"-------------------------------------------------------------");

        try {
            this.startElevatorSM(this.elevator, this.directionButton, this.currentElevatorFloor, this.destinationFloor); //heads over to the destination floor
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }


    }

    public static void main(String[] args) throws SocketException {
        //ElevatorSubsystem e = new ElevatorSubsystem(30);
        //Thread elevatorSystem = new Thread(e);
        //elevatorSystem.start();
    }
}

