package cs.tcd.ie;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import tcdIO.Terminal;

public class Server extends Node {
	static final int DEFAULT_PORT = 50001;

	Terminal terminal;
	
	/*
	 * 
	 */
	Server(Terminal terminal, int port) {
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
			String gay = (content.toString());
			String nameToFind="";
			char[] homo = gay.toCharArray();
			/*int c=0;
			while(homo[c]!='@')
			{
				nameToFind+=homo[c];
				homo[c]='!';
				c++;
			}
			terminal.println(nameToFind);
			if(gay.contains(nameToFind))
			{
				terminal.println("noice");
			}
			else
			{
				terminal.println("not noice");
			}*/
			if(gay.contains("john smith"))
			{
				terminal.println("hahaha");
			}
			else
			{
				terminal.println("nope");
			}
			
			System.out.println(gay);
				
		
			if (content.getType()==PacketContent.FILEINFO) {
				terminal.println("File name: " + ((FileInfoContent)content).getFileName());
				terminal.println("File size: " + ((FileInfoContent)content).getFileSize());
				
				DatagramPacket response;
				response= new AckPacketContent("OK - Received this").toDatagramPacket();
				response.setSocketAddress(packet.getSocketAddress());
				socket.send(response);
			}
		}
		catch(Exception e) {e.printStackTrace();}
	}

	
	public synchronized void start() throws Exception {
		terminal.println("Waiting for contact");
		this.wait();
	}
	
	/*
	 * 
	 */
	public static void main(String[] args) {
		try {					
			Terminal terminal= new Terminal("Server");
			(new Server(terminal, DEFAULT_PORT)).start();
			terminal.println("Program completed");
		} catch(java.lang.Exception e) {e.printStackTrace();}
	}
}
