// Run with: $ javac *.java && java FrameDecoderTest

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

public class FrameDecoderTest {

  public static class TestData {
    public byte[] bytes;
    public List<Message> expectedOutput;

    public TestData(byte[] bytes, List<Message> output) {
      this.bytes = bytes;
      this.expectedOutput = output;
    }
  }

  public static TestData[] Tests = new TestData[] {
    // 1) Both messages arrive in one single chunk
    new TestData(new byte[] { 0x04, 0x01, 'f', 'o', 'o' },
      List.of(new Pick("foo"))), /* Single Pick message, target = foo */
    new TestData(new byte[] { 0x01, 0x02 },
      List.of(new Drop())), /* Single Drop message */

    // 2) A Pick message arrives in two chunks
    new TestData(new byte[] { 0x03, 0x01 }, Collections.emptyList()), /* Nothing */
    new TestData(new byte[] { 'm', 'e' },
      List.of(new Pick("me"))), /* Single Pick message, target = me */

    // 3) An empty byte array
    new TestData(new byte[] {}, Collections.emptyList()), /* Nothing */

    // 4) Two Drop messages arrive at once
    new TestData(new byte[] { 0x01, 0x02, 0x01, 0x02 },
      List.of(new Drop(), new Drop())), /* Two Drop messages */

    // 5) Additional tests:

    // 5-1) A null byte array
    new TestData(null, Collections.emptyList()),

    // 5-2) A Pick message arrives in chunks: (a) length, (b) type, (c) body
    new TestData(new byte[] { 0x04 }, Collections.emptyList()),
    new TestData(new byte[] { 0x01 }, Collections.emptyList()),
    new TestData(new byte[] { 'f', 'o', 'o' }, List.of(new Pick("foo"))),

    // 5-3) A Pick message arrives in chunks: (a) length and  type,
    //      (b) part of body, (c) empty byte array, (d) rest of body
    new TestData(new byte[] { 0x04, 0x01 }, Collections.emptyList()),
    new TestData(new byte[] { 'f' }, Collections.emptyList()),
    new TestData(new byte[] {}, Collections.emptyList()),
    new TestData(new byte[] { 'o', 'o' },
      List.of(new Pick("foo"))), /* Single Pick message, target = foo */

    // 5-4) Two Pick messages arrive at once
    new TestData(new byte[] { 0x02, 0x01, 'a', 0x02, 0x01, 'b' },
      List.of(new Pick("a"), new Pick("b"))), /* Two pick messages, target = a, target = b */

    // 5-5) Two Pick messages and one Drop message arrive in three chunks
    new TestData(new byte[] { 0x02, 0x01, 'a', 0x02 },
      List.of(new Pick("a"))), /* Single Pick message, target = a */
    new TestData(new byte[] { 0x01, 'b', 0x01 },
      List.of(new Pick("b"))), /* Single Pick message, target = b */
    new TestData(new byte[] { 0x02 },
      List.of(new Drop())), /* Single Drop message */

    // 5-6) A Drop message arrives in two chunks
    new TestData(new byte[] { 0x01 }, Collections.emptyList()), /* Nothing */
    new TestData(new byte[] { 0x02 }, List.of(new Drop())), /* Single Drop message */

    // 5-7) Two Pick messages arrive at once, with frame lengths of 126 and 2, respectively
    new TestData(
      ByteBuffer.allocate(127 + 3)
        .put(new byte[] { 0x7E, 0x01 })
        .put("a".repeat(125).getBytes())
        .put(new byte[] { 0x02, 0x01, 'b' })
        .array(),
      List.of(new Pick("a".repeat(125)), new Pick("b")))
  };

  public static void main(String[] args) {

    FrameDecoder<Message> decoder = new MessageFrameDecoder();
    int numFailures = 0;

    System.out.println("Running tests");

    for (TestData data : Tests) {
      List<Message> result = decoder.readBytes(data.bytes);
      if (!result.equals(data.expectedOutput)) {
        System.out.println(
          "Test failed. Expected " + data.expectedOutput +
          " but found " + result + ".");
        numFailures++;
      }
    }

    if (numFailures > 0) {
      System.out.println(numFailures + " tests failed.");
      System.exit(1);
    } else {
      System.out.println("Tests ran successfully");
    }
  }
}
