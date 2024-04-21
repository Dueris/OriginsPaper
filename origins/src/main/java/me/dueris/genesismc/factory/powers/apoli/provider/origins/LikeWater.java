package me.dueris.genesismc.factory.powers.apoli.provider.origins;

import me.dueris.genesismc.GenesisMC;
import me.dueris.genesismc.factory.powers.CraftPower;
import me.dueris.genesismc.factory.powers.apoli.provider.PowerProvider;
import me.dueris.genesismc.factory.powers.genesismc.GravityPower;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;

public class LikeWater extends CraftPower implements Listener, PowerProvider {
    private static final GravityPower gravityHook = new GravityPower();
    public static ArrayList<Player> likeWaterPlayers = new ArrayList<>();
    protected static NamespacedKey powerReference = GenesisMC.originIdentifier("like_water");

    @Override
    public void run(Player p) {
        if (this.getPowerArray().contains(p))
            p.setGravity(!p.isInWaterOrBubbleColumn() && !p.isSwimming() && !p.isSneaking());
    }

    @Override
    public String getPowerFile() {
        return null;
    }

    @Override
    public ArrayList<Player> getPowerArray() {
        return likeWaterPlayers;
    }

}
