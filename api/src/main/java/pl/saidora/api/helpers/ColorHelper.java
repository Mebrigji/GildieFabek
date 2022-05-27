package pl.saidora.api.helpers;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class ColorHelper {

    public static String translateColors(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static List<String> translateColors(List<String> text){
        return text.stream().map(ColorHelper::translateColors).collect(Collectors.toList());
    }

}
