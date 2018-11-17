package de.lmu.ifi.sep.abalone.network.message;

/**
 * Used by {@link MessageHandler#close()}.
 * If the message handler receives an instance of this class,
 * it terminates the run method
 */
class CloseMessage extends Message {
}
