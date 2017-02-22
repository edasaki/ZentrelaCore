package com.edasaki.core;

import java.util.HashMap;
import java.util.Map.Entry;

public class ManagerInstances {

    private static HashMap<Class<? extends AbstractManager>, AbstractManager> instances = new HashMap<Class<? extends AbstractManager>, AbstractManager>();
    private static HashMap<Class<? extends AbstractManager>, SakiCore> associatedPlugin = new HashMap<Class<? extends AbstractManager>, SakiCore>();

    protected static final void registerManager(Class<? extends AbstractManager> clazz, AbstractManager instance, SakiCore pl) throws Exception {
        if (instances.containsKey(clazz))
            throw new Exception("Duplicate manager " + clazz + " " + instance);
        instances.put(clazz, instance);
        associatedPlugin.put(clazz, pl);
        //        System.out.println("Registered " + clazz + " to " + instance + " for " + pl);
    }

    protected static final void unloadManager(Class<? extends AbstractManager> clazz) {
        if (instances.containsKey(clazz)) {
            AbstractManager manager = instances.remove(clazz);
            SakiCore pl = associatedPlugin.remove(clazz);
            manager.unload(pl);
            System.out.println("UNLOADED " + clazz + " from " + pl.getClass().getSimpleName() + ".");
        } else {
            System.out.println("ERROR: Could not find " + clazz + " to unload.");
        }
    }

    @SuppressWarnings("unchecked")
    protected static final <T> T getInstance(Class<T> clazz) {
        AbstractManager instance = instances.get(clazz);
        if (instance == null)
            return null;
        if (clazz.isInstance(instance))
            return (T) instance;
        return null;
    }

    public static void debug() {
        System.out.println(instances);
        System.out.println(associatedPlugin);
    }

    protected static final void cleanup() {
        for (Entry<Class<? extends AbstractManager>, AbstractManager> e : instances.entrySet()) {
            SakiCore pl = associatedPlugin.get(e.getKey());
            e.getValue().unload(pl);
        }
        instances.clear();
        associatedPlugin.clear();
    }

}
