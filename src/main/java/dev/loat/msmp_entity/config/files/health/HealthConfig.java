package dev.loat.msmp_entity.config.files.health;

import dev.loat.config_lib.annotation.Annotation;


public class HealthConfig {

    @Annotation.Comment("Settings for notifications.")
    public Notification notification = new Notification();

    public static class Notification {

        @Annotation.Comment("""
            Number of server ticks between health change checks for subscribed entities.
            Lower values result in more frequent checks and potentially more notifications, but can increase server load.
        """)
        @Annotation.Key("interval-ticks")
        public int intervalTicks = 20;

        @Annotation.Comment("""
            Minimum health change (in hearts, where 1 heart = 2 HP) required to trigger a notification.
            Setting this to 0.0 will trigger a notification for any health change.
        """)
        @Annotation.Key("health-delta")
        public double healthDelta = 1.0;
    }
}
