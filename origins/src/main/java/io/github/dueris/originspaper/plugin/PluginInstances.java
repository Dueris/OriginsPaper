package io.github.dueris.originspaper.plugin;

import io.github.dueris.originspaper.OriginsPaper;
import io.papermc.paper.plugin.provider.configuration.PaperPluginMeta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.jar.JarFile;

public class PluginInstances {
	public static final PaperPluginMeta APOLI_META;
	public static final PaperPluginMeta CALIO_META;

	static {
		try {
			JarFile plugin = new JarFile(OriginsPaper.jarFile.toFile());
			APOLI_META = PaperPluginMeta.create(
				new BufferedReader(new InputStreamReader(plugin.getInputStream(plugin.getJarEntry("apoli/paper-plugin.yml"))))
			);
			CALIO_META = PaperPluginMeta.create(
				new BufferedReader(new InputStreamReader(plugin.getInputStream(plugin.getJarEntry("calio/paper-plugin.yml"))))
			);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static void init() {
	}
}
