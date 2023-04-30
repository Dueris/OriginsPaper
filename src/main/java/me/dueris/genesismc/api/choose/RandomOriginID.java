package me.dueris.genesismc.api.choose;

import me.dueris.genesismc.core.GenesisMC;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.Random;

public class RandomOriginID {

    public static int RandomOrigin(){
        Random random = new Random();
        int r = random.nextInt(18);

        if (r == 0) {
            return 0401065;
        } else
        if (r == 1) {
            return 6503044;
        } else
        if (r == 2) {
            return 0004013;
        } else
        if (r == 3) {
            return 1709012;
        } else
        if (r == 4) {
            return 2356555;
        } else
        if (r == 5) {
            return 7300041;
        } else
        if (r == 6) {
            return 2304045;
        } else
        if (r == 7) {
            return 9602042;
        } else
        if (r == 8) {
            return 9811027;
        } else
        if (r == 9) {
            return 7303065;
        } else
        if (r == 10) {
            return 1310018;
        } else
        if (r == 11) {
            return 1205048;
        } else
        if (r == 12) {
            return 5308033;
        } else
        if (r == 13) {
            return 8906022;
        } else
        if (r == 14) {
            return 6211006;
        } else
        if (r == 15) {
            return 4501011;
        } else
        if (r == 16) {
            return 6211021;
        } else
        if (r == 17) {
            return 4307015;
        }else{
            return 0;
        }
    }

}
