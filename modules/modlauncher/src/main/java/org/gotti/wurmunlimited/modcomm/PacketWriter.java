package org.gotti.wurmunlimited.modcomm;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.nio.ByteBuffer;

/**
 * Convenience class for writing packets, see {@link DataOutputStream docs for the various writing methods}
 */
public class PacketWriter extends DataOutputStream {
    private final ByteArrayOutputStream buffer;

    public PacketWriter() {
        super(new ByteArrayOutputStream());
        buffer = (ByteArrayOutputStream) out;
    }

    public ByteBuffer getBytes() {
        return ByteBuffer.wrap(buffer.toByteArray(), 0, buffer.size());
    }
}
