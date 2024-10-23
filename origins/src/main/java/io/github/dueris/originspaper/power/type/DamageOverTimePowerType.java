package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.calio.data.SerializableDataTypes;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.Power;
import io.github.dueris.originspaper.power.PowerTypeFactory;
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

import java.util.Map;

public class DamageOverTimePowerType extends PowerType {

	public static final ResourceKey<DamageType> GENERIC_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, OriginsPaper.apoliIdentifier("damage_over_time"));

	private final int damageTickInterval;
	private final int beginDamageIn;
	private final float damageAmountEasy;
	private final float damageAmount;
	private final ResourceKey<DamageType> damageType;
	private final ResourceKey<Enchantment> protectingEnchantmentKey;
	private final float protectionEffectiveness;

	private int outOfDamageTicks;
	private int inDamageTicks;

	private DamageSource damageSource;

	public DamageOverTimePowerType(Power power, LivingEntity entity, int beginDamageIn, int damageInterval, float damageAmountEasy, float damageAmount, ResourceKey<DamageType> damageType, ResourceKey<Enchantment> protectingEnchantmentKey, float protectionEffectiveness) {
		super(power, entity);
		this.damageType = damageType;
		this.beginDamageIn = beginDamageIn;
		this.damageAmount = damageAmount;
		this.damageAmountEasy = damageAmountEasy;
		this.damageTickInterval = damageInterval;
		this.protectingEnchantmentKey = protectingEnchantmentKey;
		this.protectionEffectiveness = protectionEffectiveness;
		this.setTicking(true);
	}

	public static PowerTypeFactory<?> getFactory() {
		return new PowerTypeFactory<>(
			OriginsPaper.apoliIdentifier("damage_over_time"),
			new SerializableData()
				.add("interval", SerializableDataTypes.POSITIVE_INT, 20)
				.addFunctionedDefault("onset_delay", SerializableDataTypes.INT, data -> data.getInt("interval"))
				.add("damage", SerializableDataTypes.FLOAT)
				.addFunctionedDefault("damage_easy", SerializableDataTypes.FLOAT, data -> data.getFloat("damage"))
				.add("damage_type", SerializableDataTypes.DAMAGE_TYPE, GENERIC_DAMAGE)
				.add("protection_enchantment", SerializableDataTypes.ENCHANTMENT, null)
				.add("protection_effectiveness", SerializableDataTypes.FLOAT, 1.0F),
			data -> (power, entity) -> new DamageOverTimePowerType(power, entity,
				data.getInt("onset_delay"),
				data.getInt("interval"),
				data.getFloat("damage_easy"),
				data.getFloat("damage"),
				data.get("damage_type"),
				data.get("protection_enchantment"),
				data.getFloat("protection_effectiveness")
			)
		).allowCondition();
	}

	public int getDamageBegin() {
		int prot = getProtection();
		int delay = (int) (Math.pow(prot * 2, 1.3) * protectionEffectiveness);
		return beginDamageIn + delay * 20;
	}

	@Override
	public void tick() {
		if (this.isActive()) {
			doDamage();
		} else {
			resetDamage();
		}
	}

	public void doDamage() {

		outOfDamageTicks = 0;

		if (inDamageTicks - getDamageBegin() >= 0) {
			if ((inDamageTicks - getDamageBegin()) % damageTickInterval == 0) {
				DamageSource source = getDamageSource(entity.damageSources());
				entity.hurt(source, entity.level().getDifficulty() == Difficulty.EASY ? damageAmountEasy : damageAmount);
			}
		}

		inDamageTicks++;

	}

	private DamageSource getDamageSource(DamageSources damageSources) {

		if (damageSource == null) {
			damageSource = damageSources.source(damageType);
		}

		return damageSource;

	}

	public void resetDamage() {

		if (outOfDamageTicks >= 20) {
			inDamageTicks = 0;
		} else {
			outOfDamageTicks++;
		}

	}

	@Override
	public void onRespawn() {
		inDamageTicks = 0;
		outOfDamageTicks = 0;
	}

	protected int getProtection() {

		if (protectingEnchantmentKey == null) {
			return 0;
		}

		Registry<Enchantment> enchantmentRegistry = entity.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

		Enchantment protectingEnchantment = enchantmentRegistry.getOrThrow(protectingEnchantmentKey);
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

