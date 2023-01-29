pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenLocal()

        maven { url = uri("https://maven.minecraftforge.net") }
        maven { url = uri("https://maven.fabricmc.net") }
        maven { url = uri("https://maven.architectury.dev/") }
    }
}

include("common", "fabric", "forge")

val modName: String by settings
val minecraftVersion: String by settings

rootProject.name = "$modName-$minecraftVersion"
