import dev.architectury.plugin.ArchitectPluginExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    `java-library`
    idea
    `maven-publish`
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("dev.architectury.loom") version "0.12.0-SNAPSHOT" apply false
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

val MOD_ID: String by rootProject
val MOD_NAME: String by rootProject
val MOD_VERSION: String by rootProject
val MOD_GROUP: String by rootProject

val MINECRAFT_VERSION: String by rootProject
val JAVA_VERSION: String by rootProject

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "architectury-plugin")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")

    extra["accessWidenerFile"] = rootProject.file("common/src/main/resources/$MOD_ID.accesswidener")

    group = "$MOD_GROUP.${project.name}"
    version = "$MOD_VERSION+$MINECRAFT_VERSION"
    base.archivesName.set("$MOD_ID-${project.name}")

    sourceSets.test {
        java.srcDirs( )
        resources.srcDirs( )
    }

    configure<ArchitectPluginExtension> {
        minecraft = MINECRAFT_VERSION
    }

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()

        val accessWidenerFile: File by extra

        if (accessWidenerFile.exists()) {
            accessWidenerPath.set(accessWidenerFile)
        }
    }

    repositories {
        mavenLocal()
        maven { url = uri("https://maven.parchmentmc.org") }

        maven {
            url = uri("https://www.cursemaven.com")
            content { includeGroup("curse.maven") }
        }

        maven {
            url = uri("https://api.modrinth.com/maven")
            content { includeGroup("maven.modrinth") }
        }
    }

    dependencies {
        "minecraft"("com.mojang:minecraft:$MINECRAFT_VERSION")

        "mappings"(project.extensions.getByName<LoomGradleExtensionAPI>("loom").layered {
            officialMojangMappings()

            val PARCHMENT_MAPPINGS: String? by rootProject
            val PARCHMENT_MINECRAFT_VERSION: String? by rootProject

            if (PARCHMENT_MAPPINGS != null) {
                val mcVersion = PARCHMENT_MINECRAFT_VERSION ?: MINECRAFT_VERSION
                parchment("org.parchmentmc.data:parchment-$mcVersion:$PARCHMENT_MAPPINGS@zip")
            }
        })

        compileOnly("com.google.code.findbugs:jsr305:3.0.2")
        compileOnly("com.google.errorprone:error_prone_annotations:2.11.0")
    }

    tasks {
        processResources {
            for (prop in project.properties) {
                inputs.property(prop.key, prop.value.toString())
            }

            filesMatching(listOf("*.mixins.json", "pack.mcmeta", "META-INF/mods.toml", "fabric.mod.json", "architectury.common.json")) {
                expand(project.properties)
            }
        }

        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(JAVA_VERSION))
            }

            withSourcesJar()
        }

        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(JavaLanguageVersion.of(JAVA_VERSION).asInt())

            javaToolchains {
                compilerFor {
                    languageVersion.set(JavaLanguageVersion.of(JAVA_VERSION))
                }
            }
        }

        jar {
            manifest {
                attributes(
                        "Specification-Title" to MOD_ID,
                        "Specification-Version" to MINECRAFT_VERSION,
                        "Specification-Vendor" to "ApexStudios",
                        "Implementation-Title" to project.name,
                        "Implementation-Version" to project.version.toString(),
                        "Implementation-Vendor" to "ApexStudios",
                        // "Implementation-Timestamp" to new Date().format("yyyy-MM-dd'T'HH:mm:ssZ")
                )
            }
        }
    }

    publishing {
        publications {
            create<MavenPublication>("mavenCommon") {
                groupId = project.group.toString()
                artifactId = project.base.archivesName.get()
                version = project.version.toString()

                // loom.disableDeprecatedPomGeneration(it)

                artifact(project.tasks.getByName("remapJar"))
                artifact(project.tasks.getByName("sourcesJar"))
            }
        }

        repositories {
            mavenLocal() // remove this to not publish to local maven
            // Add your maven repo here
            /*if(System.getenv('MY_MAVEN_USERNAME_PROP_KEY') != null && System.getenv('MY_MAVEN_PASSWORD_PROP_KEY') != null) {
                maven {
                    name 'MyMavenRepo'
                    url 'https://maven.my.domain'

                    credentials {
                        username System.getenv('MY_MAVEN_USERNAME_PROP_KEY')
                        password System.getenv('MY_MAVEN_PASSWORD_PROP_KEY')
                    }
                }
            }*/
        }
    }
}

apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")

idea.module {
    excludeDirs.addAll(listOf(
            file(".idea"),
            file(".gradle"),
            file("gradle")))
}
