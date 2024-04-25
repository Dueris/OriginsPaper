package me.dueris.genesismc.util;

import javax.swing.*;
import java.lang.reflect.Method;

public class LaunchWarning {
    public static void main(String[] args) {
        if (System.console() == null && !isHeadless()) {
            JOptionPane.showMessageDialog(null, getMessage(), "GenesisMC", JOptionPane.ERROR_MESSAGE);
        }

        System.out.println(getMessage());
    }

    public static boolean isHeadless() {
        try {
            Class<?> graphicsEnvironment = Class.forName("java.awt.GraphicsEnvironment");
            Method isHeadless = graphicsEnvironment.getDeclaredMethod("isHeadless");
            return (Boolean) isHeadless.invoke(null);
        } catch (Exception ignored) {
        }

        return true;
    }

    private static String getMessage() {
        return "You have tried to launch GenesisMC(a Minecraft plugin) directly, but its not an executable program or installer.\nInstead, please install Paper or a fork of it and place the jar in the plugins folder.";
    }
}
