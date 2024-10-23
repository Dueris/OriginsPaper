package io.github.dueris.originspaper;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository.Builder;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("UnstableApiUsage")
public class DependencyLoader implements PluginLoader {

	@Override
	public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
		MavenLibraryResolver resolver = new MavenLibraryResolver();

		maven(resolver, "https://repo.papermc.io/repository/maven-public/");
		maven(resolver, "https://oss.sonatype.org/content/groups/public/");
		maven(resolver, "https://repo.opencollab.dev/main/");
		maven(resolver, "https://repo.extendedclip.com/content/repositories/placeholderapi/");
		maven(resolver, "https://repo.inventivetalent.org/repository/public/");
		maven(resolver, "https://repo.codemc.org/repository/maven-releases/");
		maven(resolver, "https://maven.quiltmc.org/repository/release/");
		maven(resolver, "https://maven.fabricmc.net/");

		resolver.addDependency(new Dependency(new DefaultArtifact("com.jeff-media:MorePersistentDataTypes:2.4.0"), null));
		resolver.addDependency(new Dependency(new DefaultArtifact("org.mineskin:java-client:2.0.0-SNAPSHOT"), null));
		classpathBuilder.addLibrary(resolver);
	}

	private void maven(@NotNull MavenLibraryResolver resolver, String url) {
		resolver.addRepository(new Builder(url.replace("https://", "").split("/")[0], "default", url).build());
	}
}
