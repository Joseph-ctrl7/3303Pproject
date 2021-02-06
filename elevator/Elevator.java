
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


    public synchronized void turnOnLamps(int destination, int direction){
        this.directionLamp = true;
        this.elevatorLamp = true;
        System.out.println("lamps "+destination + " and "+ direction+" are on");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void turnOffLamps(int destination, int direction){
        this.directionLamp = false;
        this.elevatorLamp = false;
        System.out.println(destination + " and "+ direction+" lamps are off");

    }

    public synchronized void startMotor(int destination, int direction) {
        if (direction == 1){
            this.state = State.MOVING_UP;
            System.out.println("elevator is going UP");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else{
            this.state = State.MOVING_DOWN;
            System.out.println("elevator is going DOWN");
        }
    }




    public String toString() {
        return  "Floor: " + floor + "\n" + "\t State: " + state + "\n";
    }

}
