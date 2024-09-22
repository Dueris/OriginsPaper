package io.github.dueris.originspaper.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import io.github.dueris.originspaper.access.FunctionPoolRetriever;
import io.github.dueris.originspaper.access.IdentifiedLootTable;
import io.github.dueris.originspaper.access.ReplacingLootContext;
import io.github.dueris.originspaper.power.type.ReplaceLootTablePower;
import io.github.dueris.originspaper.storage.PowerHolderComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ReloadableServerRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Location;
import org.bukkit.craftbukkit.CraftLootTable;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableMixin implements IdentifiedLootTable, FunctionPoolRetriever {

	@Shadow
	@Final
	private BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
	@Shadow
	@Final
	private List<LootPool> pools;
	@Shadow
	@Final
	private Optional<ResourceLocation> randomSequence;
	@Unique
	private ResourceKey<LootTable> apoli$lootTableKey;
	@Unique
	private ReloadableServerRegistries.Holder apoli$registryLookup;

	@Unique
	private static org.bukkit.loot.@NotNull LootContext originspaper$convertContext(net.minecraft.world.level.storage.loot.LootContext info) {
		if (info == null) {
			throw new IllegalArgumentException("NMS LootContext cannot be null!");
		}
		Vec3 position = info.getParamOrNull(LootContextParams.ORIGIN);
		if (position == null) {
			// Entity isnt null if we use this
			position = info.getParam(LootContextParams.THIS_ENTITY).position();
		}
		Location location = CraftLocation.toBukkit(position, info.getLevel().getWorld());
		org.bukkit.loot.LootContext.Builder contextBuilder = new org.bukkit.loot.LootContext.Builder(location);

		if (info.hasParam(LootContextParams.ATTACKING_ENTITY)) {
			CraftEntity killer = info.getParamOrNull(LootContextParams.ATTACKING_ENTITY).getBukkitEntity();
			if (killer instanceof CraftHumanEntity) {
				contextBuilder.killer((CraftHumanEntity) killer);
			}
		}

		if (info.hasParam(LootContextParams.THIS_ENTITY)) {
			contextBuilder.lootedEntity(info.getParamOrNull(LootContextParams.THIS_ENTITY).getBukkitEntity());
		}

		contextBuilder.luck(info.getLuck());
		return contextBuilder.build();
	}

	@Override
	public void apoli$setKey(ResourceKey<LootTable> lootTableKey, ReloadableServerRegistries.Holder registryLookup) {
		this.apoli$lootTableKey = lootTableKey;
		this.apoli$registryLookup = registryLookup;
	}

	@Override
	public ResourceKey<LootTable> apoli$getLootTableKey() {
		return apoli$lootTableKey;
	}

	@WrapMethod(method = "getRandomItemsRaw(Lnet/minecraft/world/level/storage/loot/LootContext;Ljava/util/function/Consumer;)V")
	public void apoli$modifyLootTable(LootContext context, Consumer<ItemStack> lootConsumer, Operation<Void> original) {
		if (!(context instanceof ReplacingLootContext replacingContext) || replacingContext.apoli$isReplaced((LootTable) (Object) this)) {
			original.call(context, lootConsumer);
			return;
		}

		if (this.apoli$getLootTableKey() == null || !context.hasParam(LootContextParams.THIS_ENTITY)) {
			original.call(context, lootConsumer);
			return;
		}

		LootContextParamSet lootContextType = replacingContext.apoli$getType();
		Entity powerHolder = context.getParamOrNull(LootContextParams.THIS_ENTITY);

		if (lootContextType == LootContextParamSets.FISHING) {
			if (powerHolder instanceof FishingHook fishingBobberEntity) {
				powerHolder = fishingBobberEntity.getOwner();
			}
		} else if (lootContextType == LootContextParamSets.ENTITY) {
			if (context.hasParam(LootContextParams.ATTACKING_ENTITY)) {
				powerHolder = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
			}
		} else if (lootContextType == LootContextParamSets.PIGLIN_BARTER) {
			if (powerHolder instanceof Piglin piglinEntity) {

				Optional<Player> playerEntity = piglinEntity.getBrain().getMemoryInternal(MemoryModuleType.NEAREST_VISIBLE_PLAYER);

				if (playerEntity != null && playerEntity.isPresent()) {
					powerHolder = playerEntity.get();
				}

			}
		}

		Entity finalPowerHolder = powerHolder;
		List<ReplaceLootTablePower> replaceLootTablePowers = PowerHolderComponent.getPowers(powerHolder.getBukkitEntity(), ReplaceLootTablePower.class)
			.stream()
			.filter(p -> p.shouldReplace(apoli$lootTableKey) & p.doesApply(context, finalPowerHolder))
			.sorted(Comparator.comparing(ReplaceLootTablePower::getPriority))
			.toList();

		if (replaceLootTablePowers.isEmpty()) {
			original.call(context, lootConsumer);
			return;
		}

		ResourceKey<LootTable> replacement = null;
		ReplaceLootTablePower finalPower = null;

		for (ReplaceLootTablePower power : replaceLootTablePowers) {

			@NotNull ResourceKey<LootTable> replacementLootTableKey;
			try {
				replacementLootTableKey = power.getReplacement(apoli$lootTableKey);
			} catch (Exception e) {
				continue;
			}

			replacement = replacementLootTableKey;
			finalPower = power;

			if (replacement instanceof IdentifiedLootTable identifiedLootTable) {
				identifiedLootTable.apoli$setKey(replacementLootTableKey, apoli$registryLookup);
			}
		}

		if (replacement != null) {
			((ReplacingLootContext) context).apoli$setReplaced((LootTable) (Object) this);

			ResourceKey<LootTable> finalReplacement = replacement;
			final Optional<net.minecraft.world.level.storage.loot.LootTable> tableOp = Optional.of(apoli$registryLookup.getLootTable(replacement));
			CraftLootTable table = tableOp.map(lootTable -> new CraftLootTable(CraftNamespacedKey.fromMinecraft(finalReplacement.location()), lootTable)).orElseThrow();
			Collection<org.bukkit.inventory.ItemStack> stacks = table.populateLoot(null, originspaper$convertContext(context));
			for (org.bukkit.inventory.ItemStack stack : stacks) {
				lootConsumer.accept(CraftItemStack.unwrap(stack));
			}
		}
	}

	@Override
	public BiFunction<ItemStack, LootContext, ItemStack> apoli$getCompositeFunction() {
		return compositeFunction;
	}

	@Override
	public List<LootPool> apoli$getPools() {
		return pools;
	}
}
