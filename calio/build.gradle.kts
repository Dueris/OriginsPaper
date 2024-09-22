version = "v1.0.0"

println("Loaded subproject \"${project.name}\" with version '$version'")

dependencies {
    compileOnly("org.quiltmc.parsers:json:0.2.1")
    compileOnly("org.quiltmc.parsers:gson:0.2.1")
}
