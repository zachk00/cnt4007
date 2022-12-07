package torent.peer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import torent.logger.pLogger;
import torent.messages.Message;
import torent.messages.NullParamException;
import torent.messages.messageParams;

public class peer {
	//props from PeerInfo.cfg
	private int peerID;
	private int port;
	String hostname;
	int hasFile;
	
	// peer attributes
	private ObjectOutputStream out;
	private BitSet bitfield;
	private List<Integer> interestedPeers;
	private List<Integer> chockedPeers;
	private List<Integer> unchockedPeers;
	private int optimisticallyUnchockedPeer;
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
	
	public int numberOfPieces; //config var DO NOT CHANGE
	int piecesDownloaded; // how many the peer has gathered thus far
	byte[][] file;
	
	int piecesRecieved; // **** peerOne.peersInfo.get(otherPeerID).getPiecesReceived) is how many pieces peerOne has gotten from 'otherPeerID'
	double rate; 
	
	
	
	HashMap<Integer, ObjectOutputStream> contact;
	Map<Integer, peer> peersInfo;
	HashMap<Integer, BitSet> peersInterestingPieces;
	
	
	public peer(int peerID, int port, String hostname, int hasFile) {
		this.peerID = peerID;
		this.port = port;
		this.hostname = hostname;
		this.hasFile = hasFile;
		this.interestedPeers = new ArrayList<Integer>();
		this.creator = new Message();
		this.setLog(new pLogger(String.valueOf(this.peerID)));
		this.contact = new HashMap<Integer, ObjectOutputStream>();
		this.peersInterestingPieces = new HashMap<Integer, BitSet>();
		
		this.unchockedPeers = new ArrayList<Integer>();
		this.chockedPeers = new ArrayList<Integer>();
		

	}
	
	
	
	public int getPiecesRecieved() {
		return piecesRecieved;
	}

	public void resetPieces() {
		this.piecesRecieved = 0;
	}

	public void incrementPiecesReceived() {
		this.piecesRecieved++;
	}
	
	public void incrementPiecesDownload() {
		this.piecesDownloaded++;
	}
	
	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}
	
	public HashMap<Integer, BitSet> getPeersInterestingPieces() {
		return peersInterestingPieces;
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
	
	public void initNumberOfPieces() {
		this.numberOfPieces = this.fileSize / this.pieceSize;
	}
	
	public void initBitfield() {
		
		this.bitfield = new BitSet(this.numberOfPieces);
		
		if(this.hasFile()) {
			this.bitfield.set(0, this.numberOfPieces, true);
			this.piecesDownloaded = this.numberOfPieces;
		}
		else {
			this.bitfield.set(0, this.numberOfPieces, false);
		}
		
	}
	
	public void initPiecesDownloaded() {
		if (this.hasFile()) {
			this.piecesDownloaded = this.numberOfPieces;
		}
		else {
			this.piecesDownloaded = 0;
		}
	}
	
	public void initFile() {
		this.file = new byte[this.numberOfPieces][];
	}
	
	public void updateBitfield(int id, int pieceIndex) {
		
		this.peersInfo.get(id).getBitfield().set(pieceIndex, true);
		
		for (int i = 0; i < this.numberOfPieces; i++) {
			if (!this.peersInfo.get(id).getBitfield().get(i)) {
				return;
			}
		}
		
		if (this.getPeersInfo().get(id).piecesDownloaded == this.numberOfPieces) {
			this.getPeersInfo().get(id).setHasFile(1);
		}
		
	}
	
	public byte[][] getFile() {
		return file;
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

	public void setPeersInterestingPieces(int fromPeerID, BitSet fromBitSet) throws IOException, NullParamException {
		
		// need to handle whether we already do/dont have this peer's bitset
		// whether theres anything interesting in that peer'sbitset
		
		messageParams params = new messageParams();
		
		
		
		if (this.peersInterestingPieces.containsKey(fromPeerID)) {
			BitSet latest = this.peersInterestingPieces.get(fromPeerID);
			if (latest.isEmpty()) {
				
				 // the peer has nothing for us, so we dont need to store their bitfield
				// we also need to send them a not interested msg
				
				this.peersInterestingPieces.remove(fromPeerID);
				
				try {
					byte [] msg = creator.createMessage(3 , params);
					this.transmit(this.contact.get(fromPeerID), msg);
				}
				catch(Exception e) {
					System.out.println("Error transmiting not interested msg after receiving bitfield");
					e.printStackTrace();
				}
			}
			else {
				if (!latest.isEmpty())
				this.peersInterestingPieces.replace(fromPeerID, latest);
				
				try {
					byte [] msg = creator.createMessage(2 , params);
					this.transmit(this.contact.get(fromPeerID), msg);
				}
				catch(Exception e) {
					System.out.println("Error transmiting interested msg after receiving bitfield");
					e.printStackTrace();
				}
			}
		}
		else {
			this.peersInterestingPieces.put(fromPeerID, fromBitSet);
			
			try {
				byte [] msg = creator.createMessage(2 , params);
				this.transmit(this.contact.get(fromPeerID), msg);
			}
			catch(Exception e) {
				System.out.println("Error transmiting interested msg after receiving bitfield");
				e.printStackTrace();
			}
		}
		
		
	}
	
	
	public BitSet findInterestingPieces(BitSet fromBitfield) {
		
		// 
		// compare this peer's bitfield with given bitset
		BitSet comparison = (BitSet) fromBitfield.clone();
		BitSet result = new BitSet(this.numberOfPieces);
		
		for (int i = 0; i < this.numberOfPieces; i++) {
			if (comparison.get(i) && !this.bitfield.get(i)) {
				result.set(i);
			}
		}
		
		
		// return bitset that has the interesting pieces demarkated as true
		
		// so ex. this.peer bitset =  1 0 1 1 1
	    // the from bitfield has 1 1 0 0 0
		// our bitfield needs the 2nd piece and the from peer has it so we return
		// 0 1 0 0 0
		
		return result;
	}
	
	public int getRequestIndex(BitSet interestingPieces) {
		int index = 0;
		
		//TODO
		
		
		
		//given bitset of interesting pieces
		// randomly select one and return the index
		
		
		return index;
	}
	
	// feel free to change method signature as needed/ add helpers methods
	//will call this from peerProcess for each peer to start the process
	// it calls prefferd peers every X interval based on config
	//probably need a thread
	public void runPreferredPeers() {
		// TODO
	}
	
	// feel free to change method signature as needed/ add helpers methods
	// selected new preffered peers
	public void preferredPeers() {
		// TODO
	}
	
	
	// feel free to change method signature as needed/ add helpers methods
	//will call this from peerProcess for each peer to start the process
	// it calls poptimisticUnchoke every X interval based on config
	//probably need a thread
	public void runOptimistic() {
		// TODO
	}
	
	// feel free to change method signature as needed/ add helpers methods
	// select optimisticUnchoke neighbors
	public void optimisticUnchoke() {
		// TODO
	}
	
	public boolean isChocked(int peerID) {
		return this.chockedPeers.contains(peerID);
	}
	
	// reference note: port & host should be inside of the msg array
	public void transmit(ObjectOutputStream stream, byte[] msg) throws IOException {
		try {
			stream.writeObject(msg);
			stream.flush();
			System.out.println("transmitted");
		}
		catch(Exception e) {
			System.out.println("Issue sending msg");
			e.printStackTrace();
		}
		
		
	}
	
	
	public boolean neighborsHaveFile() {
		for (peer neighbor : this.peersInfo.values()) {
			if (!neighbor.hasFile()) {
				return false;
			}
		}
		
		return true;
	}
	
	public void readFile() {
	   // TODO
	}
	
	
	
	public void writeFile() {
		
		//TODO
		// all the data for the file is stored in the byte[][] file
		// save it to a file in the appropiate folder
		// you can assume the folder already exists
		// we will have the folders created before we run anything
	}
	
	
	
	@Override
	public String toString() {
		return "peer [peerID=" + peerID + ", port=" + port + ", hostname=" + hostname + ", hasFile=" + hasFile + "]";
	}




	public pLogger getLog() {
		return log;
	}




	public void setLog(pLogger log) {
		this.log = log;
	}
	
	
	
	
}
