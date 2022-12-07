package torent.messages;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class Message {
	
	final String handshakeMSG = "P2PFILESHARINGPROJ";
	
	public Message() {}
	
	public byte[] handshake(int peerID) {
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
		
		ByteBuffer buffer =  ByteBuffer.wrap(handshake.array());
        byte[] h = new byte[18];
        buffer.get(h,0,18);
        String handShakeString = new String(h, StandardCharsets.UTF_8);
		System.out.println("handshake maker " + handShakeString);
		
		
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
				
				payload = params.getPayload();
				
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
				
				payload = params.getBitfield().toByteArray();
				
				messageLength = messageLength + payload.length;
				
				length = ByteBuffer.allocate(4);
				length.putInt(messageLength);
				
				message.write(length.array());
				message.write(messageType);
				message.write(payload);
				break;
			case 7:
				//piece message
				messageLength = messageType.length;
				ByteBuffer temp;
				
				byte[] pieceField = params.getPieceField();
				int pieceIndex = params.getPieceIndex();
				
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
				break;
			default:
				System.out.println("INVALID MESSAGE TYPE");
				throw new IllegalArgumentException("Message of type: " + String.valueOf(type) + " does not exist");
				
		}
		
		
		return message.toByteArray();
	}
	
	public int getType(byte[] msg) {
		return msg[4];
	}
	
	public byte[] getPayload(byte[] msg) {
		
		// copy from message starting at 5th byte (length + type fields)
		// insert into payload array starting at 0th byte
		// copy message length - 5 bytes into payload (the five parts are the len & type)
		
		byte[] payload = new byte[msg.length - 5];
		System.arraycopy(msg, 5, payload, 0, msg.length-5);
		
		return payload;
		
	}

}
