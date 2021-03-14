/**
 *
 */

/**
 * @author: Joseph Anyia, Amith Kumar Das Orko, Tolu Ajisola,
 *          Israel Okonkwo, Mehdi Khan
 *
 */
import java.text.SimpleDateFormat;
import java.util.Date;

public class Floor {

    public int floorNum;
    public int carButton;
    public boolean floorNotified;
    private boolean upLamp;

    private boolean downLamp;

    String timeStamp = new SimpleDateFormat("hh:mm:ss.mmm").format(new Date());


    @SuppressWarnings("unused")
    private int destFloor;

    public Floor() {
        floorNotified = false;
    }

    public void setDestination(int destFloor) {
        this.destFloor = destFloor;
    }


    public void setUpLamp(boolean b) {
        upLamp = b;
    }

    public void setDownLamp(boolean b) {
        downLamp = b;
    }


    public boolean getUpLamp() {
        return upLamp;
    }

    public boolean getDownLamp() {
        return downLamp;
    }


    /**
     * checks if floor has been notified on elevator arrival
     * @return true if elevator has arrived
     */
    public boolean checkIfNotified(){
        return floorNotified;
    }

    /**
     * notifies the floor of the arrival of the elevator
     * @param currentElevatorFloor
     * @param direction, boolean. 1 when going up, 0 when going down
     */
    public synchronized void turnOnFloorLamps(int currentElevatorFloor, int direction){
        if(direction == 1){         //checks if elevator is going up
            this.setUpLamp(true);
            System.out.println("Elevator has arrived from "+ currentElevatorFloor);
        }
        else{
            this.setDownLamp(true);
            System.out.println("Elevator has arrived from "+ currentElevatorFloor);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

