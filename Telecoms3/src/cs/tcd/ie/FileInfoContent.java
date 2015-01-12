package cs.tcd.ie;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class for packet content that represents file information
 * 
 */
public class FileInfoContent extends PacketContent {

	String filename;
	int size;
	
	//new stuff
	String data;
	String name;

	/**
	 * Constructor that takes in information about a file.
	 * @param filename Initial filename.
	 * @param size Size of filename.
	 */
	FileInfoContent(String data, String name){
		type = FILEINFO;
		this.data = data;
		this.name = name;
		
	}

	/**
	 * Constructs an object out of a datagram packet.
	 * @param packet Packet that contains information about a file.
	 */
	protected FileInfoContent(ObjectInputStream oin){
		try{
			type = FILEINFO;
			data = oin.readUTF();
			name = oin.readUTF();
		}catch(Exception e){e.printStackTrace();}
	}
	
	/**
	 * Writes the content into an ObjectOutputStream
	 *
	 */
	protected void toObjectOutputStream(ObjectOutputStream oout) {
		try {
			oout.writeUTF(data);
			oout.writeUTF(name);
		}
		catch(Exception e) {e.printStackTrace();}
	}
	
	public String getData(){
		return data;
	}
	
	public String getName(){
		return name;
	}

	/**
	 * Returns the content of the packet as String.
	 * 
	 * @return Returns the content of the packet as String.
	 */
	public String toString() {
		return data;
	}
	
	/**
	 * Returns the file name contained in the packet.
	 * 
	 * @return Returns the file name contained in the packet.
	 */
	public String getFileName() {
		return filename;
	}
	
	/**
	 * Returns the file size contained in the packet.
	 * 
	 * @return Returns the file size contained in the packet.
	 */
	public int getFileSize() {
		return size;
	}
}