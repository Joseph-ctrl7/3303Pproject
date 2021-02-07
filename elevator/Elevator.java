
package elevator;

import java.util.HashSet;
import java.util.Set;

public class Elevator {
    public enum State {
        MOVING_UP, MOVING_DOWN, STOPPED
    }

    private int floor;
    private State state;
    private boolean buttonPressed;
    private boolean directionLamp;
    private boolean elevatorLamp;
    private boolean startMotor;
    private boolean openDoors;
    private boolean elevatorNotified;

    @SuppressWarnings({ "unchecked" })
    private Set<Integer> pressedButtons = (Set<Integer>) new HashSet<Integer>();
    private int directionButton;
    private int floorButton;

    public Elevator() {

        state = State.STOPPED;
        buttonPressed = false;
        directionLamp = false;
        elevatorLamp = false;
        startMotor = false;
        openDoors = false;
        elevatorNotified = false;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
        pressedButtons.remove(floor);
    }

    public void setDirectionButton(int directionButton){
        this.directionButton = directionButton;
    }

    public void setElevatorButton(int floorButton){
        this.floorButton = floorButton;
    }

    public State getState() {
        return state;
    }

    public void setState(State s) {
        state = s;
    }
    public boolean isMoving() {
        return state == State.MOVING_UP || state == State.MOVING_DOWN;
    }

    public void buttonPressed(int i) {
        pressedButtons.add(i);
    }

    public Set<Integer> getButtons() {
        return pressedButtons;
    }

    /**
     * This method turns on the lamps for the destination and direction
     * @param destination
     * @param direction
     */
    public synchronized void turnOnLamps(int destination, int direction){
        this.directionLamp = true;
        this.elevatorLamp = true;
        if(direction == 1){
            System.out.println("lamps "+destination + " and UP are on");
        }
        else{
            System.out.println("lamps "+destination + " and DOWN are on");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method turns off the lamps for the destination and direction
     * @param destination
     * @param direction
     */
    public void turnOffLamps(int destination, int direction){
        this.directionLamp = false;
        this.elevatorLamp = false;
        System.out.println(destination + " and "+ direction+" lamps are off");

    }

    /**
     * This method starts the motor to move the elevator
     * @param destination
     * @param direction
     * @param currentElevatorFloor
     * @param floorNumber
     */
    public synchronized void startMotor(int destination, int direction, int currentElevatorFloor, int floorNumber) {
        System.out.println("Elevator is currently at floor "+currentElevatorFloor);
        if(currentElevatorFloor == floorNumber){       //if the elevator is that the right floor
            System.out.println("opening doors");
        }
        if (direction == 1){    //if the destination direction is up
            if(currentElevatorFloor > floorNumber) {
                this.state = State.MOVING_DOWN;
                System.out.println("elevator is going DOWN");
            }
            if(currentElevatorFloor < floorNumber){
                this.state = State.MOVING_UP;
                System.out.println("elevator is going UP");
            }
        }
        else{   //if the destination direction is down
            if(currentElevatorFloor < floorNumber) {
                this.state = State.MOVING_UP;
                System.out.println("elevator is going UP");
            }
            if(currentElevatorFloor > floorNumber){
                this.state = State.MOVING_DOWN;
                System.out.println("elevator is going DOWN");
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public String toString() {
        return  "Floor: " + floor + "\n" + "\t State: " + state + "\n";
    }

}
