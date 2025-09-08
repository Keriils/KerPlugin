package online.keriils.gradle.utils

public object ConstantsHelper {

    public const val NO_SET: String = "noSet"

    // default dependency notation
    public const val NOTATION_GTNH_GROUP: String = "com.github.GTNewHorizons"
    public const val NOTATION_GTNH_NAME: String = NO_SET
    public const val NOTATION_GTNH_VERSION: String = NO_SET
    public const val NOTATION_GTNH_CLASSIFIER: String = "dev"

    public const val ELYTRA_MODPACK_VERSION_KEY: String = "elytraModpackVersion"

    public const val THIS_A_BUG: String = "Please report this bug..."

    public const val TASK_SPOTLESS_CHECK: String = "spotlessCheck"
    public const val TASK_SPOTLESS_APPLY: String = "spotlessApply"

    // spotless wrapper debug
    public const val ALWAYS_APPY_SPOTLESS: String = "ker.plugin.alwaysApplySpotless"
    public const val SPOTLESS_CI_DEBUG_PROPERTY: String = "ker.plugin.simulateEnvValue"

    public const val EXTRA_CONFIG_PROPERTY: String = "ker.plugin.extraConfiguration"
}
