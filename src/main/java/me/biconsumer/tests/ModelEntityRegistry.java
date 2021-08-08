package me.biconsumer.tests;

import me.biconsumer.tests.entities.AbstractModelEntity;
import net.minecraft.world.level.World;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModelEntityRegistry {

    private final Map<String, Function<World, ? extends AbstractModelEntity>> modelEntities = new HashMap<>();

    public <T extends AbstractModelEntity> T create(String name, World world) {
        return (T) modelEntities.get(name).apply(world);
    }

    public void register(String modelName, Function<World, ? extends AbstractModelEntity> function) {
        this.modelEntities.put(modelName, function);
    }
}