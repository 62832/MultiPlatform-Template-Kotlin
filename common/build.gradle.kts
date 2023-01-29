architectury {
    val platforms = rootProject.subprojects
            .filter { it.hasProperty("loom.platform") }
            .map { it.property("loom.platform").toString() }

    println("Platforms: $platforms")
    common(platforms)
}

val mixinVersion: String by project
val architecturyDevVersion: String by project

dependencies {
    compileOnly("org.spongepowered:mixin:$mixinVersion")
    modApi("dev.architectury:architectury:$architecturyDevVersion")
}

idea.module {
    excludeDirs.addAll(listOf(
            file(".gradle"),
            file("build"),
            file("run")))
}
