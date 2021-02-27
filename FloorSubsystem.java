
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

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
    private int direction;
    private int floorNumber;
    private Floor floor;
    private int currentElevatorFloor;
    private int elevatorButton;
    private Scheduler scheduler;
    private String inputFile;
    private Map<String, Integer> inputInfo;
    private ArrayList<Integer> elevatorInfo;
    private boolean floorSubsystemNotified = false;


    public FloorSubsystem(Scheduler scheduler, String inputFile){
        this.scheduler = scheduler;
        this.inputFile = inputFile;
        inputInfo = new HashMap<String, Integer>();
        elevatorInfo = new ArrayList<>();
        floor = new Floor();
    }


    public int getCurrentFloor() {
        return this.currentElevatorFloor;
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
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss");
        Date date1 = format.parse(dateString);
        Timestamp ts = new Timestamp(date1.getTime());
        this.time = ts;
        System.out.println("Time: "+this.time);

    }

    /**
     * this method converts the rest of the input to their respective integers
     * @param floorNumber
     * @param direction
     * @param elevatorButton
     */
    public void convertInfoToInt (String floorNumber, String direction, String elevatorButton){
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
        System.out.println("Floor: "+this.floorNumber+"\n"+"Floor Button: "+this.direction+"\n"+"Car Button: "+this.elevatorButton);
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
    public void receiveSchedulerInfo() {
        this.currentElevatorFloor = scheduler.getCurrentFloor();
        this.direction = scheduler.getDirection();

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

        scheduler.receiveInfo(time, floorNumber, direction, elevatorButton);

        if(scheduler.askForElevatorData() == true){ //if elevator data in scheduler is available
            this.receiveSchedulerInfo();
        }
        System.out.println("\nFloor Data from Scheduler--------------------------------------------------------------------");
        this.notifyFloor(floor);
        scheduler.notifyAboutFloor(true); //tell scheduler that floor was successfully notified

    }

    public static void main(String[] args) throws IOException, ParseException {
        Elevator elevator = new Elevator();
        Scheduler scheduler = new Scheduler(elevator);
        FloorSubsystem f = new FloorSubsystem(scheduler, "elevatorInputs.txt");
        ElevatorSubsystem e = new ElevatorSubsystem(scheduler);
        //f.readInputFile();

        Thread floorSubsystem = new Thread(f);
        Thread schedulerThread = new Thread(scheduler);
        Thread elevatorSystem = new Thread(e);
        floorSubsystem.start();
        schedulerThread.start();
        elevatorSystem.start();
        //System.out.println(f.readInputFile("elevatorInputs.txt"));
    }
}

