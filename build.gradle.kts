import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.isDirectory

plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.7.2-SNAPSHOT" apply true
    id("xyz.jpenilla.run-paper") version "2.2.3"
    id("io.github.goooler.shadow") version "8.1.7" apply true
}

val paperweightVersion: String = "1.21-R0.1-SNAPSHOT"

extra["mcMajorVer"] = "21"
extra["mcMinorVer"] = "1"
extra["pluginVer"] = "v1.2.0"

val mcMajorVer = extra["mcMajorVer"] as String
val mcMinorVer = extra["mcMinorVer"] as String
val pluginVer = extra["pluginVer"] as String

val mcVer = "1.$mcMajorVer" + if (mcMinorVer == "0") "" else ".$mcMinorVer"
extra["mcVer"] = mcVer
extra["fullVer"] = "mc$mcVer-$pluginVer"

println("Loading plugin version: $pluginVer")
println("Loading minecraft version: $mcVer")

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "io.papermc.paperweight.userdev")
    apply(plugin = "io.github.goooler.shadow")
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
        maven("https://maven.quiltmc.org/repository/release/")
    }

    tasks {
        processResources {
            val props = mapOf(
                "mcVer" to mcVer,
                "pluginVer" to pluginVer,
                "fullVer" to "mc$mcVer-$pluginVer",
                "apiVer" to "1.$mcMajorVer",
                "supportedVersions" to listOf("1.21", "1.21.1")
            )
            inputs.properties(props)
            filesMatching("paper-plugin.yml") {
                expand(props)
            }

            filteringCharset = Charsets.UTF_8.name()
        }
    }

}

tasks {
    build {
        dependsOn(":origins:shadowJar")
        doLast {
            val targetJarDirectory: Path = projectDir.toPath().toAbsolutePath().resolve("build/libs")
            val subProject: Project = project("origins")
            println("Loading OriginsPaper version-build: $pluginVer")
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
                file("origins/build/libs/origins-${subProject.version}-all.jar").toPath().toAbsolutePath(),
                targetJarDirectory.resolve("originspaper-mc1.${subProject.version}.jar"),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
    }
    runServer {
        minecraftVersion(mcVer)
    }
}

tasks.register<Jar>("makePublisher") {
    dependsOn(tasks.shadowJar)
    archiveFileName.set("originspaper-$pluginVer-SNAPSHOT.jar")
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
    publications.create<MavenPublication>("originspaper") {
        artifact(tasks.getByName("makePublisher")) {
            groupId = "io.github.dueris"
            artifactId = "originspaper"
            version = "$pluginVer-SNAPSHOT"
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
