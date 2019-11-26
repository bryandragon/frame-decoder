/**
 * Represents the Drop variant of a message. Drop messages do not contain a
 * body; as a result, {@link #getBody()} always returns an empty byte array.
 */
public class Drop implements Message {
  private static final byte[] NO_BODY = new byte[0];

  public Drop() {
  }

  public byte getType() {
    return MessageType.DROP.getValue();
  }

  public byte[] getBody() {
    return NO_BODY;
  }

  @Override
  public String toString() {
    return "<Drop>";
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof Drop))
      return false;

    Drop msg = (Drop) obj;
    return msg.getType() == this.getType();
  }

  @Override
  public int hashCode() {
    return 31 * Byte.hashCode(this.getType());
  }
}
