/**
 * Encapsulates the tasks of accumulating the bytes for a frame and
 * constructing a {@link Message} when the frame is complete.
 *
 * Reading a message resets the builder so that it can be used again, or the
 * builder can be reset manually with {@link #reset()}.
 */
public class MessageBuilder {
  public static final int MAX_FRAME_LENGTH = 126;

  protected int frameLength = 0;
  protected int frameCursor = 0;
  protected byte messageType = 0;
  protected byte[] messageBody = null;

  public MessageBuilder() {
  }

  public void reset() {
    this.frameLength = 0;
    this.frameCursor = 0;
    this.messageType = 0;
    this.messageBody = null;
  }

  public void initializeFrame(int frameLength) {
    if (frameLength < 1)
      throw new IllegalArgumentException("frameLength must be greater than 0");

    if (frameLength > MAX_FRAME_LENGTH)
      throw new IllegalArgumentException("frameLength cannot be greater than 126");

    this.frameLength = frameLength;
    this.frameCursor = 0;
    this.messageType = 0;

    if (frameLength > 1)
      this.messageBody = new byte[frameLength - 1];
  }

  public boolean frameInitialized() {
    return this.frameLength > 0;
  }

  public int remainingBytesInFrame() {
    return this.frameLength - this.frameCursor;
  }

  /**
   * Attempts to read {@code length} bytes from {@code bytes} starting from
   * {@code offset}, decoding the message type if needed and, if the pending
   * message has a body, copying the lesser of {@code length} and
   * {@link #remainingBytesInFrame()} bytes into the message body using
   * {@link System#arraycopy}. Returns the number of bytes consumed.
   */
  public int put(byte[] bytes, int offset, int length) {
    int bytesConsumed = 0;

    if (this.frameCursor == 0 && length > 0) {
      this.messageType = bytes[offset];
      this.frameCursor++;
      offset++;
      length--;
      bytesConsumed++;
    }

    if (this.remainingBytesInFrame() > 0 && length > 0) {
      int bodyBytes = Math.min(length, this.remainingBytesInFrame());
      int bodyCursor = this.frameCursor - 1; // Minus 1 for message type byte
      System.arraycopy(bytes, offset, this.messageBody, bodyCursor, bodyBytes);
      bytesConsumed += bodyBytes;
      this.frameCursor += bodyBytes;
    }

    return bytesConsumed;
  }

  /**
   * Returns the message for a complete frame and resets the builder for
   * reuse.
   *
   * Returns {@code null} if not all frame bytes have been collected.
   */
  public Message message() {
    if (this.remainingBytesInFrame() > 0)
      return null;

    Message message = null;

    if (this.messageType == MessageType.PICK.getValue()) {
      message = new Pick(this.messageBody);
    } else if (this.messageType == MessageType.DROP.getValue()) {
      message = new Drop();
    }

    if (message != null)
      this.reset();

    return message;
  }
}
