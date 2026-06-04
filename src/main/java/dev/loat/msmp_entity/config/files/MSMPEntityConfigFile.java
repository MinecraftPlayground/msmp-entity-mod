package dev.loat.msmp_entity.config.files;

import dev.loat.config_lib.annotation.Annotation;


@Annotation.Comment("""
    Main configuration file for MSMP Entity.
""")
public final class MSMPEntityConfigFile {
    private MSMPEntityConfigFile() {}

    @Annotation.Comment("Example integer value")
    public final int exampleInt = 42;
}
