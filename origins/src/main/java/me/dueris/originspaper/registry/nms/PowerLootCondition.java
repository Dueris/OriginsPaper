package me.dueris.originspaper.registry.nms;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.registry.registries.PowerType;
import me.dueris.originspaper.registry.Registries;
import me.dueris.originspaper.util.entity.PowerHolderComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.entity.Player;

import java.util.Optional;

public class PowerLootCondition implements LootItemCondition {
	public static final Codec<PowerLootCondition> CODEC = RecordCodecBuilder.create((instance) -> instance.group(ResourceLocation.CODEC.fieldOf("power").forGetter(PowerLootCondition::getPowerId), ResourceLocation.CODEC.optionalFieldOf("source").forGetter(PowerLootCondition::getPowerSourceId)).apply(instance, PowerLootCondition::new));
	public static final LootItemConditionType TYPE;

	static {
		TYPE = new LootItemConditionType(MapCodec.assumeMapUnsafe(CODEC));
	}

	private final ResourceLocation powerId;
	private final ResourceLocation powerSourceId;

	private PowerLootCondition(ResourceLocation powerId, Optional<ResourceLocation> powerSourceId) {
		this.powerId = powerId;
		this.powerSourceId = powerSourceId.orElse(null);
	}

	public boolean test(LootContext context) {
		Entity entity = context.getParam(LootContextParams.THIS_ENTITY);
		CraftEntity var4 = entity.getBukkitEntity();
		if (var4 instanceof Player player) {
			PowerType power = OriginsPaper.getPlugin().registry.retrieve(Registries.CRAFT_POWER).get(this.powerId);
			return PowerHolderComponent.hasPower(player, power.getTag());
		} else {
			return false;
		}
	}

	public LootItemConditionType getType() {
		return TYPE;
	}

	public ResourceLocation getPowerId() {
		return this.powerId;
	}

	public Optional<ResourceLocation> getPowerSourceId() {
		return Optional.of(this.powerSourceId);
	}
}