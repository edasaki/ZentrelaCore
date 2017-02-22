package com.edasaki.core.utils;

import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_10_R1.EntityPlayer;
import net.minecraft.server.v1_10_R1.IChatBaseComponent;
import net.minecraft.server.v1_10_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_10_R1.PacketPlayOutChat;

public class RJson {

    public static void playOut(Player p, String JsonText) {
        EntityPlayer entity = ((CraftPlayer) p).getHandle();
        IChatBaseComponent component = ChatSerializer.a(JsonText);
        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(component);
        entity.playerConnection.sendPacket(packetPlayOutChat);
    }

}