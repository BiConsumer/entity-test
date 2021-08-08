package me.biconsumer.tests.entities;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.animation.ModelAnimationQueue;
import team.unnamed.hephaestus.model.view.ModelView;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ModelEntityTracker {

    private final static int RENDER_RANGE = 50;

    private final Entity entity;
    private final Model model;
    private final ModelViewRenderer renderer;
    private final double hip;

    private final ModelAnimationQueue animationQueue = new ModelAnimationQueue();
    private final Map<UUID, ModelView> playerViews = new HashMap<>();

    private Color color = Color.WHITE;

    public ModelEntityTracker(
            Entity entity,
            Model model,
            ModelViewRenderer renderer,
            double hip
    ) {
        this.entity = entity;
        this.model = model;
        this.renderer = renderer;
        this.hip = hip;
    }

    public void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ModelView modelView = playerViews.get(player.getUniqueId());

            if (modelView == null && player.getLocation().distance(entity.getLocation()) <= RENDER_RANGE) {
                modelView = renderer.render(
                        player,
                        model,
                        entity.getLocation().clone().add(0, hip, 0),
                        animationQueue
                );

                modelView.animate();
                modelView.colorize(color);
                playerViews.put(player.getUniqueId(), modelView);
            } else if (modelView != null && player.getLocation().distance(entity.getLocation()) > RENDER_RANGE) {
                modelView.hide();
                playerViews.remove(player.getUniqueId());
            }
        }

        for (UUID uuid : this.playerViews.keySet()) {
            if (Bukkit.getPlayer(uuid) == null) {
                playerViews.get(uuid).hide();
                playerViews.remove(uuid);
            }
        }
    }

    public void movementTick(Location location) {
        for (ModelView modelView : this.playerViews.values()) {
            modelView.teleport(location.clone().add(0, hip, 0));
        }
    }

    public void die() {
        for (ModelView view : this.playerViews.values()) {
            view.hide();
        }
    }

    public void colorize(Color color) {
        this.color = color;
        for (ModelView view : this.playerViews.values()) {
            view.colorize(color);
        }
    }

    public void playAnimation(String animationName, int priority, int transitionTicks) {
        this.playAnimation(this.model.getAnimations().get(animationName), priority, transitionTicks);
    }

    public void playAnimation(ModelAnimation animation, int priority, int transitionTicks) {
        this.animationQueue.pushAnimation(animation, priority, transitionTicks);
    }

    public void stopAnimation(String animationName) {
        this.animationQueue.removeAnimation(animationName);
    }

    public boolean isAnimationPlaying(String animationName) {
        return this.animationQueue.getQueuedAnimations()
                .stream()
                .anyMatch(animation -> animation.getName().equals(animationName));
    }
}