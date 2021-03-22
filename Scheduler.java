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

public class Scheduler implements Runnable {

    private String time;
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
    private ArrayList<FloorData> pendingRequests;

    private Queue<DatagramPacket> queue;
    private int portNumber;
    private DatagramSocket sendSocket, receiveSocket;
    private DatagramSocket socket;
    private InetAddress local;
    private DatagramPacket receivePacket, sendPacket;
    private DatagramPacket ackPacket;
    InetAddress address;
    int elevatorPort;

    Thread sThread;

    private Map<String, Integer> inputInfo;
    private Map<String, Integer> elevatorData;
    private boolean schedulerNotified = false;


    public enum SchedulerStates {LISTENING, RECEIVED, ELEVATORINFO, FLOORINFO}


    public Scheduler(int numberOfElevators, int numberOfFloors) throws SocketException {
        this.numberOfElevators = numberOfElevators;
        this.numberOfFloors = numberOfFloors;
        elevators = new HashMap();
        inputInfo = new HashMap();
        elevatorData = new HashMap();
        this.elevator = elevator;
        state = SchedulerStates.LISTENING;
        receiveSocket = new DatagramSocket(22);
        sendSocket = new DatagramSocket();

        numbers = new ArrayList();
        for (int i=0; i<numberOfFloors; i++){
            numbers.add(i);
        }

        //create elevators and assign their ports
        elevatorPort = 27;
        for(int i = 1; i < numberOfElevators+1; i++){
            elevatorPort = elevatorPort+i;
            subsystem = new ElevatorSubsystem(elevatorPort,this);
            subsystem.setCurrentFloor(this.getSpecificLocation());// assign a location to an elevator
            elevators.put(i, subsystem);
        }

    }

    public synchronized void sendReceivePacket() throws SocketException, UnknownHostException {
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

        //receive datagramPacket from FloorSubsystem
        try {
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.print("\nIO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("\nScheduler: Packet received from floorSubsystem:");
        System.out.println("From host: " + receivePacket.getAddress());
        System.out.println("Host port: " + receivePacket.getPort());
        int len = receivePacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: " );
        // Form a String from the byte array.
        String received = new String(data,0,len);
        System.out.println(received + "\n");
        String arr[] = received.split(" ");
        this.receiveInfo(arr[0], arr[1], arr[2], arr[3]);
        startSchedulerSM();





        int ePort = this.getBestElevator(floorNumber);
        //send received packet to elevatorSubsystem
        sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), ePort);

        System.out.println("\nScheduler: Sending packet:");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("Destination host port: " + ePort);
        int length = sendPacket.getLength();
        System.out.println("Length: " + length);
        System.out.print("Containing: ");
        System.out.println(new String(sendPacket.getData(),0,length)); // or could print "s"

        // Send the datagram packet to the server via the send/receive socket.
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Scheduler: Packet sent.\n");




        //receive from elevatorSubsystem
        byte elevatorData[] = new byte[100];
        receivePacket = new DatagramPacket(elevatorData, elevatorData.length);
        //System.out.println("\nScheduler: LISTENING");
        try {
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.print("\nIO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("\nScheduler: Packet received from elevatorSubsystem:");
        System.out.println("From host: " + receivePacket.getAddress());
        System.out.println("Host port: " + receivePacket.getPort());
        len = receivePacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: " );
        // Form a String from the byte array.
        received = new String(elevatorData,0,len);
        System.out.println(received + "\n");
        arr = received.split(" ");

        String temp[] = new String[2];
        System.arraycopy(arr, 0, temp, 0, temp.length);

        this.receiveElevatorData(temp[0], temp[1]);
        startSchedulerSM();




        //send to floorSubsystem
        sendPacket = new DatagramPacket(elevatorData, receivePacket.getLength(), receivePacket.getAddress(), 21);

        System.out.println("\nScheduler: Sending packet:");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("Destination host port: " + sendPacket.getPort());
        length = sendPacket.getLength();
        System.out.println("Length: " + length);
        System.out.print("Containing: ");
        System.out.println(new String(sendPacket.getData(),0,length)); // or could print "s"

        // Send the datagram packet to the server via the send/receive socket.
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Scheduler: Packet sent.\n");

        sendSocket.close();
        receiveSocket.close();

    }



    /**
     * returns the port of the closet elevator that can attend to request
     * @param pickupFloor floor that the passenger is currently on
     * @return the port of the closet elevator
     */
    private int getBestElevator(int pickupFloor) {
        System.out.println("Searching for elevator........");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ElevatorSubsystem chosenElevator = null;
        int minDifferece = 10;
        for (int i = 1; i < numberOfElevators+1; i++) {
            if (elevators.get(i).getCurrentFloor() == pickupFloor) {
                minDifferece = 0;
                chosenElevator = elevators.get(i);
            }
            else {
                if (elevators.get(i).state.toString().equals("IDLE")) {
                    int tempDifference = Math.abs(elevators.get(i).getCurrentFloor() - pickupFloor);
                    if (tempDifference < minDifferece) {
                        minDifferece = tempDifference;
                        chosenElevator = elevators.get(i);
                    }
                }
                if (elevators.get(i).getCurrentFloor() > pickupFloor && elevators.get(i).state.toString().equals("DOWN")) {
                    int tempDifference = elevators.get(i).getCurrentFloor() - pickupFloor;
                    if (tempDifference < minDifferece) {
                        minDifferece = tempDifference;
                        chosenElevator = elevators.get(i);
                    }
                }
                if (elevators.get(i).getCurrentFloor() < pickupFloor && elevators.get(i).state.toString().equals("UP")) {
                    System.out.println(i);
                    int tempDifference = pickupFloor - elevators.get(i).getCurrentFloor();
                    if (tempDifference < minDifferece) {
                        minDifferece = tempDifference;
                        chosenElevator = elevators.get(i);
                    }
                }
            }
        }
        System.out.println("elevator available \n port: "+chosenElevator.getPort());
        System.out.println("currentFloor: "+chosenElevator.getCurrentFloor());
        Thread elevatorSystem = new Thread(chosenElevator);
        elevatorSystem.start();
        return chosenElevator.getPort();
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
       // pendingRequests.add(new FloorData());
        this.time = time;
        this.floorNumber = Integer.parseInt(floorNumber);
        this.elevatorButton = 5; //Integer.parseInt(elevatorButton, 10);
        if(direction.equals("Up")){
            this.direction = 1;
        }else{
            this.direction = 0;
        }

        inputInfo.put("floorNumber", this.floorNumber);
        inputInfo.put("direction",this.direction);
        inputInfo.put("elevatorButton", this.elevatorButton);
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
        this.direction = 1;//Integer.parseInt(direction);

        elevatorData.put("currentFloor", this.currentFloor);
        elevatorData.put("direction", this.direction);
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
        System.out.println("starting state machine");
        switch (state){
            case LISTENING:// scheduler listens for info from both elevator and floor subsystems
                System.out.println("received");
                if(this.dataReceived == true){
                    state = SchedulerStates.RECEIVED;
                    this.dataReceived = false;
                }

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

            case FLOORINFO:
                System.out.println("Going back to listen for packets..................");
                state = SchedulerStates.LISTENING; //returns back to listening for info
                break;
            case ELEVATORINFO:
                checkData(); //checks to see if the scheduler has been notified by the ElevatorSubsystem(elevator subsystem thread will then start in order to receive scheduler info)
                if(!elevatorData.isEmpty()){
                    System.out.println("Going back to listen for packets..................");
                    state = SchedulerStates.LISTENING;
                }
                break;
        }
    }


    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            sendReceivePacket();
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws SocketException, UnknownHostException {
        Scheduler s = new Scheduler(2, 6);
        //InetAddress address2 = InetAddress.getByName("208.67.222.222");
       // System.out.println(address2.getHostName());
        //String j = "hello world";
        //String arr[] = j.split(" ");
        //System.out.println(Arrays.toString(arr));
        s.getBestElevator(2);
    }
}

