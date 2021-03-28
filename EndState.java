public class EndState extends State {
	
	/**
	 * Constructor for EndState
	 * @param ele is type Elevator class
	 */

	public EndState(Elevator ele) {
		super(ele);
	}

	
	@Override
	/**
	 * Check if the door is closing 
	 */
	public void closeDoor() {
		try {
			System.out.println("Closing Doors");
			Thread.sleep(CloseDoorTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/**
	 *  Check if elevator is moving
	 */
	public void openElevator() {
		System.out.println("elevator moving to floor" + elevator.getFloor());
		//elevator.setState(elevator.isMoving());
		try {
			Thread.sleep(FloorToFloorTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Check if door is opening
	 */
	public void movElevator() {
		try {
			System.out.println("opening door");
			Thread.sleep(OpenDoorTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * checks if the elevator moving or request  completed
	 */
	public void moveElevator() {
		try {
			System.out.println("Elevator completed request");
			Thread.sleep(CloseDoorTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
