package me.dueris.genesismc.factory.conditions.types;

import me.dueris.calio.builder.inst.factory.FactoryJsonObject;
import me.dueris.calio.registry.Registrable;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Comparison;
import me.dueris.genesismc.registry.Registries;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.damage.CraftDamageType;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.util.CraftNamespacedKey;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiPredicate;

import static org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class DamageConditions {
    public static HashMap<String, ArrayList<DamageType>> damageTagMappings = new HashMap<>();

    public void prep() {
        register(new ConditionFactory(GenesisMC.apoliIdentifier("projectile"), (condition, event) -> {
            if (event.getCause().equals(DamageCause.PROJECTILE)) {
                boolean projectile = true;
                boolean projectileCondition = true;
                if (condition.isPresent("projectile_condition")) {
                    projectileCondition = ConditionExecutor.testEntity(condition.getJsonObject("projectile_condition"), ((CraftEntity) ((EntityDamageByEntityEvent) event).getDamager()));
                }
                if (condition.isPresent("projectile") && event instanceof EntityDamageByEntityEvent eventT) {
                    String identifier = condition.getString("projectile");
                    if (identifier.contains(":")) {
                        identifier = identifier.split(":")[1];
                    }
                    projectile = eventT.getDamager().getType().equals(EntityType.valueOf(identifier.toUpperCase()));
                }
                return projectileCondition && projectile;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("name"), (condition, event) -> CraftDamageType.bukkitToMinecraft(event.getDamageSource().getDamageType()).msgId().equalsIgnoreCase(condition.getString("name"))));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("in_tag"), (condition, event) -> {
            NamespacedKey tag = NamespacedKey.fromString(condition.getString("tag"));
            TagKey<net.minecraft.world.damagesource.DamageType> key = TagKey.create(net.minecraft.core.registries.Registries.DAMAGE_TYPE, CraftNamespacedKey.toMinecraft(tag));
            Holder<net.minecraft.world.damagesource.DamageType> nmsDamageType = CraftDamageType.bukkitToMinecraftHolder(event.getDamageSource().getDamageType());
            return nmsDamageType.is(key);
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("fire"), (condition, event) -> event.getCause().equals(DamageCause.FIRE)));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("attacker"), (condition, event) -> {
            if (event.getEntity() instanceof LivingEntity li && ((CraftLivingEntity) li).getHandle().getLastAttacker() != null) {
                boolean rtn = true;
                if (condition.isPresent("entity_condition")) {
                    rtn = ConditionExecutor.testEntity(condition, ((CraftLivingEntity) li).getHandle().getLastAttacker().getBukkitEntity());
                }
                return rtn;
            }
            return false;
        }));
        register(new ConditionFactory(GenesisMC.apoliIdentifier("amount"), (condition, event) -> {
            String comparison = condition.getString("comparison");
            Long compare_to = condition.getNumber("compare_to").getLong();
            return Comparison.getFromString(comparison).compare(event.getDamage(), compare_to);
        }));
    }

    private void register(ConditionFactory factory) {
        GenesisMC.getPlugin().registry.retrieve(Registries.DAMAGE_CONDITION).register(factory);
    }

    public class ConditionFactory implements Registrable {
        NamespacedKey key;
        BiPredicate<FactoryJsonObject, EntityDamageEvent> test;

        public ConditionFactory(NamespacedKey key, BiPredicate<FactoryJsonObject, EntityDamageEvent> test) {
            this.key = key;
            this.test = test;
        }

        public boolean test(FactoryJsonObject condition, EntityDamageEvent tester) {
            return test.test(condition, tester);
        }

        @Override
        public NamespacedKey getKey() {
            return key;
        }
    }
}