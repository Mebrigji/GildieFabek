package pl.saidora.core.model.impl.guild;

import org.bukkit.Material;
import pl.saidora.core.model.impl.User;

import java.util.function.Function;

public class GuildPermission {

    public static GuildPermission BLOCK_BREAK = new GuildPermission("BLOCK_BREAK", "&d* &7Niszczenie bloków &d*", Material.STONE_PICKAXE, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.BLOCK_BREAK).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission BLOCK_PLACE = new GuildPermission("BLOCK_PLACE", "&d* &7Stawianie bloków &d*", Material.GRASS, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.BLOCK_PLACE).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission HOME_COMMAND = new GuildPermission("HOME", "&d* &7Teleportacja do domu &d*", Material.ENDER_PORTAL_FRAME, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.HOME_COMMAND).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission SETHOME_COMMAND = new GuildPermission("SETHOME", "&d* &7Ustawianie domu &d*", Material.ENDER_PORTAL_FRAME, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.SETHOME_COMMAND).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission PVP_COMMAND = new GuildPermission("PVP", "&d* &7Zarządzanie &5pvp &d*", Material.DIAMOND_SWORD, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.PVP_COMMAND).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission ALLY_COMMAND = new GuildPermission("ALLY", "&d* &7Akceptowanie sojuszu &d*", Material.GOLD_SWORD, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.ALLY_COMMAND).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission INVITE_ALLY_COMMAND = new GuildPermission("ALLY_INVITE", "&d* &7Zapraszanie do sojuszu &d*", Material.ENDER_PORTAL_FRAME, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.INVITE_ALLY_COMMAND).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission INVITE_COMMAND = new GuildPermission("INVITE", "&d* &7Zapraszanie członków &d*", Material.PAPER, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.INVITE_COMMAND).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission KICK_COMMAND = new GuildPermission("KICK", "&d* &7Wyrzucanie członków &d*", Material.IRON_DOOR, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.KICK_COMMAND).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission PERMISSION_MANAGE = new GuildPermission("PERMISSION_MANAGE", "&d* &7Zarządzanie uprawnieniami &d*", Material.BOOK, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.PERMISSION_MANAGE).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission BEACON_PLACE = new GuildPermission("BEACON_PLACE", "&d* &7Stawianie latarni &d*", Material.BEACON, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.BEACON_PLACE).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission BEACON_BREAK = new GuildPermission("BEACON_BREAK", "&d* &7Niszczenie latarni &d*", Material.BEACON, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.BEACON_BREAK).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission SETTINGS = new GuildPermission("SETTINGS", "&d* &7Zarządzanie opcjami &d*", Material.PAPER, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.SETTINGS).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission UPGRADES = new GuildPermission("UPGRADES", "&d* &7Zarządzanie ulepszeniami &d*", Material.COMMAND, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.UPGRADES).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission DEPOSIT = new GuildPermission("DEPOSIT", "&d* &7Otwieranie depozytu &d*", Material.CHEST, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.DEPOSIT).contains(user) && guild.getUsers().contains(user);
    });
    public static GuildPermission BAZAAR = new GuildPermission("BAZAAR", "&d* &7Otwieranie bazaru &d*", Material.STORAGE_MINECART, user -> {
        Guild guild = user.getGuild().orElse(null);
        return guild != null && guild.getPermissions().get(GuildPermission.BAZAAR).contains(user) && guild.getUsers().contains(user);
    });


    private final String name;
    private final Material material;
    private Function<User, Boolean> function;

    private String displayName;

    public String getName() {
        return name;
    }

    public void setFunction(Function<User, Boolean> function) {
        this.function = function;
    }

    public Material getMaterial() {
        return material;
    }

    public GuildPermission(String name, String displayName, Material material, Function<User, Boolean> function){
        this.name = name;
        this.function = function;
        this.material = material;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean hasPermission(User user){
        return function.apply(user);
    }
}
