package dev.quarris.buildersflight.network;

import dev.quarris.buildersflight.content.IFlighter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class FlightStatePacket {

    private UUID uuid;
    private boolean flying;
    private boolean syncFlying;

    public FlightStatePacket(UUID uuid) {
        this.uuid = uuid;
        this.syncFlying = true;
    }

    public FlightStatePacket(UUID uuid, boolean flying) {
        this.uuid = uuid;
        this.flying = flying;
        this.syncFlying = false;
    }

    public static void encode(FlightStatePacket packet, PacketBuffer buffer) {
        buffer.writeBoolean(packet.syncFlying);
        buffer.writeUniqueId(packet.uuid);
        buffer.writeBoolean(packet.flying);
    }

    public static FlightStatePacket decode(PacketBuffer buffer) {
        boolean sync = buffer.readBoolean();
        UUID uuid = buffer.readUniqueId();
        boolean isFlying = buffer.readBoolean();
        if (sync) {
            return new FlightStatePacket(uuid);
        }
        return new FlightStatePacket(uuid, isFlying);
    }

    public static void handle(FlightStatePacket packet, Supplier<NetworkEvent.Context> ctx) {
        LogicalSide receivingSide = ctx.get().getDirection().getReceptionSide();
        ctx.get().enqueueWork(() -> {
            if (receivingSide.isClient()) {
                PlayerEntity player = Minecraft.getInstance().world.getPlayerByUuid(packet.uuid);
                if (player instanceof IFlighter) {
                    ((IFlighter) player).setFlying(packet.flying);
                }
            }
            if (receivingSide.isServer()) {
                ServerPlayerEntity sender = ctx.get().getSender();
                ServerPlayerEntity player = sender.server.getPlayerList().getPlayerByUUID(packet.uuid);
                if (!(player instanceof IFlighter))
                    return;

                if (packet.syncFlying) {
                    PacketHandler.sendTo(new FlightStatePacket(packet.uuid, ((IFlighter) player).isFlying()), sender);
                    return;
                }

                if (packet.uuid.equals(sender.getUniqueID())) {
                    ((IFlighter) sender).setFlying(packet.flying);
                    PacketHandler.sendToAllAround(packet, player, 64);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
