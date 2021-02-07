import elevator.Elevator;

import java.util.Random;

public class ElevatorSubsystem implements Runnable{

    private int currentElevatorFloor;
    private int destinationFloor;
    private int directionButton;
    private int floorButton;
    private Scheduler scheduler;
    private Elevator elevator;

    public ElevatorSubsystem(Scheduler scheduler){
        this.scheduler = scheduler;
        elevator = new Elevator();
        Random rand = new Random();
        currentElevatorFloor = rand.nextInt(6);

    }




    public void setFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
        //pressedButtons.remove(floor);
    }

    public void setDirectionButton(int directionButton){
        this.directionButton = directionButton;
    }

    public void setElevatorButton(int floorButton){
        this.floorButton = floorButton;
    }

    /**
     * starts the operation of the elevator
     * @param e
     */
    public void notifyElevator(Elevator e){
        e.turnOnLamps(this.destinationFloor, this.directionButton);
        e.startMotor(this.destinationFloor, this.directionButton, this.currentElevatorFloor, this.floorButton);

    }

    @Override
    public void run() {
        if(scheduler.askForInput() == true){        //updates elevator tasks if there is input in the scheduler
            this.destinationFloor = scheduler.getElevatorButton();
            this.floorButton = scheduler.getFloorNumber();
            this.directionButton = scheduler.getDirection();
        }
        System.out.println("\nElevator Data from Scheduler-------------------------------------------------------------");
        this.notifyElevator(elevator);      //turns the lamps of and moves the elevator according to the updates from the scheduler
        scheduler.notifyScheduler(true);
        scheduler.receiveElevatorData(this.currentElevatorFloor, this.directionButton);     //schuduler recieves the elevators current floor and direction
        //System.out.println(this.destinationFloor+" "+this.directionButton+" "+this.floorButton);
    }
}
