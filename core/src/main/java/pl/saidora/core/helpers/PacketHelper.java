package pl.saidora.core.helpers;

import com.mojang.authlib.GameProfile;
import org.bukkit.entity.Player;
import pl.saidora.api.functions.LambdaBypass;
import pl.saidora.api.helpers.ReflectionHelper;
import pl.saidora.api.model.PacketReader;
import pl.saidora.api.model.PlayerPacket;
import pl.saidora.core.model.impl.User;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PacketHelper implements PlayerPacket {

    private static final Class<?> EntityPlayerClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "EntityPlayer");
    private static final Class<?> PacketClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "Packet");
    private static final Class<?> CraftPlayerClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.CRAFT_BUKKIT, "entity.CraftPlayer");
    private static final Class<?> PlayerConnectionClass = ReflectionHelper.getClass(ReflectionHelper.PATH_FINDER.MINECRAFT, "PlayerConnection");

    private static final Optional<Field> PlayerConnection_Field = ReflectionHelper.getField(EntityPlayerClass, "playerConnection");

    private static final Optional<Method> MGAMEPROFILE = ReflectionHelper.getMethod(CraftPlayerClass, "getProfile");
    private static final Optional<Method> MGHANDLE = ReflectionHelper.getMethod(CraftPlayerClass, "getHandle");
    private static final Optional<Method> MSPACKET = ReflectionHelper.getMethod(PlayerConnectionClass, "sendPacket", PacketClass);

    protected static Object getHandle(Player player){
        Object cp = CraftPlayerClass.cast(player);
        return LambdaBypass.getOptional(MGHANDLE, (o) -> ReflectionHelper.invoke(o, cp)).getObject();
    }


    private final User user;
    private final List<PacketReader> packetReaders = new ArrayList<>();

    public PacketHelper(User user){
        this.user = user;
    }

    @Override
    public Player getPlayer() {
        return user.asPlayer().orElse(null);
    }

    @Override
    public Optional<Object> getEntityPlayer() {
        return getPlayer() != null ? Optional.ofNullable(getHandle(getPlayer())) : Optional.empty();
    }

    @Override
    public boolean sendPacket(PacketReader packetReader) {
        PlayerConnection_Field.ifPresent(field -> getEntityPlayer().ifPresent(entity -> {
            Object pc = ReflectionHelper.getValue(field, entity).get();
            MSPACKET.ifPresent(method -> ReflectionHelper.invoke(method, pc, packetReader.getPacket()));
        }));
        return true;
    }

    @Override
    public void addPacket(PacketReader packetReader) {
        this.packetReaders.add(packetReader);
    }

    @Override
    public GameProfile getGameProfile() {
        return (GameProfile) ReflectionHelper.invoke(MGAMEPROFILE.get(), CraftPlayerClass.cast(getPlayer()));
    }

    @Override
    public void send() {
        new ArrayList<>(packetReaders).removeIf(this::send);
    }

    private boolean send(PacketReader packetReader){
        packetReaders.remove(packetReader);
        sendPacket(packetReader);
        return true;
    }
}
