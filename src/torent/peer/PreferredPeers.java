package torent.peer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.stream.Collectors;

import torent.messages.Message;
import torent.messages.NullParamException;
import torent.messages.messageParams;

import java.util.HashMap;

public class PreferredPeers extends TimerTask {
	
	peer currPeer;
	Message creator;
	
	PreferredPeers(peer currPeer) {
		this.currPeer = currPeer;
	}
	
	void choke(int peerId) {
		try {
			messageParams msgParams = new messageParams();
			
			// Choke has message type 0
			byte[] msg = this.creator.createMessage(0,msgParams);
			
			ObjectOutputStream out = this.currPeer.getContact().get(peerId);
		
			this.currPeer.transmit(out, msg);
			
		// Funky catch... may want to look into reworking
		} catch (IOException e) {
			System.out.println("Error processing message in PreferredPeers");
			e.printStackTrace();
		} catch (NullParamException e) {
			System.out.println("Error processing message in PreferredPeers");
			e.printStackTrace();
		}
	}
	
	void unchoke(int peerId) {
		try {
			messageParams msgParams = new messageParams();
			
			// Unchoke has message type 1
			byte[] msg = this.creator.createMessage(1,msgParams);
			
			ObjectOutputStream out = this.currPeer.getContact().get(peerId);
		
			this.currPeer.transmit(out, msg);
			
		// Funky catch... may want to look into reworking
		} catch (IOException e) {
			System.out.println("Error processing message in PreferredPeers");
			e.printStackTrace();
		} catch (NullParamException e) {
			System.out.println("Error processing message in PreferredPeers");
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		// Who is interested in currPeer's data?
		List<Integer> interestedPeers = this.currPeer.getInterestedPeers();
		
		// Initialize the list of preferred peers
		List<Integer> preferredPeers = new ArrayList<Integer>();
		
		// Preferred Peers Limit
		int k = this.currPeer.getNumberOfPreferredNeighbors();
		
		// If the current peer has the file, pick random
		if (this.currPeer.hasFile()){
			Random rand = new Random();
			int randIdx;
			
			for (int i = 0; i < k; i++) {
				randIdx = rand.nextInt(interestedPeers.size());
				preferredPeers.add(interestedPeers.get(randIdx));
			}
		} else {
			// Compare download rates of all interested peers
			// Sorted TreeMap of "speed" and corresponding peerIds
			TreeMap<Integer, List<Integer>> peerDownloadSpeeds = new TreeMap<Integer, List<Integer>>(Collections.reverseOrder());
			
			for (int peerId : interestedPeers) {
				int piecesDownloaded = this.currPeer.getPeersInfo().get(peerId).getPiecesDownloaded();
				
				if (!peerDownloadSpeeds.containsKey(piecesDownloaded)) {
					peerDownloadSpeeds.put(piecesDownloaded, new ArrayList<Integer>());
				}
				peerDownloadSpeeds.get(piecesDownloaded).add(peerId);
			}
			
			// Randomize ties, then add to preferredPeers
			for (List<Integer> peerIds : peerDownloadSpeeds.values()) {
				Collections.shuffle(peerIds);
				preferredPeers.addAll(peerIds);
			}
			
			// Only keep the top k preferredPeers
			preferredPeers = preferredPeers.stream().limit(k).collect(Collectors.toList());
		}
		
		
		List<Integer> unchokedPeers = this.currPeer.getUnchokedPeers();
		List<Integer> chokedPeers = this.currPeer.getChokedPeers();
		
		// Choke Slow Peers: ie. for unchokedPeers, if not in preferredPeers, choke
		for (Integer currUnchokedPeerId : unchokedPeers) {
			
			// If the currently Unchoked Peer is not in the latest preferred peers, choke
			if (!preferredPeers.contains(currUnchokedPeerId)) {
				this.choke(currUnchokedPeerId);
				chokedPeers.add(currUnchokedPeerId);
				unchokedPeers.remove(unchokedPeers.indexOf(currUnchokedPeerId));
			}
		}
		
		// Unchoke Fast Peers: ie. for all preferredPeers, if in unchokedPeers, skip. else: unchoke
		for (Integer preferredPeerId : preferredPeers) {
			if (!unchokedPeers.contains(preferredPeerId)) {
				this.unchoke(preferredPeerId);
				unchokedPeers.add(preferredPeerId);
				chokedPeers.remove(preferredPeerId);
			}
		}
		
		// Reset all piecesDownloaded
		for (peer neighborPeer : this.currPeer.getPeersInfo().values()) {
			neighborPeer.setPiecesDownloaded(0);
		}
	}
	
	public void main() {
	}

}
