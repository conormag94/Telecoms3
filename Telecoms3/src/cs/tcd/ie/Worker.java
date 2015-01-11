package cs.tcd.ie;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import tcdIO.Terminal;

public class Worker extends Node {
	static final int DEFAULT_PORT = 50001;

	Terminal terminal;
	
	/*
	 * 
	 */
	Worker(Terminal terminal, int port) {
		try {
			this.terminal= terminal;
			socket= new DatagramSocket(port);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 * 
	 */
	public void onReceipt(DatagramPacket packet) {
		try {
			terminal.println("Received packet");
			
			PacketContent content= PacketContent.fromDatagramPacket(packet);
			
			if(content.getType()==PacketContent.FILEINFO){
				String namesList = content.toString();

				String name = ((FileInfoContent)content).getName();

				terminal.println("Finding name: " + name);
				boolean nameFound;
				nameFound = findName(namesList, name);
				if(nameFound == true){
					System.out.println(name + " found!");
					terminal.println(name + " found!");
				}
				else if(nameFound == false){
					System.out.println(name + " not found");
					terminal.println(name + " not found");
				}				
				//Receipt acknowledgment
				//TODO: Add ack packet of whether or not the name was found
				DatagramPacket response;
				response = new AckPacketContent("Finding name: " + name).toDatagramPacket();
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}
	
	public boolean findName(String namesList, String nameToFind){
		String tokenizedName = nameToFind + ",";
		
		return(namesList.contains(tokenizedName));
	}

	public synchronized void start() throws Exception {
		terminal.println("Waiting for contact");
		this.wait();
	}

	public static void main(String[] args) {
		try {					
			Terminal terminal= new Terminal("Worker");
			(new Worker(terminal, DEFAULT_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
