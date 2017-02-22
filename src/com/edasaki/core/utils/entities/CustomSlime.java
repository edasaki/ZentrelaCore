package com.edasaki.core.utils.entities;

import net.minecraft.server.v1_10_R1.EntitySlime;
import net.minecraft.server.v1_10_R1.World;

public class CustomSlime extends EntitySlime {

    public CustomSlime(World world) {
        super(world);
        /*
         * try {
            Field gsa = PathfinderGoalSelector.class.getDeclaredField("b");
            gsa.setAccessible(true);
            gsa.set(this.goalSelector, Sets.newLinkedHashSet());
            gsa.set(this.targetSelector, Sets.newLinkedHashSet());
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        //                this.goalSelector.a(1, new PathfinderGoalFloat(this));
        //                this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        //                this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    public boolean isTag = false;

    @Override
    public void U() {
        if (isTag)
            return;
        super.U();
    }

}