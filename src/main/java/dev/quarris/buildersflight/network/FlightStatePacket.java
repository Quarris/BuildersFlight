package dev.quarris.buildersflight.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class FlightStatePacket {

    private boolean flying;

    public FlightStatePacket(boolean flying) {
        this.flying = flying;
    }

    public static void encode(FlightStatePacket packet, PacketBuffer buffer) {
        buffer.writeBoolean(packet.flying);
    }

    public static FlightStatePacket decode(PacketBuffer buffer) {
        return new FlightStatePacket(buffer.readBoolean());
    }

    public static void handle(FlightStatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        LogicalSide receivingSide = ctx.get().getDirection().getReceptionSide();
        ctx.get().enqueueWork(() -> {
            if (receivingSide.isClient()) {
                ClientPlayerEntity player = Minecraft.getInstance().player;
                player.getPersistentData().putBoolean("flight", packet.flying);
            }
            if (receivingSide.isServer()) {
                ServerPlayerEntity player = ctx.get().getSender();
                CompoundNBT data = player.getPersistentData();
                if (data.contains("flight")) {
                    boolean wasFlying = data.getBoolean("flying");
                    if (wasFlying && !packet.flying) {
                        player.setNoGravity(false);
                    } else if (!wasFlying && packet.flying) {
                        player.setNoGravity(true);
                    }
                }
                data.putBoolean("flight", packet.flying);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
