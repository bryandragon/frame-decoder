// Run with: $ javac *.java && java FrameDecoderTest

import java.util.List;

public class FrameDecoderTest {

  public static class TestData {
    public byte[] bytes;
    public ??? expectedOutput;

    public TestData(byte[] bytes, ??? output) {
      this.bytes = bytes;
      this.expectedOutput = output;
    }
  };

  public static TestData[] Tests = new TestData[] {
    // 1) Both messages arrive in one single chunk
    new TestData(new byte[] { 0x04, 0x01, 'f', 'o', 'o' }, /* Single Pick message, target = foo */),
    new TestData(new byte[] { 0x01, 0x02 }, /* Single Drop message */),

    // 2) A Pick messages arrives in two chunks
    new TestData(new byte[] { 0x03, 0x01 }, /* Nothing */),
    new TestData(new byte[] { 'm', 'e' }, /* Single pick message, target = me */),

    // 3) An empty byte array
    new TestData(new byte[] {}, /* Nothing */),

    // 4) Two Drop messages arrive at once
    new TestData(new byte[] { 0x01, 0x02, 0x01, 0x02 }, /* Two Drop messages */)

    // 5) Additional tests:

  };

  public static void main(String[] args) {

    FrameDecoder decoder = new MessageFrameDecoder();
    int numFailures = 0;

    System.out.println("Running tests");

    for (TestData data : Tests) {
      ??? result = decoder.readBytes(data.bytes);
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
