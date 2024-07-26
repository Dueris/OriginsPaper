version = "v1.0.0"

println("Loaded subproject \"${project.name}\" with version {$version}")

dependencies {
    // Required API
    compileOnly("io.github.classgraph:classgraph:4.8.165") // - in DependencyLoader
    compileOnly("org.reflections:reflections:0.9.12") // - in DependencyLoader

    implementation("org.quiltmc.parsers:json:0.2.1")
    implementation("org.quiltmc.parsers:gson:0.2.1")
}