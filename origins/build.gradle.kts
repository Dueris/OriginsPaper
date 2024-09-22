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
    compileOnly("io.github.classgraph:classgraph:4.8.165") // - in DependencyLoader
    compileOnly("org.reflections:reflections:0.9.12") // - in DependencyLoader
    compileOnly("org.mineskin:java-client:2.0.0-SNAPSHOT") // - in DependencyLoader
    compileOnly("org.mineskin:java-client-jsoup:2.0.0-SNAPSHOT") // - in DependencyLoader
    compileOnly("com.jeff-media:MorePersistentDataTypes:2.4.0") // - in DependencyLoader

    compileOnly("net.fabricmc:sponge-mixin:0.15.2+mixin.0.8.7") {
        exclude(group = "com.google.guava")
        exclude(group = "com.google.code.gson")
        exclude(group = "org.ow2.asm")
    }
    compileOnly("io.github.llamalad7:mixinextras-common:0.4.1") {
        exclude(group = "org.apache.commons")
    }

    implementation(files("../depends/eclipse.jar"))

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
