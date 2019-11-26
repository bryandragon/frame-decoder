import java.util.Arrays;
import java.util.Objects;

/**
 * Represents the Pick variant of a message. Pick messages may contain a body;
 * {@link #getBody()} will return an empty byte array if the Pick message has
 * no body.
 */
public class Pick implements Message {
  private final byte[] body;

  public Pick(byte[] body) {
    this.body = Objects.requireNonNull(body);
  }

  public Pick(String body) {
    this.body = body.getBytes();
  }

  public byte getType() {
    return MessageType.PICK.getValue();
  }

  public byte[] getBody() {
    return this.body;
  }

  @Override
  public String toString() {
    return String.format("<Pick body=%s>", new String(this.getBody()));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;

    if (!(obj instanceof Pick))
      return false;

    Pick msg = (Pick) obj;

    return msg.getType() == this.getType() &&
      Arrays.equals(msg.getBody(), this.getBody());
  }

  @Override
  public int hashCode() {
    int result = Byte.hashCode(this.getType());
    result = 31 * result + Arrays.hashCode(this.getBody());
    return result;
  }
}
