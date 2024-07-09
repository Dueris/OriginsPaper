version = "v1.0.0"

println("Loaded subproject \"${project.name}\" with version {$version}")

dependencies {
    // Required API
    compileOnly("io.github.classgraph:classgraph:4.8.165") // - in DependencyLoader
    compileOnly("org.reflections:reflections:0.9.12") // - in DependencyLoader
}