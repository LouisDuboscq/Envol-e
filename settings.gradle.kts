pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

rootProject.name = "Envolee"

include(":androidApp", ":shared")
include(":backend")
include(":mobileshared")
