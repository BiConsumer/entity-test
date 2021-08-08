package me.biconsumer.tests;

import team.unnamed.hephaestus.model.Model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModelRegistry {

    private final Map<String, Model> models =
            new HashMap<>();

    public Collection<Model> getValues() {
        return models.values();
    }

    public void register(Model model) {
        models.put(model.getName(), model);
    }

    public Model get(String name) {
        return models.get(name);
    }
}