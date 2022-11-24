package torent.messages;

import java.util.BitSet;

public class messageParams {
	
	private byte[] payload;
	private BitSet bitfield;
	private int pieceIndex = -1;
	private byte[] pieceField;
	
	
	public messageParams() {}
	
	
	public Object getParam(String param) throws NullParamException{

		switch(param) {
			case "payload":
				if (this.payload == null) {
					throw new NullParamException("Payload not set. (NULL)");
				}
				return this.payload;
				
			case "bitfield":
				
				if (this.bitfield == null) {
					throw new NullParamException("Bit field not set. (NULL)");
				}
				return this.bitfield;
				
			case "pieceIndex":
				if (this.pieceField == null) {
					throw new NullParamException("Piece field not set. (NULL)");
				}
				return this.pieceIndex;
				
			case "pieceField":
				if (this.pieceField == null) {
					throw new NullParamException("Piece field not set. (NULL)");
					
				}
				return this.pieceField;
			default:
				System.out.println("Requested param does not exist");
				return null;
		}
		
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
