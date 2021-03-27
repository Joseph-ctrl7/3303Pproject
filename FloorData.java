import java.sql.Timestamp;

public class FloorData {

    private String time;
    private String direction;
    private int floorNumber;
    private Floor floor;
    private int currentElevatorFloor;
    private int elevatorButton;
    private boolean requestMet = false;

    public FloorData(String time, int floorNumber, String direction, int elevatorButton) {
        this.time = time;
        this.floorNumber = floorNumber;
        this.direction = direction;
        this.elevatorButton = elevatorButton;

    }

    public int getFloorNumber(){
        return floorNumber;
    }
    public int getElevatorButton(){
        return elevatorButton;
    }
    public String getDirection(){
        return direction;
    }
    public String getTime(){
        return time;
    }
    public void setRequestMet(boolean b){
        this.requestMet = b;
    }
    public boolean getRequestMet(){
        return requestMet;
    }

}
