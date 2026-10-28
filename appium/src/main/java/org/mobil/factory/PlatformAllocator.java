package org.mobil.factory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mobil.utils.AppiumServerManager;

import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Párhuzamos futásnál (-Dplatform=any) szálanként platformot oszt ki úgy, hogy a futó session-ök
 * a lehető legkiegyensúlyozottabban oszoljanak meg az Android és iOS között. Az első két párhuzamos
 * session mindig különböző platformra kerül; ha az egyik platformon elfogytak az (online) eszközök,
 * a maradék a másikra megy.
 * Döntési sorrend: kevesebb éppen aktív session -> kevesebb eddig indított session -> android.
 */
public final class PlatformAllocator {

    private static final Logger log = LogManager.getLogger(PlatformAllocator.class);
    private static final List<String> PLATFORMS = List.of("android", "ios");
    private static final ObjectMapper JSON = new ObjectMapper();

    private static final Map<String, Integer> active = new HashMap<>();
    private static final Map<String, Integer> started = new HashMap<>();
    private static final ThreadLocal<String> assigned = new ThreadLocal<>();

    private PlatformAllocator() {}

    public static synchronized String acquire() {
        Map<String, Integer> capacity = onlineDevicesPerPlatform();

        List<String> usable = PLATFORMS.stream().filter(p -> capacity.getOrDefault(p, 0) > 0).toList();
        if (usable.isEmpty()) {
            throw new IllegalStateException("A farm nem lát online eszközt (android/ios)");
        }
        List<String> withFreeSlot = usable.stream()
                .filter(p -> active.getOrDefault(p, 0) < capacity.get(p)).toList();
        List<String> candidates = withFreeSlot.isEmpty() ? usable : withFreeSlot;

        String pick = candidates.stream()
                .min((a, b) -> {
                    int c = Integer.compare(active.getOrDefault(a, 0), active.getOrDefault(b, 0));
                    return c != 0 ? c : Integer.compare(started.getOrDefault(a, 0), started.getOrDefault(b, 0));
                })
                .orElseThrow();

        active.merge(pick, 1, Integer::sum);
        started.merge(pick, 1, Integer::sum);
        assigned.set(pick);
        log.info("Platform allocated: {} (thread: {}, active: {}, capacity: {})",
                pick, Thread.currentThread().getName(), active, capacity);
        return pick;
    }

    /** A szál által lefoglalt platform felszabadítása (hívható többször, és foglalás nélkül is). */
    public static synchronized void release() {
        String platform = assigned.get();
        if (platform == null) {
            return;
        }
        active.merge(platform, -1, Integer::sum);
        assigned.remove();
    }

    private static Map<String, Integer> onlineDevicesPerPlatform() {
        Map<String, Integer> result = new HashMap<>();
        try {
            URI uri = AppiumServerManager.url().toURI().resolve("/device-farm/api/device");
            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setConnectTimeout(3000);
            conn.setReadTimeout(5000);
            try (var in = conn.getInputStream()) {
                JsonNode root = JSON.readTree(in);
                JsonNode devices = root.isArray() ? root : root.path("devices");
                for (JsonNode d : devices) {
                    // a farm a leállított (Shutdown) szimulátorokat is listázza, azok nem használhatók
                    if (d.path("offline").asBoolean(false) || "Shutdown".equalsIgnoreCase(d.path("state").asText())) {
                        continue;
                    }
                    result.merge(d.path("platform").asText("").toLowerCase(), 1, Integer::sum);
                }
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Nem sikerült lekérni az eszközöket a farmtól", e);
        }
        return result;
    }
}
