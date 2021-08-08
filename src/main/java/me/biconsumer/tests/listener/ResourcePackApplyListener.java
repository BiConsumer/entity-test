package me.biconsumer.tests.listener;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;

public class ResourcePackApplyListener implements Listener {

    private final Plugin plugin;
    private final String resourcePackUrl;
    private final byte[] resourcePackHash;

    private final static String KICK_MESSAGE = ChatColor.of("#fbac33") + "Hey there, chief!\n\n"
            + ChatColor.of("#bec9d9") + "To play on the " + ChatColor.LIGHT_PURPLE + "BiConsumer" + ChatColor.of("#bec9d9") + " test server\n"
            + ChatColor.of("#bec9d9") + "you must accept the resource pack!\n\n"
            + ChatColor.of("#3d8747") + "To enable the resource pack, click edit\n"
            + ChatColor.of("#3d8747") + "in the server list, then select\n"
            + ChatColor.of("#61c14b") + "\"Server Resource Packs: Enabled\"";

    public ResourcePackApplyListener(
            Plugin plugin,
            String resourcePackUrl,
            byte[] resourcePackHash
    ) {
        this.plugin = plugin;
        this.resourcePackUrl = resourcePackUrl;
        this.resourcePackHash = resourcePackHash;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(
                plugin,
                () -> {
                    if (player.isOnline()) {
                        player.setResourcePack(resourcePackUrl, resourcePackHash);
                    }
                },
                20L
        );
    }

    @EventHandler
    public void onResourceStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        switch (event.getStatus()) {
            case SUCCESSFULLY_LOADED -> {
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.STRIKETHROUGH + "----------------------------------------");
                player.sendMessage("  Welcome to the " + ChatColor.LIGHT_PURPLE + "BiConsumer" + ChatColor.WHITE + " test server!");
                player.sendMessage("  Feel free to " + ChatColor.LIGHT_PURPLE + "invite your friends " + ChatColor.WHITE + "to try");
                player.sendMessage("  out my projects!");
                player.sendMessage(ChatColor.LIGHT_PURPLE + "" + ChatColor.STRIKETHROUGH + "----------------------------------------");
            }
            case DECLINED, FAILED_DOWNLOAD -> player.kickPlayer(KICK_MESSAGE);
        }
    }
}