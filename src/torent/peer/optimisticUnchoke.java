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

public class optimisticUnchoke extends TimerTask {

	peer currPeer;
	Message creator;

	optimisticUnchoke(peer currPeer) {
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
			System.out.println("Error processing message in optimisticUnchoke");
			e.printStackTrace();
		} catch (NullParamException e) {
			System.out.println("Error processing message in optimisticUnchoke");
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
			System.out.println("Error processing message in optimisticUnchoke");
			e.printStackTrace();
		} catch (NullParamException e) {
			System.out.println("Error processing message in optimisticUnchoke");
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		// How often are we checking for opt?
		int m = this.currPeer.getOptimisticUnchokingInterval();

        List<Integer> interestedPeers = this.currPeer.getInterestedPeers();
		List<Integer> chockedPeers = this.currPeer.getChokedPeers();

        ArrayList<Integer> chockedAndInterested = new ArrayList<Integer>();
        for (int i = 0; i < chockedPeers.size(); i++){
            for (int j = 0; j <interestedPeers.size(); j++){
                if (chockedPeers.indexOf(i) == interestedPeers.indexOf(j)){
                    chockedAndInterested.add(chockedPeers.indexOf(i));
                }
            }
        }

		// If the current peer has the file, pick random optimistic from union of chocked and interested
		if (this.currPeer.hasFile()){
			Random rand = new Random();
			int randIdx = rand.nextInt(chockedAndInterested.size());
            int optimisticallyUnchockedPeer = chockedAndInterested.get(randIdx); //this is what we were looking for
		}
	}

	public void main() {
	}
}