package iter1;

import java.io.Serializable;

		
public class ElevatorData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int UP = 0;

	private static final int DOWN = 1;
	
	private final int elevatorNum; //the elevator number
	private final int currFloor; //the elevator's current floor
	private boolean movingUp; //true if elevator is moving up, false otherwise
	private boolean movingDown; //true if elevator is moving down, false otherwise
	private int currDirection;


	/**
	 * Constructor for the ElevatorData object
	 * 
	 * @param elevatorNum
	 * @param currFloor
	 * @param movingUp 
	 * @param movingDown 
	 */
	public ElevatorData(int elevatorNum, int currFloor, boolean movingUp,
			boolean movingDown, int currDirection) {
		
		this.elevatorNum = elevatorNum;
		this.currFloor = currFloor;
		this.movingUp = movingUp;
		this.movingDown = movingDown;
		this.currDirection = currDirection;
	}
	public boolean isMovingUp() {
		if (currDirection == UP)
			return true;
		return false;
	}
	public int getCurrentFloor() {
		return currFloor;
	}
	public boolean isMovingDown() {
		if (currDirection == DOWN)
			return true;
		return false;
	}
	public int getElevatorNumber() {
		return elevatorNum;
	}

}
	
	
	
