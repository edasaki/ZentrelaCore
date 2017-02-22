package com.edasaki.core.utils.entities;

import java.lang.reflect.Field;

import com.google.common.collect.Sets;

import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.EntityBlaze;
import net.minecraft.server.v1_10_R1.EntityHuman;
import net.minecraft.server.v1_10_R1.EntityLiving;
import net.minecraft.server.v1_10_R1.EntitySmallFireball;
import net.minecraft.server.v1_10_R1.MathHelper;
import net.minecraft.server.v1_10_R1.PathfinderGoal;
import net.minecraft.server.v1_10_R1.PathfinderGoalFloat;
import net.minecraft.server.v1_10_R1.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_10_R1.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_10_R1.PathfinderGoalSelector;
import net.minecraft.server.v1_10_R1.World;

public class CustomBlaze extends EntityBlaze implements Leashable {

    public CustomBlaze(World world) {
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

        this.goalSelector.a(4, new PathfinderGoalBlazeFireball(this));
    }

    @Override
    public void allowWalk(int leash) {
        this.goalSelector.a(5, new PathfinderGoalMobWander(this, 1.0D, 40, leash));
    }

    static class PathfinderGoalBlazeFireball extends PathfinderGoal {
        private final EntityBlaze a;
        private int b;
        private int c;

        public PathfinderGoalBlazeFireball(EntityBlaze paramEntityBlaze) {
            this.a = paramEntityBlaze;
            a(3);
        }

        public boolean a() {
            EntityLiving localEntityLiving = this.a.getGoalTarget();
            if ((localEntityLiving == null) || (!localEntityLiving.isAlive())) {
                return false;
            }
            return true;
        }

        public void c() {
            this.b = 0;
        }

        public void d() {
            this.a.a(false);
        }

        public void e() {
            this.c -= 1;

            EntityLiving localEntityLiving = this.a.getGoalTarget();

            double d1 = this.a.h(localEntityLiving);
            if (d1 < 4.0D) {
                if (this.c <= 0) {
                    this.c = 20;
                    this.a.B(localEntityLiving);
                }
                this.a.getControllerMove().a(localEntityLiving.locX, localEntityLiving.locY, localEntityLiving.locZ, 1.0D);
            } else if (d1 < 256.0D) {
                double d2 = localEntityLiving.locX - this.a.locX;
                double d3 = localEntityLiving.getBoundingBox().b + localEntityLiving.length / 2.0F - (this.a.locY + this.a.length / 2.0F);
                double d4 = localEntityLiving.locZ - this.a.locZ;
                if (this.c <= 0) {
                    this.b += 1;
                    if (this.b == 1) {
                        this.c = 60;
                        this.a.a(true);
                    } else if (this.b <= 4) {
                        this.c = 6;
                    } else {
                        this.c = 100;
                        this.b = 0;
                        this.a.a(false);
                    }
                    if (this.b > 1) {
                        float f = MathHelper.c(MathHelper.sqrt(d1)) * 0.05F;

                        this.a.world.a(null, 1018, new BlockPosition((int) this.a.locX, (int) this.a.locY, (int) this.a.locZ), 0);
                        for (int i = 0; i < 1; i++) {
                            EntitySmallFireball localEntitySmallFireball = new EntitySmallFireball(this.a.world, this.a, d2 + this.a.getRandom().nextGaussian() * f, d3, d4 + this.a.getRandom().nextGaussian() * f);
                            localEntitySmallFireball.locY = (this.a.locY + this.a.length / 2.0F + 0.5D);
                            this.a.world.addEntity(localEntitySmallFireball);
                        }
                    }
                }
                this.a.getControllerLook().a(localEntityLiving, 10.0F, 10.0F);
            } else {
                this.a.getNavigation().o();
                this.a.getControllerMove().a(localEntityLiving.locX, localEntityLiving.locY, localEntityLiving.locZ, 1.0D);
            }
            super.e();
        }
    }
}