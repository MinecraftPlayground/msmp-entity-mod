package dev.loat.msmp_entity.config.files.position;

import dev.loat.config_lib.annotation.Annotation;


public class PositionConfig {

    @Annotation.Comment("Settings for notifications.")
    public Notification notification = new Notification();

    public static class Notification {

        @Annotation.Comment("""
            Number of server ticks between position change checks for subscribed entities.
            Lower values result in more frequent checks and potentially more notifications, but can increase server load.
        """)
        @Annotation.Key("interval-ticks")
        public int intervalTicks = 20;
    
        @Annotation.Comment("""
            Minimum distance in blocks that an entity must move to trigger a position change notification.
            Setting this to 0.0 will trigger a notification for any movement.
        """)
        @Annotation.Key("block-delta")
        public double blockDelta = 1.0;
    }
}
