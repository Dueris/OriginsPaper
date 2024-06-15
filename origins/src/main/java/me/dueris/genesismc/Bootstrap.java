package me.dueris.genesismc;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.data.JsonObjectRemapper;
import me.dueris.calio.registry.Registrar;
import me.dueris.calio.registry.impl.CalioRegistry;
import me.dueris.genesismc.factory.actions.types.BiEntityActions;
import me.dueris.genesismc.factory.actions.types.BlockActions;
import me.dueris.genesismc.factory.actions.types.EntityActions;
import me.dueris.genesismc.factory.actions.types.ItemActions;
import me.dueris.genesismc.factory.conditions.types.*;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.nms.OriginLootCondition;
import me.dueris.genesismc.registry.nms.PowerLootCondition;
import me.dueris.genesismc.registry.registries.DatapackRepository;
import me.dueris.genesismc.registry.registries.Layer;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.screen.ChoosingPage;
import me.dueris.genesismc.util.LangFile;
import me.dueris.genesismc.util.TextureLocation;
import me.dueris.genesismc.util.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

// TODO: MachineMaker PluginDatapacks
// TODO: WaterProtection Enchantment - 1.21
public class Bootstrap implements PluginBootstrap {
	public static ArrayList<String> oldDV = new ArrayList<>();

	static {
		oldDV.add("OriginsGenesis");
		oldDV.add("Origins-Genesis");
		oldDV.add("Origins-GenesisMC");
		oldDV.add("Origins-GenesisMC[0_2_2]");
		oldDV.add("Origins-GenesisMC[0_2_4]");
		oldDV.add("Origins-GenesisMC[0_2_6]");
	}

	private CalioRegistry registry;

	public static void deleteDirectory(Path directory, boolean ignoreErrors) throws IOException {
		if (Files.exists(directory)) {
			Files.walk(directory)
				.sorted(Comparator.reverseOrder()) // Sort in reverse order for correct deletion
				.forEach(path -> {
					try {
						Files.deleteIfExists(path);
						Files.delete(path);
					} catch (IOException e) {
						if (!ignoreErrors) {
							System.err.println("Error deleting: " + path + e);
						}
					}
				});
		}
	}

	public static void copyOriginDatapack(Path datapackPath) {
		for (String string : oldDV) {
			if (Files.exists(datapackPath)) {
				String path = Path.of(datapackPath + File.separator + string).toAbsolutePath().toString();
				try {
					deleteDirectory(Path.of(path), true);
				} catch (IOException e) {
					// Something happened when deleting, ignore.
				}
			} else {
				File file = new File(datapackPath.toAbsolutePath().toString());
				file.mkdirs();
				copyOriginDatapack(datapackPath);
			}
		}
		try {
			CodeSource src = Util.class.getProtectionDomain().getCodeSource();
			URL jar = src.getLocation();
			ZipInputStream zip = new ZipInputStream(jar.openStream());
			while (true) {
				ZipEntry entry = zip.getNextEntry();
				if (entry == null)
					break;
				String name = entry.getName();

				if (!name.startsWith("minecraft/")) continue;
				if (FilenameUtils.getExtension(name).equals("zip")) continue;
				if (name.equals("minecraft/")) continue;

				name = name.substring(9);
				File file = new File(datapackPath.toAbsolutePath().toString().replace(".\\", "") + File.separator + name);
				if (!file.getName().contains(".")) {
					Files.createDirectory(Path.of(file.getAbsolutePath()));
					continue;
				}

				// Ensure parent directory exists
				File parentDir = file.getParentFile();
				if (!parentDir.exists()) {
					parentDir.mkdirs();
				}

				// Copy PNG files
				if (FilenameUtils.getExtension(name).equalsIgnoreCase("png")) {
					try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
						byte[] buffer = new byte[1024];
						int len;
						while ((len = zip.read(buffer)) > 0) {
							bos.write(buffer, 0, len);
						}
					}
				} else { // Copy non-PNG files as text
					Files.writeString(Path.of(file.getAbsolutePath()), new String(zip.readAllBytes()));
				}
			}
			zip.close();
		} catch (Exception e) {
			// e.printStackTrace(); // Print stack trace for debugging // I changed my mind
		}

	}

	public static String levelNameProp() {
		Path propPath = Paths.get("server.properties");
		if (propPath.toFile().exists()) {
			Properties properties = new Properties();
			try (FileInputStream input = new FileInputStream(propPath.toFile())) {
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(properties.keySet());
			return properties.getProperty("level-name", "world");
		} else {
			return "world";
		}
	}

	@Override
	public void bootstrap(@NotNull BootstrapContext context) {
		try {
			File packDir = new File(this.parseDatapackPath());
			copyOriginDatapack(packDir.toPath());
		} catch (Exception e) {
			// ignore
		}
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("apoli", "power"), PowerLootCondition.TYPE);
		Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, ResourceLocation.fromNamespaceAndPath("origins", "origin"), OriginLootCondition.TYPE);

		JsonObjectRemapper.typeMappings.add(new Pair<String, String>() {
			@Override
			public String left() {
				return "origins";
			}

			@Override
			public String right() {
				return "apoli";
			}
		});
		// Our version of restricted_armor allows handling of both.
		JsonObjectRemapper.typeAlias.put("apoli:conditioned_restrict_armor", "apoli:restrict_armor");
		JsonObjectRemapper.typeAlias.put("apugli:edible_item", "apoli:edible_item");
		JsonObjectRemapper.typeAlias.put("apoli:modify_attribute", "apoli:conditioned_attribute");
		JsonObjectRemapper.typeAlias.put("apoli:add_to_set", "apoli:add_to_entity_set");
		JsonObjectRemapper.typeAlias.put("apoli:remove_from_set", "apoli:remove_from_entity_set");
		JsonObjectRemapper.typeAlias.put("apoli:action_on_set", "apoli:action_on_entity_set");
		JsonObjectRemapper.typeAlias.put("apoli:in_set", "apoli:in_entity_set");
		JsonObjectRemapper.typeAlias.put("apoli:set_size", "apoli:entity_set_size");

		this.registry = CalioRegistry.INSTANCE;
		// Create new registry instances
		this.registry.create(Registries.ORIGIN, new Registrar<Origin>(Origin.class));
		this.registry.create(Registries.LAYER, new Registrar<Layer>(Layer.class));
		this.registry.create(Registries.CRAFT_POWER, new Registrar<PowerType>(PowerType.class));
		this.registry.create(Registries.FLUID_CONDITION, new Registrar<FluidConditions.ConditionFactory>(FluidConditions.ConditionFactory.class));
		this.registry.create(Registries.ENTITY_CONDITION, new Registrar<EntityConditions.ConditionFactory>(EntityConditions.ConditionFactory.class));
		this.registry.create(Registries.BIOME_CONDITION, new Registrar<BiomeConditions.ConditionFactory>(BiomeConditions.ConditionFactory.class));
		this.registry.create(Registries.BIENTITY_CONDITION, new Registrar<BiEntityConditions.ConditionFactory>(BiEntityConditions.ConditionFactory.class));
		this.registry.create(Registries.BLOCK_CONDITION, new Registrar<BlockConditions.ConditionFactory>(BlockConditions.ConditionFactory.class));
		this.registry.create(Registries.ITEM_CONDITION, new Registrar<ItemConditions.ConditionFactory>(ItemConditions.ConditionFactory.class));
		this.registry.create(Registries.DAMAGE_CONDITION, new Registrar<DamageConditions.ConditionFactory>(DamageConditions.ConditionFactory.class));
		this.registry.create(Registries.ENTITY_ACTION, new Registrar<EntityActions.ActionFactory>(EntityActions.ActionFactory.class));
		this.registry.create(Registries.ITEM_ACTION, new Registrar<ItemActions.ActionFactory>(ItemActions.ActionFactory.class));
		this.registry.create(Registries.BLOCK_ACTION, new Registrar<BlockActions.ActionFactory>(BlockActions.ActionFactory.class));
		this.registry.create(Registries.BIENTITY_ACTION, new Registrar<BiEntityActions.ActionFactory>(BiEntityActions.ActionFactory.class));
		this.registry.create(Registries.TEXTURE_LOCATION, new Registrar<TextureLocation>(TextureLocation.class));
		this.registry.create(Registries.LANG, new Registrar<LangFile>(LangFile.class));
		this.registry.create(Registries.PACK_SOURCE, new Registrar<DatapackRepository>(DatapackRepository.class));
		this.registry.create(Registries.CHOOSING_PAGE, new Registrar<ChoosingPage>(ChoosingPage.class));
	}

	public String parseDatapackPath() {
		try {
			org.bukkit.configuration.file.YamlConfiguration bukkitConfiguration = YamlConfiguration.loadConfiguration(Paths.get("bukkit.yml").toFile());
			File container;
			container = new File(bukkitConfiguration.getString("settings.world-container", "."));
			String s = Optional.ofNullable(
				levelNameProp()
			).orElse("world");
			Path datapackFolder = Paths.get(container.getAbsolutePath() + File.separator + s + File.separator + "datapacks");
			return datapackFolder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
