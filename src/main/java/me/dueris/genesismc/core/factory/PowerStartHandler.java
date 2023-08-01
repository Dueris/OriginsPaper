package me.dueris.genesismc.core.factory;

import me.dueris.genesismc.core.factory.handlers.CustomOriginExistCheck;
import me.dueris.genesismc.core.factory.powers.FlightHandler;
import me.dueris.genesismc.core.factory.powers.OriginsMod.Toggle;
import me.dueris.genesismc.core.factory.powers.OriginsMod.actions.ActionOnBeingUsed;
import me.dueris.genesismc.core.factory.powers.OriginsMod.block.AirFromPotions;
import me.dueris.genesismc.core.factory.powers.OriginsMod.block.WaterBreathe;
import me.dueris.genesismc.core.factory.powers.OriginsMod.block.WaterVision;
import me.dueris.genesismc.core.factory.powers.OriginsMod.effects.EffectImmunity;
import me.dueris.genesismc.core.factory.powers.OriginsMod.effects.NightVision;
import me.dueris.genesismc.core.factory.powers.OriginsMod.genesismc.*;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.*;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.damage.DamageOverTime;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.damage.WaterDamage;
import me.dueris.genesismc.core.factory.powers.OriginsMod.world.EntityGlow;
import me.dueris.genesismc.core.factory.powers.OriginsMod.world.EntityGroupManager;
import me.dueris.genesismc.core.factory.powers.Powers;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.FlightElytra;
import me.dueris.genesismc.core.factory.powers.armour.GoldAmourBellow;
import me.dueris.genesismc.core.factory.powers.armour.GoldArmourBuff;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.RestrictArmor;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeConditioned;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.attributes.AttributeHandler;
import me.dueris.genesismc.core.factory.powers.block.CeilingWeak;
import me.dueris.genesismc.core.factory.powers.block.Webbing;
import me.dueris.genesismc.core.factory.powers.block.fluid.*;
import me.dueris.genesismc.core.factory.powers.effects.*;
import me.dueris.genesismc.core.factory.powers.entity.*;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.damage.InvulnerabilityDamage;
import me.dueris.genesismc.core.factory.powers.food.Carnivore;
import me.dueris.genesismc.core.factory.powers.food.CarrotOnly;
import me.dueris.genesismc.core.factory.powers.food.MoreExhaustion;
import me.dueris.genesismc.core.factory.powers.food.Vegitarian;
import me.dueris.genesismc.core.factory.powers.genesis.*;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.EnderPearlThrow;
import me.dueris.genesismc.core.factory.powers.item.LaunchAir;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.damage.Burn;
import me.dueris.genesismc.core.factory.powers.world.BurnInDaylight;
import me.dueris.genesismc.core.factory.powers.OriginsMod.world.WorldSpawnHandler;
import me.dueris.genesismc.core.factory.powers.OriginsMod.player.inventory.Inventory;

import static me.dueris.genesismc.core.GenesisMC.getPlugin;
import static org.bukkit.Bukkit.getServer;

public class PowerStartHandler {

    public static void StartRunnables() {

        Climbing climb = new Climbing();
        climb.runTaskTimer(getPlugin(), 0, 5);

        WaterDamage waterdamage = new WaterDamage();
        waterdamage.runTaskTimer(getPlugin(), 0, 20);

        BurningWrath burningWrath = new BurningWrath();
        burningWrath.runTaskTimer(getPlugin(), 0, 5);

        SlowFalling slowFalling = new SlowFalling();
        slowFalling.runTaskTimer(getPlugin(), 0, 5);

        Charged charged = new Charged();
        charged.runTaskTimer(getPlugin(), 0, 5);

        FelinePhobia felinePhobia = new FelinePhobia();
        felinePhobia.runTaskTimer(getPlugin(), 0, 20);

        PlayerRender transparency = new PlayerRender();
        transparency.runTaskTimer(getPlugin(), 0, 5);

        BurnInDaylight burnInDaylight = new BurnInDaylight();
        burnInDaylight.runTaskTimer(getPlugin(), 0, 10);

        JumpIncreased jumpIncreased = new JumpIncreased();
        jumpIncreased.runTaskTimer(getPlugin(), 0, 10);

        NoShield shield = new NoShield();
        shield.runTaskTimer(getPlugin(), 0, 10);

        WaterBreathe waterBreathe = new WaterBreathe();
        waterBreathe.runTaskTimer(getPlugin(), 0, 1);

        FlightHandler flightHandler = new FlightHandler();
        flightHandler.runTaskTimer(getPlugin(), 0, 2);

        TailWind tailWind = new TailWind();
        tailWind.runTaskTimer(getPlugin(), 0, 1);

        WeakBiomeCold weakBiomeCold = new WeakBiomeCold();
        weakBiomeCold.runTaskTimer(getPlugin(), 0, 5);

        CeilingWeak ceilingWeak = new CeilingWeak();
        ceilingWeak.runTaskTimer(getPlugin(), 0, 2);

        GoldAmourBellow goldAmourBellow = new GoldAmourBellow();
        goldAmourBellow.runTaskTimer(getPlugin(), 0, 1);

        HotBlooded hotBlooded = new HotBlooded();
        hotBlooded.runTaskTimer(getPlugin(), 0, 2);

        FireWeak fireWeak = new FireWeak();
        fireWeak.runTaskTimer(getPlugin(), 0, 3);

        AquaAffinity aquaAffinity = new AquaAffinity();
        aquaAffinity.runTaskTimer(getPlugin(), 5, 0);

        WaterVision waterVision = new WaterVision();
        waterVision.runTaskTimer(getPlugin(), 5, 0);

        SwimSpeed swimSpeed = new SwimSpeed();
        swimSpeed.runTaskTimer(getPlugin(), 5, 0);

        OverworldPiglinZombified piglinZombified = new OverworldPiglinZombified();
        piglinZombified.runTaskTimer(getPlugin(), 0, 40);

        NightVision nightVision = new NightVision();
        nightVision.runTaskTimer(getPlugin(), 0, 10);

        Burn burn = new Burn();
        burn.runTaskTimer(getPlugin(), 0, 1);

        RestrictArmor restrictArmor = new RestrictArmor();
        restrictArmor.runTaskTimer(getPlugin(), 0, 1);

        Bioluminescent Bioluminescent = new Bioluminescent();
        Bioluminescent.runTaskTimer(getPlugin(), 0, 1);

        Phasing phasing = new Phasing();
        phasing.runTaskTimer(getPlugin(), 0, 1);

        EffectImmunity effectImmunity = new EffectImmunity();
        effectImmunity.runTaskTimer(getPlugin(), 0, 1);

        EntityGlow entityGlow = new EntityGlow();
        entityGlow.runTaskTimer(getPlugin(), 0, 1);

        EntityGroupManager entityGroupManager = new EntityGroupManager();
        entityGroupManager.runTaskTimer(getPlugin(), 0, 10);

        Exhaust exhaust = new Exhaust();
        exhaust.runTaskTimer(getPlugin(), 0, 1);

        DamageOverTime damageOverTime = new DamageOverTime();
        damageOverTime.runTaskTimer(getPlugin(), 0, 1);

        Freeze freeze = new Freeze();
        freeze.runTaskTimer(getPlugin(), 0, 1);

        Grounded grounded = new Grounded();
        grounded.runTaskTimer(getPlugin(), 0, 1);

        Gravity gravity = new Gravity();
        gravity.runTaskTimer(getPlugin(), 0, 1);
    }

    public static void StartListeners() {

        getServer().getPluginManager().registerEvents(new CustomOriginExistCheck(), getPlugin());
        getServer().getPluginManager().registerEvents(new Powers(), getPlugin());
        getServer().getPluginManager().registerEvents(new Webbing(), getPlugin());
        getServer().getPluginManager().registerEvents(new Arthropod(), getPlugin());
        getServer().getPluginManager().registerEvents(new Carnivore(), getPlugin());
        getServer().getPluginManager().registerEvents(new LayEggs(), getPlugin());
        getServer().getPluginManager().registerEvents(new Vegitarian(), getPlugin());
        getServer().getPluginManager().registerEvents(new FreshAir(), getPlugin());
        getServer().getPluginManager().registerEvents(new AttributeHandler.Reach(), getPlugin());
        //getServer().getPluginManager().registerEvents(new NoCobwebSlowdown(), GenesisMC.getPlugin()); -- removed do to anti-cheat triggers
        getServer().getPluginManager().registerEvents(new HotHands(), getPlugin());
        getServer().getPluginManager().registerEvents(new FallImmunity(), getPlugin());
        getServer().getPluginManager().registerEvents(new HotBlooded(), getPlugin());
        getServer().getPluginManager().registerEvents(new WorldSpawnHandler(), getPlugin());
        getServer().getPluginManager().registerEvents(new ExtraFireTick(), getPlugin());
        getServer().getPluginManager().registerEvents(new SilkTouch(), getPlugin());
        getServer().getPluginManager().registerEvents(new BowInability(), getPlugin());
        getServer().getPluginManager().registerEvents(new PumpkinHate(), getPlugin());
        getServer().getPluginManager().registerEvents(new AttributeHandler(), getPlugin());
        getServer().getPluginManager().registerEvents(new WaterDamage(), getPlugin());
        getServer().getPluginManager().registerEvents(new ProjectileImmune(), getPlugin());
        getServer().getPluginManager().registerEvents(new EnderPearlThrow(), getPlugin());
        getServer().getPluginManager().registerEvents(new Phasing(), getPlugin());
        getServer().getPluginManager().registerEvents(new GoldArmourBuff(), getPlugin());
        getServer().getPluginManager().registerEvents(new GoldItemBuff(), getPlugin());
        getServer().getPluginManager().registerEvents(new BigLeap(), getPlugin());
        getServer().getPluginManager().registerEvents(new CarrotOnly(), getPlugin());
        getServer().getPluginManager().registerEvents(new DecreaseExplosion(), getPlugin());
        getServer().getPluginManager().registerEvents(new CreeperDeathDrop(), getPlugin());
        getServer().getPluginManager().registerEvents(new ExplodeTick(), getPlugin());
        getServer().getPluginManager().registerEvents(new RabbitFoot(), getPlugin());
        getServer().getPluginManager().registerEvents(new MoreExhaustion(), getPlugin());
        getServer().getPluginManager().registerEvents(new StrongArms(), getPlugin());
        getServer().getPluginManager().registerEvents(new FireImmunity(), getPlugin());
        getServer().getPluginManager().registerEvents(new FlightElytra(), getPlugin());
        getServer().getPluginManager().registerEvents(new LaunchAir(), getPlugin());
        getServer().getPluginManager().registerEvents(new GoldAmourBellow(), getPlugin());
        getServer().getPluginManager().registerEvents(new ArielCombat(), getPlugin());
        getServer().getPluginManager().registerEvents(new Fragile(), getPlugin());
        getServer().getPluginManager().registerEvents(new ImpalingMore(), getPlugin());
        getServer().getPluginManager().registerEvents(new AirFromPotions(), getPlugin());
        getServer().getPluginManager().registerEvents(new AttributeHandler(), getPlugin());
        getServer().getPluginManager().registerEvents(new StrongArmsBreakSpeed(), getPlugin());
        getServer().getPluginManager().registerEvents(new AttributeConditioned(), getPlugin());
        getServer().getPluginManager().registerEvents(new CreeperScare(), getPlugin());
        getServer().getPluginManager().registerEvents(new Inventory(), getPlugin());
        getServer().getPluginManager().registerEvents(new InvulnerabilityDamage(), getPlugin());
        getServer().getPluginManager().registerEvents(new DisableRegen(), getPlugin());
        getServer().getPluginManager().registerEvents(new FireProjectile(), getPlugin());
        getServer().getPluginManager().registerEvents(new Gravity(), getPlugin());
        getServer().getPluginManager().registerEvents(new Toggle(), getPlugin());

        //actions
        getServer().getPluginManager().registerEvents(new ActionOnBeingUsed(), getPlugin());
    }
}
