package me.biconsumer.tests.entities;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import team.unnamed.hephaestus.model.Model;
import team.unnamed.hephaestus.model.view.ModelViewRenderer;

import javax.annotation.Nullable;
import java.lang.reflect.Field;

public abstract class AbstractModelEntity extends EntityCreature {

    private final String modelName;

    protected final Plugin plugin;
    protected final ModelEntityTracker tracker;
    protected final Hitbox hitbox;

    private static Field ENTITY_SIZE_FIELD;
    private static Field ENTITY_HEIGHT_FIELD;

    static {
        try {
            ENTITY_SIZE_FIELD = Entity.class.getDeclaredField("aW");
            ENTITY_SIZE_FIELD.setAccessible(true);

            ENTITY_HEIGHT_FIELD = Entity.class.getDeclaredField("aX");
            ENTITY_HEIGHT_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public AbstractModelEntity(
            EntityTypes<? extends EntityCreature> type,
            World world,
            Model model,
            Plugin plugin,
            ModelViewRenderer renderer,
            Hitbox hitbox,
            double hip
    ) {
        super(type, world);
        this.plugin = plugin;
        this.hitbox = hitbox;
        this.modelName = model.getName();
        this.tracker = new ModelEntityTracker(this.getBukkitEntity(), model, renderer, hip);
    }

    public ModelEntityTracker getTracker() {
        return tracker;
    }

    @Override
    public void tick() {
        super.tick();
        tracker.tick();

        try {
            this.a(new AxisAlignedBB(
                    this.locX() - (this.hitbox.getWidth() / 2.0F),
                    this.locY(),
                    this.locZ() - (this.hitbox.getWidth() / 2.0F),
                    this.locX() + (this.hitbox.getWidth() / 2.0F),
                    this.locY() + this.hitbox.getHeight(),
                    this.locZ() + (this.hitbox.getWidth() / 2.0F)
            ));

            ENTITY_SIZE_FIELD.set(this, new EntitySize(this.hitbox.getWidth(), this.hitbox.getHeight(), false));
            ENTITY_HEIGHT_FIELD.set(this, this.hitbox.getEye());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        PersistentDataContainer dataContainer = this.getBukkitEntity().getPersistentDataContainer();
        dataContainer.set(new NamespacedKey(plugin, "model_entity"), PersistentDataType.STRING, modelName);

        return super.save(nbttagcompound);
    }

    @Override
    public void movementTick() {
        super.movementTick();

        tracker.movementTick(new Location(
                this.getWorld().getWorld(),
                locX(),
                locY(),
                locZ(),
                getBukkitYaw(),
                getXRot()
        ));
    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
        this.tracker.die();
    }

    @Override
    public void setLastDamager(@Nullable EntityLiving entityliving) {
        super.setLastDamager(entityliving);
        this.tracker.colorize(Color.fromRGB(240, 117, 105));
        Bukkit.getScheduler().runTaskLater(plugin, () -> this.tracker.colorize(Color.WHITE), 6);
    }
}