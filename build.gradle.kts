import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.isDirectory

plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.5.11" apply true
    id("xyz.jpenilla.run-paper") version "2.2.3"
    id("com.github.johnrengelman.shadow") version "7.1.2" apply true
}

val paperweightVersion : String = "1.20.4-R0.1-SNAPSHOT"

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "io.papermc.paperweight.userdev")
    apply(plugin = "com.github.johnrengelman.shadow")

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(17)
        }
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

tasks.register<Jar>("makePublisher"){
    dependsOn(tasks.shadowJar)
    archiveFileName.set("genesis-v0.2.8-SNAPSHOT.jar")
    from(sourceSets.main.get().output)
}

tasks.register<Jar>("buildJar"){
    dependsOn(":origins:reobfJar")
    doLast{
        val file = findOriginsFile("./origins/build/libs")
        if(file != null){
            val targetJarDirectory: Path = projectDir.toPath().toAbsolutePath().resolve("build/libs")
            val subProject: Project = project("origins");
            if(!targetJarDirectory.isDirectory()) error("Target path is not a directory?!")

            Files.createDirectories(targetJarDirectory)
            File(targetJarDirectory.toAbsolutePath().toString()).listFiles().forEach { file ->
                if(file.isFile){
                    file.delete()
                }else{
                    println("Directory was found in target dir?")
                }
            }
            Files.copy(
                file("origins/build/libs/origins-".plus(subProject.version).plus(".jar")).toPath().toAbsolutePath(),
                targetJarDirectory.resolve("genesis-".plus(subProject.version).plus(".jar")),
                StandardCopyOption.REPLACE_EXISTING
            )
        }else{
            error("Couldn't build GenesisMC because output file was null!")
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

tasks.getByName("build").dependsOn("buildJar")

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
                username=System.getenv("OSSRH_USERNAME")
                password=System.getenv("OSSRH_PASSWORD")
            }
        }
    }
}
