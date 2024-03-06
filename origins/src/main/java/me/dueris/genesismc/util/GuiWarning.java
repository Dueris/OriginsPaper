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
		String msg = "You have tried to launch GenesisMC(a Minecraft Plugin) directly, but it is not an executable program or installer.\nYou must install Paper or a fork of it and place the plugin inside the plugins directory to enable it.\nEnjoy the plugin! - Dueris";
		return msg;
	}
}
