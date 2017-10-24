package com.bsuir.danilchican.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SocketBuffer {
    private static final int SIZE_BUFF = 256;

    private byte[] bytes;
    private ByteBuffer buffer;

    public SocketBuffer() {
        this.bytes = new byte[SIZE_BUFF];
        this.buffer = ByteBuffer.wrap(bytes);

       //  this.buffer = ByteBuffer.allocateDirect(SIZE_BUFF);
    }

    public byte[] read(int count) {
        return Arrays.copyOfRange(bytes, 0, count);
    }

    public Buffer clear() {
        return buffer.clear();
    }

    public ByteBuffer getBuffer() {
        return buffer;
    }
}
