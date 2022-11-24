package torent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import torent.peer.peer;

// spinning up peers
// called by provided code
public class peerProcess {
	
	public static void main(String args[]) throws IOException {
		// args will have the peer id for which you are to start
		//this method will be called multiple times, once for each peer
		
		int peerID = Integer.valueOf(args[0]);
		Map<Integer, peer> peersInfo = null;
		peer newPeer = null;
		// eventually setup any initial vars we'll need, not sure atm
		// what else may be needed
		try {
			peersInfo = readPeerConfig();
			//debugPeersInfo((HashMap<Integer, peer>) peersInfo);
			//System.out.println(peerID);
			newPeer = peersInfo.get(peerID);
			//System.out.println(newPeer.toString());
			newPeer.setPeerContactInfo(peersInfo);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print("Peers not confiugred");
			e.printStackTrace();
			System.exit(0);
		}
		// create the server object for each peer
		
		
		
		System.out.println("srv 1");
		server srv = new server(newPeer);
		
		Thread srvThread = new Thread(srv);
		srvThread.start();
		System.out.println("past srv 1");
		
		//----------------------------//
		
		//create a seperate thread for the server
		for (int id : peersInfo.keySet()) {
			System.out.print("spin cycle " + id);
			System.out.println(peerID);
			if (id < peerID) {
				System.out.println(id);
				System.out.println(peerID);
				client cl = new client(peersInfo.get(peerID), peersInfo.get(id));
				cl.connect();
			}
		}
		//** hardcoded test **//
		
		client cl = new client(peersInfo.get(1002), peersInfo.get(1001));
		cl.connect();
		
		//client cl2 = new client(peersInfo.get(1001), peersInfo.get(1002));
		//cl2.connect();
		//--------------------------------//
		// repeat previous steps for the client
		
		
		
	}
	
	public static HashMap<Integer, peer> readPeerConfig() throws IOException {
		HashMap<Integer, peer> peersInfo = new HashMap<Integer, peer>();
	

		try {
			File peerCFG = new File("C:\\Users\\zachk\\network\\torent\\PeerInfo.cfg");
			//System.out.println("Working Directory = " + System.getProperty("user.dir"));
			Scanner scrn = new Scanner(peerCFG);
			//int peerID, int port, String hostname, int hasFile
			while(scrn.hasNextLine()) {
				String line = scrn.nextLine(); 
				String[] vars = line.split(" ");
				peer newPeer = new peer(Integer.valueOf(vars[0]), Integer.valueOf(vars[2]), vars[1], Integer.valueOf(vars[3]));
				//System.out.println(newPeer.toString());
				peersInfo.put(newPeer.getPeerID(), newPeer);
			}
			scrn.close();
			
		}
		catch(Exception e) {
			System.out.print("Error reading peer config");
			e.printStackTrace();
		}
		
		
		//debugPeersInfo(peersInfo);
		return peersInfo;
	}
	
	public static void debugPeersInfo(HashMap<Integer, peer> peersInfo) {
		System.out.println(peersInfo.toString());
	}

}