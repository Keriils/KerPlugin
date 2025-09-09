
# Ker-Plugin

*My custom Gradle plugin designed for various generic projects, w.......*

## Current Available Plugins

### SpotlessWrapper

**Desc**:  
基于Mit协议下 打包了[GtnhExample](https://github.com/GTNewHorizons/ExampleMod1.7.10)共享出的spotless配置 并基于此进行了扩展  
Based on the Mit Licence, the spotless configuration shared by [GtnhExample](https://github.com/GTNewHorizons/ExampleMod1.7.10) is packaged and extended based on this

**Usage**:  
To include and apply the `SpotlessWrapper` in your project, add the following to your `build.gradle[.kts]` file:

```kotlin
plugins {
    id("online.keriils.plugins.spotless-wrapper") version "reference-tag"
}

// convention value
wrapperSpotless {
    alwaysApplySpotless = false
    enforceSpotlessCheck = false
    enforceSpotlessCheckForCIEnv = false
    spotlessForJava = true
    spotlessForKotlin = true
    spotlessForGroovyGradle = true
    spotlessForKotlinGradle = true
}
```

### DependencyDeclarationConvention

**Desc**:  
主要用于简化GTNH依赖的声明
基于Mit协议下 包装了[elytraConvention](https://github.com/ElytraServers/elytra-conventions/)插件 使得声明依赖更加的轮椅(简单化)  
Mainly used to simplify the declaration of GTNH dependencies.  
Based on the MIT license, packages the [elytraConvention](https://github.com/ElytraServers/elytra-conventions/) plugin.  
This makes the declaration of dependencies more wheelchair-friendly (simplified).  

**Usage**:  
Only kts scripts are supported  
To include and apply the `dependencyDeclarationConvention` in your project, add the following to your `build.gradle.kts` file:

```kotlin
plugins {
    id("online.keriils.plugins.dependency-declaration-convention") version "reference-tag"
}

dependencies {

    // example for use
    dependencyDeclarations {
        // gtnh version
        gtnhVersion = "2.8.0-beta-4"

        declarationApi {
            declare("GT5-Unofficial")
        }

        declarationImplementation {
            declare("Avaritiaddons")
        }

        // declaration[available configuration name]
    }

}
```

