package me.dueris.genesismc.core;


import java.util.ArrayList;

import static me.dueris.genesismc.core.factory.CraftApoli.getCustomOrigins;

public class CraftOrigin {

    public static ArrayList<String> getLoadedOrigins() {
        ArrayList<String> origins = new ArrayList<>(getCustomOrigins().keySet());
        origins.add("genesis:origin-human");
        origins.add("genesis:origin-enderian");
        origins.add("genesis:origin-merling");
        origins.add("genesis:origin-phantom");
        origins.add("genesis:origin-elytrian");
        origins.add("genesis:origin-blazeborn");
        origins.add("genesis:origin-avian");
        origins.add("genesis:origin-arachnid");
        origins.add("genesis:origin-shulk");
        origins.add("genesis:origin-feline");
        origins.add("genesis:origin-starborne");
        origins.add("genesis:origin-allay");
        origins.add("genesis:origin-rabbit");
        origins.add("genesis:origin-bee");
        origins.add("genesis:origin-human");
        origins.add("genesis:origin-sculkling");
        origins.add("genesis:origin-creep");
        origins.add("genesis:origin-slimeling");
        origins.add("genesis:origin-piglin");
        return origins;
    }


}
