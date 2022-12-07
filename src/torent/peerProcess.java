package torent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
		Map<String, String> peersCommon = null;
		peer newPeer = null;
		
		// eventually setup any initial vars we'll need, not sure atm
		// what else may be needed
		try {
			peersInfo = readPeerConfig();
			peersCommon = readCommonConfig();
			//debugPeersInfo((HashMap<Integer, peer>) peersInfo);
			//System.out.println(peerID);
			newPeer = peersInfo.get(peerID);
			//System.out.println(newPeer.toString());
			newPeer.setPeersInfo(peersInfo);
			
			newPeer.setFilename(peersCommon.get("FileName"));
			newPeer.setFileSize(Integer.valueOf(peersCommon.get("FileSize")));
			newPeer.setNumberOfPreferredNeighbors(Integer.valueOf(peersCommon.get("NumberOfPreferredNeighbors")));
			newPeer.setOptimisticUnchokingInterval(Integer.valueOf(peersCommon.get("OptimisticUnchokingInterval")));
			newPeer.setPieceSize(Integer.valueOf(peersCommon.get("PieceSize")));
			newPeer.setUnchokingInterval(Integer.valueOf(peersCommon.get("UnchokingInterval")));
			
			//setBitfield should only becalue if the following properites have been set
			// piece size
			// file size
			// hasFile
			newPeer.initNumberOfPieces();
			newPeer.initBitfield();
			newPeer.initPiecesDownloaded();
			newPeer.initFile();
			
			if (newPeer.hasFile()) {
				newPeer.readFile();	
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print("Peer not confiugred");
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
		
		
		try {
			peer newPeers = null;
			peersInfo = readPeerConfig();
			peersCommon = readCommonConfig();
			//debugPeersInfo((HashMap<Integer, peer>) peersInfo);
			//System.out.println(peerID);
			newPeers = peersInfo.get(1002);
			//System.out.println(newPeer.toString());
			newPeers.setPeersInfo(peersInfo);
			
			newPeers.setFilename(peersCommon.get("FileName"));
			newPeers.setFileSize(Integer.valueOf(peersCommon.get("FileSize")));
			newPeers.setNumberOfPreferredNeighbors(Integer.valueOf(peersCommon.get("NumberOfPreferredNeighbors")));
			newPeers.setOptimisticUnchokingInterval(Integer.valueOf(peersCommon.get("OptimisticUnchokingInterval")));
			newPeers.setPieceSize(Integer.valueOf(peersCommon.get("PieceSize")));
			newPeers.setUnchokingInterval(Integer.valueOf(peersCommon.get("UnchokingInterval")));
			
			//setBitfield should only becalue if the following properites have been set
			// piece size
			// file size
			// hasFile
			newPeers.initNumberOfPieces();
			newPeers.initBitfield();
			newPeers.initPiecesDownloaded();
			newPeers.initFile();
			
			if (newPeer.hasFile()) {
				newPeer.readFile();	
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.print("Peer not confiugred");
			e.printStackTrace();
			System.exit(0);
		}
		
		
		 client cl = new client(peersInfo.get(1002), peersInfo.get(1001));
		 cl.connect();

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
	
	public static HashMap<String, String> readCommonConfig() throws IOException {
		HashMap<String, String> commonConfig = new HashMap<String, String>();
		
		try {
			File peerCFG = new File("C:\\Users\\zachk\\network\\torent\\Common.cfg");
			//System.out.println("Working Directory = " + System.getProperty("user.dir"));
			Scanner scrn = new Scanner(peerCFG);
			
			while(scrn.hasNextLine()) {
				String line = scrn.nextLine(); 
				String[] vars = line.split(" ");
				
				commonConfig.put(vars[0], vars[1]);
			}
			scrn.close();
		}
		catch(Exception e) {
			System.out.println("Error reading common config");
		}
		
		
		return commonConfig;
	}
	
	
	
	
	public static void debugPeersInfo(HashMap<Integer, peer> peersInfo) {
		System.out.println(peersInfo.toString());
	}

}
