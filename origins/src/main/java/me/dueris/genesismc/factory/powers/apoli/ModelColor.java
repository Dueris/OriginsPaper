package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.RequiresPlugin;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.PowerUpdateEvent;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.RaycastUtils;
import me.dueris.modelcolor.ModelColorAPI;
import me.dueris.modelcolor.colortransformers.OriginsTransformer;
import net.skinsrestorer.api.PropertyUtils;
import net.skinsrestorer.api.SkinsRestorer;
import net.skinsrestorer.api.SkinsRestorerProvider;
import net.skinsrestorer.api.exception.DataRequestException;
import net.skinsrestorer.api.property.SkinProperty;
import net.skinsrestorer.api.storage.PlayerStorage;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.profile.PlayerTextures;
import org.mineskin.MineskinClient;
import org.mineskin.data.Skin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RequiresPlugin(pluginName = "SkinsRestorer")
public class ModelColor extends PowerType {
	private final float r;
	private final float g;
	private final float b;

	public ModelColor(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, float r, float g, float b) {
		super(name, description, hidden, condition, loading_priority);
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("model_color"))
			.add("red", float.class, 1.0F)
			.add("green", float.class, 1.0F)
			.add("blue", float.class, 1.0F);
	}

	public void applyModelTransformer(ModelColorAPI api, SkinsRestorer skinsRestorer, Player p) {
		PlayerStorage storage = skinsRestorer.getPlayerStorage();
		try {
			Optional<SkinProperty> property = storage.getSkinForPlayer(p.getUniqueId(), p.getName());
			if (property.isPresent()) {
				String currentSkinURL = PropertyUtils.getSkinTextureUrl(property.get());
				String uuid = p.getUniqueId().toString();
				String modifiedFile = uuid + "_modified";

				CompletableFuture.runAsync(() -> {
					try {
						// Generate skin files
						BufferedImage currentSkin = api.createSourceFile(currentSkinURL, uuid);
						File modifiedSkin = api.createTransformed(currentSkin, new OriginsTransformer(), modifiedFile, r, g, b);

						MineskinClient client = new MineskinClient();
						CompletableFuture<Skin> future = client.generateUpload(modifiedSkin);
						future.thenAccept(skin -> {
							if (skin == null) {
								future.cancel(true);
								throw new RuntimeException("MineSkinClient returned null skin data! Perhaps check your internet connection?");
							}
							String url = skin.data.texture.url;
							url = formatUrl(url);
							p.getPersistentDataContainer().set(GenesisMC.identifier("modified-skin-url"), PersistentDataType.STRING, url);
						});

						p.getPersistentDataContainer().set(GenesisMC.identifier("original-skin-url"), PersistentDataType.STRING, currentSkinURL);
						p.saveData();
					} catch (IOException e) {
						throw new RuntimeException("An exception occurred when attempting to apply model changes to a player", e);
					}
				}).get();
			}
		} catch (DataRequestException | InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public String formatUrl(String url) {
		if (!url.startsWith("http://textures.minecraft.net")) {
			return "http://textures.minecraft.net" + url;
		}
		return url;
	}

	public String getSlim(Player p) {
		return p.getPlayerProfile().getTextures().getSkinModel().equals(PlayerTextures.SkinModel.CLASSIC) ? "CLASSIC" : "SLIM";
	}

	@EventHandler
	public void update(PowerUpdateEvent e) {
		if (Bukkit.getPluginManager().isPluginEnabled("SkinsRestorer")) {
			if (e.getPower().getTag().equalsIgnoreCase(getTag())) {
				if (e.isRemoved()) {
					RaycastUtils.executeNMSCommand(((CraftEntity) e.getPlayer()).getHandle(), CraftLocation.toVec3D(e.getPlayer().getLocation()), "skin clear @s");
				} else {
					ModelColorAPI api = ModelColorAPI.create(Bukkit.getPluginsFolder().getAbsolutePath() + File.separator + "GenesisMC" + File.separator + "skins");
					SkinsRestorer skinsRestorer = SkinsRestorerProvider.get();
					applyModelTransformer(api, skinsRestorer, e.getPlayer());
					String url = e.getPlayer().getPersistentDataContainer().get(GenesisMC.identifier("modified-skin-url"), PersistentDataType.STRING);
					String SPACE = " ";
					RaycastUtils.executeNMSCommand(((CraftEntity) e.getPlayer()).getHandle(), CraftLocation.toVec3D(e.getPlayer().getLocation()), "skin set " + url + SPACE + "@s" + SPACE + getSlim(e.getPlayer()));
				}
			}
		}
	}
}
