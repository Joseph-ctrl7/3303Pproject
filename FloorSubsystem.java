import elevator.Elevator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import javax.security.auth.login.AccountException;

/**
 *
 */

/**
 * @author janyi
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
    //private

    public FloorSubsystem(Scheduler scheduler, String inputFile){
        this.scheduler = scheduler;
        this.inputFile = inputFile;
        floor = new Floor();
    }


    public synchronized void readInputFile() throws IOException, ParseException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile));
        String[] input = null;
        String result = null;
        for(String texts = bufferedReader.readLine(); texts != null; texts = bufferedReader.readLine()) {
            Scanner s = new Scanner(texts).useDelimiter(" ");
            this.convertTime(s.next());
            this.convertInfoToInt(s.next(), s.next(), s.next());
            //result = s.next();
            //input = new String[] {s.next(), s.next(), s.next(), s.next()};
        }
        //return result;
        notifyAll();
    }

    /**
     * this method converts the time gotten from the input to a timestamp
     * (it still shows the date for some reason ¯\_(ツ)_/¯)
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
        System.out.println(this.time);
        //return time;
        // return format.parse(dateString);
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
        System.out.println(this.floorNumber+" "+this.direction+" "+this.elevatorButton);
    }

    public void notifyFloor(Floor f){
        f.turnOnFloorLamps(currentElevatorFloor, direction);
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

        if(scheduler.askForElevatorData() == true){
            this.currentElevatorFloor = scheduler.getCurrentFloor();
            this.direction = scheduler.getDirection();
        }
        System.out.println("\nFloor Data--------------------------------------------------------------------");
        this.notifyFloor(floor);

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
