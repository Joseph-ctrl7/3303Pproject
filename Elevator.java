/**
 *
 */

/**
 * @author: Joseph Anyia, Amith Kumar Das Orko, Tolu Ajisola,
 *          Israel Okonkwo, Mehdi Khan
 *
 */






import java.io.IOException;
import java.net.*;
import java.util.*;

public class Elevator implements Runnable {
    //private  Time timer;
    private static long initialTime;


    public enum State {
        MOVING_UP, MOVING_DOWN, STOPPED
    }

    public static final int WORKING = 0;
    public static final int STUCK = 1;
    public static final int OPENED = 2;
    public static final int CLOSED = 3;
    public static final int ARRIVED = 4;
    public static final int MOVING_UP = 5;
    public static final int MOVING_DOWN = 6;
    public static final int IDLE = 7;
    public static final int SHUTDOWN = 8;
    public int status = WORKING;

    private int floor;
    private State state;
    private boolean buttonPressed;
    private boolean directionLamp;
    private boolean elevatorLamp;
    private boolean startMotor;
    private boolean openDoors;
    private boolean elevatorNotified;
    private boolean hasArrived;
    private boolean doorClosed = false;
    private boolean elevatorFunctioning = false;
    private ElevatorSubsystem e;
    private boolean stopRequested;

    private List<FloorData> requests;
    private List<Integer> floorRequests;
    private List<Integer> destinationRequests;
    private List<Integer> floors;
    private int directionButton;
    private int floorButton;
    private int currentFloor;
    private int numOfFloors;
    private int timerDuration;
    private int doorDuration;
    private int elevatorDuration;
    private UI gui;
    DatagramSocket sendSocket;


    public Elevator(ElevatorSubsystem e, int numOfFloors) throws SocketException {
        state = State.STOPPED;
        buttonPressed = false;
        directionLamp = false;
        elevatorLamp = false;
        startMotor = false;
        openDoors = false;
        elevatorNotified = false;
        hasArrived = false;
        this.e = e;
        requests = new ArrayList<>();
        floorRequests = new ArrayList<>();
        destinationRequests = new ArrayList<>();
        initialTime = System.currentTimeMillis();
        timerDuration = 6000;
        doorDuration = 3000;
        elevatorDuration = 5000;
        this.numOfFloors = numOfFloors;
        floors = new ArrayList<>();
        for(int i=0; i<numOfFloors; i++){
            floors.add(i);
        }
        sendSocket = new DatagramSocket();

    }
    public Elevator(ElevatorSubsystem e, int numOfFloors, UI gui) throws SocketException {
        this.gui = gui;
        state = State.STOPPED;
        buttonPressed = false;
        directionLamp = false;
        elevatorLamp = false;
        startMotor = false;
        openDoors = false;
        elevatorNotified = false;
        hasArrived = false;
        this.e = e;
        requests = new ArrayList<>();
        floorRequests = new ArrayList<>();
        destinationRequests = new ArrayList<>();
        initialTime = System.currentTimeMillis();
        timerDuration = 6000;
        doorDuration = 3000;
        elevatorDuration = 5000;
        this.numOfFloors = numOfFloors;
        floors = new ArrayList<>();
        for(int i=0; i<numOfFloors; i++){
            floors.add(i);
        }
        sendSocket = new DatagramSocket();

    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public void setDirectionButton(int directionButton){
        this.directionButton = directionButton;
    }

    public void setElevatorButton(int floorButton){
        this.floorButton = floorButton;
    }

    public State getState() {
        return state;
    }

    public void setState(State s) {
        state = s;
    }
    public boolean isMoving() {
        return state == State.MOVING_UP || state == State.MOVING_DOWN;
    }


    public int getCurrentFloor(){
        return this.currentFloor;
    }

    public boolean checkDoorClosed(){
        return doorClosed;
    }
    public boolean checkDoorOpened(){
        return openDoors;
    }
    public boolean checkElevatorStatus(){
        return elevatorFunctioning;
    }

    public void addRequests(FloorData f){
        requests.add(f);
    }
    public void addFloorRequests(int i){
        floorRequests.add(i);
    }
    public void addDestinationRequests(int i){
        destinationRequests.add(i);
    }
    public void setTimerDuration(int duration){
        timerDuration = duration;
    }
    public void setDoorDuration(int duration){
        doorDuration = duration;
    }
    public void setElevatorDuration(int duration){
        elevatorDuration = duration;
    }


    public synchronized void openDoors(int duration) throws InterruptedException {
        Time timer = new Time(timerDuration);
        Thread t = new Thread(timer);//make a new timer thread
        t.start();//start the thread for the given duration
        System.out.println("port:"+e.getPort()+" Elevator Doors Opening........");
        //gui.setElevatorDoorStatus(WORKING, e.getPort());
        int x = duration/1000;
        this.pause(x, timer);
        if(timer.countCompleted()) {//if timer finished before doors could be opened
            System.out.println("DOOR FAULT: Please wait a few seconds...");
            gui.setElevatorDoorStatus(STUCK, 28);
            Thread.sleep(4000);//since transient fault, wait a few seconds before doors fully open
        }
        gui.setElevatorDoorStatus(OPENED, e.getPort());
        System.out.println("port:"+e.getPort()+" Elevator Doors Opened");
        openDoors = true;
        notifyAll();
    }

    public synchronized void closeDoors(int duration) throws InterruptedException {
        Time timer = new Time(timerDuration);
        Thread t = new Thread(timer);
        t.start();//start the timer thread for the given duration
        System.out.println("port:"+e.getPort()+" Elevator Doors Closing........");
        int x = duration/1000;
        this.pause(x, timer);
        if(timer.countCompleted()) {//if timer finished before doors could be closed
            System.out.println("DOOR FAULT: Please wait a few seconds...");
            gui.setElevatorDoorStatus(STUCK, e.getPort());
            Thread.sleep(4000);//since transient fault, wait a few seconds before doors fully close
        }
        gui.setElevatorDoorStatus(CLOSED, e.getPort());
        System.out.println("port:"+e.getPort()+" Elevator Doors Closed");
        doorClosed = true;
        notifyAll();
    }

    /**
     * checks if elevator has arrived its destination
     * @return true if elevator has arrived
     */
    public synchronized boolean checkIfArrived(){
        return this.hasArrived;
    }

    /**
     * turns on the required lamps in the elevator
     * @param destination
     * @param direction
     */

    public synchronized void turnOnLamps(int destination, int direction){
        this.directionLamp = true;
        this.elevatorLamp = true;
        if(direction == 1){
            System.out.println("lamps "+destination + " and UP are on");
        }
        else{
            System.out.println("lamps "+destination + " and DOWN are on");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void turnOffLamps(int destination, int direction){
        this.directionLamp = false;
        this.elevatorLamp = false;
        System.out.println(destination + " and "+ direction+" lamps are off");

    }

    /**
     * this method starts the motor of the elevator and takes it to the requested floor
     * @param currentElevatorFloor where the elevator is currently at
     */
    public synchronized void startMotor(int currentElevatorFloor, int duration) throws InterruptedException, IOException {
        Collections.sort(floorRequests);
        hasArrived = false;
        Iterator<Integer> iter = floors.iterator();
        while(iter.hasNext()){//iterate through floors in the building
            if(floorRequests.isEmpty()){//if there are no more requests to meet, attend to the destinations instead
                break;
            }
            int i = iter.next();
            while(!floorRequests.isEmpty()) {
                System.out.println(floorRequests.toString());
                if (currentElevatorFloor > floorRequests.get(0)) {//if the current elevator floor is above the first request, then elevator moves down
                    Collections.reverse(floors);
                    Time timer = new Time(duration);
                    Thread t = new Thread(timer);
                    System.out.println("port:" + e.getPort() + " Elevator is currently at floor " + currentElevatorFloor);
                    gui.setDirection(MOVING_DOWN, e.getPort());
                    gui.setCurrentFloor2(currentElevatorFloor, e.getPort());
                    this.state = State.MOVING_DOWN;
                    int nextFloor = currentElevatorFloor - 1;
                    e.setCurrentFloor(currentElevatorFloor);//notify the subsystem about elevator location
                    Thread.sleep(2000);
                    //send("ArrivalNotification Floor " + currentElevatorFloor +" port: "+e.getPort());
                    System.out.println("port:" + e.getPort() + " Elevator is going DOWN to floor " + nextFloor);
                    currentElevatorFloor--;
                    int x = elevatorDuration/1000;
                    t.start();// start the timer
                    this.pause(x, timer);
                    if (timer.countCompleted()) {//if timer finishes before elevator gets to next floor
                        System.out.println("ELEVATOR FAULT: Shutting Down Elevator...");//since hard fault, elevator shuts down
                        Thread.sleep(2000);
                        gui.setElevatorDoorStatus(SHUTDOWN, e.getPort());
                        gui.setElevatorStatus(STUCK, e.getPort());
                        requestStop();//request stop and shut down the elevator thread
                        return;
                    }
                }
                if (currentElevatorFloor < floorRequests.get(0)) {//if the current elevator floor is below the first request, then elevator moves up
                    Collections.sort(floors);
                    Collections.sort(floorRequests);
                    Time timer = new Time(duration);
                    Thread t = new Thread(timer);
                    System.out.println("port:" + e.getPort() + " Elevator is currently at floor " + currentElevatorFloor);
                    gui.setDirection(MOVING_UP, e.getPort());
                    gui.setCurrentFloor2(currentElevatorFloor, e.getPort());
                    this.state = State.MOVING_UP;
                    int nextFloor = currentElevatorFloor + 1;
                    e.setCurrentFloor(currentElevatorFloor);
                    Thread.sleep(2000);
                    //send("ArrivalNotification Floor " + currentElevatorFloor +" port: "+e.getPort());
                    System.out.println("port:" + e.getPort() + " Elevator is going UP to floor " + nextFloor);
                    currentElevatorFloor++;
                    int x = elevatorDuration/1000;
                    t.start();//start timer
                    this.pause(x, timer);
                    if (timer.countCompleted()) {//if timer finishes before elevator gets to next floor
                        System.out.println("ELEVATOR FAULT: Shutting Down Elevator...");//since hard fault, elevator shuts down
                        Thread.sleep(2000);
                        gui.setElevatorDoorStatus(SHUTDOWN, e.getPort());
                        gui.setElevatorStatus(STUCK, e.getPort());
                        requestStop();//request stop and shut down the elevator thread
                        return;
                    }
                }

                if(currentElevatorFloor == floorRequests.get(0)){//if elevator is on the request floor
                    System.out.println("port:"+e.getPort()+" Elevator has arrived at floor "+currentElevatorFloor);
                    gui.setDirection(IDLE, e.getPort());
                    gui.setCurrentFloor2(currentElevatorFloor, e.getPort());
                    hasArrived = true;
                    this.currentFloor = currentElevatorFloor;
                    e.setCurrentFloor(currentElevatorFloor);// update the elevator subsystem
                    gui.setArrivalStatus(e.getPort());
                    Thread.sleep(2000);
                    send("ArrivalNotification Floor " + currentElevatorFloor +" port: "+e.getPort());// notify scheduler about arrival
                    floorRequests.remove(0);//remove the request since it has been met
                    notifyAll();
                }
            }
        }

        Iterator<Integer> iter2 = floors.iterator();
        while(iter2.hasNext()){
            if(destinationRequests.isEmpty()){//if there are no more destinations to attend to, leave the loop
                break;
            }
            int i = iter2.next();
            System.out.println(destinationRequests.toString());
            while(!destinationRequests.isEmpty()) {
                if (currentElevatorFloor > destinationRequests.get(0)) {//if elevator is above destination floor, elevator moves down
                    Collections.reverse(floors);
                    Collections.reverse(floorRequests);
                    Time timer = new Time(duration);
                    Thread t = new Thread(timer);
                    System.out.println("port:" + e.getPort() + " Elevator is currently at floor " + currentElevatorFloor);
                    gui.setCurrentFloor2(currentElevatorFloor, e.getPort());
                    gui.setDirection(MOVING_DOWN, e.getPort());
                    this.state = State.MOVING_DOWN;
                    int nextFloor = currentElevatorFloor - 1;
                    e.setCurrentFloor(currentElevatorFloor);
                    Thread.sleep(2000);
                    //send("ArrivalNotification Floor " + currentElevatorFloor +" port: "+e.getPort());
                    System.out.println("port:" + e.getPort() + " Elevator is going DOWN to floor " + nextFloor);
                    currentElevatorFloor--;
                    int x = elevatorDuration/1000;
                    t.start();
                    this.pause(x, timer);
                    if (timer.countCompleted()) {
                        System.out.println("ELEVATOR FAULT: Shutting Down Elevator...");//since hard fault, elevator shuts down
                        Thread.sleep(2000);
                        gui.setElevatorDoorStatus(SHUTDOWN, e.getPort());
                        gui.setElevatorStatus(STUCK, e.getPort());
                        requestStop();
                        return;
                    }
                }
                if (currentElevatorFloor < destinationRequests.get(0)) {//if elevator is below destination floor, elevator moves up
                    Collections.sort(floors);
                    //Collections.sort(destinationRequests);
                    Time timer = new Time(duration);
                    Thread t = new Thread(timer);
                    System.out.println("port:" + e.getPort() + " Elevator is currently at floor " + currentElevatorFloor);
                    gui.setCurrentFloor2(currentElevatorFloor, e.getPort());
                    e.setCurrentFloor(currentElevatorFloor);
                    Thread.sleep(2000);
                    //send("ArrivalNotification Floor " + currentElevatorFloor +" port: "+e.getPort());
                    gui.setDirection(MOVING_UP, e.getPort());
                    this.state = State.MOVING_UP;
                    int nextFloor = currentElevatorFloor + 1;
                    System.out.println("port:" + e.getPort() + " Elevator is going UP to floor " + nextFloor);
                    currentElevatorFloor++;
                    int x = elevatorDuration/1000;
                    t.start();
                    this.pause(x, timer);
                    if (timer.countCompleted()) {
                        System.out.println("ELEVATOR FAULT: Shutting Down Elevator...");//since hard fault, elevator shuts down
                        Thread.sleep(2000);
                        gui.setElevatorDoorStatus(SHUTDOWN, e.getPort());
                        gui.setElevatorStatus(STUCK, e.getPort());
                        requestStop();
                        return;
                    }
                }

                if(currentElevatorFloor == destinationRequests.get(0)){// if elevator is on the destination floor
                    System.out.println("port:"+e.getPort()+" Elevator has arrived at floor "+currentElevatorFloor);
                    gui.setDirection(IDLE, e.getPort());
                    gui.setCurrentFloor2(currentElevatorFloor, e.getPort());
                    hasArrived = true;
                    this.state = State.STOPPED;
                    this.currentFloor = currentElevatorFloor;
                    e.setCurrentFloor(currentElevatorFloor);
                    gui.setArrivalStatus(e.getPort());
                    Thread.sleep(2000);
                    send("ArrivalNotification Floor " + currentElevatorFloor +" port: "+e.getPort());//notify scheduler about arrival
                    destinationRequests.remove(0);
                }
            }
        }

    }

    /**
     * this method is responisble for sending arrival notification packets to the scheduler
     * @param s packet to be sent
     * @throws IOException
     * @throws InterruptedException
     */

    public void send(String s) throws IOException, InterruptedException {
        String notificationData = s; //store the data gotten from the elevator
        //System.out.println(notificationData);
        byte elevatorStringArr[] = notificationData.getBytes();
        byte[] dataArray = new byte[elevatorStringArr.length];
        System.arraycopy(elevatorStringArr, 0, dataArray, 0, elevatorStringArr.length);
        //send packet back to scheduler
        DatagramPacket sendPacket = new DatagramPacket(dataArray, dataArray.length, InetAddress.getLocalHost(), 22);
        System.out.println("Sending data from elevator:");
        System.out.println("To: " + sendPacket.getAddress());
        System.out.println("host port: " + sendPacket.getPort());
        System.out.print("Data: ");
        System.out.println(new String(sendPacket.getData(),0,sendPacket.getLength()));
        try {
            sendSocket.send(sendPacket);//send notification packet to scheduler
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("sent\n");

        byte[] data = new byte[100];//create a byte array to receive the acknowledgement packet from the scheduler
        sendPacket = new DatagramPacket(data, data.length);
        sendSocket.receive(sendPacket);
        System.out.println("\nElevatorSubsystem: Packet received:");
        System.out.println("From host: " + sendPacket.getAddress());
        System.out.println("Host port: " + sendPacket.getPort());
        int len = sendPacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: " );
        // Form a String from the byte array.
        String received = new String(data,0,len);
        System.out.println(received + "\n");
        String arr[] = received.split(" ");// split the received packet into a String array
        System.out.println(Arrays.toString(arr));
        if(arr[0].equals("Door")){// if a door request is received from the scheduler, open or close doors
            openDoors(doorDuration);
            Thread.sleep(2000);//passenger wait time
            closeDoors(doorDuration);
            notifyAll();
            //receiveAndSend();

        }
        if(arr[0].equals("NA")){//if there are no requests for that particular floor
            System.out.println("no pending request");
            notifyAll();
        }
        return;
    }

    /**
     * this method waits for a paricular period of time, if the timer completes before the duration is finished,
     * the method returns
     * @param x duration to wait
     * @param timer checks duration time
     * @throws InterruptedException
     */
    public void pause(int x, Time timer) throws InterruptedException {
        for(int i = 0; i < x; i++){
            if(timer.countCompleted()){return;}
            Thread.sleep(1000);
        }
    }

    /**
     * this method is used to request a stop if an elevator fault erupts
     */
    public synchronized void requestStop() {
        stopRequested = true;
    }

    /**
     * checks the status of the stop request
     * @return true if stopRequested
     */
    private synchronized boolean stopRequested() {
        return stopRequested;
    }

    @Override
    public void run() {
        try {
            this.startMotor(e.getCurrentFloor(), timerDuration);
        } catch (InterruptedException | IOException interruptedException) {
            interruptedException.printStackTrace();
        }
        if(stopRequested){//if fault erupts in an elevator
            return;//shut down elevator thread
        }
    }


    public String toString() {
        return  "Floor: " + floor + "\n" + "\t State: " + state + "\n";
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        //Scheduler s = new Scheduler(1, 6, 22);
        UI g = new UI();
        ElevatorSubsystem e = new ElevatorSubsystem(28, 6, g);
        Elevator es = new Elevator(e, 6, g);
        //es.addFloorRequests(4);
        es.addFloorRequests(3);
        //es.addDestinationRequests(2);
        //es.addDestinationRequests(5);
        es.startMotor(5, 2000);
        //es.openDoors(2000);
/*
        ElevatorSubsystem e = new ElevatorSubsystem(28, 6);
        Elevator es = new Elevator(e, 6);
        Thread el = new Thread(e);
        Thread elevatorSystem = new Thread(e);
        el.start();
        Scheduler s = new Scheduler(2, 6, 22);
        Thread schedulerThread = new Thread(s);
        schedulerThread.start();
        FloorSubsystem f = new FloorSubsystem("elevatorInputs.txt", 21);
        Thread floorSubsystem = new Thread(f);
        floorSubsystem.start();
        /*
        //es.openDoors();
        es.addFloorRequests(4);
        es.addFloorRequests(3);
        es.addDestinationRequests(2);
        es.addDestinationRequests(5);
        es.startMotor(5);
        //walk(1000);
        //es.pause(7);
         */
    }

}

