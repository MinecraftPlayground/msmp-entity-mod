package dev.loat.msmp_entity.config.files;

import dev.loat.config_lib.annotation.Annotation;
import dev.loat.msmp_entity.config.files.position.PositionConfig;
import dev.loat.msmp_entity.config.files.rotation.RotationConfig;


@Annotation.Comment("""
    Main configuration file for MSMP Entity.
""")
public class MSMPEntityConfigFile {
    private MSMPEntityConfigFile() {}

    @Annotation.Comment("Configuration for position-related settings.")
    public PositionConfig position = new PositionConfig();

    @Annotation.Comment("Configuration for rotation-related settings.")
    public RotationConfig rotation = new RotationConfig();
}
