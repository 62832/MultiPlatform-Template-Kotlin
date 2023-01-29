architectury {
    val platforms = rootProject.subprojects
            .filter { it.hasProperty("loom.platform") }
            .map { it.property("loom.platform").toString() }

    println("Platforms: $platforms")
    common(platforms)
}

val MIXIN_VERSION: String by project
val ARCHITECTURY_DEV_VERSION: String by project

dependencies {
    compileOnly("org.spongepowered:mixin:$MIXIN_VERSION")
    modApi("dev.architectury:architectury:$ARCHITECTURY_DEV_VERSION")
}

idea.module {
    excludeDirs.addAll(listOf(
            file(".gradle"),
            file("build"),
            file("run")))
}
