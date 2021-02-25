package iter2;

import java.net.DatagramSocket;


public interface SchedulerPipeline {
	public SubsystemConstants getObjectType();
	public DatagramSocket getSendSocket();
	public DatagramSocket getReceiveSocket();

}
