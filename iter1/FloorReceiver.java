package floor;

import java.util.concurrent.BlockingQueue;

import elevator.elevator;

public class FloorReceiver implements Runnable{

	private BlockingQueue<elevator> messages;

	public FloorReceiver(BlockingQueue<elevator> messages2) {
		this.messages = messages2;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("waiting for data from elevator");
			elevator elevatorToSchedulerData = messages.take();
			System.out.println("data from elevator" + elevatorToSchedulerData);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
