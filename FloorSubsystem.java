
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.security.auth.login.AccountException;

/**
 * @author: Joseph Anyia, Amith Kumar Das Orko, Tolu Ajisola,
 *          Israel Okonkwo, Mehdi Khan
 *
 */
public class FloorSubsystem implements Runnable {

    private boolean buttonPressed = false;
    private boolean lampOn = false;
    private int downButton = 0;
    private int upButton = 1;
    private Timestamp time;
    private String timeString;
    private int direction;
    private int floorNumber;
    private Floor floor;
    private int currentElevatorFloor;
    private int elevatorButton;
    private Scheduler scheduler;
    private String inputFile;
    private Map<String, Integer> inputInfo;
    private ArrayList<Integer> elevatorInfo;
    private ArrayList<FloorData> floorData;
    private boolean floorSubsystemNotified = false;
    private boolean dataValidated = false;
    private String dataString;
    private int port;

    DatagramSocket sendReceiveSocket;
    DatagramPacket packetToSend;
    DatagramPacket receivedPacket;
    DatagramPacket replyPacket;


    public FloorSubsystem(String inputFile, int port){
        this.scheduler = scheduler;
        this.inputFile = inputFile;
        inputInfo = new HashMap<String, Integer>();
        elevatorInfo = new ArrayList<>();
        floorData = new ArrayList<>();
        floor = new Floor();
        this.port = port;

        try {
            sendReceiveSocket = new DatagramSocket(port);
        } catch (SocketException se) {   // Can't create the socket.
            se.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * this method is responsible for sending and receiving data packets from the scheduler
     * @throws IOException
     */
    public synchronized void sendAndReceiveInfoInPacket() throws IOException, InterruptedException {
        for(FloorData f: floorData) {
            dataString = f.getTime();
            dataString ="FLOOR "+ dataString +" "+f.getFloorNumber()+" "+f.getDirection()+" "+f.getElevatorButton();
            byte[] dataStringArr = dataString.getBytes();
            byte[] dataArray;
            if(f.getDirection().equals("Down")){
                dataArray = new byte[25];
            }else {
                dataArray = new byte[23];
            }
            System.arraycopy(dataStringArr, 0, dataArray, 0, dataStringArr.length);
            System.out.println(Arrays.toString(dataArray));


            packetToSend = new DatagramPacket(dataArray, dataArray.length, InetAddress.getLocalHost(), 22);//create a new packet to send data

            System.out.println("\nFloorSubsystem: Sending packet:");
            System.out.println("To Scheduler: " + packetToSend.getAddress());
            System.out.println("Destination host port: " + packetToSend.getPort());
            int len = packetToSend.getLength();
            System.out.println("Length: " + len);
            System.out.print("Containing: String - ");
            System.out.println(new String(packetToSend.getData(), 0, len)); // or could print "s"
            System.out.println("Bytes - " + Arrays.toString(packetToSend.getData()));


            sendReceiveSocket.send(packetToSend);//send packet
            System.out.println(new String(packetToSend.getData(), 0, packetToSend.getLength()));
            Thread.sleep(5000);

        }


        //receive data from scheduler
        byte elevatorData[] = new byte[100];
        receivedPacket = new DatagramPacket(elevatorData, elevatorData.length);// create a new packet to receive data from scheduler

        try {
            sendReceiveSocket.receive(receivedPacket);
        } catch (IOException e) {
            System.out.print("\nIO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("\nFloorSubsystem: Packet received:");
        System.out.println("From host: " + receivedPacket.getAddress());
        System.out.println("Host port: " + receivedPacket.getPort());
        int len = receivedPacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: " );
        // Form a String from the byte array.
        String received = new String(elevatorData,0,len);
        System.out.println(received + "\n");

        String arr[] = received.split(" "); //split the string received from packet and store it in an array

        this.receiveSchedulerInfo(arr[0], arr[1]); //process the data in the array

        sendReceiveSocket.close();

    }


    //getters and setters for the fields in the floorSubsystem class---------------------------------------------------------------------
    public int getCurrentFloor() {
        return this.currentElevatorFloor;
    }

    public void setCurrentFloor(int floor) {
        this.currentElevatorFloor = floor;
    }

    public Timestamp getTime() {
        return this.time;
    }

    public int getDirection() {
        return this.direction;
    }

    public int getElevatorButton() {
        return this.elevatorButton;
    }

    public int getFloorNumber() {
        return this.floorNumber;
    }

    public String getDataString(){
        return dataString;
    }

    public int getPort(){
        return port;
    }
//-------------------------------------------------------------------------------------------------------------------------------------

    /**
     * checks if data about the elevator received from scheduler is empty
     * @return true if hashmap is empty
     */
    public boolean checkIfEmpty() {
        if(elevatorInfo.isEmpty()) {
            return true;
        }
        return false;
    }



    /**
     * reads in the input text file
     * @throws IOException
     * @throws ParseException
     */
    public synchronized void readInputFile() throws IOException, ParseException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
        String[] input = null;
        String result = null;
        for(String texts = bufferedReader.readLine(); texts != null; texts = bufferedReader.readLine()) {
            Scanner s = new Scanner(texts).useDelimiter(" ");
            this.convertTime(s.next());
            this.convertInfoToInt(s.next(), s.next(), s.next());

        }

        notifyAll();
    }


    /**
     * checks if input text files where stored successfully
     * @return false if its stored successfully
     */
    public boolean isEmpty() {
        if (inputInfo.isEmpty()) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * this method converts the time gotten from the input to a timestamp
     * (it still shows the date for some reason )
     *
     * @param dateString
     * @return
     * @throws ParseException
     */
    private void convertTime(String dateString) throws ParseException {
        timeString = dateString;
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        Date date1 = format.parse(dateString);
        Timestamp ts = new Timestamp(date1.getTime());
        this.time = ts;
        System.out.println("\nTime: "+this.time);

    }

    /**
     * this method converts the rest of the input to their respective integers
     * @param floorNumber
     * @param direction
     * @param elevatorButton
     */
    public void convertInfoToInt (String floorNumber, String direction, String elevatorButton){
        //dataString = dataString +" "+floorNumber+" "+direction+" "+ elevatorButton;

        this.floorNumber = Integer.parseInt(floorNumber);
        this.elevatorButton = Integer.parseInt(elevatorButton);
        if(direction.equals("Up")){
            this.direction = 1;
        }else{
            this.direction = 0;
        }
        inputInfo.put("floorNumber", this.floorNumber);
        inputInfo.put("direction", this.direction);
        inputInfo.put("elevatorButton", this.elevatorButton);
        floorData.add(new FloorData(this. timeString,this.floorNumber, direction, this.elevatorButton));
        System.out.println("Floor: "+this.floorNumber+"\n"+"Floor Button: "+this.direction+"\n"+"Car Button: "+this.elevatorButton);

    }
    public void printInfo(){
        /*for (String name: inputInfo.keySet()){
            String key = name;
            String value = inputInfo.get(name).toString();
            System.out.println(key + " " + value);
        }*/
        System.out.println(floorData);
    }

    /**
     * notifies floor about the location of the elevator
     * @param f floor to be notified
     */
    public synchronized void notifyFloor(Floor f){
        f.turnOnFloorLamps(currentElevatorFloor, direction);
    }


    /**
     * receives information from the scheduler
     */
    public void receiveSchedulerInfo(String currentElevatorFloor, String direction) {
        this.currentElevatorFloor = Integer.parseInt(currentElevatorFloor);
        this.direction = Integer.parseInt(direction);

        elevatorInfo.add(this.currentElevatorFloor);
        elevatorInfo.add(this.direction);

        floorSubsystemNotified = true;

    }


    @Override
    public void run() {
        try {
            this.readInputFile();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            sendAndReceiveInfoInPacket();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        this.notifyFloor(floor);
        scheduler.notifyAboutFloor(true); //tell scheduler that floor was successfully notified

    }

    public static void main(String[] args) throws IOException, ParseException {
        //Elevator elevator = new Elevator();
        //Scheduler scheduler = new Scheduler(2, 6, 22);
        FloorSubsystem f = new FloorSubsystem("elevatorInputs.txt", 21);
        //ElevatorSubsystem e = new ElevatorSubsystem();
        //f.readInputFile();
        //f.printInfo();
        //System.out.println(f.getDataString());
        //f.sendInfoInPacket();

        Thread floorSubsystem = new Thread(f);
        //Thread schedulerThread = new Thread(scheduler);
        //Thread elevatorSystem = new Thread(e);
        floorSubsystem.start();
        //schedulerThread.start();
        //elevatorSystem.start();
        //System.out.println(f.readInputFile("elevatorInputs.txt"));
    }
}

