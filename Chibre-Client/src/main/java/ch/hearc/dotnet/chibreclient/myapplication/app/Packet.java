package ch.hearc.dotnet.chibreclient.myapplication.app;

/**
 * Created by Sébastien on 08.04.2014.
 */
public class Packet {

    String payload;

    public Packet(String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }
}
