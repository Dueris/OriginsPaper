package me.dueris.genesismc.util;

import javax.swing.*;
import java.lang.reflect.Method;

public class GuiWarning {
    public static void main(String[] args) {
        if (System.console() == null && !isHeadless()) {
            JOptionPane.showMessageDialog(null, getMessage(), "GenesisMC-Origins", JOptionPane.ERROR_MESSAGE);
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
        String msg = "You have attempted to load Genesis as a jar! You CANNOT do this!\nGenesisMC is a plugin and should be placed in the plugins directory of your server. Then start your server jar, and enjoy GenesisMC!";
        return msg;
    }
}
