import java.util.List;

/**
 * Defines the generic interface for a protocol frame decoder.
 *
 * @param <E> The type to which protocol frames are to be decoded
 */
public interface FrameDecoder<E> {
  // Read bytes as they arrive, decoding any complete frames
  List<E> readBytes(byte[] bytes);
}
