import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A stateful implementation of {@link FrameDecoder} that produces
 * {@link Message} instances for complete frames.
 *
 * Consumers are responsible for ensuring thread-safety.
 */
public class MessageFrameDecoder implements FrameDecoder<Message> {
  protected final MessageBuilder builder = new MessageBuilder();

  public MessageFrameDecoder() {
  }

  /**
   * Reads bytes as they arrive and returns a list of zero or more messages
   * for complete frames. Partial frames are accumulated across invocations
   * and messages are returned when the remaining bytes arrive.
   *
   * Has a worst-case running time of O(N).
   *
   * @param bytes The bytes to be decoded
   * @return A list of completed messages
   */
  public List<Message> readBytes(byte[] bytes) {
    if (bytes == null || bytes.length == 0)
      return Collections.emptyList();

    int index = 0;
    int numBytes = bytes.length;

    List<Message> messages = new ArrayList<>();

    while (index < numBytes) {
      if (!this.builder.frameInitialized()) {
        byte frameLength = bytes[index++];
        this.builder.initializeFrame(frameLength);
      }

      int remainingBytes = numBytes - index;
      int bytesConsumed = this.builder.put(bytes, index,
        Math.min(remainingBytes, this.builder.remainingBytesInFrame()));

      index += bytesConsumed;

      if (this.builder.remainingBytesInFrame() == 0)
        messages.add(this.builder.message());
    }

    return messages;
  }
}
