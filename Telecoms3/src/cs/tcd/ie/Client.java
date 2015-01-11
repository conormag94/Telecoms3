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
 */
public class Client extends Node {
	static final int DEFAULT_SRC_PORT = 50000;
	static final int DEFAULT_DST_PORT = 50001;
	static final String DEFAULT_DST_NODE = "localhost";	
	
	static final int LINES_PER_CHUNK = 2000;
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
	
	/**
	 * Sender Method
	 * TODO: Multiple packets/servers
	 * @Author: Conor Maguire
	 */
	public synchronized void start() throws Exception {
		String fname, testName;
		String[] chunks = null;
		
		fname= terminal.readString("Name of file: ");
		testName = terminal.readString("Name to find: ");
		File file = null;
		
		try{
			file= new File(fname);	
			chunks = splitFile(file);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		FileInfoContent fcontent = new FileInfoContent(chunks[0], testName);
		DatagramPacket packet;
		terminal.println("Sending packet:");
		packet = fcontent.toDatagramPacket();
		packet.setSocketAddress(dstAddress);
		socket.send(packet);
		terminal.println("Packet sent");
		
		this.wait();
	}
	
	/** 
	 * @param  inputFile - Text File to split (names-short.txt)
	 * @return chunks    - String array containing file split into 5 chunks
	 * @throws IOException
	 * 
	 * NOTE:
	 * ====
	 * We separate the names by commas in the chunks to avoid partial matches
	 * If we searched for "john williams" and the name "john williamson" was there,
	 * this would give us a false match as "john williamson" contains "john williams"
	 * So "john williamson" becomes "john williamson,"
	 * and "john williams" becomes "john williams,".
	 * This fixes the problem
	 */
	public String[] splitFile(File inputFile) throws IOException{
		File in = inputFile;
		BufferedReader reader = new BufferedReader(new FileReader(in));
		StringBuilder sb = new StringBuilder(PACKETSIZE); 
		
		String[] chunks = new String[5];
		String line;
		int linesRead = 0;
		int i = 0;
		while((line = reader.readLine()) != null){
			linesRead+=1;
			sb.append(line.toLowerCase() + ",");
			
			if(linesRead == LINES_PER_CHUNK){
				linesRead = 0;
				chunks[i] = sb.toString();
				sb = new StringBuilder(PACKETSIZE);
				i+=1;
			}
		}		
		reader.close();
		return chunks;
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
