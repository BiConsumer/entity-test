package me.biconsumer.tests.listener;

import me.biconsumer.tests.ModelEntityRegistry;
import me.biconsumer.tests.entities.RedstoneMonsterEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.Objects;

public class ChatSpawnListener implements Listener {

    private final Plugin plugin;
    private final ModelEntityRegistry registry;

    public ChatSpawnListener(
            Plugin plugin,
            ModelEntityRegistry registry
    ) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.getMessage().contains("spawn redstone")) {
            String[] args = event.getMessage().split(" ");
            int amount = 1;

            if (args.length == 3) {
                amount = Integer.parseInt(args[2]);
            }

            Location location = event.getPlayer().getLocation();

            for (int i = 0; i < amount; i ++) {
                RedstoneMonsterEntity redstoneMonsterEntity = registry.create(
                        "redstone",
                        ((CraftWorld) Objects.requireNonNull(location.getWorld())).getHandle()
                );

                redstoneMonsterEntity.setPositionRotation(
                        location.getX(),
                        location.getY(),
                        location.getZ(),
                        location.getYaw(),
                        location.getPitch()
                );

                Bukkit.getScheduler().runTask(plugin, () ->
                        ((CraftWorld) location.getWorld()).getHandle().addEntity(redstoneMonsterEntity, CreatureSpawnEvent.SpawnReason.CUSTOM)
                );
            }
        }
    }
}