architectury {
    val platforms: List<String> by rootProject.extra
    println("Platforms: $platforms")
    common(platforms)
}

dependencies {
    val mixinVersion: String by project
    val architecturyDevVersion: String by project

    compileOnly("org.spongepowered:mixin:$mixinVersion")
    modApi("dev.architectury:architectury:$architecturyDevVersion")
}
