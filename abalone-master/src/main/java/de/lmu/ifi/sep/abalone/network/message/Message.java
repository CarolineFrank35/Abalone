package de.lmu.ifi.sep.abalone.network.message;

import java.io.Serializable;

/**
 * Used for network message communication.
 */
public class Message implements Serializable {

    private final MessageType messageType;
    private final Serializable payload;

    /**
     * Default constructor
     */
    Message() {
        this.messageType = null;
        this.payload = null;
    }

    /**
     * Creates a message
     *
     * @param messageType value of enum {@link MessageType}
     * @param payload     must be serializable
     */
    public Message(MessageType messageType, Serializable payload) {
        this.messageType = messageType;
        this.payload = payload;
    }

    /**
     * @return message type of the message
     */
    public MessageType getMessageType() {
        return messageType;
    }

    /**
     * @return payload of the message
     */
    public Serializable getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Message[" + this.messageType + "||" + (this.payload == null ? "null" : this.payload.getClass()) + "]";
    }

    /**
     * Enumeration of message types.
     * Currently contains SYNC, SYNC_REQUEST, MOVE & ERROR
     */
    public enum MessageType {
        SYNC, SYNC_REQUEST, MOVE, ERROR, WIN, CONFIRM_WIN, INIT, RDY
    }
}
