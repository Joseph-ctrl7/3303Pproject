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
    private int destinationFloor;
    private int directionButton;
    private int floorButton;
    private Scheduler scheduler;
    private Elevator elevator;
    private ArrayList<Integer> elevatorInfo;
    private ElevatorMovingState state;
    private ElevatorDoorState doorState;


    public enum ElevatorMovingState { UP, DOWN, IDLE};
    public enum ElevatorDoorState {OPEN, CLOSE};

    public ElevatorSubsystem(Scheduler scheduler){
        this.scheduler = scheduler;
        elevator = new Elevator();
        elevatorInfo = new ArrayList<>();
        state = ElevatorMovingState.IDLE;
        Random rand = new Random();
        currentElevatorFloor = rand.nextInt(6); //elevator goes up to the 6th floor

    }




    public void setFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
        //pressedButtons.remove(floor);
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

    public void receiveSchedulerInfo() {
        this.destinationFloor = scheduler.getElevatorButton();
        this.floorButton = scheduler.getFloorNumber();
        this.directionButton = scheduler.getDirection();

        elevatorInfo.add(this.floorButton);
        elevatorInfo.add(this.directionButton);
        elevatorInfo.add(this.destinationFloor);
    }



    /**
     * starts the operation of the elevator
     * @param e
     */
    public synchronized void notifyElevator(Elevator e){
        e.turnOnLamps(this.destinationFloor, this.directionButton);
        e.startMotor(this.destinationFloor, this.directionButton, this.currentElevatorFloor, this.floorButton);

    }


    /**
     * this method is the state machine responsible for elevator movement
     * @param e elevator to be notified
     */
    public synchronized void startElevatorSM(Elevator e) throws InterruptedException {
        switch(state){
            case IDLE: //when elevator is idle and a request is made
                if(this.elevatorInfo.contains(this.directionButton)) {
                    if (this.directionButton == 0) {
                        state = ElevatorMovingState.DOWN;
                    }
                    if (this.directionButton == 1) {
                        state = ElevatorMovingState.UP;
                    }
                    e.turnOnLamps(this.destinationFloor, this.directionButton); //notify elevator to turn on lamps
                    e.startMotor(this.destinationFloor, this.directionButton, this.currentElevatorFloor, this.floorButton);//notify elevator to start motors
                }
            case UP: //when elevator is going up and arrives at its destination
                if(e.checkIfArrived()==true){
                    e.openDoors();
                    Thread.sleep(2000);
                    e.closeDoors();
                    state = ElevatorMovingState.IDLE;
                }
                break;
            case DOWN: //when elevator is going down and arrives at its destination
                if(e.checkIfArrived()==true){
                    e.openDoors();
                    Thread.sleep(2000);
                    e.closeDoors();
                    state = ElevatorMovingState.IDLE;
                }
                break;
        }
    }

    @Override
    public void run() {
        if(scheduler.askForInput() == true){  //updates elevator tasks if there is input in the scheduler
            this.receiveSchedulerInfo();

        }
        System.out.println("\nElevator Data from Scheduler-------------------------------------------------------------");
        try {
            this.startElevatorSM(elevator);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //this.notifyElevator(elevator);  //turns the lamps of and moves the elevator according to the updates from the scheduler
        scheduler.notifyScheduler(true);
        scheduler.receiveElevatorData(this.currentElevatorFloor, this.directionButton); //scheduler recieves the elevators current floor and direction
    }
}

