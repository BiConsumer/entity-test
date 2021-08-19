package me.biconsumer.tests;

import me.biconsumer.tests.entities.AbstractModelEntity;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ModelEntityRegistry {

    private final Map<String, Function<Location, ? extends AbstractModelEntity>> modelEntities = new HashMap<>();

    public <T extends AbstractModelEntity> T create(String name, Location location) {
        return (T) modelEntities.get(name).apply(location);
    }

    public void register(String modelName, Function<Location, ? extends AbstractModelEntity> function) {
        this.modelEntities.put(modelName, function);
    }
}