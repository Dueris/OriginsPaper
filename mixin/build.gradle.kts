version = "v1.0.0"

println("Loaded subproject \"${project.name}\" with version {$version}")

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.22")

    // Compile scope in Maven is equivalent to 'implementation' in Gradle
    implementation("org.ow2.asm:asm:9.7")
    implementation("org.ow2.asm:asm-util:9.7")
    implementation("com.github.olivergondza:maven-jdk-tools-wrapper:0.1")
    implementation("org.javassist:javassist:3.30.2-GA")
    implementation("net.bytebuddy:byte-buddy-agent:1.14.19")
    implementation("io.github.kasukusakura:jvm-self-attach:0.0.1")
    implementation("io.github.karlatemp:unsafe-accessor:1.7.0")
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.10.0.202406032230-r")
}