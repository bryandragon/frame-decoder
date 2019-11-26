/**
 * Enumerates valid types for {@link Message}s.
 */
public enum MessageType {
  PICK((byte) 0x01),
  DROP((byte) 0x02);

  private final byte value;

  MessageType(byte value) {
    this.value = value;
  }

  public byte getValue() {
    return this.value;
  }
}
