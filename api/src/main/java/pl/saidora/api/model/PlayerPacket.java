package pl.saidora.api.model;

import org.bukkit.entity.Player;

import java.util.Optional;

public interface PlayerPacket {

    Player getPlayer();

    Optional<Object> getEntityPlayer();

    boolean sendPacket(PacketReader packetReader);

    void addPacket(PacketReader packetReader);

    void send();

}
