import java.util.List;

public interface FrameDecoder {
  // Read bytes as they arrive, decoding any complete frames
  public ??? readBytes(byte[] bytes);
}
