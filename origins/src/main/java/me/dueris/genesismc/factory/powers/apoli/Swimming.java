package me.dueris.genesismc.factory.powers.apoli;

import me.dueris.calio.data.FactoryData;
import me.dueris.calio.data.factory.FactoryJsonObject;
import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.holder.PowerType;
import net.minecraft.world.phys.Vec3;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.util.CraftVector;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class Swimming extends PowerType {

	public Swimming(String name, String description, boolean hidden, FactoryJsonObject condition, int loading_priority) {
		super(name, description, hidden, condition, loading_priority);
	}

	public static FactoryData registerComponents(FactoryData data) {
		return PowerType.registerComponents(data).ofNamespace(GenesisMC.apoliIdentifier("swimming"));
	}

	@Override
	public void tick(Player p) {
		CraftPlayer player = (CraftPlayer) p;
		if (isActive(player)) {
			player.setPose(Pose.SWIMMING);
			player.setFlying(true);
			Vec3 look = Vec3.directionFromRotation(player.getHandle().getRotationVector());
			player.setVelocity(CraftVector.toBukkit(new Vec3(look.x / 4, look.y / 4, look.z / 4)));
		} else if (!(player.getGameMode().equals(GameMode.SPECTATOR) || player.getGameMode().equals(GameMode.CREATIVE))) {
			player.setFlying(false);
		}
	}
}
