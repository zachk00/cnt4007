package torent.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;

import torent.logger.pLogger;
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
						
						int pieceIndex;
						
						byte[] data = this.creator.getPayload(message);
						pieceIndex = ByteBuffer.wrap(data).getInt();
						
						System.out.println("piece index " + pieceIndex);
						
						if (!this.currPeer.isChocked(this.otherPeerID)) {
							byte[] piece = this.currPeer.getFile()[pieceIndex];
							
							messageParams params = new messageParams();
							
							params.setPieceField(piece);
						}
						
						
						break;
					case 7:
						// piece
						
						break;
					default:
						// handshake
						ByteBuffer handshake = ByteBuffer.wrap(message);
						byte[] temp = new byte[18];
						
						handshake.get(temp, 0, 18);
						
						String handShakeString = new String(temp, StandardCharsets.UTF_8);
						
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
