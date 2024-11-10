group = "io.github.dueris"
description = "A full, but unofficial, port of Calio/Apoli/Origins to PaperMC servers"

dependencies {
    compileOnly("org.mineskin:java-client-jsoup:2.0.0-SNAPSHOT")
    compileOnly(files("../depends/eclipse-1.3.2-all.jar"))
    compileOnly("org.mineskin:java-client:2.0.0-SNAPSHOT")
    project("calio")
}

fun DependencyHandler.project(project: Any): Dependency? =
    add("implementation", project(mapOf("path" to ":$project")))
