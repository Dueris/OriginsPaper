package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.condition.EntityCondition;
import io.github.dueris.originspaper.data.TypedDataObjectFactory;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerConfiguration;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

public class DamageOverTimePowerType extends PowerType {

	public static final ResourceKey<DamageType> GENERIC_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, OriginsPaper.apoliIdentifier("damage_over_time"));

	public static final TypedDataObjectFactory<DamageOverTimePowerType> DATA_FACTORY = PowerType.createConditionedDataFactory(
		new SerializableData()
			.add("damage_type", SerializableDataTypes.DAMAGE_TYPE, GENERIC_DAMAGE)
			.add("protection_enchantment", SerializableDataTypes.ENCHANTMENT.optional(), Optional.empty())
			.add("protection_effectiveness", SerializableDataTypes.FLOAT, 1.0F)
			.add("damage", SerializableDataTypes.FLOAT)
			.addFunctionedDefault("damage_easy", SerializableDataTypes.FLOAT, data -> data.get("damage"))
			.add("interval", SerializableDataTypes.POSITIVE_INT, 20)
			.addFunctionedDefault("onset_delay", SerializableDataTypes.POSITIVE_INT, data -> data.get("interval")),
		(data, condition) -> new DamageOverTimePowerType(
			data.get("damage_type"),
			data.get("protection_enchantment"),
			data.get("protection_effectiveness"),
			data.get("damage"),
			data.get("damage_easy"),
			data.get("interval"),
			data.get("onset_delay"),
			condition
		),
		(powerType, serializableData) -> serializableData.instance()
			.set("damage_type", powerType.damageType)
			.set("protection_enchantment", powerType.protectionEnchantmentKey)
			.set("protection_effectiveness", powerType.protectionEffectiveness)
			.set("damage", powerType.damageAmount)
			.set("damage_easy", powerType.damageAmountEasy)
			.set("interval", powerType.damageInterval)
			.set("onset_delay", powerType.damageOnsetDelay)
	);

	private final ResourceKey<DamageType> damageType;
	private final Optional<ResourceKey<Enchantment>> protectionEnchantmentKey;

	private final float protectionEffectiveness;

	private final float damageAmount;
	private final float damageAmountEasy;

	private final int damageInterval;
	private final int damageOnsetDelay;

	private int outOfDamageTicks = 0;
	private int inDamageTicks = 0;

	public DamageOverTimePowerType(ResourceKey<DamageType> damageType, Optional<ResourceKey<Enchantment>> protectionEnchantmentKey, float protectionEffectiveness, float damageAmountEasy, float damageAmount, int damageInterval, int damageOnsetDelay, Optional<EntityCondition> condition) {
		super(condition);
		this.damageType = damageType;
		this.protectionEnchantmentKey = protectionEnchantmentKey;
		this.protectionEffectiveness = protectionEffectiveness;
		this.damageAmountEasy = damageAmountEasy;
		this.damageAmount = damageAmount;
		this.damageInterval = damageInterval;
		this.damageOnsetDelay = damageOnsetDelay;
		this.setTicking(true);
	}

	@Override
	public @NotNull PowerConfiguration<?> getConfig() {
		return PowerTypes.DAMAGE_OVER_TIME;
	}

	public int getDamageBegin() {

		int protection = getProtection();
		int delay = (int) (Math.pow(protection * 2, 1.3) * protectionEffectiveness);

		return damageOnsetDelay + delay * 20;

	}

	@Override
	public void serverTick() {

		if (this.isActive()) {
			doDamage();
		}

		else {
			resetDamage();
		}

	}

	public void doDamage() {

		LivingEntity holder = getHolder();
		this.outOfDamageTicks = 0;

		if (inDamageTicks++ - getDamageBegin() >= 0 && (inDamageTicks - getDamageBegin()) % damageInterval == 0) {

			DamageSource damageSource = holder.damageSources().source(damageType);
			float amount = holder.level().getDifficulty() == Difficulty.EASY
				? damageAmountEasy
				: damageAmount;

			holder.hurt(damageSource, amount);

		}

	}

	public void resetDamage() {

		if (outOfDamageTicks >= 20) {
			this.inDamageTicks = 0;
		}

		else {
			this.outOfDamageTicks++;
		}

	}

	@Override
	public void onRespawn() {
		this.inDamageTicks = 0;
		this.outOfDamageTicks = 0;
	}

	protected int getProtection() {

		if (protectionEnchantmentKey.isEmpty()) {
			return 0;
		}

		LivingEntity holder = getHolder();
		Registry<Enchantment> enchantmentRegistry = holder.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

		Enchantment protectingEnchantment = enchantmentRegistry.getOrThrow(protectionEnchantmentKey.get());
		Holder<Enchantment> protectingEnchantmentEntry = enchantmentRegistry.wrapAsHolder(protectingEnchantment);

		Map<EquipmentSlot, ItemStack> potentialItems = protectingEnchantment.getSlotItems(holder);

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

	@Override
	public Tag toTag() {

		CompoundTag nbt = new CompoundTag();

		nbt.putInt("InDamage", inDamageTicks);
		nbt.putInt("OutDamage", outOfDamageTicks);

		return nbt;

	}

	@Override
	public void fromTag(Tag tag) {

		if (tag instanceof CompoundTag nbt) {
			inDamageTicks = nbt.getInt("InDamage");
			outOfDamageTicks = nbt.getInt("OutDamage");
		}

	}

}
