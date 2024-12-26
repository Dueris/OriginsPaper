group = "io.github.dueris"
description = "A full, but unofficial, port of Calio/Apoli/Origins to PaperMC servers"

dependencies {
    implementation("com.github.DavidNiessen:JsonConfigAPI:1.0")
    compileOnly("org.mineskin:java-client-jsoup:2.0.0-SNAPSHOT")
    compileOnly(files("../depends/eclipse-2.0.0-all.jar"))
    compileOnly("org.mineskin:java-client:2.0.0-SNAPSHOT")
    project("calio")
}

fun DependencyHandler.project(project: Any): Dependency? =
    add("implementation", project(mapOf("path" to ":$project")))
