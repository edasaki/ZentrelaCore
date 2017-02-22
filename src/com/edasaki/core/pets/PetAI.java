package com.edasaki.core.pets;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.edasaki.core.utils.RMath;
import com.edasaki.core.utils.RScheduler;
import com.edasaki.core.utils.RTicks;

import net.minecraft.server.v1_10_R1.AttributeInstance;
import net.minecraft.server.v1_10_R1.EntityInsentient;
import net.minecraft.server.v1_10_R1.EntityLiving;
import net.minecraft.server.v1_10_R1.GenericAttributes;

public class PetAI {

    private Player owner;
    private LivingEntity le;
    private PetType type;

    private boolean started = false;

    public PetAI(PetType type, Player owner) {
        this.type = type;
        this.owner = owner;
    }

    public void start() {
        if (started)
            return;
        started = true;
        System.out.println(this + " starting pet AI for " + owner.getName() + "'s " + type);
        RScheduler.schedule(PetManager.plugin, () -> {
            le = type.spawn(owner, owner.getLocation());
            PetManager.spawnedPets.put(le.getUniqueId(), owner.getUniqueId());
            RScheduler.schedule(PetManager.plugin, () -> {
                tick();
            });
        }, RTicks.seconds(1));
    }

    public void tick() {
        if (owner != null && owner.isOnline()) {
            RScheduler.schedule(PetManager.plugin, () -> {
                tick();
            }, 10);
        } else {
            halt();
            return;
        }
        double diff = RMath.flatDistance(le.getLocation(), owner.getLocation());
        if (diff < 3.7)
            return;
        if (diff > 35 || Math.abs(le.getLocation().getY() - owner.getLocation().getY()) > 10)
            le.teleport(owner);
        Location loc = owner.getLocation();
        //        Vector v = owner.getLocation().getDirection();
        //        loc.add(v.normalize().multiply(-1.5));
        //                loc.add(0, -5, 0);
        AttributeInstance ati = ((EntityLiving) (((CraftLivingEntity) le).getHandle())).getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        double old = ati.getValue();
        diff = RMath.flatDistance(loc, le.getLocation());
        if (old < diff) {
            ati.setValue(diff + 10);
        }
        ((EntityInsentient) ((CraftLivingEntity) le).getHandle()).getNavigation().a(loc.getX(), loc.getY(), loc.getZ(), 1.1);
        ati.setValue(old);
    }

    public void halt() {
        if (PetManager.spawnedPets != null && le != null)
            PetManager.spawnedPets.remove(le.getUniqueId());
        if (le != null)
            le.remove();
        le = null;
        owner = null;
    }
}
