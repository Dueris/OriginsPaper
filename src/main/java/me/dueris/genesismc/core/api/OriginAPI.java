package me.dueris.genesismc.core.api;

import me.dueris.genesismc.core.api.factory.CustomOriginAPI;

import java.util.ArrayList;

import static me.dueris.genesismc.core.api.factory.CustomOriginAPI.customOrigins;

public class OriginAPI {
    
    public static ArrayList<String> getLoadedOrigins(){
        customOrigins.put("genesis:origin-human", "GenesisMC-Core");
        customOrigins.put("genesis:origin-enderian", "GenesisMC-Core");
        customOrigins.put("genesis:origin-merling", "GenesisMC-Core");
        customOrigins.put("genesis:origin-phantom", "GenesisMC-Core");
        customOrigins.put("genesis:origin-elytrian", "GenesisMC-Core");
        customOrigins.put("genesis:origin-blazeborn", "GenesisMC-Core");
        customOrigins.put("genesis:origin-avian", "GenesisMC-Core");
        customOrigins.put("genesis:origin-arachnid", "GenesisMC-Core");
        customOrigins.put("genesis:origin-shulk", "GenesisMC-Core");
        customOrigins.put("genesis:origin-feline", "GenesisMC-Core");
        customOrigins.put("genesis:origin-starborne", "GenesisMC-Core");
        customOrigins.put("genesis:origin-allay", "GenesisMC-Core");
        customOrigins.put("genesis:origin-rabbit", "GenesisMC-Core");
        customOrigins.put("genesis:origin-bee", "GenesisMC-Core");
        customOrigins.put("genesis:origin-human", "GenesisMC-Core");
        customOrigins.put("genesis:origin-sculkling", "GenesisMC-Core");
        customOrigins.put("genesis:origin-creep", "GenesisMC-Core");
        customOrigins.put("genesis:origin-slimeling", "GenesisMC-Core");
        customOrigins.put("genesis:origin-piglin", "GenesisMC-Core");
        return new ArrayList<>(customOrigins.keySet());
    }
    
    
}
