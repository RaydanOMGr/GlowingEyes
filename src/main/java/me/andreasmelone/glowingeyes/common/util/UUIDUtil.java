package me.andreasmelone.glowingeyes.common.util;

import java.nio.ByteBuffer;
import java.util.UUID;

public class UUIDUtil {
    public static byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer buffer = ByteBuffer.wrap(new byte[16]);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

    public static UUID getUUIDFromBytes(byte[] uuidBytes) {
        ByteBuffer buffer = ByteBuffer.wrap(uuidBytes);
        long mostSignificantBits = buffer.getLong();
        long leastSignificantBits = buffer.getLong();
        return new UUID(mostSignificantBits, leastSignificantBits);
    }
}
