package dev.quarris.buildersflight.network;

import dev.quarris.buildersflight.BuildersFlight;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.Optional;

public class PacketHandler {

    private static String VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            BuildersFlight.res("channel"),
            () -> VERSION, VERSION::equals, VERSION::equals
            );

    public static void init() {
        CHANNEL.registerMessage(0, FlightStatePacket.class, FlightStatePacket::encode, FlightStatePacket::decode, FlightStatePacket::handle);
        CHANNEL.registerMessage(1, FlightStatePacket.class, FlightStatePacket::encode, FlightStatePacket::decode, FlightStatePacket::handle);
    }

    public static void sendTo(Object packet, ServerPlayerEntity player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToServer(Object packet) {
        CHANNEL.sendToServer(packet);
    }
}
