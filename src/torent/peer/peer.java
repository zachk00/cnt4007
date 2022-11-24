package torent.peer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import torent.Common;
import torent.client;
import torent.server;

public class peer {

	private int peerID;
	private int port;
	String hostname;
	private ObjectOutputStream out;
	//Common commonProps = new Common();
	
	
	public int getHasFile() {
		return hasFile;
	}


	public void setHasFile(int hasFile) {
		this.hasFile = hasFile;
	}


	public Map<Integer, peer> getPeersInfo() {
		return peersInfo;
	}


	public void setPeersInfo(Map<Integer, peer> peersInfo) {
		this.peersInfo = peersInfo;
	}

	int hasFile;
	Map<Integer, peer> peersInfo;
	
	// peer ID linked to the info for creating connection (hostname, port num)
	Map<Integer, peer> peerContactInfo = new HashMap<Integer, peer>();
	
	public peer(int peerID, int port, String hostname, int hasFile) {
		this.peerID = peerID;
		this.port = port;
		this.hostname = hostname;
		this.hasFile = hasFile;
	}
	

	
	// reference note: port & host should be inside of the msg array
	public void transmit(ObjectOutputStream stream, byte[] msg) throws IOException {
		try {
			stream.writeObject(msg);
			stream.flush();
		}
		catch(Exception e) {
			System.out.println("Issue sending msg");
			e.printStackTrace();
		}
		System.out.println("transmitted");
		
	}
	
	public int getPeerID() {
		return peerID;
	}

	public void setPeerID(int peerID) {
		this.peerID = peerID;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Map<Integer, peer> getPeerContactInfo() {
		return peerContactInfo;
	}

	public void setPeerContactInfo(Map<Integer, peer> peerContactInfo) {
		this.peerContactInfo = peerContactInfo;
	}


	public ObjectOutputStream getOut() {
		return out;
	}


	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}


	@Override
	public String toString() {
		return "peer [peerID=" + peerID + ", port=" + port + ", hostname=" + hostname + ", hasFile=" + hasFile + "]";
	}
	
	
	
}
