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
		elevator elevatorData = messages.take();
		
	
	

}
