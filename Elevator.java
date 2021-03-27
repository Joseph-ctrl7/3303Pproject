/**
 *
 */

/**
 * @author: Joseph Anyia, Amith Kumar Das Orko, Tolu Ajisola,
 *          Israel Okonkwo, Mehdi Khan
 *
 */


import java.io.IOException;
import java.net.UnknownHostException;
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
    private boolean hasArrived;
    private ElevatorSubsystem e;

    @SuppressWarnings({ "unchecked" })
    private Set<Integer> pressedButtons = (Set<Integer>) new HashSet<Integer>();
    private int directionButton;
    private int floorButton;
    private int currentFloor;

    public Elevator(ElevatorSubsystem e) {

        state = State.STOPPED;
        buttonPressed = false;
        directionLamp = false;
        elevatorLamp = false;
        startMotor = false;
        openDoors = false;
        elevatorNotified = false;
        hasArrived = false;
        this.e = e;

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

    public int getCurrentFloor(){
        return this.currentFloor;
    }


    public synchronized void openDoors()  {
        openDoors = true;
        System.out.println("port:"+e.getPort()+" Elevator Doors Opening........");
    }

    public synchronized void closeDoors(){
        openDoors = false;
        System.out.println("port:"+e.getPort()+" Elevator Doors Closing........");
    }

    /**
     * checks if elevator has arrived its destination
     * @return true if elevator has arrived
     */
    public synchronized boolean checkIfArrived(){
        return this.hasArrived;
    }

    /**
     * turns on the required lamps in the elevator
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

    public void turnOffLamps(int destination, int direction){
        this.directionLamp = false;
        this.elevatorLamp = false;
        System.out.println(destination + " and "+ direction+" lamps are off");

    }

    /**
     * this method starts the motor of the elevator and takes it to the requested floor
     * @param direction direction elevator goes
     * @param currentElevatorFloor where the elevator is currently at
     * @param floorNumber floor the passenger is at/ where passenger wants to go(depending on elevator functionality)
     */
    public synchronized void startMotor(int direction, int currentElevatorFloor, int floorNumber) throws InterruptedException, IOException {
        //System.out.println("port:"+e.getPort()+" Elevator is currently at floor "+currentElevatorFloor);
        while(currentElevatorFloor != floorNumber) {
            if (direction == 1) {
                if (currentElevatorFloor > floorNumber) {
                    System.out.println("port:"+e.getPort()+" Elevator is currently at floor "+currentElevatorFloor);
                    this.state = State.MOVING_DOWN;
                    int nextFloor = currentElevatorFloor - 1;
                    wait(5000);
                    e.setCurrentFloor(currentElevatorFloor);
                    e.notifyOnArrival();
                    System.out.println("port:"+e.getPort()+" Elevator is going DOWN to floor "+nextFloor);
                    currentElevatorFloor--;
                }
                if (currentElevatorFloor < floorNumber) {
                    System.out.println("port:"+e.getPort()+" Elevator is currently at floor "+currentElevatorFloor);
                    this.state = State.MOVING_UP;
                    int nextFloor = currentElevatorFloor + 1;
                    wait(5000);
                    e.setCurrentFloor(currentElevatorFloor);
                    e.notifyOnArrival();
                    System.out.println("port:"+e.getPort()+" Elevator is going UP to floor "+nextFloor);
                    currentElevatorFloor++;
                }
            } else {
                if (currentElevatorFloor < floorNumber) {
                    System.out.println("port:"+e.getPort()+" Elevator is currently at floor "+currentElevatorFloor);
                    this.state = State.MOVING_UP;
                    int nextFloor = currentElevatorFloor + 1;
                    wait(5000);
                    e.setCurrentFloor(currentElevatorFloor);
                    e.notifyOnArrival();
                    System.out.println("port:"+e.getPort()+" Elevator is going UP to floor "+nextFloor);
                    currentElevatorFloor++;
                }
                if (currentElevatorFloor > floorNumber) {
                    System.out.println("port:"+e.getPort()+" Elevator is currently at floor "+currentElevatorFloor);
                    this.state = State.MOVING_DOWN;
                    int nextFloor = currentElevatorFloor - 1;
                    wait(5000);
                    e.setCurrentFloor(currentElevatorFloor);
                    e.notifyOnArrival();
                    System.out.println("port:"+e.getPort()+" Elevator is going DOWN to floor "+nextFloor);
                    currentElevatorFloor--;
                }
            }
        }
        if(currentElevatorFloor == floorNumber){
            System.out.println("port:"+e.getPort()+" Elevator has arrived at floor "+floorNumber);
            hasArrived = true;
            this.currentFloor = currentElevatorFloor;
            e.setCurrentFloor(currentElevatorFloor);
            e.notifyOnArrival();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void wait(int x){
        try {
            Thread.sleep(x);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    public String toString() {
        return  "Floor: " + floor + "\n" + "\t State: " + state + "\n";
    }

}

