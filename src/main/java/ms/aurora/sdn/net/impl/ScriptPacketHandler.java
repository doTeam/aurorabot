package ms.aurora.sdn.net.impl;

import ms.aurora.core.script.ScriptLoader;
import ms.aurora.sdn.net.IncomingPacket;
import ms.aurora.sdn.net.PacketHandler;
import ms.aurora.sdn.net.api.Repository;
import ms.aurora.util.JarInputStreamClassLoader;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;

/**
 * @author tobiewarburton
 */
public class ScriptPacketHandler implements PacketHandler {

    @Override
    public int getOpcode() {
        return 5;
    }

    @Override
    public void handle(IncomingPacket incomingPacket) throws IOException {
        DataInputStream in = incomingPacket.getStream();
        int count = in.readInt();
        List<JarInputStream> streams = new ArrayList<JarInputStream>(count);
        for (int i = 0; i < count; i++) {
            int len = in.readInt();
            byte[] bytes = new byte[len];
            int res = in.read(bytes); // todo maybe add a crc
            streams.add(new JarInputStream(new ByteArrayInputStream(bytes)));
        }
        ScriptLoader.remoteStreams = streams;
        synchronized (Repository.script_lock) {
            Repository.script_lock.notifyAll();
        }
    }
}