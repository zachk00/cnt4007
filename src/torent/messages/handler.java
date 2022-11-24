package torent.messages;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import torent.peer.peer;

public class handler implements Runnable{
		
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private peer currPeer;
	private Socket socket;
	private message creator = new message();
	
	public handler(ObjectOutputStream out, ObjectInputStream in, peer currPeer, Socket sock) {
		this.currPeer = currPeer;
		this.out = out;
		this.in = in;
		this.socket = sock;
	}
	
	

	@Override
	public void run() {
		
		
		
		try{
		while(true)
		{
			System.out.println("handler while");
			out = new ObjectOutputStream(this.socket.getOutputStream());
			out.flush();
			System.out.println("outs");
			in = new ObjectInputStream(this.socket.getInputStream());
			System.out.println("in sock");
			//receive the message sent from the client
			String message = (String)in.readObject();
			System.out.println("read sock");
			//show the message to the user
			System.out.println("READ : " + message);
			
		}
		}
		catch(Exception e) {
			System.out.println("AH");
			e.printStackTrace();
		}
		
		
	}
	
}
