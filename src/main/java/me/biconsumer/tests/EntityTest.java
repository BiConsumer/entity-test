package me.biconsumer.tests;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.biconsumer.tests.entities.RedstoneMonsterEntity;
import me.biconsumer.tests.listener.ChatSpawnListener;
import me.biconsumer.tests.listener.DefaultListener;
import me.biconsumer.tests.listener.ModelEntityPersistantLoad;
import me.biconsumer.tests.listener.ResourcePackApplyListener;
import me.biconsumer.tests.resourcepack.ResourceExportMethodFactory;
import org.bukkit.GameRule;
import org.bukkit.plugin.java.JavaPlugin;
import team.unnamed.hephaestus.adapt.AdaptionModuleFactory;
import team.unnamed.hephaestus.io.Streamable;
import team.unnamed.hephaestus.io.Streams;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.view.DefaultModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewAnimator;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.reader.blockbench.BlockbenchModelReader;
import team.unnamed.hephaestus.resourcepack.ResourceExporter;
import team.unnamed.hephaestus.resourcepack.ResourceExports;
import team.unnamed.hephaestus.resourcepack.ResourcePackInfo;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class EntityTest extends JavaPlugin {

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        ModelRegistry registry = new ModelRegistry();

        ModelViewAnimator animator = new DefaultModelViewAnimator(this);
        ModelViewRenderer renderer = AdaptionModuleFactory.create().createRenderer(animator);

        ModelReader modelReader = new BlockbenchModelReader();

        List<Model> models = new ArrayList<>();

        try {
            models.add(fromResource(modelReader, "redstone", "models/redstone.bbmodel"));
        } catch (Exception exception) {
            this.getLogger().log(Level.SEVERE, "Error while loading models", exception);
        }

        try {
            ResourceExporter<?> resourceExporter = ResourceExportMethodFactory.createExporter(
                    this.getConfig().getString("pack.namespace", "entity-test"),
                    new ResourcePackInfo(
                            this.getConfig().getInt("pack.format", 6),
                            this.getConfig().getString(
                                    "pack.description",
                                    "Models for entity testing"
                            ),
                            Streamable.ofResource(
                                    ResourceExportMethodFactory.class.getClassLoader(),
                                    "cubecraft.png"
                            )
                    ),
                    this.getDataFolder(),
                    this.getConfig().getString("pack.generate", "file:cubelet-generated.zip")
            );

            if (resourceExporter instanceof ResourceExports.HttpExporter) {
                JsonObject response = new JsonParser().parse(
                        resourceExporter.export(models).toString()
                ).getAsJsonObject();

                this.getServer().getPluginManager().registerEvents(new ResourcePackApplyListener(
                        this,
                        response.get("url").getAsString(),
                        Streams.getBytesFromHex(response.get("hash").getAsString())
                ), this);

            } else {
                resourceExporter.export(models);
            }

            models.forEach(model -> {
                getLogger().info("Registered model " + model.getName());
                model.discardResourcePackData();
                registry.register(model);
            });
        } catch (Exception exception) {
            this.getLogger().log(Level.SEVERE, "Error while loading models", exception);
        }

        ModelEntityRegistry entityRegistry = new ModelEntityRegistry();
        entityRegistry.register(
                "redstone",
                location -> new RedstoneMonsterEntity(
                        location,
                        registry.get("redstone"),
                        renderer,
                        this
                )
        );

        this.getServer().getWorlds().forEach(world -> {
            world.setStorm(false);
            world.setTime(1000);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_FIRE_TICK, false);
        });

        this.getServer().getPluginManager().registerEvents(new ModelEntityPersistantLoad(this, entityRegistry), this);
        this.getServer().getPluginManager().registerEvents(new DefaultListener(), this);
        this.getServer().getPluginManager().registerEvents(new ChatSpawnListener(this, entityRegistry), this);
    }

    private Model fromResource(ModelReader reader, String name, String resource) throws Exception {
        InputStream inputStream = this.getClassLoader().getResourceAsStream(resource);

        if (inputStream != null) {
            return reader.read(name, new InputStreamReader(inputStream));
        } else {
            throw new IllegalArgumentException("Resource '" + resource + "' does not exist!");
        }
    }
}