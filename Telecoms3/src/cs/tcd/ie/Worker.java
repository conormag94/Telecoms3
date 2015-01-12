package cs.tcd.ie;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.*;

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
			//Constructs a datagram socket and binds it to any available port on the local host machine.
			socket = new DatagramSocket();
			terminal.println("My port: " + socket.getLocalPort());
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
				DatagramPacket response;
				response = new AckPacketContent("Finding name: " + name).toDatagramPacket();
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);

				terminal.println("Finding name: " + name);
				boolean nameFound;
				nameFound = findName(namesList, name);
				String resultString = "";
				if(nameFound){
					System.out.println(name + " found!");
					terminal.println("***I FOUND " + name +"!***");
					resultString = "Found";
				}
				else{
					System.out.println(name + " not found");
					terminal.println(name + " not found");
					resultString = "Not Found";
				}				

				DatagramPacket result;
				result = new AckPacketContent(resultString).toDatagramPacket();
				result.setSocketAddress(packet.getSocketAddress());
				socket.send(result);
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
		DatagramPacket connectRequest = new AckPacketContent("Connect Me").toDatagramPacket();
		connectRequest.setSocketAddress(new InetSocketAddress("localhost", 50000));
		socket.send(connectRequest);
		terminal.println("Connection Request sent");
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
