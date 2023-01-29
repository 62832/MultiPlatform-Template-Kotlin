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

val MOD_NAME: String by settings
val MINECRAFT_VERSION: String by settings

rootProject.name = "$MOD_NAME-$MINECRAFT_VERSION"
