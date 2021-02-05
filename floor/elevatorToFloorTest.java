package floor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import elevator.elevator;
import elevator.elevatorExchange;

public class elevatorToFloorTest {
	
	public static void main(String[] args) {
		BlockingQueue<elevator> messages = new ArrayBlockingQueue<elevator>(1);
		elevatorExchange retriever = new elevatorExchange(messages);
		FloorReceiver receiver = new FloorReceiver(messages);
		
		new Thread(retriever).start();
		new Thread(receiver).start();
	
	
	}

}
