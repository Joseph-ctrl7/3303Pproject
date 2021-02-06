package scheduler;

import java.util.concurrent.BlockingQueue;

import elevator.elevator;

public class Scheduler implements Runnable {

	private BlockingQueue<elevator> messages;

	public Scheduler(BlockingQueue<elevator> messages) {
		this.messages = messages;
		
	}
	@Override
	public void run() {
		try {
			
			@SuppressWarnings("unused")
			elevator elevatorData = messages.take();
			
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unused")
	private Scheduler retrieveSchedulerData() throws InterruptedException {
		Thread.sleep(5000);
		Scheduler elevatorToSchedulerData = new Scheduler(messages);
		return elevatorToSchedulerData;
	}

	

}
