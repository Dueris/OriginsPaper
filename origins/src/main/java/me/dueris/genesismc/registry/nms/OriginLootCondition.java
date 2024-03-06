package me.dueris.genesismc.registry.nms;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Origin;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.entity.Player;

import java.util.Optional;

public class OriginLootCondition implements LootItemCondition {
	public static final Codec<OriginLootCondition> CODEC = RecordCodecBuilder.create((instance) -> {
		return instance.group(ResourceLocation.CODEC.fieldOf("origin").forGetter(OriginLootCondition::getOriginId), ResourceLocation.CODEC.optionalFieldOf("source").forGetter(OriginLootCondition::getOriginSourceId)).apply(instance, OriginLootCondition::new);
	});
	public static final LootItemConditionType TYPE;

	static {
		TYPE = new LootItemConditionType(CODEC);
	}

	private ResourceLocation originId;
	private ResourceLocation originSourceId;

	private OriginLootCondition(ResourceLocation originId, Optional<ResourceLocation> originSourceId) {
	}

	public boolean test(LootContext context) {
		Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
		CraftEntity var4 = entity.getBukkitEntity();
		if (var4 instanceof Player player) {
			NamespacedKey key = CraftNamespacedKey.fromMinecraft(this.originId);
			Origin origin = (Origin) GenesisMC.getPlugin().registry.retrieve(Registries.ORIGIN).get(key);
			return OriginPlayerAccessor.hasOrigin(player, origin.getTag());
		} else {
			return false;
		}
	}

	public LootItemConditionType getType() {
		return OriginLootCondition.TYPE;
	}

	public ResourceLocation getOriginId() {
		return this.originId;
	}

	public Optional<ResourceLocation> getOriginSourceId() {
		return Optional.of(this.originSourceId);
	}
}