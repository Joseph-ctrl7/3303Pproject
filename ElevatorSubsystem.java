/**
 *
 */

/**
 * @authors: Joseph Anyia, Amith Kumar Das Orko, Tolu Ajisola,
 *          Israel Okonkwo, Mehdi Khan
 *
 */

import java.util.ArrayList;
import java.util.Random;



public class ElevatorSubsystem implements Runnable{

    private int currentElevatorFloor;
    private int previousElevatorFloor;
    private int destinationFloor;
    private int directionButton;
    private int floorButton;
    private Scheduler scheduler;
    private Elevator elevator;
    private ArrayList<Integer> elevatorInfo;
    private ElevatorMovingState state;
    private ElevatorDoorState doorState;
    private boolean operateDoors = false;


    public enum ElevatorMovingState {UP, DOWN, IDLE};
    public enum ElevatorDoorState {OPEN, CLOSE};

    public ElevatorSubsystem(Scheduler scheduler){
        this.scheduler = scheduler;
        elevator = new Elevator();
        elevatorInfo = new ArrayList<>();
        state = ElevatorMovingState.IDLE;
        doorState = ElevatorDoorState.CLOSE;
        Random rand = new Random();
        currentElevatorFloor = rand.nextInt(6); //elevator goes up to the 6th floor

    }




    public void setFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }



    public int getFloor() {
        return this.destinationFloor;
    }

    public void setDirectionButton(int directionButton){
        this.directionButton = directionButton;
    }

    public int getDirectionButton() {
        return this.directionButton;
    }

    public void setElevatorButton(int floorButton){
        this.floorButton = floorButton;
    }

    public int getElevatorButton() {
        return this.floorButton;
    }

    public int getCurrentFloor() {
        return this.currentElevatorFloor;
    }


    public boolean checkIfEmpty() {
        if(elevatorInfo.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * this method gets the information from the scheduler
     */
    public void receiveSchedulerInfo() {
        this.destinationFloor = scheduler.getElevatorButton();
        this.floorButton = scheduler.getFloorNumber();
        this.directionButton = scheduler.getDirection();

        elevatorInfo.add(this.floorButton);
        elevatorInfo.add(this.directionButton);
        elevatorInfo.add(this.destinationFloor);
    }



    /**
     * this method is the state machine responsible for elevator movement
     * @param e elevator to be notified
     */
    public synchronized void startElevatorSM(Elevator e, int direction, int currentElevatorFloor, int floorNumber) throws InterruptedException {
        switch(state){
            case IDLE: //when elevator is idle and a request is made
                if(this.elevatorInfo.contains(direction)) {
                    if (direction == 0) {
                        state = ElevatorMovingState.DOWN;
                    }
                    if (direction == 1) {
                        state = ElevatorMovingState.UP;
                    }
                    e.turnOnLamps(this.destinationFloor, direction); //notify elevator to turn on lamps
                    e.startMotor(direction, currentElevatorFloor, floorNumber);//notify elevator to start motors
                }
            case UP: //when elevator is going up and arrives at its destination
                if(e.checkIfArrived()==true){
                    this.previousElevatorFloor = this.currentElevatorFloor;
                    this.currentElevatorFloor = this.elevator.getCurrentFloor();
                    scheduler.notifyScheduler(true); //notify scheduler that elevator has arrived
                    state = ElevatorMovingState.IDLE;
                }
                break;
            case DOWN: //when elevator is going down and arrives at its destination
                if(e.checkIfArrived()==true){
                    this.previousElevatorFloor = this.currentElevatorFloor;
                    this.currentElevatorFloor = this.elevator.getCurrentFloor();
                    scheduler.notifyScheduler(true); //notify scheduler that elevator has arrived
                    state = ElevatorMovingState.IDLE;
                }
                break;
        }
    }


    /**
     * state machine for door operations
     * @param elevator elevator whose door will be opened or closed
     * @throws InterruptedException
     */
    public synchronized void doorsStateMachine(Elevator elevator) throws InterruptedException {
        switch(doorState){
            case CLOSE:
                Thread.sleep(2000); //wait for elevator to be notified
                if(scheduler.notifyAboutFloor(true) == true){
                    doorState = ElevatorDoorState.OPEN;
                    elevator.openDoors();
                }
            case OPEN:
                Thread.sleep(2000); // wait a while before doors close
                doorState = ElevatorDoorState.CLOSE;
                elevator.closeDoors();
        }

        notifyAll();
    }

    @Override
    public void run() {
        if(scheduler.askForInput() == true){  //updates elevator tasks if there is input in the scheduler
            this.receiveSchedulerInfo();

        }
        System.out.println("\nElevator Data from Scheduler-------------------------------------------------------------");
        try {
            this.startElevatorSM(this.elevator, this.directionButton, this.currentElevatorFloor, this.floorButton); //brings elevator to the floor the passenger is located
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scheduler.receiveElevatorData(this.previousElevatorFloor, this.directionButton); //scheduler receives the elevators current floor and direction
        try {
            this.doorsStateMachine(elevator); //starts door operation
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nElevator now heading to floor "+ this.destinationFloor+"-------------------------------------------------------------");

        try {
            this.startElevatorSM(this.elevator, this.directionButton, this.currentElevatorFloor, this.destinationFloor); //heads over to the destination floor
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}

