package com.dragoncommissions.mixbukkit.api.shellcode.impl.inner;

import com.dragoncommissions.mixbukkit.api.shellcode.LocalVarManager;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCode;
import com.dragoncommissions.mixbukkit.api.shellcode.ShellCodeInfo;
import javassist.bytecode.Opcode;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

@ShellCodeInfo(
	name = "Load Class From Plugin Class Loader",
	description = "Load a class from the plugin class loader, and get its \"class\" instance",
	stacksContent = {"Class<T>"},
	calledDirectly = true
)
public class IShellCodeLoadClassFromPCL extends ShellCode {

	private final String name;

	public IShellCodeLoadClassFromPCL(String className) {
		this.name = className.replace("/", ".");
	}

	public IShellCodeLoadClassFromPCL(Class<?> clazz) {
		this.name = clazz.getName();
	}

	@Override
	@SneakyThrows
	public InsnList generate(MethodNode methodNode, LocalVarManager varManager) {
		InsnList out = new InsnList();
		out.add(new LdcInsnNode(name));
		out.add(new IShellCodePushInt(1).generate()); // true
		try {
			out.add(new IShellCodeMethodInvoke(Bukkit.class.getDeclaredMethod("getPluginManager")).generate());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		try {
			out.add(new IShellCodeMethodInvoke(PluginManager.class.getDeclaredMethod("getPlugins")).generate());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		out.add(new IShellCodePushInt(0).generate());
		out.add(new InsnNode(Opcode.AALOAD));
		try {
			out.add(new IShellCodeMethodInvoke(Object.class.getDeclaredMethod("getClass")).generate());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		try {
			out.add(new IShellCodeMethodInvoke(Class.class.getDeclaredMethod("getClassLoader")).generate());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		try {
			out.add(new IShellCodeMethodInvoke(Class.class.getDeclaredMethod("forName", String.class, boolean.class, ClassLoader.class)).generate());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
		return out;
	}
}
