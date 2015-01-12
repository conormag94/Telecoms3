/**
 * 
 */
package cs.tcd.ie;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.io.*;
import java.net.*;

import tcdIO.*;

/**
 *
 * Coordinator class
 * 
 * An instance accepts user input 
 *
 */
public class Coordinator extends Node {
	static final int DEFAULT_SRC_PORT = 50000;
	static final int DEFAULT_DST_PORT = 50001;
	static final String DEFAULT_DST_NODE = "localhost";	
	
	static final int LINES_PER_CHUNK = 2500;
	Terminal terminal;
	InetSocketAddress dstAddress;
	
	SocketAddress[] workerAddresses = new SocketAddress[50];;
	int workersConnected = 0;
	boolean connecting = true;
	boolean sending = true;
	
	boolean nameFound = false;
	
	/**
	 * Constructor
	 * 
	 * 	 
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Coordinator(Terminal terminal, String dstHost, int dstPort, int srcPort) {
		try {
			this.terminal= terminal;
			dstAddress= new InetSocketAddress(dstHost, dstPort);
			socket= new DatagramSocket(srcPort);
			listener.go();
		}
		catch(java.lang.Exception e) {e.printStackTrace();}
	}

	/**
	 * Assume that incoming packets contain a String and print the string.
	 */
	public void onReceipt(DatagramPacket packet) {
		PacketContent content= PacketContent.fromDatagramPacket(packet);
		if(content.toString().equals("ACK:Connect Me")){
			SocketAddress receivedAddress = packet.getSocketAddress();
			workerAddresses[workersConnected] = receivedAddress;
			workersConnected+=1;
			terminal.println("Connected Worker: " + receivedAddress);
			
			synchronized(terminal){
				if(workersConnected < workerAddresses.length)
					connecting = true;
			}
		}
		
		else if(content.toString().equals("ACK:Found")){
			nameFound = true;
			sending = false;
		}
		
		else if(content.toString().equals("ACK:Not Found")){
			nameFound = false;
		}
	}
	
	/**
	 * Sender Method
	 * 
	 */
	public synchronized void start() throws Exception {
		
		//Awaits inital connection requests from workers
		while(connecting){
			connecting = false;
			synchronized(terminal){
				terminal.println("Waiting for workers to connect...");
				terminal.wait(5000);
			}
		}	
		terminal.println(workersConnected + " workers connected");
		
		//if no workers connected, ends the program
		if(workersConnected == 0)
			return;

		String fname, testName;
		fname= terminal.readString("Name of file: ");
		testName = terminal.readString("Name to find: ");
		File file = null;

		try{
			file= new File(fname);	
			splitFile(file, testName);
		}catch(Exception e){
			e.printStackTrace();
			return;
		}
		String result = (nameFound) ? "Name Found: " + testName : "Name Not Found: " + testName;
		terminal.println("***" + result + "**");
		System.out.println("**" + result + "**");
		
		this.wait();
	}
	
	/** 
	 * splitFile method
	 * ================
	 * Splits up the text file into chunks of 2500 lines each
	 * and sends out the chunks to the next available worker as they are made available
	 * 
	 * @param  inputFile - Text File to split (names-short.txt)
	 *         testName  - The name to find in the text
	 * @throws IOException

	 */
	public void splitFile(File inputFile, String testName) throws IOException{
		File in = inputFile;
		BufferedReader reader = new BufferedReader(new FileReader(in));
		
		String line;
		int linesRead = 0;
		int i = 0;
		String current = "";
		while( ((line = reader.readLine()) != null) && (sending == true) ){
			linesRead+=1;
			current += line + ",";
			
			if(linesRead == LINES_PER_CHUNK && sending==true){
				linesRead = 0;				
				DatagramPacket work = new FileInfoContent(current, testName).toDatagramPacket();
				work.setSocketAddress(workerAddresses[i%workersConnected]);
				socket.send(work);
				terminal.println("Packet " + i + " sent");

				current = "";
				i+=1;
			}
		}		
		reader.close();

	}

	/**
	 * Test method
	 * 
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
		try {					
			Terminal terminal= new Terminal("Coordinator");		
			(new Coordinator(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
