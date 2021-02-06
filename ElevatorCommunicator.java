package iter1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ElevatorCommunicator extends Thread{
	DatagramPacket sendPacket, receivePacket;
	DatagramSocket sendSocket, receiveSocket;
	
	
	private boolean running;
	private InetAddress schedulerAddress; 
	
	
	/**
	 * Send a packet to the scheduler
	 */
	public void send() {
		ElevatorData elevDat = elevator.getElevatorData();

		try {
			// Convert the ElevatorData object into a byte array
			ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
			ObjectOutputStream ooStream = new ObjectOutputStream(new BufferedOutputStream(baoStream));
			ooStream.flush();
			ooStream.writeObject(elevDat);
			ooStream.flush();

			byte msg[] = baoStream.toByteArray();
			
			sendPacket = new DatagramPacket(msg, msg.length, schedulerAddress, 3000);

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// Send the datagram packet to the client via the send socket.
				try {
					sendSocket.send(sendPacket);
				} catch (IOException e) {
					e.printStackTrace();
					System.exit(1);
				}
				elevator.print("Sending to address: " + schedulerAddress);
				elevator.print("Sent packet to scheduler.\n Containing:\n	" + elevDat.getStatus() + "\n");
			}
	/**
	 * Receive a packet from the scheduler
	 */
	public void receive() {
		// Construct a DatagramPacket for receiving packets up
		// to 5000 bytes long (the length of the byte array).

		
			
			byte data[] = new byte[5000];
			receivePacket = new DatagramPacket(data, data.length);
			receiveSocket.receive(receivePacket);
			schedulerAddress = receivePacket.getAddress();

		
				//Retrieve the ElevatorData object from the receive packet
				ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
				ObjectInputStream is;
				is = new ObjectInputStream(new BufferedInputStream(byteStream));
				Object o = is.readObject();
				is.close();
				
				elevator.print("Received packet from address: " + schedulerAddress);
				elevator.processPacket(o);
				elevator.wake();
			
	}
	/**
	 * Close sockets
	 */
	public void closeSockets() {
		running = false;
		
		receiveSocket.close();
		sendSocket.close();
		
		
	}

	public void run() {
		while (running) {
			receive();
			try {
				wait(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
