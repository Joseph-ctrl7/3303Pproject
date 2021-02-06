package elevator;


import java.util.HashSet;
import java.util.Set;

public class elevator {
	
	
	
	public enum State {
		MOVING_UP, MOVING_DOWN, STOPPED
	}

	
	
	private int floor;
	
	private State state;
	
	@SuppressWarnings({ })
	private Set<Integer> pressedButtons = (Set<Integer>) new HashSet<Integer>();

	public elevator() {
		
		state = State.STOPPED;
	}
	
	public int getFloor() {
		return floor;
	}

	public void setFloor(int floor) {
		this.floor = floor;
		pressedButtons.remove(floor);
	}

	public State getState() {
		return state;
	}

	public void setState(State s) {
		state = s;	
	}
	public boolean isMoving() {
		return state == State.MOVING_UP || state == State.MOVING_DOWN;
	}
	
	public void buttonPressed(int i) {
		pressedButtons.add(i);
	}
	
	public Set<Integer> getButtons() {
		return pressedButtons;
	}

	public String toString() {
		return  "Floor: " + floor + "\n" + "\t State: " + state + "\n";
	}
	
}
