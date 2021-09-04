package io.specialrooter.standard.component.log;

import brave.internal.Platform;
import brave.internal.RecyclableBuffers;
import brave.internal.codec.HexCodec;

import java.util.concurrent.ThreadLocalRandom;

public class NodeIdUtils {
    static long nextNodeIdHigh(){
        return nextNodeIdHigh(ThreadLocalRandom.current().nextInt());
    }

    static long nextNodeIdHigh(int random) {
        long epochSeconds = System.currentTimeMillis() / 1000L;
        return (epochSeconds & 4294967295L) << 32 | (long)random & 4294967295L;
    }

    static long randomLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    static long nextId() {
        long nextId;
        for(nextId = randomLong(); nextId == 0L; nextId = randomLong()) {
        }

        return nextId;
    }

    static String toTraceIdString(long traceIdHigh, long traceId) {
        if (traceIdHigh != 0L) {
            char[] result = RecyclableBuffers.parseBuffer();
            HexCodec.writeHexLong(result, 0, traceIdHigh);
            HexCodec.writeHexLong(result, 16, traceId);
            return new String(result, 0, 32);
        } else {
            return HexCodec.toLowerHex(traceId);
        }
    }

    static String toNodeIdString(long spanId){
        return HexCodec.toLowerHex(spanId);
    }

    public static void main(String[] args) {
        long l = NodeIdUtils.nextNodeIdHigh();
        long l1 = NodeIdUtils.nextId();
        System.out.println(l);
        System.out.println(l1);
        String s = toTraceIdString(l, l1);
        String s1 = toNodeIdString(l1);
        System.out.println(s1);
        System.out.println(s);
    }
}
