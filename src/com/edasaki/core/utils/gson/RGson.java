package com.edasaki.core.utils.gson;

import java.util.ArrayList;

import org.bukkit.Location;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RGson {

    private static ArrayList<GsonBuilderModifiers> list = new ArrayList<GsonBuilderModifiers>();

    private static GsonBuilder builder, conciseBuilder;

    public static void registerModifier(GsonBuilderModifiers mod) {
        list.add(mod);
    }

    public static Gson getGson() {
        if (builder != null)
            return builder.create();
        builder = new GsonBuilder();
        builder.serializeNulls();
        builder.setPrettyPrinting();
        builder.enableComplexMapKeySerialization();
        builder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
        builder.registerTypeAdapter(Location.class, new LocationAdapter());
        for (GsonBuilderModifiers gbm : list)
            gbm.modify(builder);
        builder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
        builder.registerTypeAdapterFactory(new ClassTypeAdapterFactory());
        return builder.create();
    }

    public static Gson getConciseGson() {
        if (conciseBuilder != null)
            return conciseBuilder.create();
        conciseBuilder = new GsonBuilder();
        conciseBuilder.serializeNulls();
        conciseBuilder.enableComplexMapKeySerialization();
        conciseBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
        conciseBuilder.registerTypeAdapter(Location.class, new LocationAdapter());
        for (GsonBuilderModifiers gbm : list)
            gbm.modify(conciseBuilder);
        conciseBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
        conciseBuilder.registerTypeAdapterFactory(new ClassTypeAdapterFactory());
        return conciseBuilder.create();
    }

}
