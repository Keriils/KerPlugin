package online.keriils.gradle.dsl.api

import org.gradle.api.artifacts.Configuration

public interface ConfigurationWrapper {

    public val wrappedConfiguration: String
        get() = "_NONE_"

    public val configuration: Configuration
}
