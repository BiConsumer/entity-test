package me.biconsumer.tests.listener;

import me.biconsumer.tests.ModelEntityRegistry;
import me.biconsumer.tests.entities.AbstractModelEntity;
import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class ModelEntityPersistantLoad implements Listener {

    private final Plugin plugin;
    private final ModelEntityRegistry registry;

    public ModelEntityPersistantLoad(Plugin plugin, ModelEntityRegistry registry) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();

        for (Entity entity : world.getEntities()) {
            PersistentDataContainer dataContainer = entity.getPersistentDataContainer();

            if (dataContainer.has(new NamespacedKey(plugin, "model_entity"), PersistentDataType.STRING)) {
                String modelName = dataContainer.get(new NamespacedKey(plugin, "model_entity"), PersistentDataType.STRING);

                AbstractModelEntity modelEntity = registry.create(modelName, ((CraftWorld) world).getHandle());
                modelEntity.load(((CraftEntity) entity).getHandle().save(new NBTTagCompound()));
                entity.remove();

                ((CraftWorld) world).getHandle().addEntity(modelEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            }
        }
    }
}