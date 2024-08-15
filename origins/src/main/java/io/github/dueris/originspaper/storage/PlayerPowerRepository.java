package io.github.dueris.originspaper.storage;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.JsonOps;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.origin.Origin;
import io.github.dueris.originspaper.origin.OriginLayer;
import io.github.dueris.originspaper.power.PowerType;
import io.github.dueris.originspaper.registry.Registries;
import io.github.dueris.originspaper.util.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

public final class PlayerPowerRepository {
	static final ConcurrentMap<ServerPlayer, PlayerPowerRepository> REPO = new ConcurrentHashMap<>() {};
	final Map<OriginLayer, Origin> origins = new ConcurrentHashMap<>();
	private final ServerPlayer player;
	private final Map<OriginLayer, Set<PowerType>> appliedPowers = new ConcurrentHashMap<>() {};

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

	public ServerPlayer player() {
		return player;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (PlayerPowerRepository) obj;
		return Objects.equals(this.player, that.player);
	}

	public void addOrigin(Origin origin, OriginLayer layer) {
		origins.put(layer, origin);
	}

	public void removeOrigin(Origin origin, OriginLayer layer) {
		origins.remove(layer, origin);
	}

	public void addPower(PowerType type, OriginLayer layer) {
		if (type == null) return;
		if (appliedPowers.containsKey(layer)) {
			appliedPowers.get(layer).add(type);
			return;
		}
		appliedPowers.put(layer, new CopyOnWriteArraySet<>());
		appliedPowers.get(layer).add(type);
	}

	public void removePower(PowerType type, OriginLayer layer) {
		if (type == null) return;
		if (appliedPowers.containsKey(layer)) {
			appliedPowers.get(layer).remove(type);
			return;
		}
		appliedPowers.put(layer, new CopyOnWriteArraySet<>());
		appliedPowers.get(layer).remove(type);
	}

	@Unmodifiable
	public @NotNull List<PowerType> getAppliedPowers() {
		return Util.collapseSet(appliedPowers.values()).stream().filter(Objects::nonNull).toList();
	}

	@Unmodifiable
	public @NotNull List<PowerType> getAppliedPowers(OriginLayer layer) {
		return appliedPowers.get(layer).stream().filter(Objects::nonNull).toList();
	}

	public synchronized @NotNull CompoundTag serializePowers(@NotNull CompoundTag nbt) {
		if (nbt.contains("PowerRepository")) {
			nbt.put("PowerRepository", new ListTag());
		}

		ListTag repository = nbt.getList("PowerRepository", 10);
		for (OriginLayer layer : this.appliedPowers.keySet()) {
			CompoundTag tag = new CompoundTag();
			tag.putString("Layer", layer.getTag());

			ListTag powers = new ListTag();
			for (PowerType type : this.appliedPowers.get(layer)) {
				if (type == null) continue;
				powers.add(StringTag.valueOf(type.getTag()));
			}
			tag.put("Powers", powers);
			tag.putString("Origin", origins.getOrDefault(layer, OriginsPaper.EMPTY_ORIGIN).getTag());
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

				tag.getList("Powers", 8).forEach(powerTag -> {
					ResourceLocation powerLocation = ResourceLocation.parse(powerTag.getAsString());
					powerTypes.add(OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(powerLocation));
				});

				OriginLayer layer = OriginsPaper.getPlugin().registry.retrieve(Registries.LAYER).get(layerLocation);
				appliedPowers.put(layer, powerTypes);
				origins.put(layer, OriginsPaper.getPlugin().registry.retrieve(Registries.ORIGIN).get(originLocation));
			} else throw new JsonSyntaxException("Layer value not found in CompoundTag!");
		});
	}
}
