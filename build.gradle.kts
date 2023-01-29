import java.text.SimpleDateFormat
import java.util.Date

import dev.architectury.plugin.ArchitectPluginExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
    `java-library`
    idea
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1"
    id("architectury-plugin") version "3.4-SNAPSHOT" apply false
    id("dev.architectury.loom") version "0.12.0-SNAPSHOT" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
}

idea.module {
    excludeDirs.addAll(listOf(
            file(".idea"),
            file(".gradle"),
            file("gradle")))
}

val modId: String by rootProject
val minecraftVersion: String by rootProject
val javaVersion: String by rootProject

val platforms by extra {
    listOf("fabric", "forge")
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "architectury-plugin")
    apply(plugin = "dev.architectury.loom")
    apply(plugin = "org.jetbrains.gradle.plugin.idea-ext")

    group = "${property("modGroup")}.${project.name}"
    version = "${property("modVersion")}+$minecraftVersion"
    base.archivesName.set("$modId-${project.name}")

    sourceSets.test {
        java.srcDirs( )
        resources.srcDirs( )
    }

    configure<ArchitectPluginExtension> {
        minecraft = minecraftVersion
    }

    configure<LoomGradleExtensionAPI> {
        silentMojangMappingsLicense()

        val accessWidenerFile = project(":common").file("src/main/resources/$modId.accesswidener")

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
        "minecraft"("com.mojang:minecraft:$minecraftVersion")
        "mappings"(project.extensions.getByName<LoomGradleExtensionAPI>("loom").layered {
            officialMojangMappings()

            val parchmentMappings: String? by rootProject
            val parchmentMinecraftVersion: String? by rootProject

            if (parchmentMappings != null) {
                val mcVersion = parchmentMinecraftVersion ?: minecraftVersion
                parchment("org.parchmentmc.data:parchment-$mcVersion:$parchmentMappings@zip")
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
                languageVersion.set(JavaLanguageVersion.of(javaVersion))
            }

            withSourcesJar()
        }

        withType<JavaCompile> {
            options.encoding = "UTF-8"
            options.release.set(JavaLanguageVersion.of(javaVersion).asInt())

            javaToolchains {
                compilerFor {
                    languageVersion.set(JavaLanguageVersion.of(javaVersion))
                }
            }
        }

        jar {
            manifest {
                attributes(
                        "Specification-Title" to modId,
                        "Specification-Version" to minecraftVersion,
                        "Specification-Vendor" to "ExampleVendor",
                        "Implementation-Title" to project.name,
                        "Implementation-Version" to project.version.toString(),
                        "Implementation-Vendor" to "ExampleVendor",
                        "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date())
                )
            }
        }
    }

    idea.module {
        excludeDirs.addAll(listOf(
                file(".gradle"),
                file("build"),
                file("run")))
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven${project.name.capitalize()}") {
                groupId = project.group.toString()
                artifactId = project.base.archivesName.get()
                version = project.version.toString()

                project.extensions.getByName<LoomGradleExtensionAPI>("loom").disableDeprecatedPomGeneration(this)

                artifact(project.tasks.getByName("remapJar"))
                artifact(project.tasks.getByName("sourcesJar"))
            }
        }

        repositories {
            mavenLocal() // remove this to not publish to local maven

            // Add your maven repo here
            /*
            if (System.getenv("MY_MAVEN_USERNAME_PROP_KEY") != null && System.getenv("MY_MAVEN_PASSWORD_PROP_KEY") != null) {
                maven {
                    name = "MyMavenRepo"
                    url = uri("https://maven.my.domain")

                    credentials {
                        username = System.getenv("MY_MAVEN_USERNAME_PROP_KEY")
                        password = System.getenv("MY_MAVEN_PASSWORD_PROP_KEY")
                    }
                }
            }
            */
        }
    }
}

for (platform in platforms) {
    project(":$platform") {
        apply(plugin = "com.github.johnrengelman.shadow")

        evaluationDependsOn(":common")

        configure<ArchitectPluginExtension> {
            platformSetupLoomIde()
            loader(platform)
        }

        val common: Configuration by configurations.creating
        val shadowCommon: Configuration by configurations.creating

        configurations {
            compileClasspath.get().extendsFrom(common)
            runtimeClasspath.get().extendsFrom(common)
            getByName("development${platform.capitalize()}").extendsFrom(common)
        }

        dependencies {
            common(project(path = ":common", configuration = "namedElements")) { isTransitive = false }
            shadowCommon(project(path = ":common", configuration = "transformProduction${platform.capitalize()}")) { isTransitive = false }
        }

        sourceSets.main.get().resources.srcDirs("src/main/resources", "src/main/generated")

        tasks {
            withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
                exclude("architectury.common.json")
                configurations = listOf(shadowCommon)
                archiveClassifier.set("dev-shadow")
            }

            withType<net.fabricmc.loom.task.RemapJarTask> {
                val shadowJar: com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar by project
                inputFile.set(shadowJar.archiveFile)
                dependsOn(shadowJar)
                archiveClassifier.set(null as String?)
            }

            jar {
                archiveClassifier.set("dev")
            }

            getByName<Jar>("sourcesJar") {
                val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
                dependsOn(commonSources)
                from(commonSources.archiveFile.map { zipTree(it) })
            }
        }

        val javaComponent = components["java"] as AdhocComponentWithVariants
        javaComponent.withVariantsFromConfiguration(configurations["shadowRuntimeElements"]) {
            skip()
        }
    }
}
