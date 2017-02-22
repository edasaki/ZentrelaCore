package com.edasaki.core.utils.entities;

import net.minecraft.server.v1_10_R1.EntityCreature;
import net.minecraft.server.v1_10_R1.PathfinderGoal;
import net.minecraft.server.v1_10_R1.RandomPositionGenerator;
import net.minecraft.server.v1_10_R1.Vec3D;

public class PathfinderGoalNPCWander extends PathfinderGoal {
    private EntityCreature entity;
    private double destX;
    private double destY;
    private double destZ;
    private double walkSpeed;
    private int frequency;
    private boolean forceMoveOnce;
    
    private int leash;
    
    private double oX, oY, oZ;

    public PathfinderGoalNPCWander(EntityCreature entity, double walkSpeed) {
        this(entity, walkSpeed, 120, 20);
    }

    public PathfinderGoalNPCWander(EntityCreature entity, double walkSpeed, int frequency, int leash) {
        this.entity = entity;
        this.walkSpeed = walkSpeed;
        this.frequency = frequency;
        this.oX = entity.locX;
        this.oY = entity.locY;
        this.oZ = entity.locZ;
        this.leash = leash;
        a(1);
    }

    public boolean a() {
        if (!this.forceMoveOnce) {
            if (this.entity.bK() >= 100) { // player nearby
                return false;
            }
            if (this.entity.getRandom().nextInt(this.frequency) != 0) {
                return false;
            }
        }
        Vec3D localVec3D = RandomPositionGenerator.a(this.entity, 10, 10);
        if (localVec3D == null) {
            return false;
        }
        this.destX = localVec3D.x;
        this.destY = localVec3D.y;
        this.destZ = localVec3D.z;
        if(Math.abs(this.destX - oX) > leash)
            this.destX = oX;
        if(Math.abs(this.destY - oY) > leash)
            this.destY = oY;
        if(Math.abs(this.destZ - oZ) > leash)
            this.destZ = oZ;
        this.forceMoveOnce = false;
        return true;
    }

    public boolean b() {
        return !this.entity.getNavigation().n();
    }

    public void c() {
        this.entity.getNavigation().a(this.destX, this.destY, this.destZ, this.walkSpeed);
    }

    public void f() {
        this.forceMoveOnce = true;
    }

    public void setTimeBetweenMovement(int paramInt) {
        this.frequency = paramInt;
    }
}
