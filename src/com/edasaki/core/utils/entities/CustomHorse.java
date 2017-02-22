package com.edasaki.core.utils.entities;

import java.lang.reflect.Field;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_10_R1.EntityHorse;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.Material;
import net.minecraft.server.v1_10_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_10_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_10_R1.World;

public class CustomHorse extends EntityHorse implements Leashable {

    public CustomHorse(World world) {
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
    public boolean a(Material material) {
        return false;
    }

    @Override
    public void stopRiding() {
        return;
    }

    /*
     * Code in EntityHorse looks like this: 
       public void dJ() {
        setStanding();
        SoundEffect soundeffect = dA();
        if (soundeffect != null) {
        a(soundeffect, ch(), ci());
       }
     */
    public void rearUp() {
        this.dJ();
    }

}