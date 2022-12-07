package torent.messages;

import java.util.BitSet;

/**
 * @author zachk
 *
 */
public class messageParams {
	
	private byte[] payload;
	private BitSet bitfield;
	private int pieceIndex = -1;
	private byte[] pieceField;
	
	
	public messageParams() {}
	
	
	

	public byte[] getPayload() throws NullParamException {
		
		if (this.payload == null) {
			throw new NullParamException("Payload not set. (NULL)");
		}
		return this.payload;
	}




	public BitSet getBitfield() throws NullParamException {
		
		if (this.bitfield == null) {
			throw new NullParamException("bitfield not set. (NULL)");
		}
		return bitfield;
	}




	public int getPieceIndex() throws NullParamException{
		
		if (this.pieceIndex == -1) {
			throw new NullParamException("piece index not set. (NULL)");
		}
		
		return pieceIndex;
	}




	public byte[] getPieceField() throws NullParamException {
		
		if (this.pieceField == null) {
			throw new NullParamException("piece field not set. (NULL)");
		}
		return pieceField;
	}




	public void setPayload(byte[] payload) {
		this.payload = payload;
	}


	public void setBitfield(BitSet bitfield) {
		this.bitfield = bitfield;
	}


	public void setPieceIndex(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}


	public void setPieceField(byte[] pieceField) {
		this.pieceField = pieceField;
	};
	
	
	
}
