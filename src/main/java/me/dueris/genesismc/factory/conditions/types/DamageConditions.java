package me.dueris.genesismc.factory.conditions.types;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.TagRegistryParser;
import me.dueris.genesismc.factory.conditions.Condition;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.registry.Registerable;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.util.Utils;
import org.bukkit.Fluid;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_20_R3.damage.CraftDamageSource;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftLivingEntity;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiPredicate;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageConditions {
    public static HashMap<String, ArrayList<DamageType>> damageTagMappings = new HashMap<>();

    public void prep(){
        register(new ConditionFactory(GenesisMC.apoliIdentifier("projectile"), (condition, event) -> {
            if (event.getCause().equals(DamageCause.PROJECTILE)) {
                boolean projectile = true;
                boolean projectileCondition = true;
                if (condition.containsKey("projectile_condition")) {
                    projectileCondition = ConditionExecutor.testEntity((JSONObject) condition.get("projectile_condition"), ((CraftEntity)((EntityDamageByEntityEvent) event).getDamager()));
                }
                if (condition.containsKey("projectile") && event instanceof EntityDamageByEntityEvent eventT){
                    String identifier = condition.get("projectile").toString();
                    if(identifier.contains(":")){
                        identifier = identifier.split(":")[1];
                    }
                    projectile = eventT.getDamager().getType().equals(EntityType.valueOf(identifier.toUpperCase()));
                }
                return projectileCondition && projectile;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("name"), (condition, event) -> {
            String input = condition.get("name").toString();
            StringBuilder output = new StringBuilder();

            for (char c : input.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    output.append('_').append(Character.toLowerCase(c));
                } else {
                    output.append(c);
                }
            }
            return event.getDamageSource().getDamageType().getKey() == NamespacedKey.minecraft(output.toString());
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, event) -> {
            String tag = condition.get("tag").toString();
            try {
                if (TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString()) != null) {
                    if (!damageTagMappings.containsKey(condition.get("tag"))) {
                        damageTagMappings.put(condition.get("tag").toString(), new ArrayList<>());
                        for (String mat : TagRegistryParser.getRegisteredTagFromFileKey(condition.get("tag").toString())) {
                            damageTagMappings.get(condition.get("tag")).add(Registry.DAMAGE_TYPE.get(NamespacedKey.minecraft(mat.split(":")[1].toLowerCase())));
                        }
                    } else {
                        // mappings exist, now we can start stuff
                        return damageTagMappings.get(condition.get("tag")).contains(event.getDamageSource().getDamageType().key().asString());
                    }
                }
            } catch (IllegalArgumentException e) {
                // yeah imma just ignore this one ty
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fire"), (condition, event) -> {
            return event.getCause().equals(DamageCause.FIRE);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("attacker"), (condition, event) -> {
            if(event.getEntity() instanceof LivingEntity li && ((CraftLivingEntity)li).getHandle().getLastAttacker() != null){
                boolean rtn = true;
                if(condition.containsKey("entity_condition")){
                    rtn = ConditionExecutor.testEntity(condition, ((CraftLivingEntity)li).getHandle().getLastAttacker().getBukkitEntity());
                }
                return rtn;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("amount"), (condition, event) -> {
            String comparison = condition.get("comparison").toString();
            Long compare_to = (Long) condition.get("compare_to");
            return Utils.compareValues(event.getDamage(), comparison, compare_to);
        }));
    }

    private void register(ConditionFactory factory){
        GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registerable {
        NamespacedKey key;
        BiPredicate<JSONObject, EntityDamageEvent> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<JSONObject, EntityDamageEvent> test){
            this.key = key;
            this.test = test;
        }

        public boolean test(JSONObject condition, EntityDamageEvent tester){
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}