package torent;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import torent.messages.handler;
import torent.messages.message;
import torent.peer.peer;

public class client {

	private ObjectOutputStream out;
	private ObjectInputStream in;
	private peer currPeer, receiverPeer;
	
	public ObjectOutputStream getOut() {
		return out;
	}

	public void setOut(ObjectOutputStream out) {
		this.out = out;
	}

	public ObjectInputStream getIn() {
		return in;
	}

	public void setIn(ObjectInputStream in) {
		this.in = in;
	}

	public client(peer currPeer, peer receiverPeer) {
		this.currPeer = currPeer;
		this.receiverPeer = receiverPeer;
	}
	
	public void connect() throws UnknownHostException, IOException {
		// should add try catch statement to see in case host/port problem
		System.out.println(this.receiverPeer.getHostname());
		System.out.println(this.receiverPeer.getPort());
		System.out.println(this.receiverPeer.getPeerID());
		Socket socket = new Socket("localhost", this.receiverPeer.getPort());
		
		out = new ObjectOutputStream(socket.getOutputStream());
		out.flush();
		
		in = new ObjectInputStream(socket.getInputStream());
		currPeer.setOut(out);
		
		System.out.println("Spinning up client");
		
		/*handler hdr = new handler(out, in, this.currPeer, socket);
		
		Thread handlerThread = new Thread(hdr);
		handlerThread.start();
		
		*/
		
		currPeer.transmit(out,message.handshake(receiverPeer.getPeerID()));
		
		
		
		
	}
}
