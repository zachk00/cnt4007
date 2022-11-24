package torent;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import torent.messages.handler;
import torent.peer.peer;

public class server implements Runnable{
	
	
	private peer currPeer;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	public server(peer curr) {
		this.currPeer = curr;
	}
	
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		ServerSocket listeningSocket = null;
		Socket socket;
		System.out.println("in srv");
		
		try {
			System.out.println("listen on: "  + currPeer.getPort());
			listeningSocket = new ServerSocket(currPeer.getPort());
		}
		catch(Exception e) {
			System.out.print("Issue with listening socket");
			e.printStackTrace();
		}
		try {
			while(true) {
				socket = listeningSocket.accept();
				out = new ObjectOutputStream(socket.getOutputStream());
				out.flush();
				
				in = new ObjectInputStream(socket.getInputStream());
				currPeer.setOut(out);
				handler hdr = new handler(out, in, this.currPeer, socket);
				
				Thread handlerThread = new Thread(hdr);
				handlerThread.start();
			}
		}
		catch(Exception e) {
			System.out.println("Server issue");
			e.printStackTrace();
		}
		finally {
			try {
				listeningSocket.close();
			}
			catch(Exception e) {
				System.out.println("Closing socket in server failed");
				e.printStackTrace();
			}
		}
	}

}
