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
			System.out.println("Closing Doors.");
			Thread.sleep(CloseDoorTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Check if door is opening
	 */
	public void openElevator() {
		try {
			System.out.println("elevator moving");
			Thread.sleep(FloorToFloorTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * Check if elevator is moving
	 */
	public void movElevator() {
		try {
			System.out.println("Opening Doors.");
			Thread.sleep(OpenDoorTime);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
