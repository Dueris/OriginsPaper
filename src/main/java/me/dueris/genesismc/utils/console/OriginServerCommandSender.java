package me.dueris.genesismc.utils.console;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class OriginServerCommandSender implements CommandSender {
    public final PermissibleBase perm;

    public OriginServerCommandSender() {
        this.perm = new PermissibleBase(this);
    }

    protected OriginServerCommandSender(PermissibleBase perm) {
        this.perm = perm;
    }

    @Override
    public boolean isPermissionSet(String name) {
        return true;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return true;
    }

    @Override
    public boolean hasPermission(String name) {
        return true;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return true;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return this.perm.addAttachment(plugin, name, value);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return this.perm.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return this.perm.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return this.perm.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        this.perm.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.perm.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.perm.getEffectivePermissions();
    }

    public boolean isPlayer() {
        return false;
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public @NotNull String getName() {
        return "ORIGINS";
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return null;
    }

    @Override
    public @NotNull Component name() {
        return Component.text(getName());
    }

    @Override
    public void sendMessage(@NotNull String message) {

    }

    @Override
    public void sendMessage(@NotNull String... messages) {

    }

    @Override
    public void sendMessage(UUID uuid, String message) {

    }

    @Override
    public void sendMessage(UUID uuid, String... messages) {

    }

    @Override
    public boolean isOp() {
        return true;
    }

    @Override
    public void setOp(boolean value) {

    }
}
