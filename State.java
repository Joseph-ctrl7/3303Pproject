public abstract class State {
	

	public static final long FloorToFloorTime = 4800;
	public static final long OpenDoorTime = 1400;
	public static final long HoldDoorTime = 3300;
	public static final long CloseDoorTime = 1500;
	public Elevator elevator;

  
    public State(Elevator ele) {
        this.elevator = ele;
    }

	public abstract void closeDoor(); 
    public abstract void movElevator(); 
    public abstract void openElevator();
  

}
