package com.edasaki.core.utils.entities;

import java.lang.reflect.Field;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_10_R1.EntityChicken;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_10_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_10_R1.World;

public class CustomChicken extends EntityChicken implements Leashable {

    public CustomChicken(World world) {
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
        this.goalSelector.a(1, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(2, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        //        this.goalSelector.a(3, new PathfinderGoalRandomStroll(this, 1.0D, 40));
        // this.bs = (this.random.nextInt(6000) + 6000); sets egg laying timer, max value stops egg laying
        // bb in 1.9 bD in 1.10 MUST be updated every patch
        this.bD = Integer.MAX_VALUE;
    }

    @Override
    public void allowWalk(int leash) {
        this.goalSelector.a(5, new PathfinderGoalMobWander(this, 1.0D, 40, leash));
    }

    @Override
    public void stopRiding() {
        return;
    }

}