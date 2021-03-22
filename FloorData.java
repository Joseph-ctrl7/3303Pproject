import java.sql.Timestamp;

public class FloorData {

    private Timestamp time;
    private String direction;
    private int floorNumber;
    private Floor floor;
    private int currentElevatorFloor;
    private int elevatorButton;

    public FloorData(int floorNumber, String direction, int elevatorButton) {
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

}
