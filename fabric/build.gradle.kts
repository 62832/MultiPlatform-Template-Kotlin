apply(from = rootProject.file("loader.gradle"))

loom {
    runs {
        /*data {
            inherit(client)
            vmArg('-Dfabric-api.datagen')
            vmArg("-Dfabric-api.datagen.output-dir=${file('src/main/generated')}")
            // vmArg("-Dfabric-api.datagen.output-dir=${project(':common').file('src/main/generated')}")
            vmArg("-Dfabric-api.datagen.modid=${MOD_ID}")
            vmArg('-Dfabric-api.datagen.strict-validation=true')
        }*/
    }
}