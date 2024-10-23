package io.github.dueris.originspaper.power.type;

import io.github.dueris.calio.util.IdentifierAlias;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.power.PowerTypeFactory;
import io.github.dueris.originspaper.registry.ApoliRegistries;
import net.minecraft.core.Registry;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PowerTypes {

	public static final IdentifierAlias ALIASES = new IdentifierAlias();

	public static final PowerTypeFactory<PowerType> SIMPLE = register(PowerType.createSimpleFactory(OriginsPaper.apoliIdentifier("simple"), PowerType::new));
	public static final PowerTypeFactory<PowerType> MULTIPLE = register(PowerType.createSimpleFactory(OriginsPaper.apoliIdentifier("multiple"), PowerType::new));

	public static void register() {
		register(ActionOverTimePowerType::getFactory);
		register(ActionOnBeingUsedPowerType::getFactory);
		register(ActionOnBlockBreakPowerType::getFactory);
		register(ActionOnBlockPlacePowerType::getFactory);
		register(ActionOnBlockUsePowerType::getFactory);
		register(ActionOnCallbackPowerType::getFactory);
		register(ActionOnDeathPowerType::getFactory);
		register(TogglePowerType::getFactory);
		register(ResourcePowerType::getFactory);
		register(CooldownPowerType::getFactory);
		register(ActiveCooldownPowerType::getLaunchFactory);
		register(ActiveCooldownPowerType::getActiveSelfFactory);
		register(ActionOnEntityUsePowerType::getFactory);
		register(ActionOnHitPowerType::getFactory);
		register(ActionOnItemPickupPowerType::getFactory);
		register(ActionOnItemUsePowerType::getFactory);
		register(ActionOnLandPowerType::getFactory);
		register(ActionOnWakeUpPowerType::getFactory);
		register(ActionWhenHitPowerType::getFactory);
		register(AttackerActionWhenHitPowerType::getFactory);
		register(AttributeModifyTransferPowerType::getFactory);
		register(AttributePowerType::getFactory);
		register(BurnPowerType::getFactory);
		register(ClimbingPowerType::getFactory);
		register(ConditionedAttributePowerType::getFactory);
		register(ConditionedRestrictArmorPowerType::getFactory);
		register(RestrictArmorPowerType::getFactory);
		register(DamageOverTimePowerType::getFactory);
		register(PowerType.createSimpleFactory(OriginsPaper.apoliIdentifier("disable_regen"), DisableRegenPowerType::new));
		register(EdibleItemPowerType::getFactory);
		register(EffectImmunityPowerType::getFactory);
		register(ElytraFlightPowerType::getFactory);
		register(CreativeFlightPowerType::getFactory);
		register(EntityGlowPowerType::getFactory);
		register(EntitySetPowerType::getFactory);
		register(ExhaustOverTimePowerType::getFactory);
		register(PowerType.createSimpleFactory(OriginsPaper.apoliIdentifier("fire_immunity"), FireImmunityPowerType::new));
		register(FireProjectilePowerType::getFactory);
		register(PowerType.createSimpleFactory(OriginsPaper.apoliIdentifier("freeze"), FreezePowerType::new));
		register(GameEventListenerPowerType::getFactory);
		register(PowerType.createSimpleFactory(OriginsPaper.apoliIdentifier("grounded"), GroundedPowerType::new));
		register(InventoryPowerType::getFactory);
		register(InvisibilityPowerType::getFactory);
		register(InvulnerablePowerType::getFactory);
		register(ItemOnItemPowerType::getFactory);
		register(KeepInventoryPowerType::getFactory);
		register(ModelColorPowerType::getFactory);
		register(ValueModifyingPowerType.createValueModifyingFactory(OriginsPaper.apoliIdentifier("modify_air_speed"), ModifyAirSpeedPowerType::new));
		register(ModifyAttributePowerType::getFactory);
		register(ModifyBlockRenderPowerType::getFactory);
		register(ModifyBreakSpeedPowerType::getFactory);
		register(ModifyCraftingPowerType::getFactory);
		register(ModifyDamageDealtPowerType::getFactory);
		register(ModifyDamageTakenPowerType::getFactory);
		register(ModifyProjectileDamagePowerType::getFactory);
		register(ModifyEnchantmentLevelPowerType::getFactory);
		register(ValueModifyingPowerType.createValueModifyingFactory(OriginsPaper.apoliIdentifier("modify_exhaustion"), ModifyExhaustionPowerType::new));
		register(ValueModifyingPowerType.createValueModifyingFactory(OriginsPaper.apoliIdentifier("modify_xp_gain"), ModifyExperiencePowerType::new));
		register(ModifyFallingPowerType::getFactory);
		register(ModifyFoodPowerType::getFactory);
		register(ModifyHarvestPowerType::getFactory);
		register(ValueModifyingPowerType.createValueModifyingFactory(OriginsPaper.apoliIdentifier("modify_healing"), ModifyHealingPowerType::new));
		register(ValueModifyingPowerType.createValueModifyingFactory(OriginsPaper.apoliIdentifier("modify_insomnia_ticks"), ModifyInsomniaTicksPowerType::new));
		register(ModifyJumpPowerType::getFactory);
		register(ModifyPlayerSpawnPowerType::getFactory);
		register(ModifySlipperinessPowerType::getFactory);
		register(ModifyLavaSpeedPowerType::getFactory);
		register(ModifyStatusEffectAmplifierPowerType::getFactory);
		register(ModifyStatusEffectDurationPowerType::getFactory);
		register(ModifySwimSpeedPowerType::getFactory);
		register(ModifyTypeTagPowerType::getFactory);
		register(NightVisionPowerType::getFactory);
		register(ParticlePowerType::getFactory);
		register(PhasingPowerType::getFactory);
		register(PreventBeingUsedPowerType::getFactory);
		register(PreventBlockPlacePowerType::getFactory);
		register(PreventBlockUsePowerType::getFactory);
		register(PreventDeathPowerType::getFactory);
		register(PreventElytraFlightPowerType::getFactory);
		register(PreventEntityCollisionPowerType::getFactory);
		register(PreventEntityRenderPowerType::getFactory);
		register(PreventEntityUsePowerType::getFactory);
		register(PreventGameEventPowerType::getFactory);
		register(PreventItemPickupPowerType::getFactory);
		register(PreventItemUsePowerType::getFactory);
		register(PreventSleepPowerType::getFactory);
		register(PowerType.createSimpleFactory(OriginsPaper.apoliIdentifier("prevent_sprinting"), PreventSprintingPowerType::new));
		register(RecipePowerType::getFactory);
		register(ReplaceLootTablePowerType::getFactory);
		register(SelfActionOnHitPowerType::getFactory);
		register(SelfActionOnKillPowerType::getFactory);
		register(SelfActionWhenHitPowerType::getFactory);
		register(SelfGlowPowerType::getFactory);
		register(StackingStatusEffectPowerType::getFactory);
		register(StartingEquipmentPowerType::getFactory);
		register(TargetActionOnHitPowerType::getFactory);
		register(ToggleNightVisionPowerType::getFactory);
	}

	@SuppressWarnings("unchecked")
	public static <T extends PowerType> @NotNull PowerTypeFactory<T> register(PowerTypeFactory<?> powerTypeFactory) {
		return (PowerTypeFactory<T>) Registry.register(ApoliRegistries.POWER_FACTORY, powerTypeFactory.getSerializerId(), powerTypeFactory);
	}

	public static <T extends PowerType> @NotNull PowerTypeFactory<T> register(@NotNull Supplier<PowerTypeFactory<?>> powerTypeFactory) {
		return register(powerTypeFactory.get());
	}

}

