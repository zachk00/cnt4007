package torent.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import torent.logger.pLogger;
import torent.peer.peer;

public class handler implements Runnable{
		
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private peer currPeer;
	private Socket socket;
	private volatile boolean running = true;
	private Message creator;
	private pLogger log;
	private int otherPeerID;
	
	
	public handler(ObjectOutputStream out, ObjectInputStream in, peer currPeer, Socket sock) {
		this.currPeer = currPeer;
		this.out = out;
		this.in = in;
		this.socket = sock;
		this.creator = new Message();
		this.log = new pLogger(String.valueOf(this.currPeer.getPeerID()));
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
						
						this.log.choked(String.valueOf(this.otherPeerID));
						break;
					case 1:
						// unchoke from neighbor
						
						
						this.log.unchoked(String.valueOf(this.otherPeerID));
						
						if(!this.currPeer.hasFile()) {
							
							// if peer receives unchoke msg
							// peer should respond with request message
							
							
							
							
						}
						
						break;
					case 2:
						// interested
						
						this.currPeer.addInterestedPeer(this.otherPeerID);
						this.log.recInterested(String.valueOf(this.otherPeerID));
						
						break;
					case 3:
						// not interested
						this.currPeer.removeInterestedPeer(this.otherPeerID);
						this.log.recNotInterested(String.valueOf(this.otherPeerID));
						break;
					case 4:
						// have
						break;
					case 5:
						// bitfield
						break;
					case 6:
						// request
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
							
							
							// respond with bitfield
							// create proper param object 
							// use message class to create byte array to send
	
							messageParams params = new messageParams();
							params.setBitfield(this.currPeer.getBitfield());
							byte[] response = creator.createMessage(5, params);
							this.currPeer.transmit(out, response);
							
							// extract the peerID of the other peer
							// save in to this peer's contact map
							byte[] otherPeerID = new byte[4];
							System.arraycopy(message, 28, otherPeerID, 0, 4);
							int id = ByteBuffer.wrap(otherPeerID).getInt();
							
							this.currPeer.getContact().put(id, out);
							
							log.recTCP(String.valueOf(id));
							
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
