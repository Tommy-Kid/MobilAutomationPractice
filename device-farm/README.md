# device-farm

Önálló, a Java tesztkerettől (`appium/`) független modul: helyi Appium device farm indítása
(`appium-device-farm` + `inspector` plugin).

| fájl | szerep |
| :--- | :----- |
| `lib/env.sh` | `ANDROID_HOME`, `JAVA_HOME`, `PATH` |
| `config/capabilities.json` | alapértelmezett capabilityk (külön szerkeszthető) |
| `start-appium.sh` / `stop-appium.sh` | szerver indítás/leállítás, env változókkal paraméterezhető |
| `emulator.sh` | AVD létrehozás, emulátor indítás (boot-ra várással), leállítás |

## Használat
```sh
device-farm/emulator.sh create Pixel_9_Pro
device-farm/emulator.sh start Pixel_9_Pro 5554     # = config.properties: emulator-5554
PLATFORM=android ANDROID_TYPE=emulated device-farm/start-appium.sh
mvn -f appium/pom.xml verify -Ddevice=pixel9Pro
```
A `AppiumServerManager` a már futó, egészséges szervert újrahasznosítja (`/status`), ezért a base path `/`.
Az eszközök (udid, systemPort) továbbra is az `appium/src/test/resources/config.properties`-ben vannak.
