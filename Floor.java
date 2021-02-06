import java.text.SimpleDateFormat;
import java.util.Date;

public class Floor {

    public enum directionLampState {
        UP, DOWN
    }

    ;

    public int floorNum;
    public int carButton;

    String timeStamp = new SimpleDateFormat("hh:mm:ss.mmm").format(new Date());


    @SuppressWarnings("unused")
    private int destFloor;

    public void setDestination(int destFloor) {
        this.destFloor = destFloor;
    }

    private boolean upLamp;

    private boolean downLamp;

    public void setUpLamp(boolean b) {
        upLamp = b;
    }

    public void setDownLamp(boolean b) {
        downLamp = b;
    }


    private directionLampState[] directionLamps;

    public void setDirectionLamp(int elevatorShaft, directionLampState state) {
        directionLamps[elevatorShaft] = state;
    }

    public directionLampState getDirectionLamp(int elevatorShaft) {
        return directionLamps[elevatorShaft];
    }

    public boolean getUpLamp() {
        return upLamp;
    }

    public boolean getDownLamp() {
        return downLamp;
    }

    /**
     * notifies the floor of the arrival of the elevator
     * @param currentElevatorFloor
     * @param direction
     */
    public synchronized void turnOnFloorLamps(int currentElevatorFloor, int direction){
        if(direction == 1){
            this.setUpLamp(true);
            System.out.println("Elevator is arriving from "+ currentElevatorFloor);
        }
        else{
            this.setDownLamp(true);
            System.out.println("Elevator is arriving from "+ currentElevatorFloor);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
