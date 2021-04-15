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
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Scheduler implements Runnable {

    private String time;
    private boolean dataReceived = false; // if any piece of data either from elevator or floor subsystem is received
    private boolean elevatorInfoReceived = false; //info from ElevatorSubsystem
    private boolean floorInfoReceived = false; //info from FloorSubsystem
    private boolean hasArrived = false;
    private int direction;
    private int floorNumber;
    private int elevatorButton;
    private int currentFloor;
    private int numberOfElevators;
    private int numberOfFloors;
    private ElevatorSubsystem subsystem;
    private ElevatorSubsystem chosenElevator;
    private int bestPort;
    private Elevator elevator;
    private SchedulerStates state;
    private boolean floorNotified = false;
    private boolean requestsEmpty = false;
    public ArrayList numbers;
    private Map<Integer, ElevatorSubsystem> elevators;
    private Map<Integer, FloorData> floorRequests;
    private Map<Integer, FloorData> destinationRequests;
    private List<FloorData> requests;
    private ArrayList<FloorData> pendingRequests;
    private long creationTime, startTime, completedTime;

    private int portNumber;
    private DatagramSocket sendSocket, receiveSocket;
    private DatagramSocket socket;
    private InetAddress local;
    private DatagramPacket receivePacket, sendPacket;
    private DatagramPacket ackPacket;
    InetAddress address;
    int port;
    int elevatorPort;


    private Map<String, Integer> inputInfo;
    private Map<String, Integer> subsystemData;
    private List<ElevatorData> elevatorData;
    private boolean schedulerNotified = false;


    public enum SchedulerStates {LISTENING, RECEIVED, ELEVATORINFO, FLOORINFO}


    public Scheduler(int numberOfElevators, int numberOfFloors) {
        this.numberOfElevators = numberOfElevators;
        this.numberOfFloors = numberOfFloors;
        elevators = new HashMap();
        floorRequests = new HashMap<>();
        destinationRequests = new HashMap<>();
        inputInfo = new HashMap();
        subsystemData = new HashMap();
        requests = new ArrayList<>();
        elevatorData = new ArrayList<>();
        pendingRequests = new ArrayList<>();
        this.elevator = elevator;
        state = SchedulerStates.LISTENING;
        numbers = new ArrayList();
        for (int i=0; i<numberOfFloors; i++){
            numbers.add(i);
        }
    }
    public Scheduler(int numberOfElevators, int numberOfFloors, int port) throws SocketException {
        this.numberOfElevators = numberOfElevators;
        this.numberOfFloors = numberOfFloors;
        this.port = port;
        elevators = new HashMap();
        floorRequests = new HashMap<>();
        destinationRequests = new HashMap<>();
        inputInfo = new HashMap();
        subsystemData = new HashMap();
        requests = new ArrayList<>();
        elevatorData = new ArrayList<>();
        pendingRequests = new ArrayList<>();
        this.elevator = elevator;
        state = SchedulerStates.LISTENING;
        receiveSocket = new DatagramSocket(port);
        sendSocket = new DatagramSocket();

        numbers = new ArrayList();
        for (int i=0; i<numberOfFloors; i++){
            numbers.add(i);
        }

    }

    /**
     * this method is used to receive and process packets from the elevator, elevatorSubsystem and the floorSubsystem
     * @throws UnknownHostException
     */
    public void receiveAndProcessPacket() throws UnknownHostException {
        try {
            //receiveSocket = new DatagramSocket(portNumber); //creates a socket bound to port portNumber
            byte[] data = new byte[40];
            System.out.println(Thread.currentThread().getName() + " is running on port: " + port);
            local = InetAddress.getLocalHost(); //Creates inetaddress containing localhost
            byte[] ackData = "ack".getBytes(); //Defines ack byte array
            byte[] negAck = "NA".getBytes();
            byte[] doorRequest = "Door".getBytes();
            receivePacket = new DatagramPacket(data, data.length); //create the packet to recieve into
            int len = receivePacket.getLength();
            while (requestsEmpty != true) { //loop infinitely
                receivePacket = new DatagramPacket(data, data.length);
                receiveSocket.receive(receivePacket);//Recieve a packet
                System.out.println("\nScheduler: Packet received:");
                System.out.println("From host: " + receivePacket.getAddress());
                System.out.println("Host port: " + receivePacket.getPort());
                len = receivePacket.getLength();
                System.out.println("Length: " + len);
                // Form a String from the byte array
                String received = new String(data, 0, len);
                System.out.println("Containing: "+received + "\n");

                String arr[] = received.split(" ");

                if(arr[0].equals("ArrivalNotification")) { //If the receivedPacket was an arrival notification
                    for (ElevatorData e : elevatorData){
                        if(Integer.parseInt(arr[4]) == e.getPort()){
                            e.setLocation(Integer.parseInt(arr[2])); //update elevator data current location
                        }
                    }
                    if (floorRequests.isEmpty()) { //If there are no floor requests to forward
                        if(destinationRequests.containsKey(Integer.parseInt(arr[2]))){//check if destinationRequests contains the specified destination
                            hasArrived = true;
                            ackPacket = new DatagramPacket(doorRequest, doorRequest.length, local, receivePacket.getPort());
                            receiveSocket.send(ackPacket);//if true send a door request
                            System.out.println("Door request sent for floor: "+Integer.parseInt(arr[2]));
                            destinationRequests.remove(Integer.parseInt(arr[2]));
                            if(destinationRequests.isEmpty()){
                                System.out.println("done");
                                requestsEmpty = true;
                            }
                        }
                    } else {
                        if(floorRequests.containsKey(Integer.parseInt(arr[2]))){//if floorRequests contains the specified request
                            hasArrived = true;
                            ackPacket = new DatagramPacket(doorRequest, doorRequest.length, local, receivePacket.getPort());
                            receiveSocket.send(ackPacket);//send a door request
                            System.out.println("Door request sent for floor: " + Integer.parseInt(arr[2]));
                            floorRequests.remove(Integer.parseInt(arr[2]));//remove the current floor request since it has been met
                        }
                        else{
                            ackPacket = new DatagramPacket(negAck, negAck.length, local, receivePacket.getPort());
                            receiveSocket.send(ackPacket);//send a negative acknowledgement showing that there are no requests to send
                            System.out.print("sent");
                        }

                    }
                } else { //if the receivedPacket was not a request, it must have been data
                    if(arr[0].equals("FLOOR")) {
                        //Thread.sleep(2000);
                        this.receiveInfo(arr[1], arr[2], arr[3], arr[4]);//store the received floor data
                        FloorData f = new FloorData(arr[1], Integer.parseInt(arr[2]), arr[3], Integer.parseInt(arr[4]));
                        requests.add(f);
                        floorRequests.put(f.getFloorNumber(), f);
                        destinationRequests.put(f.getElevatorButton(), f); //update requests
                        int port = this.getBestElevator(Integer.parseInt(arr[2]));//get the best elevator port to send the request to
                        if(port != -1) {//if a suitable elevator port is found
                            receivePacket.setPort(port);
                            receiveSocket.send(receivePacket);//send request to specified elevator
                            requests.remove(f);
                        }
                        else{// if there are no suitable elevators at the moment, add them to the pending requests list
                            pendingRequests.add(new FloorData(arr[1], Integer.parseInt(arr[2]), arr[3], Integer.parseInt(arr[4])));
                        }

                    }
                    if(arr[0].equals("ElevatorData")){//receives locations of all elevators
                        elevatorData.add(new ElevatorData(Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), arr[1]));
                        System.out.println(elevatorData.size());
                    }
                }

                hasArrived = false;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }



    /**
     * returns the port of the closet elevator that can attend to request
     * @param pickupFloor floor that the passenger is currently on
     * @return the port of the closet elevator
     */
    private synchronized int getBestElevator(int pickupFloor) {
        System.out.println("Searching for elevator........");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        int location = -1;
        int minDifferece = 10;
        for (ElevatorData e: elevatorData) {
            if (e.getLocation() == pickupFloor) {//if elevator location is at a request
                minDifferece = 0;
                bestPort = e.getPort();
                location = e.getLocation();
            }
            else  {
                if(!(e.getState().equals("IDLE"))){// if elevator is currently in use
                    System.out.println("No elevators close by for floor "+pickupFloor+" request");
                    return -1;
                }
                if (e.getState().equals("IDLE")) {
                    if(e.getLocation() == pickupFloor){// if the elevator is idle and at a request floor
                        bestPort = e.getPort();
                        location = e.getLocation();
                    }
                    int tempDifference = Math.abs(e.getLocation() - pickupFloor);
                    if (tempDifference < minDifferece) {
                        minDifferece = tempDifference;
                        bestPort = e.getPort();
                        location = e.getLocation();
                    }
                }
                if (e.getLocation() > pickupFloor && e.getState().equals("DOWN")) {// if an elevator is above a request and moving down
                    int tempDifference = e.getLocation() - pickupFloor;
                    if (tempDifference < minDifferece) {
                        minDifferece = tempDifference;
                        bestPort = e.getPort();
                        location = e.getLocation();
                    }
                }
                if (e.getLocation() < pickupFloor && e.getState().equals("UP")) {//if an elevator is below a request floor and moving up
                    int tempDifference = pickupFloor - e.getLocation();
                    if (tempDifference < minDifferece) {
                        minDifferece = tempDifference;
                        bestPort = e.getPort();
                        location = e.getLocation();
                    }
                }
            }
        }
        System.out.println("elevator available \n port: "+bestPort);
        System.out.println("currentFloor: "+location);
        return bestPort;
    }



    /**
     * this method gets the location of all the elevators in the building
     */
    public void getLocations(){
        for(Map.Entry<Integer, ElevatorSubsystem> e : elevators.entrySet()){
            System.out.println( e.getValue().getCurrentFloor());
        }
    }

    /**
     * this method gets the specific location of an elevator
     * @return the floor the elevator is currently located
     */
    public int getSpecificLocation(){
        int i = 0;
        Collections.shuffle(numbers);
        return (int) numbers.get(i);
    }


    //getters and setters for the fields in Scheduler class ---------------------------------------------------------------------------
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
    public SchedulerStates getState() {
        return state;
    }

    public int getPort(){
        return port;
    }
    //-----------------------------------------------------------------------------------------------------------------------------------------
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
        while (!schedulerNotified) {
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
    public synchronized void receiveInfo(String time, String floorNumber, String direction, String elevatorButton) {
        this.time = time;
        this.floorNumber = Integer.parseInt(floorNumber);
        this.elevatorButton = Integer.parseInt(elevatorButton);
        if(direction.equals("Up")){
            this.direction = 1;
        }else{
            this.direction = 0;
        }

        inputInfo.put("floorNumber", this.floorNumber);
        inputInfo.put("direction",this.direction);
        inputInfo.put("elevatorButton", this.elevatorButton);
        requests.add(new FloorData(this.time, this.floorNumber, direction, this.elevatorButton));
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
    public synchronized void receiveElevatorData(String currentFloor, String direction) {
        this.currentFloor = Integer.parseInt(currentFloor);
        this.direction = Integer.parseInt(direction);

        subsystemData.put("currentFloor", this.currentFloor);
        subsystemData.put("direction", this.direction);
        dataReceived = true;
        elevatorInfoReceived = true;

        while (subsystemData.isEmpty()) {  //waits until elevator data hashmap is updated
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        System.out.println("\nScheduler data from ElevatorSubsystem---------------------------------------------");

        if(schedulerNotified){
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
        while(subsystemData.isEmpty()){
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
        System.out.println("starting state machine");
        switch (state){
            case LISTENING:// scheduler listens for info from both elevator and floor subsystems
                System.out.println("received");
                if(this.dataReceived == true){
                    state = SchedulerStates.RECEIVED;
                    this.dataReceived = false;
                }
                break;

            case RECEIVED:
                if(this.floorInfoReceived == true){
                    System.out.println("floorInfo");
                    state = SchedulerStates.FLOORINFO;
                    this.floorInfoReceived = false; //clear flag
                }

                if(this.elevatorInfoReceived == true){
                    System.out.println("elevator info");
                    state = SchedulerStates.ELEVATORINFO;
                    this.elevatorInfoReceived = false; //clear flag
                }
                break;

            case FLOORINFO:
                System.out.println("Going back to listen for packets..................");
                state = SchedulerStates.LISTENING; //returns back to listening for info
                break;
            case ELEVATORINFO:
                checkData(); //checks to see if the scheduler has been notified by the ElevatorSubsystem(elevator subsystem thread will then start in order to receive scheduler info)
                if(!subsystemData.isEmpty()){
                    System.out.println("Going back to listen for packets..................");
                    state = SchedulerStates.LISTENING;
                }
                break;
        }
    }


    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            receiveAndProcessPacket();//continually receive and process packet
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        long finishTime = System.currentTimeMillis() - startTime;
        System.out.println("Elapsed Scheduler Time: "+finishTime);
    }


    public static void main(String[] args) throws SocketException, UnknownHostException {
        Scheduler s = new Scheduler(4, 22, 22);
        Thread schedulerThread = new Thread(s);
        schedulerThread.start();
        //FloorSubsystem f = new FloorSubsystem("elevatorInputs.txt", 21);
        //Thread floorSubsystem = new Thread(f);
        //floorSubsystem.start();
        //ElevatorSubsystem es = new ElevatorSubsystem(28, 6);
        //Thread el = new Thread(es);
        //el.start();
        //InetAddress address2 = InetAddress.getByName("208.67.222.222");
        // System.out.println(address2.getHostName());
        //String j = "hello world";
        //String arr[] = j.split(" ");
        //System.out.println(Arrays.toString(arr));
        //long startTime = System.currentTimeMillis();
        //System.out.println(startTime);
        //s.getBestElevator(2);
        //long endTime = System.currentTimeMillis();
        //System.out.println(endTime);
        //System.out.println(endTime - startTime);
    }
}

