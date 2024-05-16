package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonArray;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.calio.data.types.OptionalInstance;
import me.dueris.calio.data.types.RequiredInstance;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import me.dueris.genesismc.util.Utils;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.potion.CraftPotionEffectType;
import org.bukkit.craftbukkit.potion.CraftPotionUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class StackingStatusEffect extends PowerType {
	protected final List<MobEffectInstance> createdEffects = new ArrayList<>();
	private final int minStacks;
	private final int maxStacks;
	private final int durationPerStack;
	private final int tickRate;
	private final List<FactoryJsonObject> effects;
	protected int currentStack = 0;

	public StackingStatusEffect(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, int minStacks, int maxStacks, int durationPerStack, int tickRate, @Nullable FactoryJsonObject effect, @Nullable FactoryJsonArray effects) {
		super(name, description, hidden, condition, loading_priority);
		this.minStacks = minStacks;
		this.maxStacks = maxStacks;
		this.durationPerStack = durationPerStack;
		this.tickRate = tickRate;
		this.effects = effect != null ? List.of(effect) : effects.asJsonObjectList();
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("stacking_status_effect"))
			.add("min_stacks", int.class, new RequiredInstance())
			.add("max_stacks", int.class, new RequiredInstance())
			.add("duration_per_stack", int.class, new RequiredInstance())
			.add("tick_rate", int.class, 10)
			.add("effect", FactoryJsonObject.class, new OptionalInstance())
			.add("effects", FactoryJsonArray.class, new OptionalInstance());
	}

	@Override
	public void tick(Player p) {
		if (p.getTicksLived() % tickRate == 0) {
			if (isActive(p)) {
				currentStack += 1;
				if (currentStack > maxStacks) {
					currentStack = maxStacks;
				}
				if (currentStack > 0) {
					apoli$StackingStatusEffectPower$applyEffects(((CraftPlayer) p).getHandle());
				}
			} else {
				currentStack -= 1;
				if (currentStack < minStacks) {
					currentStack = minStacks;
				}
			}
		}
	}

	public void addEffect(MobEffect effect) {
		addEffect(effect, 80);
	}

	public void addEffect(MobEffect effect, int lingerDuration) {
		addEffect(effect, lingerDuration, 0);
	}

	public void addEffect(MobEffect effect, int lingerDuration, int amplifier) {
		addEffect(new MobEffectInstance(CraftPotionEffectType.bukkitToMinecraftHolder(CraftPotionEffectType.minecraftToBukkit(effect)), lingerDuration, amplifier));
	}

	public void addEffect(MobEffectInstance instance) {
		createdEffects.add(instance);
	}

	public void applyEffects(LivingEntity entity) {
		createdEffects.stream().map(MobEffectInstance::new).forEach(entity::addEffect);
	}

	public void apoli$StackingStatusEffectPower$applyEffects(LivingEntity entity) {
		if (createdEffects.isEmpty()) {
			createdEffects.addAll(effects.stream().map(Utils::parsePotionEffect).map(CraftPotionUtil::fromBukkit).toList());
		}
		List<MobEffectInstance> effectInstances = createdEffects;
		effectInstances.forEach(sei -> {
			int duration = durationPerStack * currentStack;
			if (duration > 0) {
				MobEffectInstance applySei = new MobEffectInstance(sei.getEffect(), duration, sei.getAmplifier(), sei.isAmbient(), sei.isVisible(), sei.showIcon());
				// GenesisMC - Paper/Spigot makers lots of changes to potion effects, making it work differently. This fixes it
				PotionEffectType bukkitType = CraftPotionEffectType.minecraftHolderToBukkit(sei.getEffect());
				if (entity.getBukkitLivingEntity().hasPotionEffect(bukkitType)) {
					entity.getBukkitLivingEntity().removePotionEffect(bukkitType);
				}
				entity.getBukkitLivingEntity().addPotionEffect(CraftPotionUtil.toBukkit(applySei), true);
			}
		});
	}
}
