package me.dueris.originspaper.factory.powers;

import io.github.dueris.calio.SerializableDataTypes;
import io.github.dueris.calio.parser.InstanceDefiner;
import me.dueris.originspaper.OriginsPaper;
import me.dueris.originspaper.factory.condition.ConditionFactory;
import me.dueris.originspaper.registry.registries.PowerType;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DamageOverTimePower extends PowerType {
	public static final ResourceKey<DamageType> GENERIC_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, OriginsPaper.apoliIdentifier("damage_over_time"));
	private final int interval;
	private final float damageAmount;
	private final ResourceKey<DamageType> damageType;
	private final ResourceKey<Enchantment> protectionEnchantment;
	private final float effectiveness;
	private final int onsetDelay;
	private final float damageAmountEasy;
	private final ConcurrentHashMap<Player, Integer> inTicks = new ConcurrentHashMap<>();
	private final ConcurrentHashMap<Player, Integer> outTicks = new ConcurrentHashMap<>();
	private DamageSource damageSource;

	public DamageOverTimePower(@NotNull ResourceLocation key, @NotNull ResourceLocation type, Component name, Component description, boolean hidden, ConditionFactory<Entity> condition, int loadingPriority,
							   int interval, @NotNull Optional<Integer> onsetDelay, float damageAmount, @NotNull Optional<Float> damageAmountEasy, ResourceKey<DamageType> damageType, ResourceKey<Enchantment> protectionEnchantment, float effectiveness) {
		super(key, type, name, description, hidden, condition, loadingPriority);
		this.interval = interval;
		this.damageAmount = damageAmount;
		this.damageType = damageType;
		this.protectionEnchantment = protectionEnchantment;
		this.effectiveness = effectiveness;
		this.damageAmountEasy = damageAmountEasy.orElse(damageAmount);
		this.onsetDelay = onsetDelay.orElse(interval);
	}

	public static InstanceDefiner buildDefiner() {
		return PowerType.buildDefiner().typedRegistry(OriginsPaper.apoliIdentifier("damage_over_time"))
			.add("interval", SerializableDataTypes.POSITIVE_INT, 20)
			.add("onset_delay", SerializableDataTypes.optional(SerializableDataTypes.INT), Optional.empty())
			.add("damage", SerializableDataTypes.FLOAT)
			.add("damage_easy", SerializableDataTypes.optional(SerializableDataTypes.FLOAT), Optional.empty())
			.add("damage_type", SerializableDataTypes.DAMAGE_TYPE, GENERIC_DAMAGE)
			.add("protection_enchantment", SerializableDataTypes.ENCHANTMENT, null)
			.add("protection_effectiveness", SerializableDataTypes.FLOAT, 1.0F);
	}

	public int getDamageBegin(LivingEntity entity) {
		int prot = getProtection(entity);
		int delay = (int) (Math.pow(prot * 2, 1.3) * effectiveness);
		return onsetDelay + delay * 20;
	}

	private DamageSource getDamageSource() {
		if (damageSource == null) {
			damageSource = new DamageSource(OriginsPaper.server.registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(damageType));
		}
		return damageSource;
	}

	@Override
	public void tick(Player player) {
		outTicks.putIfAbsent(player, 0);
		inTicks.putIfAbsent(player, 0);
		int outOfDamageTicks = outTicks.get(player);
		int inDamageTicks = inTicks.get(player);
		if (isActive(player)) {
			outOfDamageTicks = 0;
			if (inDamageTicks - getDamageBegin(player) >= 0) {
				if ((inDamageTicks - getDamageBegin(player)) % interval == 0) {
					DamageSource source = getDamageSource();
					player.hurt(source, player.level().getDifficulty() == Difficulty.EASY ? damageAmountEasy : damageAmount);
				}
			}
			inDamageTicks++;
		} else {
			if (outOfDamageTicks >= 20) {
				inDamageTicks = 0;
			} else {
				outOfDamageTicks++;
			}
		}

		outTicks.put(player, outOfDamageTicks);
		inTicks.put(player, inDamageTicks);
	}

	@EventHandler
	public void respawn(@NotNull PlayerRespawnEvent e) {
		Player player = ((CraftPlayer) e.getPlayer()).getHandle();
		if (getPlayers().contains(player)) {
			outTicks.put(player, 0);
			inTicks.put(player, 0);
		}
	}

	private int getProtection(LivingEntity entity) {
		if (protectionEnchantment == null) {
			return 0;
		}

		Registry<Enchantment> enchantmentRegistry = entity.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

		Enchantment protectingEnchantment = enchantmentRegistry.getOrThrow(protectionEnchantment);
		Holder<Enchantment> protectingEnchantmentEntry = enchantmentRegistry.wrapAsHolder(protectingEnchantment);

		Map<EquipmentSlot, ItemStack> potentialItems = protectingEnchantment.getSlotItems(entity);

		int accumLevel = 0;
		int items = 0;

		for (ItemStack potentialItem : potentialItems.values()) {

			int level = EnchantmentHelper.getItemEnchantmentLevel(protectingEnchantmentEntry, potentialItem);
			accumLevel += level;

			if (level > 0) {
				items++;
			}

		}

		return accumLevel + items;

	}
}
