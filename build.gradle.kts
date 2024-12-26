import io.github.dueris.kotlin.eclipse.gradle.MinecraftVersion
import io.papermc.paperweight.userdev.ReobfArtifactConfiguration
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.isDirectory

plugins {
    java
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.7.7" apply true
    id("com.gradleup.shadow") version "9.0.0-beta4" apply true
    id("io.github.dueris.eclipse.gradle") version "1.2.2" apply true
}

version = "v1.3.0"
val apoli = "2.12.0-alpha.14+mc.1.21.1"
val calio = "1.14.0-alpha.8+mc.1.21.x"
val mcMajor = "21"
val mcMinor = "1"
val paper: String = "1.$mcMajor-R0.1-SNAPSHOT"

val minecraft = "1.$mcMajor" + if (mcMinor == "0") "" else ".$mcMinor"

println("Loading plugin version: $version")
println("Loading minecraft version: $minecraft")

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "io.papermc.paperweight.userdev")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "io.github.dueris.eclipse.gradle")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    repositories {
        mavenCentral()
        mavenLocal()
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://repo.opencollab.dev/main/")
        maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        maven("https://repo.inventivetalent.org/repository/public/")
        maven("https://repo.codemc.org/repository/maven-releases/")
        maven("https://maven.quiltmc.org/repository/release/")
        maven("https://jitpack.io")
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
        processResources {
            val props = mapOf(
                "minecraft" to minecraft,
                "version" to project.rootProject.version,
                "full_version" to "mc$minecraft-${project.rootProject.version}",
                "api" to "1.$mcMajor",
                "apoli" to apoli,
                "calio" to calio
            )
            inputs.properties(props)
            filesMatching(listOf("paper-plugin.yml", "apoli/paper-plugin.yml", "calio/paper-plugin.yml")) {
                expand(props)
            }

            filteringCharset = Charsets.UTF_8.name()
        }
    }

}

allprojects {
    dependencies {
        paperweight.paperDevBundle(paper)
    }

    paperweight {
        injectPaperRepository = true
        reobfArtifactConfiguration = ReobfArtifactConfiguration.MOJANG_PRODUCTION
    }

    eclipse {
        minecraft.set(MinecraftVersion.MC1_21_1.version)
        wideners = files("origins.accesswidener", "calio.accesswidener", "fabricapi.accesswidener")
    }

    tasks.shadowJar {
        exclude("com/google/gson/**")
        exclude("org/intellij/**")
        exclude("org/jetbrains/**")
    }
}

tasks {
    build {
        dependsOn(":origins:shadowJar")
        doLast {
            val targetJarDirectory: Path = projectDir.toPath().toAbsolutePath().resolve("build/libs")
            println("Loading OriginsPaper version-build: $version")
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
                file("origins/build/libs/origins-all.jar").toPath().toAbsolutePath(),
                targetJarDirectory.resolve("originspaper-mc${minecraft}-${version}.jar"),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
    }
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