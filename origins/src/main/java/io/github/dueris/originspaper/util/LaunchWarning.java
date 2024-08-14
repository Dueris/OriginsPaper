package io.github.dueris.originspaper.util;

import javax.swing.*;
import java.lang.reflect.Method;

public class LaunchWarning {
	public static void main(String[] args) {
		System.out.println(getMessage());
		if (System.console() == null && !isHeadless()) {
			JOptionPane.showMessageDialog(null, getMessage(), "OriginsPaper", JOptionPane.ERROR_MESSAGE);
		}
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
		return "You have tried to launch an OriginsPaper (a Minecraft plugin) directly, but its not an executable program or installer.\nInstead, please install Paper or a fork of it and place the jar in the plugins folder.";
	}
}
