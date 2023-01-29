loom {
    runs {
        /*
        data {
            client()
            property("fabric-api.datagen")
            property("fabric-api.datagen.output-dir", file("src/main/generated").absolutePath)
            // property("fabric-api.datagen.output-dir", project(":common").file("src/main/generated").absolutePath)
            property("fabric-api.datagen.strict-validation", true)

            val modId: String by project
            property("fabric-api.datagen.modid", rootProject.property("modId").toString())
        }
        */
    }
}

dependencies {
    val fabricLoaderVersion: String by project
    val fabricApiVersion: String by project
    val minecraftVersion: String by project
    val architecturyDevVersion: String by project

    modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
    modApi("net.fabricmc.fabric-api:fabric-api:${fabricApiVersion}+${minecraftVersion}")

    modApi("dev.architectury:architectury-fabric:${architecturyDevVersion}") {
        exclude(group = "net.fabricmc")
        exclude(group = "net.fabricmc.fabric-api")
    }
}

tasks.remapJar {
    injectAccessWidener.set(true)
}
