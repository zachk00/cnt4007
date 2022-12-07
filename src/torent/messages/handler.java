package torent.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;


import torent.peer.peer;

public class handler implements Runnable{
		
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private peer currPeer;
	private Socket socket;
	private volatile boolean running = true;
	private Message creator;
	private int otherPeerID;
	
	
	public handler(ObjectOutputStream out, ObjectInputStream in, peer currPeer, Socket sock) {
		this.currPeer = currPeer;
		this.out = out;
		this.in = in;
		this.socket = sock;
		this.creator = new Message();
		
	}
	
	

	@Override
	public void run() {
		
		
		
		try{
			//Initial handshake
			byte[] handshake = creator.handshake(this.currPeer.getPeerID());
			this.currPeer.transmit(out, handshake);
			
		}
		catch(Exception e) {
			System.out.println("Inital Handshake Failed: " + this.currPeer.toString());
			e.printStackTrace();
		}
		
		while(running)
		{
			// if everyone has the file, we start terminating threads
			// when it reaches the end of this run method the handler thread will exit
			if (this.currPeer.hasFile() && this.currPeer.neighborsHaveFile()) {
				stop();
			}
			try {
				byte [] message = (byte[])in.readObject();
				
				int type = creator.getType(message);
				int pieceIndex;
				byte[] piece;
				switch (type) {
					case 0:
						// choked by neighbor
						this.currPeer.getLog().choked(String.valueOf(this.otherPeerID));
						break;
					case 1:
						// unchoke from neighbor
						
						
						this.currPeer.getLog().unchoked(String.valueOf(this.otherPeerID));
						
						if(!this.currPeer.hasFile()) {
							
							// if peer receives unchoke msg
							// peer should respond with request message
							BitSet fromPieces = this.currPeer.getPeersInfo().get(this.otherPeerID).getBitfield();
							BitSet interestingPieces = this.currPeer.findInterestingPieces(fromPieces);
							int requestIndex  = this.currPeer.getRequestIndex(interestingPieces);
							
							messageParams params = new messageParams();
							params.setPieceIndex(requestIndex);
							
							byte[] response = creator.createMessage(6, params);
							
							this.currPeer.transmit(out, response);
							
							

						}
						
						break;
					case 2:
						// interested
						
						this.currPeer.addInterestedPeer(this.otherPeerID);
						this.currPeer.getLog().recInterested(String.valueOf(this.otherPeerID));
						
						break;
					case 3:
						// not interested
						this.currPeer.removeInterestedPeer(this.otherPeerID);
						this.currPeer.getLog().recNotInterested(String.valueOf(this.otherPeerID));
						break;
					case 4:
						// have
						byte[] temp = creator.getPayload(message);
						pieceIndex = ByteBuffer.wrap(temp).getInt();
						
						this.currPeer.getLog().recHave(String.valueOf(this.otherPeerID), String.valueOf(pieceIndex));
						
						this.currPeer.updateBitfield(this.otherPeerID, pieceIndex);
						
						
						// take the new updated bitset
						// compare it to this peer's bitset
						// place the result of that comparison in a new bitset
						// if that resulting bitset has any 'set' bits that means we found at least 1 interesting piece
						
						BitSet newFromBitField = this.currPeer.getPeersInfo().get(this.otherPeerID).getBitfield();
						BitSet newBitFieldInterestingPieces = this.currPeer.findInterestingPieces(newFromBitField);
						
						
						// if the other peer has stuff we want now, send interested
						// otherwise say we arent interested
						
						messageParams params  = new messageParams();
						
						if (newBitFieldInterestingPieces.nextSetBit(0) != -1) {
							this.currPeer.transmit(out, creator.createMessage(2, params));
						}
						else {
							this.currPeer.transmit(out, creator.createMessage(3, params));
						}

						
						break;
					case 5:
						// bitfield
						System.out.println("got bifeild mesg");
						byte[] payload = creator.getPayload(message);
						
						BitSet fromBitfield = BitSet.valueOf(payload);
						
						BitSet currPeerBitfield = (BitSet) this.currPeer.getBitfield().clone();
						
						// if the bitsets are the same, this peer is not interested
						
						if (currPeerBitfield.equals(fromBitfield)) {
							
							System.out.println("equal bitfield");
							
							messageParams msg = new messageParams();
							
							byte[] response = this.creator.createMessage(3, msg);
							
							this.currPeer.transmit(out, response);
							System.out.println("sent not intr");
							
						}
						else {
						
							System.out.println("differing bitfields " + this.otherPeerID + " " + this.currPeer.getPeerID());
										
							// determine what pieces fromPeer has that we dont
							BitSet interestingPieces = this.currPeer.findInterestingPieces(fromBitfield);
							if (!interestingPieces.isEmpty()) {
								System.out.println(interestingPieces.toString());
								//store them & send not/interested msg
								System.out.println(this.otherPeerID);
								this.currPeer.setPeersInterestingPieces(this.otherPeerID, interestingPieces);
							}
							
							
							
						}
						
						
						break;
					case 6:
						// request
						
						byte[] data = this.creator.getPayload(message);
						pieceIndex = ByteBuffer.wrap(data).getInt();
						
						System.out.println("piece index " + pieceIndex);
						
						if (!this.currPeer.isChocked(this.otherPeerID)) {
							
							// if the requesting peer isnt choked send them their requested piece
							piece = this.currPeer.getFile()[pieceIndex];
							
							messageParams params = new messageParams();
							
							params.setPieceField(piece);
							params.setPieceIndex(pieceIndex);
							
							byte[] response = this.creator.createMessage(7, params);
							
							this.currPeer.transmit(out, response);
							
						}
						
						
						break;
					case 7:
						// get piece and piece index
						// save to file[][]
						int pieceStart = message.length - 9;
						
						
						byte[] tempIndex = new byte[4];
						System.arraycopy(message, 5, tempIndex, 0, 4);
						pieceIndex = ByteBuffer.wrap(tempIndex).getInt();						
						
						
						piece = new byte[pieceStart];
						
						System.arraycopy(message, pieceStart, piece, 0, piece.length);
						
						
						this.currPeer.getFile()[pieceIndex] = piece;
						this.currPeer.getLog().pieceDownloaded(String.valueOf(this.otherPeerID),String.valueOf(pieceIndex), String.valueOf(this.currPeer.numberOfPieces));
						// send have msg to all other peers
						// update how many pieces we've received from them
						this.currPeer.getPeersInfo().get(this.otherPeerID).incrementPiecesReceived();
						this.currPeer.incrementPiecesDownload();
						for (int id : this.currPeer.getContact().keySet()) {
							if (id != this.currPeer.getPeerID()) {
								messageParams params = new messageParams();
								params.setPieceIndex(pieceIndex);
								
								byte[] response = creator.createMessage(4, params);
								
								this.currPeer.transmit(this.currPeer.getContact().get(id), response);
							}
						}
						
						// if we have all the file
						// else send another request
						
						if (this.currPeer.hasFile()) {
							this.currPeer.writeFile();
							this.currPeer.getLog().fileDownloaded();
						}
						else {
							BitSet fromPieces = this.currPeer.getPeersInfo().get(this.otherPeerID).getBitfield();
							BitSet interestingPieces = this.currPeer.findInterestingPieces(fromPieces);
							int requestIndex  = this.currPeer.getRequestIndex(interestingPieces);
							
							messageParams params = new messageParams();
							params.setPieceIndex(requestIndex);
							
							byte[] response = creator.createMessage(6, params);
							
							this.currPeer.transmit(out, response);
						}
						
						
						
						break;
					default:
						// handshake
						ByteBuffer handshake = ByteBuffer.wrap(message);
						byte[] temps = new byte[18];
						
						handshake.get(temps, 0, 18);
						
						String handShakeString = new String(temps, StandardCharsets.UTF_8);
						
						if(handShakeString.equalsIgnoreCase(this.creator.handshakeMSG)) {
							
							
							// extract the peerID of the other peer
							// save in to this peer's contact map
							byte[] otherPeer = new byte[4];
							System.arraycopy(message, 28, otherPeer, 0, 4);
							int id = ByteBuffer.wrap(otherPeer).getInt();
							this.otherPeerID = id;
							this.currPeer.getContact().put(id, out);
							
							this.currPeer.getLog().recTCP(String.valueOf(id));
							
							
							
							
							// respond with bitfield
							// create proper param object 
							// use message class to create byte array to send
	
							messageParams params = new messageParams();
							
							
							
							
							params.setBitfield(this.currPeer.getBitfield());
							byte[] response = creator.createMessage(5, params);
							System.out.println("sending bitfield msg after handshake " + this.currPeer.getPeerID() + " " + this.otherPeerID + " " + id);
							this.currPeer.transmit(out, response);
							
					
						}
						
				}
				
				
			}
			catch (Exception e) {
				System.out.println("Error processing message in handler");
				e.printStackTrace();
			}
		
		}
		
	}
	
	public void stop() {
		this.running = false;
	}
	
}
