package me.dueris.genesismc.core.factory.powers.OriginsMod.player;

import me.dueris.genesismc.core.entity.OriginPlayer;
import me.dueris.genesismc.core.utils.OriginContainer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import static me.dueris.genesismc.core.factory.powers.Powers.fire_projectile;

public class FireProjectile implements Listener {
    /*
    {
    "type": "origins:fire_projectile",
    "entity_type": "minecraft:arrow",
    "cooldown": 2,
    "hud_render": {
        "should_render": false
    },
    "tag": "{pickup:0b}",
    "key": {
        "key": "key.attack",
        "continuous": true
    }
}
     */

    public void FireProjectile(Player p) {
        for(OriginContainer origin : OriginPlayer.getOrigin(p).values()){
            if(fire_projectile.contains(p)){
                EntityType type = EntityType.valueOf(origin.getPowerFileFromType("origins:fire_projectile").get("entity_type").split(":")[1].toUpperCase());
                int cooldown = Integer.parseInt(origin.getPowerFileFromType("origins:fire_projectile").get("cooldown"));
                //TODO: RENDER SHIT UGHHHHH
                String tag = origin.getPowerFileFromType("origins:fire_projectile").get("tag");
            }
        }
    }

    //TODO: make the kye thinger and all the executorss
}
