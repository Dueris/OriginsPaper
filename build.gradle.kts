import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.isDirectory

plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.6.0" apply true
    id("xyz.jpenilla.run-paper") version "2.2.3"
    id("com.github.johnrengelman.shadow") version "7.1.2" apply true
}

val paperweightVersion: String = "1.20.5-R0.1-SNAPSHOT"

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "io.papermc.paperweight.userdev")
    apply(plugin = "com.github.johnrengelman.shadow")
    paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    dependencies {
        paperweight.paperDevBundle(paperweightVersion)
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.opencollab.dev/main/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.inventivetalent.org/repository/public/")
        maven("https://repo.codemc.org/repository/maven-releases/")
        maven("https://jitpack.io")
    }
}

tasks {
    build {
        dependsOn(":origins:shadowJar")
        doLast {
            val targetJarDirectory: Path = projectDir.toPath().toAbsolutePath().resolve("build/libs")
            val subProject: Project = project("origins")
            println("Loading GenesisMC version-build : ".plus(subProject.version))
            if (!targetJarDirectory.isDirectory()) error("Target path is not a directory?!")

            Files.createDirectories(targetJarDirectory)
            File(targetJarDirectory.toAbsolutePath().toString()).listFiles()?.forEach { file ->
                if (file.isFile) {
                    file.delete()
                } else {
                    println("Directory was found in target dir?")
                }
            }
            Files.copy(
                file("origins/build/libs/origins-".plus(subProject.version).plus("-all").plus(".jar")).toPath()
                    .toAbsolutePath(),
                targetJarDirectory.resolve("genesis-".plus(subProject.version).plus(".jar")),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
    }
    runServer {
        minecraftVersion("1.20.5")
    }
}

tasks.register<Jar>("makePublisher") {
    dependsOn(tasks.shadowJar)
    archiveFileName.set("genesis-v1.0.0-SNAPSHOT.jar")
    from(sourceSets.main.get().output)
}

fun findOriginsFile(path: String): File? {
    val directory = File(path)

    if (!directory.exists() || !directory.isDirectory) {
        error("Specified path is not a valid directory.")
    }

    val originsFiles = directory.listFiles { file ->
        file.name.startsWith("origins") &&
                !file.name.endsWith("-dev.jar") &&
                !file.name.endsWith("-all.jar")
    }

    return if (originsFiles != null && originsFiles.isNotEmpty()) {
        originsFiles.first()
    } else {
        error("No matching file found in the specified directory.")
    }
}

publishing {
    publications.create<MavenPublication>("genesismc") {
        artifact(tasks.getByName("makePublisher")) {
            groupId = "io.github.dueris"
            artifactId = "genesis"
            version = "v1.0.0-SNAPSHOT"
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
