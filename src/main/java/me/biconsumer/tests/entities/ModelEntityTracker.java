package me.biconsumer.tests.entities;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.animation.ModelAnimation;
import team.unnamed.hephaestus.model.view.ModelView;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ModelEntityTracker {

    private final static int RENDER_RANGE = 50;

    private final Entity entity;
    private final Model model;
    private final double hip;

    private final ModelView modelView;

    private final Set<UUID> renderedPlayers = new HashSet<>();

    public ModelEntityTracker(
            Entity entity,
            Model model,
            ModelViewRenderer renderer,
            double hip
    ) {
        this.entity = entity;
        this.model = model;
        this.hip = hip;

        this.modelView = renderer.render(model, entity.getLocation());
    }

    public void tick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            boolean isRendered = renderedPlayers.contains(player.getUniqueId());

            if (!isRendered && player.getLocation().distance(entity.getLocation()) <= RENDER_RANGE) {
                modelView.addViewer(player);
                renderedPlayers.add(player.getUniqueId());
            } else if (isRendered && player.getLocation().distance(entity.getLocation()) > RENDER_RANGE) {
                modelView.removeViewer(player);
                renderedPlayers.remove(player.getUniqueId());
            }
        }

        this.renderedPlayers.removeIf(uuid -> Bukkit.getPlayer(uuid) == null);
    }

    public void movementTick(Location location) {
        modelView.teleport(location.clone().add(0, hip, 0));
    }

    public void die() {
        modelView.hide();
    }

    public void colorize(Color color) {
        modelView.colorize(color);
    }

    public void playAnimation(String animationName, int priority, int transitionTicks) {
        this.playAnimation(this.model.getAnimations().get(animationName), priority, transitionTicks);
    }

    public void playAnimation(ModelAnimation animation, int priority, int transitionTicks) {
        this.modelView.playAnimation(animation, priority, transitionTicks);
    }

    public void stopAnimation(String animationName) {
        this.modelView.stopAnimation(animationName);
    }

    public boolean isAnimationPlaying(String animationName) {
        return this.modelView.getAnimationQueue()
                .getQueuedAnimations()
                .stream()
                .anyMatch(animation -> animation.getName().equals(animationName));
    }
}