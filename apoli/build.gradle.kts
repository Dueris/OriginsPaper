dependencies {
    implementation(project(":calio"))
    // Required Dependencies
    compileOnly("org.mineskin:java-client:1.2.4-SNAPSHOT") // - in DependencyLoader
    implementation("com.github.Dueris:ModelColorAPI:1.0.5-SNAPSHOT")
    // Optional Hook
    compileOnly("me.clip:placeholderapi:2.11.4")
    compileOnly("org.geysermc.geyser:api:2.2.0-SNAPSHOT")
    compileOnly("net.skinsrestorer:skinsrestorer-api:15.0.4")
}