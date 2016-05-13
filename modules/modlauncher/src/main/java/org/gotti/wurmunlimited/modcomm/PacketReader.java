package org.gotti.wurmunlimited.modcomm;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Convenience class for reading packets, see {@link DataInputStream docs for the various reading methods}
 */
public class PacketReader extends DataInputStream {
    private static class ByteBufferBackedInputStream extends InputStream {
        private final ByteBuffer buf;

        private ByteBufferBackedInputStream(ByteBuffer buf) {
            this.buf = buf;
        }

        public int read() throws IOException {
            if (buf.hasRemaining()) {
                return buf.get() & 0xFF;
            } else {
                return -1;
            }
        }

        public int read(byte[] bytes, int off, int len) throws IOException {
            if (buf.hasRemaining()) {
                len = Math.min(len, buf.remaining());
                buf.get(bytes, off, len);
                return len;
            } else {
                return -1;
            }
        }
    }

    public PacketReader(ByteBuffer buffer) {
        super(new ByteBufferBackedInputStream(buffer));
    }
}
