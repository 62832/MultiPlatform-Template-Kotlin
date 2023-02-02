loom {
    runs {
        /*data {
            data()

            // Arch-loom hard-wires a whole collection of forge-data-gen properties
            // which stops us from overriding them with custom ones
            programArgs('--output', project(':common').file('src/main/generated').absolutePath)
            programArgs('--existing', project(':common').file('src/main/resources').absolutePath)
            programArgs('--existing', file('src/main/resources').absolutePath)
        }*/
    }

    forge {
        if (accessWidenerPath.isPresent) {
            convertAccessWideners.set(true)
            extraAccessWideners.add(accessWidenerPath.toString())
        }

        val modId: String by project

        if (file("src/main/resources/$modId.mixins.json").exists()) {
            mixinConfig("$modId.mixins.json")
        }

        if (project(":common").file("src/main/resources/$modId-common.mixins.json").exists()) {
            mixinConfig("$modId-common.mixins.json")
        }

        dataGen {
            mod(modId)
        }
    }
}

dependencies {
    val minecraftVersion: String by project
    val forgeVersion: String by project
    val architecturyDevVersion: String by project

    forge("net.minecraftforge:forge:$minecraftVersion-$forgeVersion")
    modApi("dev.architectury:architectury-forge:$architecturyDevVersion")
}
