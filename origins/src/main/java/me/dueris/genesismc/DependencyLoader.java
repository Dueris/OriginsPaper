package me.dueris.genesismc;

import io.papermc.paper.plugin.loader.PluginClasspathBuilder;
import io.papermc.paper.plugin.loader.PluginLoader;
import io.papermc.paper.plugin.loader.library.impl.MavenLibraryResolver;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.jetbrains.annotations.NotNull;

public class DependencyLoader implements PluginLoader {

    @Override
    public void classloader(@NotNull PluginClasspathBuilder classpathBuilder) {
        MavenLibraryResolver resolver = new MavenLibraryResolver();
        resolver.addRepository(new RemoteRepository.Builder("paper", "default", "https://repo.papermc.io/repository/maven-public/").build());
        resolver.addRepository(new RemoteRepository.Builder("sonatype", "default", "https://oss.sonatype.org/content/groups/public/").build());
        resolver.addRepository(new RemoteRepository.Builder("jitpack", "default", "https://jitpack.io").build());
        resolver.addRepository(new RemoteRepository.Builder("inventivetalentDev", "default", "https://repo.inventivetalent.org/repository/public/").build());

        resolver.addDependency(new Dependency(new DefaultArtifact("io.github.classgraph:classgraph:4.8.165"), null));
        resolver.addDependency(new Dependency(new DefaultArtifact("org.reflections:reflections:0.9.12"), null));
        classpathBuilder.addLibrary(resolver);
    }

}
