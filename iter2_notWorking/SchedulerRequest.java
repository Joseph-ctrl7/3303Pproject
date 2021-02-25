package iter2;

import java.net.DatagramPacket;
import java.net.InetAddress;

import org.graalvm.compiler.loop.InductionVariable.Direction;

public class SchedulerRequest {
	public SchedulerRequest(DatagramPacket packet) {
		InetAddress receivedAddress = packet.getAddress();
		private SubsystemConstants type;
		private InetAddress receivedAddress;
		private int destFloor = -1;
		private startElevatorSM requestDirection;
		private int targetFloor = -1;
		
	}
}
	public SchedulerRequest(InetAddress receivedAddress, SubsystemConstants type,
			startElevatorSM requestDirection, int destFloor) {
		
		this.receivedAddress = receivedAddress;
		this.type = type;
		this.destFloor = destFloor; 
		this.requestDirection = requestDirection;
		this.targetFloor = destFloor; //final destination 
}
