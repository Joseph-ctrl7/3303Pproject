import elevator.Elevator;

import java.util.Random;

public class ElevatorSubsystem implements Runnable{

    private int currentFloor;
    private int destinationFloor;
    private int directionButton;
    private int floorButton;
    private Scheduler scheduler;
    private Elevator elevator;

    public ElevatorSubsystem(Scheduler scheduler){
        this.scheduler = scheduler;
        elevator = new Elevator();
        Random rand = new Random();
        currentFloor = rand.nextInt(6);

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

    public void notifyElevator(Elevator e){
        e.turnOnLamps(this.destinationFloor, this.directionButton);
        e.startMotor(this.destinationFloor, this.directionButton);

    }

    @Override
    public void run() {
        if(scheduler.askForInput() == true){
            this.destinationFloor = scheduler.getElevatorButton();
            this.floorButton = scheduler.getFloorNumber();
            this.directionButton = scheduler.getDirection();
        }
        System.out.println("\nElevator Data-------------------------------------------------------------");
        this.notifyElevator(elevator);
        scheduler.notifyScheduler(true);
        scheduler.receiveElevatorData(this.currentFloor, this.directionButton);
        //System.out.println(this.destinationFloor+" "+this.directionButton+" "+this.floorButton);
    }
}
