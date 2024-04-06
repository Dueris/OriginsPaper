package me.dueris.genesismc.factory.actions.types;

import it.unimi.dsi.fastutil.Pair;
import me.dueris.calio.registry.Registerable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.event.AddToSetEvent;
import me.dueris.genesismc.event.RemoveFromSetEvent;
import me.dueris.genesismc.factory.data.types.VectorGetter;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.Utils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Tameable;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;

import java.util.function.BiConsumer;

public class BiEntityActions {

    public void register() {
        register(new ActionFactory(GenesisMC.apoliIdentifier("add_velocity"), (action, entityPair) -> {
            boolean set = action.containsKey("set") && (boolean) action.get("set");
            Vector vector = VectorGetter.getVector(action);

            if (set) entityPair.right().setVelocity(vector);
            else entityPair.right().setVelocity(entityPair.right().getVelocity().add(vector));
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("remove_from_set"), (action, entityPair) -> {
            RemoveFromSetEvent ev = new RemoveFromSetEvent(entityPair.right(), action.get("set").toString());
            ev.callEvent();
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("add_to_set"), (action, entityPair) -> {
            AddToSetEvent ev = new AddToSetEvent(entityPair.right(), action.get("set").toString());
            ev.callEvent();
        }));
        register(new ActionFactory(GenesisMC.apoliIdentifier("damage"), (action, entityPair) -> {
            if (entityPair.right().isDead() || !(entityPair.right() instanceof LivingEntity)) return;
            float amount = 0.0f;

            if (action.containsKey("amount"))
                amount = Float.parseFloat(action.get("amount").toString());

            String namespace;
            String key;
            if (action.get("damage_type") != null) {
                if (action.get("damage_type").toString().contains(":")) {
                    namespace = action.get("damage_type").toString().split(":")[0];
                    key = action.get("damage_type").toString().split(":")[1];
                } else {
                    namespace = "minecraft";
                    key = action.get("damage_type").toString();
                }
            } else {
                namespace = "minecraft";
                key = "generic";
            }
            DamageType dmgType = Utils.DAMAGE_REGISTRY.get(new ResourceLocation(namespace, key));
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

    public static class ActionFactory implements Registerable {
        NamespacedKey key;
        BiConsumer<JSONObject, Pair<CraftEntity, CraftEntity>> test;

        public ActionFactory(NamespacedKey key, BiConsumer<JSONObject, Pair<CraftEntity, CraftEntity>> test) {
            this.key = key;
            this.test = test;
        }

        public void test(JSONObject action, Pair<CraftEntity, CraftEntity> tester) {
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
