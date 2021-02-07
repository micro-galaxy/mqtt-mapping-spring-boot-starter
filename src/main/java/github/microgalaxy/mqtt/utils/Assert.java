
package github.microgalaxy.mqtt.utils;

public abstract class Assert {

    public static void isBlank(String str, String message) {
        if (str == null || "".equals(str.trim())) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
