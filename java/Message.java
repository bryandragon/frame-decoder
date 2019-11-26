/**
 * Defines the interface for decoded messages.
 */
public interface Message {

  byte getType();

  byte[] getBody();
}
