apply(from = rootProject.file("loader.gradle"))

loom {
    runs {
        /*data {
            data()

            // Arch-loom hard wires a whole collection of forge-data-gen properties
            // which stops us from overriding them with custom ones
            programArgs('--output', project(':common').file('src/main/generated').absolutePath)
            programArgs('--existing', project(':common').file('src/main/resources').absolutePath)
            programArgs('--existing', file('src/main/resources').absolutePath)
        }*/
    }
}
