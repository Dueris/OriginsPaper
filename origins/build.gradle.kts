plugins {
    id("xyz.jpenilla.run-paper") version "2.2.3"
}

group = "me.dueris"
version = "mc1.20-v0.2.8"
description = "Bringing the Origins Mod to PaperMC"

dependencies {
    // Project
    implementation(project(mapOf("path" to ":calio")))
    // Required Dependencies
    implementation("com.github.Dueris:ModelColorAPI:1.0.5-SNAPSHOT")
    compileOnly("org.mineskin:java-client:1.2.4-SNAPSHOT") // - in DependencyLoader
    compileOnly("io.github.classgraph:classgraph:4.8.165") // - in DependencyLoader - shaded in calio
    compileOnly("org.reflections:reflections:0.9.12") // - in DependencyLoader - shaded in calio
    compileOnly("org.mineskin:java-client:1.2.4-SNAPSHOT") // - in DependencyLoader
    // Optional Hook
    compileOnly("org.geysermc.geyser:api:2.2.0-SNAPSHOT")
    compileOnly("net.skinsrestorer:skinsrestorer-api:15.0.4")
    compileOnly("me.clip:placeholderapi:2.11.4")

}

tasks {
    jar {
        manifest {
            attributes(
                "Main-Class" to "me.dueris.genesismc.util.LaunchWarning"
            )
        }
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()

        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.20"
        )
        inputs.properties(props)
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }
}

tasks.register<Jar>("makePublisher") {
    dependsOn(tasks.shadowJar)
    archiveFileName.set("genesis-v0.2.8-SNAPSHOT.jar")
    from(sourceSets.main.get().output)
}

publishing {
    publications.create<MavenPublication>("genesismc") {
        artifact(tasks.getByName("makePublisher")) {
            groupId = "io.github.dueris"
            artifactId = "genesis"
            version = "v0.2.8-SNAPSHOT"
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