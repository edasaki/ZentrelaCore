package com.edasaki.core.utils.entities;

import java.lang.reflect.Field;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_10_R1.Block;
import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.EntityWitch;
import net.minecraft.server.v1_10_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_10_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_10_R1.SoundEffect;
import net.minecraft.server.v1_10_R1.SoundEffects;
import net.minecraft.server.v1_10_R1.World;

public class CustomWitch extends EntityWitch implements Leashable {

    public CustomWitch(World world) {
        super(world);
        try {
            Field gsa = PathfinderGoalSelector.class.getDeclaredField("b");
            gsa.setAccessible(true);
            gsa.set(this.goalSelector, Sets.newLinkedHashSet());
            gsa.set(this.targetSelector, Sets.newLinkedHashSet());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
//        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D, 40));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    public void allowWalk(int leash) {
        this.goalSelector.a(5, new PathfinderGoalMobWander(this, 1.0D, 40, leash));
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