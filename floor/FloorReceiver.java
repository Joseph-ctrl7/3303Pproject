package floor;

import java.util.concurrent.BlockingQueue;

import elevator.elevator;

public class FloorReceiver implements Runnable{

	private BlockingQueue<elevator> messages;

	public FloorReceiver(BlockingQueue<elevator> messages) {
		this.messages = messages;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("waiting for data from elevator");
			elevator elevatorData = messages.take();
			System.out.println("data from elevator" + elevatorData);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
