package torent.messages;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class message {
	
	final static String handshakeMSG = "P2PFILESHARINGPROJ";
	
	public message() {}
	
	public static byte[] handshake(int peerID) {
		byte[] header = handshakeMSG.getBytes();
		byte[] zeros = new byte[10];
		
		ByteBuffer temp = ByteBuffer.allocate(4);
		temp.putInt(peerID);
		byte[] id = temp.array();
		
		byte[] handshakeTemp = new byte[header.length + zeros.length + id.length];
		
		ByteBuffer handshake = ByteBuffer.wrap(handshakeTemp);
		handshake.put(header);
		handshake.put(zeros);
		handshake.put(id);
		
		return handshake.array();
		
	}
	
	public byte[] createMessage(int type, messageParams params) throws IOException, NullParamException {
		
		ByteArrayOutputStream message = new ByteArrayOutputStream();
		
		
		byte[] payload;
		ByteBuffer length;
		
		int messageLength = 0;
		
		byte[] messageType = new byte[1];
		messageType[0] = (byte) type;
		
		
		
		switch(type) {
			case 0:
			case 1:
			case 2:
			case 3:
				// choke, unchoke, interested, not interested messages
				messageLength = messageType.length;
				
				length = ByteBuffer.allocate(4);
				length.putInt(messageLength);
				
				message.write(length.array());
				message.write(messageType);
				break;
			case 4:
			case 6:
				// have message(4) request message(6)
				messageLength = messageType.length;
				
				payload = (byte[]) params.getParam("pieceIndex");
				
				messageLength = messageLength + payload.length;
				
				length = ByteBuffer.allocate(4);
				length.putInt(messageLength);
				
				message.write(length.array());
				message.write(messageType);
				message.write(payload);
				break;
			case 5:
				//bitfield message
				messageLength = messageType.length;
				
				payload = (byte[]) params.getParam("bitfield");
				
				messageLength = messageLength + payload.length;
				
				length = ByteBuffer.allocate(4);
				length.putInt(messageLength);
				
				message.write(length.array());
				message.write(messageType);
				message.write(payload);
			case 7:
				//piece message
				messageLength = messageType.length;
				ByteBuffer temp;
				
				byte[] pieceField = (byte[]) params.getParam("bitfield");
				int pieceIndex = (int) params.getParam("pieceIndex");
				
				temp = ByteBuffer.allocate(4 + pieceField.length);
				temp.putInt(pieceIndex);
				temp.put(pieceField);
				
				payload = temp.array();
						
				messageLength = messageLength + payload.length;
				
				length = ByteBuffer.allocate(4);
				length.putInt(messageLength);
				
				message.write(length.array());
				message.write(messageType);
				message.write(payload);
			default:
				System.out.println("INVALID MESSAGE TYPE");
				throw new IllegalArgumentException("Message of type: " + String.valueOf(type) + " does not exist");
				
		}
		
		
		return message.toByteArray();
	}

}
