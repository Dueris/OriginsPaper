import io.github.dueris.kotlin.eclipse.gradle.MinecraftVersion
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.isDirectory

plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.7.3" apply true
    id("xyz.jpenilla.run-paper") version "2.2.3"
    id("com.gradleup.shadow") version "8.3.3" apply true
    id("io.github.dueris.eclipse.gradle") version "1.1.0-beta82" apply true
}

val paperweightVersion: String = "1.21-R0.1-SNAPSHOT"

extra["mcMajorVer"] = "21"
extra["mcMinorVer"] = "1"
extra["pluginVer"] = "v1.3.0"

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
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "io.github.dueris.eclipse.gradle")
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

    eclipse {
        minecraft = MinecraftVersion.MC1_21_1
        wideners = files("origins.accesswidener", "calio.accesswidener", "fabricapi.accesswidener")
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
        processResources {
            val props = mapOf(
                "mcVer" to mcVer,
                "pluginVer" to pluginVer,
                "fullVer" to "mc$mcVer-$pluginVer",
                "apiVer" to "1.$mcMajorVer",
                "supported" to listOf("1.21", "1.21.1"),
                "apoli" to "2.12.0-alpha.12+mc.1.21.x",
                "calio" to "1.14.0-alpha.7+mc.1.21.x"
            )
            inputs.properties(props)
            filesMatching("paper-plugin.yml") {
                expand(props)
            }
            filesMatching("apoli/paper-plugin.yml") {
                expand(props)
            }
            filesMatching("calio/paper-plugin.yml") {
                expand(props)
            }

            filteringCharset = Charsets.UTF_8.name()
        }
    }

    tasks.getByName<Jar>("jar") {
        manifest {
            attributes(
                "Premain-Class" to "space.vectrix.ignite.agent.IgniteAgent",
                "Agent-Class" to "space.vectrix.ignite.agent.IgniteAgent",
                "Launcher-Agent-Class" to "space.vectrix.ignite.agent.IgniteAgent",
                "Main-Class" to "space.vectrix.ignite.IgniteBootstrap",
                "Multi-Release" to true,
                "Automatic-Module-Name" to "net.minecrell.terminalconsole",

                "Specification-Title" to "ignite",
                "Specification-Version" to "v1.3.0",
                "Specification-Vendor" to "vectrix.space",

                "Implementation-Title" to project.name,
                "Implementation-Version" to "v1.3.0",
                "Implementation-Vendor" to "vectrix.space"
            )

            attributes(
                "org/objectweb/asm/",
                "Implementation-Version" to "9.7.1"
            )
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
                targetJarDirectory.resolve("originspaper-mc${subProject.version}.jar"),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
    }
    runServer {
        minecraftVersion(mcVer)
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
