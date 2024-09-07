plugins {
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "me.dueris"
version = "1.${rootProject.extra["mcMajorVer"]}-${rootProject.extra["pluginVer"]}"
description = "Bringing the Origins Mod to PaperMC"

println("Loaded subproject \"${project.name}\" with version {$version}")

dependencies {
    // Project
    implementation(project(mapOf("path" to ":calio")))
    implementation(project(mapOf("path" to ":mixin")))
    // Required Dependencies
    compileOnly("io.github.classgraph:classgraph:4.8.165") // - in DependencyLoader
    compileOnly("org.reflections:reflections:0.9.12") // - in DependencyLoader
    compileOnly("org.mineskin:java-client:2.0.0-SNAPSHOT") // - in DependencyLoader
    compileOnly("org.mineskin:java-client-jsoup:2.0.0-SNAPSHOT")
    compileOnly("com.jeff-media:MorePersistentDataTypes:2.4.0") // - in DependencyLoader
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

tasks.register<Jar>("makePublisher") {
    dependsOn(tasks.shadowJar)
    archiveFileName.set("originspaper-v1.0.4-SNAPSHOT.jar")
    from(sourceSets.main.get().output)
}

publishing {
    publications.create<MavenPublication>("originspaper") {
        artifact(tasks.getByName("makePublisher")) {
            groupId = "io.github.dueris"
            artifactId = "originspaper"
            version = "v1.0.4-SNAPSHOT"
        }
    }
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = System.getenv("OSSRH_USERNAME")
                password = System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}
