package io.github.dueris.originspaper.storage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.factory.PowerType;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Tuple;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.LinkedList;
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

	public PlayerPowerRepository(ServerPlayer player, RepositoryComponent component) {
		this.player = player;
	}

	public static PlayerPowerRepository getOrCreateRepo(ServerPlayer player) {
		if (REPO.containsKey(player)) {
			return REPO.get(player);
		}
		REPO.put(player, new PlayerPowerRepository(player, new RepositoryComponent()));
		return REPO.get(player);
	}

	public static void clearRepository() {
		REPO.clear();
	}

	public void clearData() {
		this.repo.clear();
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
		return Util.collapseList(repo.values().stream().map(Tuple::getA).toList());
	}

	@Unmodifiable
	public @NotNull List<PowerType> getAppliedPowers(OriginLayer layer) {
		return new CopyOnWriteArrayList<>(layer == null ? getAppliedPowers() : repo.get(layer).getA());
	}

	public synchronized @NotNull CompoundTag serializePowers(@NotNull CompoundTag nbt) {
		if (nbt.contains("PowerRepository")) {
			nbt.put("PowerRepository", new ListTag());
		}

		ListTag repository = nbt.getList("PowerRepository", 10);
		for (OriginLayer layer : this.repo.keySet()) {
			CompoundTag tag = new CompoundTag();
			tag.putString("Layer", layer.getTag());

			ListTag powers = new ListTag();
			for (PowerType type : this.repo.get(layer).getA()) {
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
			tag.putString("Origin", this.getOriginOrDefault(Origin.EMPTY, layer).getTag());
			repository.add(tag);
		}

		nbt.put("PowerRepository", repository);
		return nbt;
	}

	public void readPowers(@Nullable String nbt) {
		if (nbt == null) throw new IllegalArgumentException("Provided NBT was null!");
		readPowers(CompoundTag.CODEC.decode(JsonOps.INSTANCE, new Gson().fromJson(nbt, JsonElement.class)).getOrThrow().getFirst());
	}

	public synchronized void readPowers(@NotNull CompoundTag nbt) {
		if (!nbt.contains("PowerRepository")) return;
		ListTag repository = nbt.getList("PowerRepository", 10);
		repository.forEach(layerObject -> {
			if (!(layerObject instanceof CompoundTag tag))
				throw new JsonSyntaxException("LayerObject within PowerRepository should be a CompoundTag!");
			if (tag.contains("Layer")) {
				ResourceLocation layerLocation = ResourceLocation.parse(tag.getString("Layer"));
				ResourceLocation originLocation = ResourceLocation.parse(tag.getString("Origin"));
				Set<PowerType> powerTypes = new CopyOnWriteArraySet<>();

				tag.getList("Powers", 10).forEach(powerTag -> {
					CompoundTag power = (CompoundTag) powerTag;
					ResourceLocation powerLocation = ResourceLocation.parse(power.getString("Power"));
					PowerType powerType = OriginsPaper.getRegistry().retrieve(Registries.POWER).get(powerLocation);
					if (powerType == null) {
						log.error("Stored PowerType not found! ID: {}, skipping power..", powerLocation.toString());
						return;
					}
					powerType.loadFromData(power, player);
					powerTypes.add(powerType);
				});

				OriginLayer layer = OriginsPaper.getRegistry().retrieve(Registries.LAYER).get(layerLocation);
				if (layer == null) {
					log.error("Stored Layer not found! ID: {}, skipping layer..", layerLocation.toString());
				} else {
					for (PowerType type : powerTypes) {
						this.addPower(type, layer);
					}
					this.setOrigin(OriginsPaper.getRegistry().retrieve(Registries.ORIGIN).getOptional(originLocation).orElse(Origin.EMPTY), layer);
				}
			} else throw new JsonSyntaxException("Layer value not found in CompoundTag!");
		});

		// Final check to ensure all layers have a value
		for (OriginLayer layer : OriginsPaper.getRegistry().retrieve(Registries.LAYER).values()) {
			if (!this.repo.containsKey(layer)) {
				this.repo.put(layer, new Tuple<>(new LinkedList<>(), new AtomicReference<>(Origin.EMPTY)));
			}

		}

	}

	public ServerPlayer player() {
		return player;
	}

}
