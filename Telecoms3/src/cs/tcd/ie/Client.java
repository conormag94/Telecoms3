/**
 * 
 */
package cs.tcd.ie;

import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.io.*;

import tcdIO.*;

/**
 *
 * Client class
 * 
 * An instance accepts user input 
 * 
 * TODO: FIRST CHANGE TEST
 * TODO: SOME OTHER TEST FOR MERGING.
 *
 */
public class Client extends Node {
	static final int DEFAULT_SRC_PORT = 50000;
	static final int DEFAULT_DST_PORT = 50001;
	static final String DEFAULT_DST_NODE = "localhost";	
	
	static final int LINES_PER_CHUNK = 5000;
	static final int CHAR_LIMIT = 70;
	Terminal terminal;
	InetSocketAddress dstAddress;
	
	/**
	 * Constructor
	 * 	 
	 * Attempts to create socket at given port and create an InetSocketAddress for the destinations
	 */
	Client(Terminal terminal, String dstHost, int dstPort, int srcPort) {
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
		
		terminal.println(content.toString());
	}

	public String[] splitFile(File inputFile) throws IOException{
		File in = inputFile;
		BufferedReader reader = new BufferedReader(new FileReader(in));
		
		String[] parts = null;
		
		String[] chunks = null;
		String line = reader.readLine();
		//Big enough to hold 5000 lines (LINES_PER_CHUNK) of 70 chars each (CHAR_LIMIT)
		StringBuilder sb = new StringBuilder(LINES_PER_CHUNK * CHAR_LIMIT); 
		
		while(line != null){
			
		}
		reader.close();
		return parts;
		/*
		String[] chunks = new String[10];
		
		for(int i = 0; i < 10; i++){
			String info = "";
			
			for(int j = 0; j < 100; j++){
				info += (char)buffer[j];
			}
			
			chunks[i] = info;
		}
			
		return chunks;
		*/
	}
	
	/**
	 * Sender Method
	 * 
	 */
	public synchronized void start() throws Exception {
		String fname;

		FileInputStream fin= null;
		FileInfoContent fcontent;
		
		int size = 0;
		byte[] buffer= null;
		DatagramPacket packet= null;
		
		fname= terminal.readString("Name of file: ");
		
		BufferedReader reader = null;
		File file = null;

		//Reading in files line by line
		try{
			file= new File(fname);	
			String[] chunks = splitFile(file);
			// Reserve buffer for length of file and read file
			reader = new BufferedReader(new FileReader(file));
			
			int numOfLines =0;
			String line = reader.readLine();
			while (line != null){
				numOfLines++;
				line = reader.readLine();
			}
			reader = new BufferedReader(new FileReader(file));
			line = reader.readLine();

			//String[] chunks = new String[numOfLines/10];
			
			String testString = "";
			int count = 0;
			while (line != null && count < 10){
				System.out.println(line);
				testString += line + ",";
				line = reader.readLine();
				count++;
			}
			System.out.println(testString);
			
			if(testString.contains("chris smith,"))
				System.out.print("Dicks");		
			
		}catch(Exception e){
			e.printStackTrace();
		}
		

		//terminal.println("File size: " + buffer.length);

		fcontent= new FileInfoContent(fname, size);
		
		terminal.println("Sending packet w/ name & length"); // Send packet with file name and length
		packet= fcontent.toDatagramPacket();
		packet.setSocketAddress(dstAddress);
		socket.send(packet);
		terminal.println("Packet sent");
		this.wait();
		//fin.close();
	}


	/**
	 * Test method
	 * 
	 * Sends a packet to a given address
	 */
	public static void main(String[] args) {
		try {					
			Terminal terminal= new Terminal("Coordinator");		
			(new Client(terminal, DEFAULT_DST_NODE, DEFAULT_DST_PORT, DEFAULT_SRC_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
