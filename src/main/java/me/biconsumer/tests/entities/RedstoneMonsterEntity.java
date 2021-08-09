package me.biconsumer.tests.entities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.goal.PathfinderGoalFloat;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

import javax.annotation.Nullable;

public class RedstoneMonsterEntity extends AbstractModelEntity {

    //TODO: FIND CORRECT VALUE FOR MOVEMENT SPEED
    private final static double MOVEMENT_SPEED = 0.25;

    private boolean awakened = false;
    private boolean awakening = false;

    public RedstoneMonsterEntity(
            World world,
            Model model,
            ModelViewRenderer renderer,
            Plugin plugin
    ) {
        super(
                EntityTypes.ak,
                world,
                model,
                plugin,
                renderer,
                new Hitbox(
                        4F,
                        6.6F,
                        4.2F
                ),
                -0.73
        );
        this.tracker.playAnimation("sleep", 1, 0);
        this.collides = false;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        PersistentDataContainer dataContainer = this.getBukkitEntity().getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(plugin, "redstone_awakened"), PersistentDataType.INTEGER, this.awakened ? 1 : 0);

        return super.save(nbttagcompound);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);

        PersistentDataContainer dataContainer = this.getBukkitEntity().getPersistentDataContainer();
        if (dataContainer.has(new NamespacedKey(plugin, "redstone_awakened"), PersistentDataType.INTEGER)) {
            this.awakened = dataContainer.get(new NamespacedKey(plugin, "redstone_awakened"), PersistentDataType.INTEGER) == 1;

            if (this.awakened) {
                this.tracker.stopAnimation("sleep");
            }
        }
    }

    @Override
    protected void initPathfinder() {
        this.bP.a(0, new PathfinderGoalFloat(this));
    }

    @Override
    public void movementTick() {
        if (awakened && !awakening) {
            super.movementTick();
        }
    }

    @Override
    public void tick() {

        super.tick();
        this.setInvisible(true);

        if (awakened && !awakening) {
            Vec3D motion = getMot();

            double x = Math.abs(motion.getX());
            double y = Math.abs(motion.getY());
            double z = Math.abs(motion.getZ());

            if (
                    !this.tracker.isAnimationPlaying("walk")
                            && (x >= MOVEMENT_SPEED || y >= MOVEMENT_SPEED || z >= MOVEMENT_SPEED)
            ) {
                this.tracker.stopAnimation("idle");
                this.tracker.playAnimation("walk", 2, 10);
            } else if (
                    !this.tracker.isAnimationPlaying("idle")
                            && (x < MOVEMENT_SPEED || y < MOVEMENT_SPEED || z < MOVEMENT_SPEED)
            ) {
                this.tracker.stopAnimation("walk");
                this.tracker.playAnimation("idle", 2, 10);
            }
        }
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (awakened && !awakening) {
            return super.damageEntity(damagesource, f);
        }

        if (!this.awakened) {
            this.awakening = true;
            this.awakened = true;
            this.tracker.stopAnimation("sleep");
            this.tracker.playAnimation("awaken", 3, 5);

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                this.awakening = false;
                this.collides = true;
            }, 54L);
        }

        return false;
    }
}