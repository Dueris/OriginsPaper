plugins {
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "me.dueris"
version = "${rootProject.extra["mcVer"]}-${rootProject.extra["pluginVer"]}"
description = "Bringing the Origins Mod to PaperMC"

println("Loaded subproject \"${project.name}\" with version {$version}")

dependencies {
    // Project
    implementation(project(mapOf("path" to ":calio")))
    // Required Dependencies
    implementation("com.github.Dueris:ModelColorAPI:1.0.5-SNAPSHOT")
    implementation("org.mineskin:java-client:1.2.4-SNAPSHOT")
    compileOnly("io.github.classgraph:classgraph:4.8.165") // - in DependencyLoader - shaded in calio
    compileOnly("org.reflections:reflections:0.9.12") // - in DependencyLoader - shaded in calio
    compileOnly("org.mineskin:java-client:1.2.4-SNAPSHOT") // - in DependencyLoader
    // Optional Hook
    compileOnly("org.geysermc.floodgate:api:2.2.2-SNAPSHOT")
    compileOnly("net.skinsrestorer:skinsrestorer-api:15.0.4")
    compileOnly("me.clip:placeholderapi:2.11.4")

}

tasks {
    jar {
        manifest {
            attributes(
                "Main-Class" to "me.dueris.originspaper.util.LaunchWarning"
            )
        }
    }
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
