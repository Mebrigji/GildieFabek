package pl.saidora.api.model;

import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;

import java.util.Optional;

public interface PlayerPacket {

    Player getPlayer();

    Optional<Object> getEntityPlayer();

    boolean sendPacket(PacketReader packetReader);

    void addPacket(PacketReader packetReader);

    GameProfile getGameProfile();

    void send();

}
