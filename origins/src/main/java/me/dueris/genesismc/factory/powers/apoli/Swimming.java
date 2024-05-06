package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.genesismc.factory.conditions.ConditionExecutor;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.registry.registries.Power;
import net.minecraft.world.phys.Vec3;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;

@ApiStatus.Experimental
public class Swimming extends CraftPower {

	@Override
	public void run(Player p, Power power) {
		CraftPlayer player = (CraftPlayer) p;
		if (ConditionExecutor.testEntity(power.getJsonObject("condition"), player)) {
			player.setPose(Pose.SWIMMING);
			player.setFlying(true);
			Vec3 look = Vec3.directionFromRotation(player.getHandle().getRotationVector());
			player.setVelocity(CraftVector.toBukkit(new Vec3(look.x / 4, look.y / 4, look.z / 4)));
		} else if (!(player.getGameMode().equals(GameMode.SPECTATOR) || player.getGameMode().equals(GameMode.CREATIVE))) {
			player.setFlying(false);
		}
	}

	@Override
	public String getType() {
		return "apoli:swimming";
	}

	@Override
	public ArrayList<Player> getPlayersWithPower() {
		return swimming;
	}
}
