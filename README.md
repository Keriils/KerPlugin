
# Ker-Plugin

*My custom Gradle plugin designed for various generic projects, w.......*

## Current Module Plugins

### SpotlessWrapper

**Desc**:
Wraps commonly used Spotless plugin settings to your projects.

**Usage**:
To include and apply the `SpotlessWrapper` in your project, add the following to your `build.gradle` file:

```kotlin
plugins {
    id("online.keriils.plugins.spotless-wrapper") version "reference-tag"
}
