pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()

        maven { url = uri("https://maven.minecraftforge.net") }
        maven { url = uri("https://maven.fabricmc.net") }
        maven { url = uri("https://maven.architectury.dev/") }
    }
}

include("common")

for (platform in providers.gradleProperty("enabledPlatforms").get().split(',')) {
    include(platform)
}

val modName: String by settings
val minecraftVersion: String by settings

rootProject.name = "$modName-$minecraftVersion"
