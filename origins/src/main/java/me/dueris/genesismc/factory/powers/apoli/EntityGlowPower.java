package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.annotations.Register;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.data.types.Shape;
import me.dueris.genesismc.util.TextureLocation;
import me.dueris.genesismc.util.entity.GlowingEntitiesUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.CraftLocation;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Set;

public class EntityGlowPower extends SelfGlow {

	@Register
	public EntityGlowPower(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority, FactoryJsonObject entityCondition, FactoryJsonObject bientityCondition, boolean useTeams, float red, float green, float blue) {
		super(name, description, hidden, condition, loading_priority, entityCondition, bientityCondition, useTeams, red, green, blue);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return SelfGlow.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("entity_glow"));
	}

	@Override
	public void tick(Player p) {
		ServerPlayer player = ((CraftPlayer)p).getHandle();
		ServerLevel level = (ServerLevel) player.level();
		Set<Entity> entities = Shape.getEntities(Shape.SPHERE, level, CraftLocation.toVec3D(p.getLocation()), 60);
		entities.add(player);
		GlowingEntitiesUtils utils = GenesisMC.glowingEntitiesUtils;
		try {
			if (isActive(p)) {
				for (Entity entity : entities) {
					if (!ConditionExecutor.testEntity(getEntityCondition(), entity.getBukkitEntity())) {
						utils.unsetGlowing(entity.getBukkitEntity(), p);
						continue;
					}
					if (!ConditionExecutor.testBiEntity(getBientityCondition(), entity.getBukkitEntity(), p)) {
						utils.unsetGlowing(entity.getBukkitEntity(), p);
						continue;
					}
					if (useTeams() && player.getTeam() != null) {
						utils.setGlowing(entity.getBukkitEntity(), p, CraftChatMessage.getColor(player.getTeam().getColor()));
					} else {
						Color awtColor = new Color(getRed(), getGreen(), getBlue());
						utils.setGlowing(entity.getBukkitEntity(), p, translateBarColor(TextureLocation.convertToBarColor(awtColor)));
					}
				}
			} else {
				for (Entity entity : entities) {
					utils.unsetGlowing(entity.getBukkitEntity(), p);
				}
			}
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}
}
