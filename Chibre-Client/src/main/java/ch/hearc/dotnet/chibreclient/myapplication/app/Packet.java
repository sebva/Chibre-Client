package ch.hearc.dotnet.chibreclient.myapplication.app;

/**
 * Created by Sébastien on 08.04.2014.
 */
public class Packet {

    String payload;

    public Packet(String payload) {
        this.payload = payload;
    }

    public Packet(byte[] payload, int length) {
        this(new String(payload, 0, length));
    }

    public String getPayload() {
        return payload;
    }

    public byte[] getPayloadBytes() {
        return payload.getBytes();
    }

    public byte[] getPayloadLengthBytes() {
        return intToByteArray(payload.length());
    }

    public static byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }

    public static int byteArrayToInt(byte[] bytes) {
        return bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
    }

    @Override
    public String toString()
    {
        return payload;
    }
}
