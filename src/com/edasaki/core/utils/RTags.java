package com.edasaki.core.utils;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

import com.edasaki.core.SakiCore;

public class RTags {

    public static ArmorStand makeFloatingText(String name, Location loc, double xzOffset, double yMin, double yMax, double durationSec) {
        loc.add(-xzOffset / 2 + (Math.random() * (xzOffset)), (Math.random() * (yMax - yMin)) + yMin, -xzOffset / 2 + (Math.random() * (xzOffset)));
        final ArmorStand as = (ArmorStand) REntities.createLivingEntity(CustomArmorStand.class, loc);
        as.setVisible(false);
        as.setSmall(true);
        as.setMarker(true);
        as.setGravity(false);
        as.setArms(false);
        as.setBasePlate(false);
        as.setCanPickupItems(false);
        as.setCustomName(name);
        as.setCustomNameVisible(true);
        as.setRemoveWhenFarAway(false);
        RScheduler.schedule(SakiCore.plugin, new Runnable() {
            public void run() {
                if (as != null && as.isValid())
                    as.remove();
            }
        }, RTicks.seconds(durationSec));
        return as;
    }

    public static ArmorStand makeStand(String name, Location loc, boolean marker) {
        ArmorStand as = (ArmorStand) REntities.createLivingEntity(CustomArmorStand.class, loc);
        as.setVisible(false);
        as.setSmall(true);
        if (marker)
            as.setMarker(true);
        as.setGravity(false);
        as.setArms(false);
        as.setBasePlate(false);
        as.setCanPickupItems(false);
        as.setCustomName(name);
        as.setCustomNameVisible(true);
        as.setRemoveWhenFarAway(false);
        return as;
    }
    //
    //    public static Slime makeSlime(Location loc) {
    //        return makeSlime(1, loc);
    //    }
    //
    //    public static Slime makeSlime(int size, Location loc) {
    //        Slime slime = (Slime) REntities.createLivingEntity(CustomSlime.class, loc);
    //        ((CustomSlime) ((CraftSlime) slime).getHandle()).isTag = true;
    //        slime.setSize(size);
    //        ((CustomSlime) ((CraftSlime) slime).getHandle()).setInvisible(true);
    //        slime.setRemoveWhenFarAway(false);
    //        return slime;
    //    }
}
