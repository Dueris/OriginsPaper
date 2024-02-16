plugins {
    `java-library`
    id("io.papermc.paperweight.userdev") version "1.5.11"
    id("xyz.jpenilla.run-paper") version "2.2.3"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "me.dueris"
version = "mc1.20-v0.2.8"
description = "Bringing the Origins Mod to PaperMC"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    // Paperweight
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    // Optional Hook
    compileOnly("me.clip:placeholderapi:2.11.4")
    compileOnly("org.geysermc.geyser:api:2.2.0-SNAPSHOT")
    compileOnly("net.skinsrestorer:skinsrestorer-api:15.0.4")
    // Required API
    compileOnly("io.github.classgraph:classgraph:4.8.165")
    compileOnly("com.github.LinsMinecraftStudio.LighterAPI:lightapi-bukkit-common:5.4.0-SNAPSHOT")
    compileOnly("org.reflections:reflections:0.9.12")
    compileOnly("org.mineskin:java-client:1.2.4-SNAPSHOT")
    compileOnly("com.github.Dueris:ModelColorAPI:1.0.5-SNAPSHOT")
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

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    jar {
        manifest {
            attributes(
                "Main-Class" to "me.dueris.genesismc.util.GuiWarning"
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
