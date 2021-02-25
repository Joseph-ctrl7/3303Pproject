package iter2;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ElevatorPipeline extends Thread implements SchedulerPipeline{

	private DatagramSocket receiveSocket;
	private DatagramSocket sendSocket; 
	
	@Override
	public SubsystemConstants getObjectType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatagramSocket getSendSocket() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DatagramSocket getReceiveSocket() {
		// TODO Auto-generated method stub
		return null;
	}



	public void setReceiveSocket(DatagramSocket receiveSocket) {
		this.receiveSocket = receiveSocket;
	}

	public void setSendSocket(DatagramSocket sendSocket) {
		this.sendSocket = sendSocket;
	}
	
	public ElevatorPipeline(SubsystemConstants objectType) throws SocketException {
		
		this.receiveSocket = new DatagramSocket();
		this.sendSocket = new DatagramSocket();
	
	}
	public void addEvent(SchedulerRequest request) {
		synchronized (elevatorEvents) {
			elevatorEvents.add(request);
			elevatorEvents.notifyAll();
		}
	}


}
