package me.dueris.genesismc.registry.nms;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.registry.Registries;
import me.dueris.genesismc.registry.registries.Power;
import me.dueris.genesismc.util.entity.OriginPlayerAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_20_R3.util.CraftNamespacedKey;
import org.bukkit.entity.Player;

public class PowerLootCondition implements LootItemCondition {
   public static final Codec<PowerLootCondition> CODEC = RecordCodecBuilder.create((instance) -> {
      return instance.group(ResourceLocation.CODEC.fieldOf("power").forGetter(PowerLootCondition::getPowerId), ResourceLocation.CODEC.optionalFieldOf("source").forGetter(PowerLootCondition::getPowerSourceId)).apply(instance, PowerLootCondition::new);
   });
   public static final LootItemConditionType TYPE;
   private ResourceLocation powerId;
   private ResourceLocation powerSourceId;

   private PowerLootCondition(ResourceLocation powerId, Optional<ResourceLocation> powerSourceId) {
   }

   public boolean test(LootContext context) {
      Entity entity = (Entity)context.getParam(LootContextParams.THIS_ENTITY);
      CraftEntity var4 = entity.getBukkitEntity();
      if (var4 instanceof Player) {
         Player player = (Player)var4;
         NamespacedKey key = CraftNamespacedKey.fromMinecraft(this.powerId);
         Power power = (Power)GenesisMC.getPlugin().registry.retrieve(Registries.POWER).get(key);
         return OriginPlayerAccessor.hasPower(player, power.getTag());
      } else {
         return false;
      }
   }

   public LootItemConditionType getType() {
      return TYPE;
   }

   public ResourceLocation getPowerId() {
      return this.powerId;
   }

   public Optional<ResourceLocation> getPowerSourceId() {
      return Optional.of(this.powerSourceId);
   }

   static {
      TYPE = new LootItemConditionType(CODEC);
   }
}