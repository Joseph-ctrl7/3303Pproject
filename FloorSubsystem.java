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
    private int elevatorButton;
    private Scheduler scheduler;

    public FloorSubsystem(Scheduler scheduler){
        this.scheduler = scheduler;

    }


    public void readInputFile(String file) throws IOException, ParseException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
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

    @Override
    public void run() {
        //scheduler.receiveInfo();

    }

    public static void main(String[] args) throws IOException, ParseException {
        Scheduler scheduler = new Scheduler();
        FloorSubsystem f = new FloorSubsystem(scheduler);
        f.readInputFile("elevatorInputs.txt");
        //System.out.println(f.readInputFile("elevatorInputs.txt"));
    }
}
