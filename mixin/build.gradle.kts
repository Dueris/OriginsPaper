version = "v1.0.0"

println("Loaded subproject \"${project.name}\" with version {$version}")

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.22")

    // Compile scope in Maven is equivalent to 'implementation' in Gradle
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-util:9.2")
    implementation("com.github.olivergondza:maven-jdk-tools-wrapper:0.1")
    implementation("org.javassist:javassist:3.28.0-GA")
    implementation("net.bytebuddy:byte-buddy-agent:1.12.8")
    implementation("io.github.kasukusakura:jvm-self-attach:0.0.1")
    implementation("io.github.karlatemp:unsafe-accessor:1.6.1")
    implementation("org.eclipse.jgit:org.eclipse.jgit:5.13.0.202109080827-r")
}