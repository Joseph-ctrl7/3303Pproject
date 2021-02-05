package elevator;

import java.util.concurrent.BlockingQueue;

public class elevatorExchange implements Runnable{

	

	private BlockingQueue<elevator> messages;

	public elevatorExchange(BlockingQueue<elevator> messages) {
		this.messages = messages;
		
	}
	
	@Override
	public void run() {
		try {
			elevator elevatorData = retrieveData();
			messages.put(elevatorData);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private elevator retrieveData() throws InterruptedException {
		Thread.sleep(5000);
		elevator elevatorData = new elevator();
		return elevatorData;
	}

	
}
