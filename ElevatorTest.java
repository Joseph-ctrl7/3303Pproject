
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

//import main.elevator.Elevator;

/**
 * 
 */

/**
 * @author Tolu
 *
 */
class ElevatorTest {

	Elevator elevator;
	/**
	 * @throws java.lang.Exception
	 
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		elevator = new Elevator();
	}*/

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		elevator = new Elevator();
		
	}

	/**
	 * Test method for {@link Elevator#Elevator()}.
	 */
	@Test
	void testElevator() {
		
		assertEquals(false,elevator.checkIfArrived());
	}

	/**
	 * Test method for {@link Elevator#getFloor()}.
	 */
	@Test
	void testGetFloor() {
		elevator.setFloor(5);
		assertEquals(5,elevator.getFloor());
	}

	/**
	 * Test method for {@link Elevator#setFloor(int)}.
	 */
	@Test
	void testSetFloor() {
		elevator.setFloor(4);
	}

	/**
	 * Test method for {@link Elevator#setDirectionButton(int)}.
	 */
	@Test
	void testSetDirectionButton() {
		elevator.setDirectionButton(0);
	}

	/**
	 * Test method for {@link Elevator#setElevatorButton(int)}.
	 */
	@Test
	void testSetElevatorButton() {
		elevator.setElevatorButton(3);
	}

	/**
	 * Test method for {@link Elevator#getState()}.
	 */
	@Test
	void testGetState() {
		Elevator.State state = Elevator.State.STOPPED;
		//System.out.println(elevator.getState());
		assertEquals(state, elevator.getState());
	}

	/**
	 * Test method for {@link Elevator#setState(Elevator.State)}.
	 */
	@Test
	void testSetState() {
		Elevator.State state = Elevator.State.MOVING_UP;
		elevator.setState(state);
	}

	/**
	 * Test method for {@link Elevator#isMoving()}.
	 */
	@Test
	void testIsMoving() {
		Elevator.State state = Elevator.State.MOVING_UP;
		elevator.setState(state);
		assertEquals(true, elevator.isMoving());
		state = Elevator.State.MOVING_DOWN;
		elevator.setState(state);
		assertEquals(true, elevator.isMoving());
	}
	
	

	/**
	 * Test method for {@link Elevator#buttonPressed(int)}.
	 */
	@Test
	void testButtonPressed() {
		elevator.buttonPressed(3);
	}

	/**
	 * Test method for {@link Elevator#getButtons()}.
	 */
	@Test
	void testGetButtons() {
		elevator.buttonPressed(3);
		assertTrue(elevator.getButtons().contains(3));
	}

	/**
	 * Test method for {@link Elevator#getCurrentFloor()}.
	 */
	@Test
	void testGetCurrentFloor() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link Elevator#openDoors()}.
	 */
	@Test
	void testOpenDoors() {
		System.out.println("\ntest openDoors");
		elevator.openDoors();
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link Elevator#closeDoors()}.
	 */
	@Test
	void testCloseDoors() {
		System.out.println("\ntest closeDoors");
		elevator.closeDoors();
		
	}

	/**
	 * Test method for {@link Elevator#checkIfArrived()}.
	 */
	@Test
	void testCheckIfArrived() {
		assertEquals(false,elevator.checkIfArrived());
		
	}

	/**
	 * Test method for {@link Elevator#turnOnLamps(int, int)}.
	 */
	@Test
	void testTurnOnLamps() {
		System.out.println("\ntest TurnOnLamps");
		elevator.turnOnLamps(5, 0);
		elevator.turnOnLamps(5, 1);
		
	}

	/**
	 * Test method for {@link Elevator#turnOffLamps(int, int)}.
	 */
	@Test
	void testTurnOffLamps() {
		System.out.println("\ntest TurnOffLamps");
		elevator.turnOffLamps(5, 1);
		
	}

	/**
	 * Test method for {@link Elevator#startMotor(int, int, int)}.
	 */
	@Test
	void testStartMotor() {
		System.out.println("\ntest motor, direction: down");
		elevator.startMotor(0,5,4);
		elevator.startMotor(0,4,5);
		elevator.startMotor(0,4,4);
		System.out.println("\ntest motor, direction: up");
		elevator.startMotor(1,5,4);
		elevator.startMotor(1,4,5);
		elevator.startMotor(1,4,4);
		//elevator.closeDoors();
		//elevator.toString();
	}

	/**
	 * Test method for {@link Elevator#toString()}.
	 */
	@Test
	void testToString() {
		System.out.println("\ntest toString");
		Elevator.State state = Elevator.State.MOVING_UP;
		elevator.setState(state);
		elevator.setFloor(4);
		System.out.println(elevator.toString());
	}

}
