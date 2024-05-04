package me.dueris.genesismc.factory.actions.types;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.AddToSetEvent;
import me.dueris.genesismc.event.RemoveFromSetEvent;
import me.dueris.genesismc.factory.data.types.VectorGetter;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.Utils;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.util.Vector;

import java.util.function.BiConsumer;

public class BiEntityActions {

    public void register() {
	register(new ActionFactory(GenesisMC.apoliIdentifier("add_velocity"), (action, entityPair) -> {
	    boolean set = action.isPresent("set") && action.getBoolean("set");
	    Vector vector = VectorGetter.getVector(action);

	    if (set) entityPair.right().setVelocity(vector);
	    else entityPair.right().setVelocity(entityPair.right().getVelocity().add(vector));
	}));
	register(new ActionFactory(GenesisMC.apoliIdentifier("remove_from_set"), (action, entityPair) -> {
	    RemoveFromSetEvent ev = new RemoveFromSetEvent(entityPair.right(), action.getString("set"));
	    ev.callEvent();
	}));
	register(new ActionFactory(GenesisMC.apoliIdentifier("add_to_set"), (action, entityPair) -> {
	    AddToSetEvent ev = new AddToSetEvent(entityPair.right(), action.getString("set"));
	    ev.callEvent();
	}));
	register(new ActionFactory(GenesisMC.apoliIdentifier("damage"), (action, entityPair) -> {
	    if (entityPair.right().isDead() || !(entityPair.right() instanceof LivingEntity)) return;
	    float amount = 0.0f;

	    if (action.isPresent("amount"))
		amount = action.getNumber("amount").getFloat();

	    NamespacedKey key = NamespacedKey.fromString(action.getStringOrDefault("damage_type", "generic"));
	    DamageType dmgType = Utils.DAMAGE_REGISTRY.get(CraftNamespacedKey.toMinecraft(key));
	    net.minecraft.world.entity.LivingEntity serverEn = ((CraftLivingEntity) entityPair.right()).getHandle();
	    serverEn.hurt(Utils.getDamageSource(dmgType), amount);
	}));
	register(new ActionFactory(GenesisMC.apoliIdentifier("set_in_love"), (action, entityPair) -> {
	    if (entityPair.right() instanceof Animals targetAnimal) {
		targetAnimal.setLoveModeTicks(600);
	    }
	}));
	register(new ActionFactory(GenesisMC.apoliIdentifier("mount"), (action, entityPair) -> entityPair.right().addPassenger(entityPair.left())));
	register(new ActionFactory(GenesisMC.apoliIdentifier("tame"), (action, entityPair) -> {
	    if (entityPair.right() instanceof Tameable targetTameable && entityPair.left() instanceof AnimalTamer actorTamer) {
		targetTameable.setOwner(actorTamer);
	    }
	}));
    }

    private void register(BiEntityActions.ActionFactory factory) {
	GenesisMC.getPlugin().registry.retrieve(Registries.BIENTITY_ACTION).register(factory);
    }

    public static class ActionFactory implements Registrable {
	NamespacedKey key;
	BiConsumer<FactoryJsonObject, Pair<CraftEntity, CraftEntity>> test;

	public ActionFactory(NamespacedKey key, BiConsumer<FactoryJsonObject, Pair<CraftEntity, CraftEntity>> test) {
	    this.key = key;
	    this.test = test;
	}

	public void test(FactoryJsonObject action, Pair<CraftEntity, CraftEntity> tester) {
	    if (action == null || action.isEmpty()) return; // Dont execute empty actions
	    try {
		test.accept(action, tester);
	    } catch (Exception e) {
		GenesisMC.getPlugin().getLogger().severe("An Error occurred while running an action: " + e.getMessage());
		e.printStackTrace();
	    }
	}

	@Override
	public NamespacedKey getKey() {
	    return key;
	}
    }
}
