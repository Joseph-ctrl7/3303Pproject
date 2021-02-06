public class ElevatorSubsystem implements Runnable{

    private boolean buttonPressed;
    private boolean switchOnLamp;
    private boolean startMotor;
    private boolean openDoors;
    private int currentFloor;
    private int destinationFloor;
    private int directionButton;
    private int floorButton;
    private Scheduler scheduler;

    public ElevatorSubsystem(Scheduler scheduler){
        this.scheduler = scheduler;
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

    @Override
    public void run() {
        if(scheduler.askForInput() == true){
            this.destinationFloor = scheduler.getElevatorButton();
            this.floorButton = scheduler.getFloorNumber();
            this.directionButton = scheduler.getDirection();
        }
        System.out.println(this.destinationFloor+" "+this.directionButton+" "+this.floorButton);
    }
}
