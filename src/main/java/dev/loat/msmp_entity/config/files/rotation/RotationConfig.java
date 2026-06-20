package dev.loat.msmp_entity.config.files.rotation;

import dev.loat.config_lib.annotation.Annotation;


public class RotationConfig {

    @Annotation.Comment("Settings for notifications.")
    public Notification notification = new Notification();

    public static class Notification {

        @Annotation.Comment("""
            Number of server ticks between rotation change checks for subscribed entities.
            Lower values result in more frequent checks and potentially more notifications, but can increase server load.
        """)
        @Annotation.Key("interval-ticks")
        public int intervalTicks = 20;

        @Annotation.Comment("""
            Minimum change in yaw or pitch, in degrees, that an entity must rotate to trigger a rotation change notification.
            Setting this to 0.0 will trigger a notification for any rotation change.
        """)
        @Annotation.Key("rotation-delta")
        public double rotationDelta = 10.0;
    }
}
