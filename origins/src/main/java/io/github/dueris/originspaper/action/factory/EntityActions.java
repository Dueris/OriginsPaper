package io.github.dueris.originspaper.action.factory;

import io.github.dueris.calio.data.SerializableData;
import io.github.dueris.originspaper.OriginsPaper;
import io.github.dueris.originspaper.action.type.entity.*;
import io.github.dueris.originspaper.data.ApoliDataTypes;
import io.github.dueris.originspaper.registry.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.function.Consumer;
import java.util.function.Function;

public class EntityActions {

	public static void register() {

		MetaActions.register(ApoliDataTypes.ENTITY_ACTION, ApoliDataTypes.ENTITY_CONDITION, Function.identity(), EntityActions::register);

		register(DamageActionType.getFactory());
		register(HealActionType.getFactory());
		register(PlaySoundActionType.getFactory());
		register(ExhaustActionType.getFactory());
		register(ApplyEffectActionType.getFactory());
		register(ClearEffectActionType.getFactory());
		register(SetOnFireActionType.getFactory());
		register(AddVelocityActionType.getFactory());
		register(SpawnEntityActionType.getFactory());
		register(GainAirActionType.getFactory());
		register(BlockActionAtActionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("extinguish"), Entity::clearFire));
		register(ExecuteCommandActionType.getFactory());
		register(ChangeResourceActionType.getFactory());
		register(FeedActionType.getFactory());
		register(AddXpActionType.getFactory());
		register(SetFallDistanceActionType.getFactory());
		register(GiveActionType.getFactory());
		register(EquippedItemActionType.getFactory());
		register(TriggerCooldownActionType.getFactory());
		register(ToggleActionType.getFactory());
		register(EmitGameEventActionType.getFactory());
		register(SetResourceActionType.getFactory());
		register(GrantPowerActionType.getFactory());
		register(RevokePowerActionType.getFactory());
		register(RevokeAllPowersActionType.getFactory());
		register(RemovePowerActionType.getFactory());
		register(ExplodeActionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("dismount"), Entity::stopRiding));
		register(PassengerActionType.getFactory());
		register(RidingActionType.getFactory());
		register(AreaOfEffectActionType.getFactory());
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("crafting_table"), CraftingTableActionType::action));
		register(createSimpleFactory(OriginsPaper.apoliIdentifier("ender_chest"), EnderChestActionType::action));
		register(SwingHandActionType.getFactory());
		register(RaycastActionType.getFactory());
		register(SpawnParticlesActionType.getFactory());
		register(ModifyInventoryActionType.getFactory());
		register(ReplaceInventoryActionType.getFactory());
		register(DropInventoryActionType.getFactory());
		register(ModifyDeathTicksActionType.getFactory());
		register(ModifyResourceActionType.getFactory());
		register(ModifyStatActionType.getFactory());
		register(FireProjectileActionType.getFactory());
		register(SelectorActionType.getFactory());
		register(GrantAdvancementActionType.getFactory());
		register(RevokeAdvancementActionType.getFactory());
		register(ActionOnEntitySetActionType.getFactory());
		register(RandomTeleportActionType.getFactory());
		register(SpawnEffectCloudActionType.getFactory());

	}

	public static ActionTypeFactory<Entity> createSimpleFactory(ResourceLocation id, Consumer<Entity> action) {
		return new ActionTypeFactory<>(id, new SerializableData(), (data, entity) -> action.accept(entity));
	}

	public static ActionTypeFactory<Entity> register(ActionTypeFactory<Entity> actionFactory) {
		return OriginsPaper.getRegistry().retrieve(Registries.ENTITY_ACTION).register(actionFactory, actionFactory.getSerializerId());
	}

}
