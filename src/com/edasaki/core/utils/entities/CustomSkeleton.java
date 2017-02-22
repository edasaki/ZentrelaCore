package com.edasaki.core.utils.entities;

import java.lang.reflect.Field;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_10_R1.Block;
import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.Entity;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.EntityLiving;
import net.minecraft.server.v1_10_R1.EntitySkeleton;
import net.minecraft.server.v1_10_R1.EnumSkeletonType;
import net.minecraft.server.v1_10_R1.Material;
import net.minecraft.server.v1_10_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_10_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_10_R1.SoundEffect;
import net.minecraft.server.v1_10_R1.SoundEffects;
import net.minecraft.server.v1_10_R1.World;

public class CustomSkeleton extends EntitySkeleton implements Leashable {

    public CustomSkeleton(World world) {
        super(world);
        try {
            Field gsa = PathfinderGoalSelector.class.getDeclaredField("b");
            gsa.setAccessible(true);
            gsa.set(this.goalSelector, Sets.newLinkedHashSet());
            gsa.set(this.targetSelector, Sets.newLinkedHashSet());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    public void allowWalk(int leash) {
        this.goalSelector.a(5, new PathfinderGoalMobWander(this, 1.0D, 40, leash));
    }

    @Override
    public boolean a(Material material) {
        return false;
    }

    @Override
    public void stopRiding() {
        return;
    }

    @Override
    public boolean B(Entity entity) {
        boolean r = super.B(entity);
        if (this.getSkeletonType() == EnumSkeletonType.WITHER && entity instanceof EntityLiving) {
            if (entity.getBukkitEntity() instanceof Player) {
                ((Player) (entity.getBukkitEntity())).removePotionEffect(PotionEffectType.WITHER);
            }
        }
        return r;
    }

    public void makeWither() {
        setSkeletonType(EnumSkeletonType.WITHER);
    }

    public void makeStray() {
        setSkeletonType(EnumSkeletonType.STRAY);
    }

    @Override
    protected SoundEffect G() {
        return null;
    }

    @Override
    protected SoundEffect bV() {
        return SoundEffects.ENTITY_PLAYER_HURT;
    }

    @Override
    protected SoundEffect bW() {
        return SoundEffects.gF;
    }

    @Override
    protected void a(BlockPosition blockposition, Block block) {
        //makeSound("mob.skeleton.step", 0.15F, 1.0F);
    }

}