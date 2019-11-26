# Code Challenge: Frame Decoder

## Description / Instructions
Given the protocol specification below, create a protocol frame decoder. As
bytes arrive, they are passed to the frame decoder which collects them until
one or more completed protocol frames are available. The same buffer will not
be passed to the decoder more than once. For each completed frame, the frame
decoder decodes the bytes into an appropriate representation. The frame decoder
may be called with bytes that consist of one, more than one, or partial frames.

For the sake of this exercise, there is no need to consider replying to the
messages that arrive. Additionally, it is the responsibility of the user of the
frame decoder to ensure thread safety, so there is no need to consider race
conditions for this exercise.

Frames contain one of two message types; `Pick` or `Drop`. In the table below,
[`Message`, `Message`] represents a sequence of two `Message`s. Using the
protocol below, an example of how a functioning frame decoder would behave
follows (subsequent calls to the same decoder):

| Call | Decoder called with bytes of:     | Result           |
|------|:---------------------------------:|------------------|
| 1    | [partial `Pick(msg)` message]     | []               |
| 2    | [rest of `Pick(msg)` message]     | [`Pick(msg)`]    |
| 3    | (empty byte array)                | []               |
| 4    | [`Drop` message] [`Drop` message] | [`Drop`, `Drop`] |


Your task is to write an implementation of this frame decoder. Make sure that
you explicitly test the cases presented above. In addition, suggest a few
additional corner cases and test for them as well.

We will provide a scaffold with tests that correspond to each test case
described above.

## Protocol Specification

This is a binary protocol which is capable of transmitting two types of
messages. Each protocol frame is a sequence of bytes consisting of the frame
length, an identifier which corresponds to the message type, and an optional
message body:

    >-- increasing byte index -->
    +-----------------+-----------------+------------------------+
    | FrameLength (1) | MessageType (1) | Body (FrameLength - 1) |
    +-----------------+-----------------+------------------------+

`FrameLength` is a 1 byte signed integer, (with a max value of 126) that
represents the length of the data to follow (`MessageType` + `Body`).
`MessageType` is a 1 byte signed integer (with a max value of 126) which
indicates the type of message. `Body` contains the body of the message, if
present.

#### Message Variants

##### Pick
    MessageType = 0x01
    Body = UTF-8 string encoded in (FrameLength - 1) bytes

__Example__: 0x[04 01 ‘f’ ‘o’ ‘o’] == `Pick(“foo”)`

__Explanation__:
* 0x04: 4 byte `FrameLength`
* 0x01: `MessageType` == `Pick`
* 0x['f' 'o' 'o']: string "foo"

##### Drop
    MessageType = 0x02
    Body = (no body, 0 bytes of data)

__Example__: 0x[01 02] == `Drop`

__Explanation__:
* 0x01: 1 byte `FrameLength`
* 0x02: `MessageType` == `Drop`

## Rubric

* The code challenge instructions were followed
* Messages decoded correctly
  * The decoder passes the test cases specified in the instructions
  * Extra tests cases check possible corner cases
* The solution does not exhibit any unnecessary time/memory complexity
* Code Organization:
  * Appropriate use of scoping/privacy levels
  * Appropriate use of comments
  * Language best practices followed
  * Code is organized into appropriate source files
  * Another developer could extend or modify this code
* Documentation:
  * Source code documentation (comments)
  * Readme.md including instructions on how to build/run
