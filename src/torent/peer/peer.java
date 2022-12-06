package torent.peer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import torent.Common;
import torent.client;
import torent.server;
import torent.logger.pLogger;
import torent.messages.Message;

public class peer {
	//props from PeerInfo.cfg
	private int peerID;
	private int port;
	String hostname;
	int hasFile;
	
	// peer attributes
	private ObjectOutputStream out;
	private BitSet bitfield;
	List<Integer> interestedPeers;
	
	// tools
	private Message creator;
	private pLogger log;
	
	// Common commonProps = new Common();
	// common attributes
	int numberOfPreferredNeighbors;
	int unchokingInterval;
	int optimisticUnchokingInterval;
	String filename;
	int fileSize;
	int pieceSize;

	//derived attributes
	
	int numberOfPieces;
	int piecesDownloaded;
	
	byte[][] file;
	
	
	// contact info for other peers
	
	HashMap<Integer, ObjectOutputStream> contact;
	Map<Integer, peer> peersInfo;
	
	public peer(int peerID, int port, String hostname, int hasFile) {
		this.peerID = peerID;
		this.port = port;
		this.hostname = hostname;
		this.hasFile = hasFile;
		this.interestedPeers = new ArrayList<>();
		this.creator = new Message();
		this.log  = new pLogger(String.valueOf(this.peerID));
		this.contact = new HashMap<Integer, ObjectOutputStream>();
		
	}
	
	
	
	
	public int getNumberOfPreferredNeighbors() {
		return numberOfPreferredNeighbors;
	}

	public void setNumberOfPreferredNeighbors(int numberOfPreferredNeighbors) {
		this.numberOfPreferredNeighbors = numberOfPreferredNeighbors;
	}

	public int getUnchokingInterval() {
		return unchokingInterval;
	}

	public void setUnchokingInterval(int unchokingInterval) {
		this.unchokingInterval = unchokingInterval;
	}

	public int getOptimisticUnchokingInterval() {
		return optimisticUnchokingInterval;
	}

	public void setOptimisticUnchokingInterval(int optimisticUnchokingInterval) {
		this.optimisticUnchokingInterval = optimisticUnchokingInterval;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public int getFileSize() {
		return fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	public int getPieceSize() {
		return pieceSize;
	}

	public void setPieceSize(int pieceSize) {
		this.pieceSize = pieceSize;
	}

	public void addInterestedPeer(int peerID) {
		if (!this.interestedPeers.contains(peerID)) {
			this.interestedPeers.add(peerID);
		}
	}
	
	public void removeInterestedPeer(int peerID) {
		if (this.interestedPeers.contains(peerID)) {
			this.interestedPeers.remove(Integer.valueOf(peerID));
		}
	}
	
	public boolean hasFile() {
		return this.hasFile == 1;
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

	public BitSet getBitfield() {
		return bitfield;
	}

	public void setBitfield() {
		this.numberOfPieces = this.fileSize / this.pieceSize;
		this.bitfield = new BitSet(this.numberOfPieces);
		
		if(this.hasFile()) {
			this.bitfield.set(0, this.numberOfPieces, true);
			this.piecesDownloaded = this.numberOfPieces;
		}
		else {
			this.bitfield.set(0, this.numberOfPieces, false);
		}
		this.setFile();
	}

	
	
	public byte[][] getFile() {
		return file;
	}




	public void setFile() {
		this.file = new byte[this.numberOfPieces][];
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



	public HashMap<Integer, ObjectOutputStream> getContact() {
		return contact;
	}


	public void setContact(HashMap<Integer, ObjectOutputStream> contact) {
		this.contact = contact;
	}


	public ObjectOutputStream getOut() {
		return out;
	}


	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}
	
	public void updateBitfield(int requestIndex) {
		
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
	
	
	public boolean neighborsHaveFile() {
		for (peer neighbor : this.peersInfo.values()) {
			if (!neighbor.hasFile()) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return "peer [peerID=" + peerID + ", port=" + port + ", hostname=" + hostname + ", hasFile=" + hasFile + "]";
	}
	
	
	
	
}
