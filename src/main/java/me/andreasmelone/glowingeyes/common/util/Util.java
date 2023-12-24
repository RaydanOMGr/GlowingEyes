package me.andreasmelone.glowingeyes.common.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.UUID;

public class Util {
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

    public static byte[] serializeHashMap(HashMap<?, ?> glowingEyesMap) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(glowingEyesMap);
        return bos.toByteArray();
    }

    public static <K, V> HashMap<K, V> deserializeHashMap(byte[] glowingEyesMapBytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(glowingEyesMapBytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (HashMap<K, V>) ois.readObject();
    }
}
