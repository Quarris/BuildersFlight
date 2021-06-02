package dev.quarris.buildersflight.network;

import dev.quarris.buildersflight.content.IFlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class FlightTargetRotationPacket {

    private UUID uuid;
    private float armRotation;
    private float legRotation;

    public FlightTargetRotationPacket(UUID uuid, float armRotation, float legRotation) {
        this.uuid = uuid;
        this.armRotation = armRotation;
        this.legRotation = legRotation;
    }

    public static void encode(FlightTargetRotationPacket packet, PacketBuffer buf) {
        buf.writeUniqueId(packet.uuid);
        buf.writeFloat(packet.armRotation);
        buf.writeFloat(packet.legRotation);
    }

    public static FlightTargetRotationPacket decode(PacketBuffer buf) {
        return new FlightTargetRotationPacket(buf.readUniqueId(), buf.readFloat(), buf.readFloat());
    }

    public static void handle(FlightTargetRotationPacket packet, Supplier<NetworkEvent.Context> ctx) {
        LogicalSide receivingSide = ctx.get().getDirection().getReceptionSide();
        if (receivingSide.isClient()) {
            ctx.get().enqueueWork(() -> {
                PlayerEntity player = Minecraft.getInstance().world.getPlayerByUuid(packet.uuid);
                if (player instanceof IFlighter) {
                    ((IFlighter) player).setTargetRotations(packet.armRotation, packet.legRotation);
                }
            });
        }

        if (receivingSide.isServer()) {
            ctx.get().enqueueWork(() -> {
                PacketHandler.sendToAllAround(packet, ctx.get().getSender(), 64);
            });
        }
        ctx.get().setPacketHandled(true);
    }
}
