package io.github.dueris.originspaper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository.Builder;
import org.jetbrains.annotations.NotNull;

public class DependencyLoader implements PluginLoader {

	@Override
	public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
		MavenLibraryResolver resolver = new MavenLibraryResolver();
		resolver.addRepository(new Builder("inventivetalentDev", "default", "https://repo.inventivetalent.org/repository/public/").build());
		resolver.addRepository(new Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build());
		resolver.addRepository(new Builder("sonatype", "default", "https://oss.sonatype.org/content/groups/public/").build());
		resolver.addRepository(new Builder("jitpack", "default", "https://jitpack.io").build());
		resolver.addRepository(new Builder("quilt", "default", "https://maven.quiltmc.org/repository/release/").build());
		resolver.addDependency(new Dependency(new DefaultArtifact("io.github.classgraph:classgraph:4.8.165"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("org.reflections:reflections:0.9.12"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("com.jeff-media:MorePersistentDataTypes:2.4.0"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("org.mineskin:java-client:2.0.0-SNAPSHOT"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("org.mineskin:java-client-jsoup:2.0.0-SNAPSHOT"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("org.quiltmc.parsers:json:0.2.1"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("org.quiltmc.parsers:gson:0.2.1"), null));
		classpathBuilder.addLibrary(resolver);
	}
}
