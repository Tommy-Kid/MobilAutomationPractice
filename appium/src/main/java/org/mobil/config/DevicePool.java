package org.mobil.config;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Szálbiztos eszközkiosztás párhuzamos futtatáshoz, valódi foglalás/visszaadás (checkout) alapon.
 * -Ddevice=xxx megadásával minden szál ugyanazt az eszközt kapja (szekvenciális viselkedés).
 * -Ddevice nélkül: acquire() a device.pool-ból foglal egy szabad eszközt a hívó szálnak - ha egy
 * sincs szabadon, MEGVÁRJA, amíg egy másik szál release()-eli a sajátját. Ez ThreadLocal helyett
 * explicit foglalás, mert a JUnit5 párhuzamos végrehajtója (ForkJoinPool) nem garantálja, hogy
 * ugyanaz a Thread-objektum szolgál ki egymás utáni teszteket - egy pusztán szál-azonosság alapú
 * (ThreadLocal round-robin) kiosztás emiatt ütközést okozhat két egyszerre futó session között.
 */
public final class DevicePool {

    private static final String OVERRIDE = System.getProperty("device");

    private static final BlockingQueue<String> AVAILABLE =
            new LinkedBlockingQueue<>(List.of(Config.CONFIG.devicePool().split(",")));

    private static final ThreadLocal<String> inUse = new ThreadLocal<>();

    private DevicePool() {}

    /** Lefoglal egy eszközt a hívó szálnak. Ha nincs szabad eszköz, megvárja, amíg felszabadul. */
    public static String acquire() {
        if (OVERRIDE != null) {
            inUse.set(OVERRIDE);
            return OVERRIDE;
        }
        try {
            String device = AVAILABLE.take();
            inUse.set(device);
            return device;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Megszakítva, miközben szabad eszközre várt", e);
        }
    }

    /** Visszaadja a poolba a hívó szálhoz jelenleg lefoglalt eszközt. */
    public static void release() {
        String device = inUse.get();
        if (device == null) {
            return;
        }
        inUse.remove();
        if (OVERRIDE == null) {
            AVAILABLE.offer(device);
        }
    }

    /** A hívó szálhoz jelenleg lefoglalt eszköz neve - csak acquire() és release() között érvényes. */
    public static String current() {
        String device = inUse.get();
        if (device == null) {
            throw new IllegalStateException("Nincs eszköz lefoglalva ehhez a szálhoz - előbb hívj acquire()-t");
        }
        return device;
    }
}
