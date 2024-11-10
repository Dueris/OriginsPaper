version = "v1.0.0"

dependencies {
    implementation("org.quiltmc.parsers:json:0.2.1") {
        exclude("com.google.code.gson:gson:2.11.0")
    }
    implementation("org.quiltmc.parsers:gson:0.2.1") {
        exclude("com.google.code.gson:gson:2.11.0")
    }
}
