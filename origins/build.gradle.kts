plugins {
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "me.dueris"
version = "1.${rootProject.extra["mcMajorVer"]}-${rootProject.extra["pluginVer"]}"
description = "Bringing the Origins Mod to PaperMC"

println("Loaded subproject \"${project.name}\" with version '$version'")

dependencies {
    // Project
    implementation(project(mapOf("path" to ":calio")))
    // Required Dependencies
    compileOnly("org.mineskin:java-client:2.0.0-SNAPSHOT") // - in DependencyLoader
    implementation("org.mineskin:java-client-jsoup:2.0.0-SNAPSHOT")
    compileOnly("com.jeff-media:MorePersistentDataTypes:2.4.0") // - in DependencyLoader

    compileOnly(files("../depends/eclipse-1.3.2-all.jar")) // Dependency

}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(21)
        options.isWarnings = false
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

}