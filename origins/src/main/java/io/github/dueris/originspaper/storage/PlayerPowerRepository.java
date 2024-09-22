package io.github.dueris.originspaper.storage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicReference;

public final class PlayerPowerRepository extends RepositoryComponent {
	static final ConcurrentMap<ServerPlayer, PlayerPowerRepository> REPO = new ConcurrentHashMap<>() {
	};
	private static final Logger log = LogManager.getLogger(PlayerPowerRepository.class);
	private final ServerPlayer player;

	public PlayerPowerRepository(ServerPlayer player) {
		this.player = player;
	}

	public static PlayerPowerRepository getOrCreateRepo(ServerPlayer player) {
		if (REPO.containsKey(player)) {
			return REPO.get(player);
		}
		REPO.put(player, new PlayerPowerRepository(player));
		return REPO.get(player);
	}

	public static void clearRepository() {
		REPO.clear();
	}

	public void clearData() {
		this.clear();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (PlayerPowerRepository) obj;
		return Objects.equals(this.player, that.player);
	}

	@Unmodifiable
	public @NotNull List<PowerType> getAppliedPowers() {
		return Util.collapseCollection(powerRepo.values());
	}

	@Unmodifiable
	public @NotNull List<PowerType> getAppliedPowers(ResourceLocation source) {
		if (source == null) return getAppliedPowers();
		return new CopyOnWriteArrayList<>(this.getPowers(source));
	}

	public synchronized @NotNull CompoundTag serializePowers(@NotNull CompoundTag nbt) {
		if (nbt.contains("PowerRepository")) {
			nbt.put("PowerRepository", new ListTag());
		}

		if (nbt.contains("OriginRepository")) {
			nbt.put("OriginRepository", new ListTag());
		}

		ListTag powerRepo = nbt.getList("PowerRepository", 10);
		for (ResourceLocation layer : this.powerRepo.keySet()) {
			CompoundTag tag = new CompoundTag();
			tag.putString("Source", layer.toString());

			ListTag powers = new ListTag();
			for (PowerType type : this.getPowers(layer)) {
				if (type == null) continue;
				CompoundTag powerTag = new CompoundTag();
				powerTag.put("Power", StringTag.valueOf(type.getTag()));
				@Nullable CompoundTag extra = type.saveData(player);
				if (extra != null) {
					powerTag.merge(extra);
				}
				powers.add(powerTag);
			}
			tag.put("Powers", powers);
			powerRepo.add(tag);
		}

		ListTag originRepo = nbt.getList("OriginRepository", 10);
		for (OriginLayer layer : this.originRepo.keySet()) {
			CompoundTag tag = new CompoundTag();
			tag.putString("Layer", layer.getId().toString());
			tag.putString("Origin", this.getOriginOrDefault(Origin.EMPTY, layer).getId().toString());
			originRepo.add(tag);
		}

		nbt.put("OriginRepository", originRepo);
		nbt.put("PowerRepository", powerRepo);
		return nbt;
	}

	public void readPowers(@Nullable String nbt) {
		if (nbt == null) throw new IllegalArgumentException("Provided NBT was null!");
		readPowers(CompoundTag.CODEC.decode(JsonOps.INSTANCE, new Gson().fromJson(nbt, JsonElement.class)).getOrThrow().getFirst());
	}

	public synchronized void readPowers(@NotNull CompoundTag nbt) {
		if (!nbt.contains("PowerRepository") && !nbt.contains("OriginRepository")) return;
		ListTag powerRepo = nbt.getList("PowerRepository", 10);
		ListTag originRepo = nbt.getList("OriginRepository", 10);
		powerRepo.forEach(sourceObject -> {
			if (!(sourceObject instanceof CompoundTag tag))
				throw new JsonSyntaxException("Source Object within PowerRepository should be a CompoundTag!");
			if (tag.contains("Source")) {
				ResourceLocation sourceLocation = ResourceLocation.parse(tag.getString("Source"));
				Set<PowerType> powerTypes = new CopyOnWriteArraySet<>();

				for (Tag rawTag : tag.getList("Powers", 10)) {
					CompoundTag powerTag = (CompoundTag) rawTag;
					ResourceLocation powerLocation = ResourceLocation.parse(powerTag.getString("Power"));
					PowerType powerType = PowerType.REGISTRY.get(powerLocation);
					if (powerType == null) {
						log.error("Stored PowerType not found! ID: {}, skipping power..", powerLocation.toString());
						continue;
					}

					powerType.loadFromData(powerTag, player);
					powerTypes.add(powerType);
				}

				for (PowerType powerType : powerTypes) {
					this.addPower(powerType, sourceLocation);
				}
			} else throw new JsonSyntaxException("Source value not found in CompoundTag!");
		});

		originRepo.forEach(layerObject -> {
			if (!(layerObject instanceof CompoundTag tag))
				throw new JsonSyntaxException("Layer Object within OriginRepository should be a CompoundTag!");
			if (tag.contains("Layer")) {
				ResourceLocation layerLocation = ResourceLocation.parse(tag.getString("Layer"));
				ResourceLocation originLocation = ResourceLocation.parse(tag.getString("Origin"));

				OriginLayer layer = OriginLayer.REGISTRY.get(layerLocation);
				if (layer == null) {
					log.error("Stored Layer not found! ID: {}, skipping layer..", layerLocation.toString());
				} else {
					this.setOrigin(Origin.REGISTRY.getOrDefault(originLocation, Origin.EMPTY), layer);
				}
			} else throw new JsonSyntaxException("Layer value not found in CompoundTag!");
		});

		// Final check to ensure all layers have a value
		for (OriginLayer layer : OriginLayer.REGISTRY.values()) {
			if (!this.originRepo.containsKey(layer)) {
				this.originRepo.put(layer, new AtomicReference<>(Origin.EMPTY));
			}

		}

	}

	public ServerPlayer player() {
		return player;
	}

}
