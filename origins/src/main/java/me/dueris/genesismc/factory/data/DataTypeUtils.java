package me.dueris.genesismc.factory.data;

import org.bukkit.Material;

public class DataTypeUtils {
    public static Material getMaterial(Object object){
        if(object instanceof String stringedMaterial){
            if(stringedMaterial.startsWith("apoli:material")){
                String matSt = stringedMaterial.split("/")[1];
                return Material.valueOf(matSt.toUpperCase());
            }else{
                String matSt;
                if(stringedMaterial.contains(":")){
                    matSt = stringedMaterial.split(":")[1];
                }else{
                    matSt = stringedMaterial;
                }
                return Material.valueOf(matSt.toUpperCase());
            }
        }
        return null;
    }
}
