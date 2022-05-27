package pl.saidora.core.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import pl.saidora.api.helpers.ColorHelper;
import pl.saidora.core.helpers.ItemHelper;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Header("#####################################")
@Header("# Advanced plugin for guild servers #")
@Header("# Author: Saidora#2503              #")
@Header("#####################################")
public class Configuration extends OkaeriConfig {

    public String LICENSE_KEY = "empty";

    @Comment("1000 = 1 sekunda")
    public long TELEPORT_EXPIRE = 30000;

    @Comment("20 = 1 sekunda")
    public long TELEPORT_SCHEDULER_DELAY = 100;

    @Comment({"###", "(0) Ustawienia", "###"})
    public String PREFIX_GUILD_WAR = "&4[%tag%]";
    public String PREFIX_GUILD_OTHER = "&c[%tag%]";
    public String PREFIX_GUILD_ALLY = "&6[%tag%]";
    public String PREFIX_GUILD_OWN = "&a[%tag%]";

    public int GUILD_MEMBERS_LEVEL_1_AMOUNT = 15;
    public int GUILD_MEMBERS_LEVEL_2_AMOUNT = 20;
    public int GUILD_MEMBERS_LEVEL_3_AMOUNT = 30;

    public double GUILD_MEMBERS_LEVEL_1_COST = 100;
    public double GUILD_MEMBERS_LEVEL_2_COST = 250;
    public double GUILD_MEMBERS_LEVEL_3_COST = 500;

    public int GENERATORS_PER_USER_FOR_PLAYER = 25, GENERATORS_PER_USER_FOR_VIP = 50;

    public double GENERATOR_STONE_HEALTH = 40, GENERATOR_OBSIDIAN_HEALTH = 100;

    public long GENERATOR_STONE_DELAY = 40,
            GENERATOR_OBSIDIAN_DELAY = 120,
            GENERATOR_STONE_UPGRADE_DELAY_SUBTRACT = 1,
            GENERATOR_OBSIDIAN_UPGRADE_DELAY_SUBTRACT = 2,
            GENERATOR_STONE_UPGRADE_COST_PER_LEVEL = 75,
            GENERATOR_OBSIDIAN_UPGRADE_COST_PER_LEVEL = 150;

    public ItemStack GENERATOR_ITEM = new ItemHelper(Material.ENDER_STONE).editMeta(itemMeta -> {
        itemMeta.setDisplayName("&d* &7Generator &d*");
        itemMeta.setLore(ColorHelper.translateColors(Arrays.asList("&7Połóż blok na ziemie",
                "&7aby stworzyć &dgenerator.",
                " ",
                "&8* &7Dostępne rodzaje:",
                "&8- &7Generator Kamienia",
                "&8- &7Generator Obsydianu")));
        itemMeta.addEnchant(Enchantment.DURABILITY, 4, true);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    });

    @Comment({"###", "(1) Wiadomości dotyczące teleportacji", "###"})
    public String MESSAGE_TELEPORT_REQUEST_TARGET = "&7Masz oczekujaca teleportacje od gracza: &d%playerName%&7, wygasnie ona za: &d%time%";
    public String MESSAGE_TELEPORT_REQUEST_EXECUTOR = "&7Pomyslnie wyslano prosbe o teleportacje do gracza: &d%playerName%&7, wygasnie ona za: &d%time%";

    public String MESSAGE_TELEPORT_REQUEST_ALREADY = "&7Wyslales juz prosbe o teleportacje do gracza: &d%playerName%&7, pozostalo do wygasniecia: &d%time%";

    public String MESSAGE_TELEPORT_REQUEST_MAX_COUNT = "&7Osiagnięto limit prosb o teleportacje &8( &d%count% &8).\n &7Aby wyslac kolejna prosbe musisz poczekac, az liczba prosb bedzie wynosic <= 8.\n&7Liste wyslanych teleportacji znajdziesz pod: &d/teleport list";

    public String MESSAGE_TELEPORT_ACCEPT_EXECUTOR_SINGLE = "&7Zaakceptowales teleportacje od gracza: &d%playerName%";
    public String MESSAGE_TELEPORT_ACCEPT_EXECUTOR_MANY = "&7Zaakceptowales teleportacje od &d%count% &7graczy.";

    public String MESSAGE_TELEPORT_ACCEPT_TARGET_SINGLE = "&7Gracz &d%playerName% &7zaackeptowal prosbe o teleportacje.";
    public String MESSAGE_TELEPORT_ACCEPT_TARGET_MANY = "&7Gracz &d%playerName% &7zaakceptowal prosbe o teleportacje od Ciebie i &d%count% &7innych graczy.";


    public String MESSAGE_TELEPORT_SCHEDULER_START = "&7Ustawiam miejsce docelowe, zaczynam rozgrzewanie teleportu..";
    public String MESSAGE_TELEPORT_SCHEDULER_TIMER = "&7Teleport się rozgrzewa.. &7Pozostalo: &d%count% sek.";
    public String MESSAGE_TELEPORT_SCHEDULER_END_NEW = "&cAnuluje teleportacje.. &7Zmieniam miejsce docelowe..";
    public String MESSAGE_TELEPORT_SCHEDULER_END = "&7Pomyslnie przeteleportowano do miejsca docelowego.";
    public String MESSAGE_TELEPORT_SCHEDULER_OBSTACLE = "&6Przerywam teleportacje.. &7Struktura teleportu zostala naruszona.";
    public String MESSAGE_TELEPORT_SCHEDULER_MOVE = "&6Przerywam teleportacje.. &7Musisz stać w miejscu, aby teleportacja się powiodla.";

    @Comment({"###", "(2) Wiadomości dotyczące wydarzeń na terenie gildii", "###"})
    public String MESSAGE_GUILD_REGION_BLOCK_BREAK_NO_PERM = "&7Nie posiadasz uprawnien do niszczenia blokow na terenie gildi.\n&7Zglos się do &dLider'a &7lub &dvLider'a &7gildii po uprawnienie.\n&cAby wylaczyc wiadomosci o uprawnieniach uzyj &4/notify guild off";
    public String MESSAGE_GUILD_REGION_BLOCK_BREAK = "&7Nie mozesz niszczyc blokow na terenie gildii do której nie nalezysz.\n&7Jeśli jest to &esojusznicza &7gildia zglos sie po dostep, pamietaj że &dTwoja &7gildia musisz miec gildie &d%guildName% &7w sojuszu.\n&cAby wylaczyc wiadomości o uprawnieniach uzyj &4/notify guild off";

    public String MESSAGE_GUILD_REGION_BLOCK_PLACE_NO_PERM = "&7Nie posiadasz uprawnien do budowania na terenie gildi.\n&7Zglos się do &dLider'a &7lub &dvLider'a &7gildii po uprawnienie.\n&cAby wylaczyc wiadomosci o uprawnieniach uzyj &4/notify guild off";
    public String MESSAGE_GUILD_REGION_BLOCK_PLACE = "&7Nie mozesz budowac na terenie gildii do ktorej nie nalezysz.\n&cAby wylaczyc wiadomosci o uprawnieniach uzyj &4/notify guild off";
    public String MESSAGE_GUILD_REGION_BLOCK_PLACE_EXPLODE = "&7Na terenie &dTwojej &7gildii wybuchlo TNT.\nNie mozesz jeszcze budowac przez: &c%count% sekund.";

    public String MESSAGE_GUILD_REGION_BLOCK_EXPLODE = "&7Na terenie &dTwojej &7gildii wybuchlo TNT.\n&7Aby zobaczyc poziom zniszczenie uzyj &d/g explosion";

    public String MESSAGE_GUILD_REGION_USER_ENTRY_INTRUDER = "&7Na teren &dTwojej &7gildii wkroczyl intruz.\n&8&l* &7Ilosc aktualnych intruzow: &d%count%";
    public String MESSAGE_GUILD_REGION_USER_EXIT_INTRUDER = "&7Intruz opuscil teren &dTwojej &7gildii.\n&8&l* &7Ilosc aktualnych intruzow: &d%count%";

    public String MESSAGE_GUILD_REGION_INTRUDER_ENTRY = "&a&l&ox &cGildia: &d%guildTag% &a&l&ox";
    public String MESSAGE_GUILD_REGION_INTRUDER_EXIT = "&4&l&ox &cGildia: &d%guildTag% &4&l&ox";

    public String MESSAGE_GUILD_REGION_ALLY_ENTRY = "&a&l&ox &6Gildia: &d%guildTag% &a&l&ox";
    public String MESSAGE_GUILD_REGION_ALLY_EXIT = "&4&l&ox &6Gildia: &d%guildTag% &4&l&ox";

    public String MESSAGE_GUILD_REGION_MEMBER_ENTRY = "&a&l&ox &aGildia: &d%guildTag% &a&l&ox";
    public String MESSAGE_GUILD_REGION_MEMBER_EXIT = "&4&l&ox &aGildia: &d%guildTag% &4&l&ox";

    @Comment({"###", "(3) Tablista", "###", "PING | Kiedy ping jest równy '-1' wtedy jest on pobierany z aktualnego pingu gracza."})
    public int TABLIST_PING = -1;

    public List<String> TABLIST_HEADER = Arrays.asList(" ",
            "&8&m---&d&m---&8&m---&5&m---[-&r &d&lWonder&f&lMC.PL &5&m-]---&8&m---&d&m---&8&m---",
            "&aGraczy online aktualnie &8(&a%playersOnline%&7/&2%playersMax%&8)",
            " ");
    public List<String> TABLIST_FOOTER = Arrays.asList(" ",
            "Ukrywaj skina,nick,range pod komenda&8(&d/incognito&8)",
            "&fMozesz edytowac ustawienia chatu&8(&d/cc&8)",
            "&5\u30fb&7Strona&8: &dhttps://www.wondermc.pl &5\u30fb",
            "&5\u30fb&7Discord&8: &fdc.wondermc.pl &5\u30fb",
            " ");

    public List<String> TABLIST_ROWS = Arrays.asList(" ",
            "    &5\u30fb  &f&lINFORMACJE  &5\u30fb",
            "          &8(&dO tobie&8)",
            " ",
            "\u30fb &7Twoj nick&8: %groupDisplay% &d%name%",
            "\u30fb &7Incognito&8: &d%incognito%",
            "\u30fb &7Punkty&8: &a%points%",
            "\u30fb &7Zabojstwa&8: &a%kills%",
            "\u30fb &7Zgony&8: &c%deaths%",
            " ",
            "    &5\u2022  &f&lGILDIA  &5\u2022",
            "        &8(&dTwoja&8)",
            "",
            "\u30fb &7Nazwa&8: &a%g-tag%",
            "\u30fb &7Zalozyciel&8: &d%g-leader%",
            "\u30fb &7Punkty&8: &a%g-points%",
            "\u30fb &7Zabojstwa&8: &a%g-kills%",
            "\u30fb &7Zgony&8: &c%g-deaths%",
            "\u30fb &7Pozycja&8: &d%g-position%",
            " ",
            " ",
            "   &5\u30fb  &f&lTOPOWI GRACZE  &5\u30fb",
            "         &8(&dPunkty&8)",
            "",
            "&e&l1. &8[&e%top_1-points%&8] %top_1-g-tag% &e%top_1-name%",
            "&62. &8[&6%top_2-points%&8] %top_2-g-tag% &6%top_2-name%",
            "&f3. &8[&f%top_3-points%&8] %top_3-g-tag% &f%top_3-name%",
            "&74. &8[&7%top_4-points%&8] %top_4-g-tag% &7%top_4-name%",
            "&75. &8[&7%top_5-points%&8] %top_5-g-tag% &7%top_5-name%",
            "&76. &8[&7%top_6-points%&8] %top_6-g-tag% &7%top_6-name%",
            "&77. &8[&7%top_7-points%&8] %top_7-g-tag% &7%top_7-name%",
            "&78. &8[&7%top_8-points%&8] %top_8-g-tag% &7%top_8-name%",
            "&79. &8[&7%top_9-points%&8] %top_9-g-tag% &7%top_9-name%",
            "&710. &8[&7%top_10-points%&8] %top_10-g-tag% &7%top_10-name%",
            "&711. &8[&7%top_11-points%&8] %top_11-g-tag% &7%top_11-name%",
            "&712. &8[&7%top_12-points%&8] %top_12-g-tag% &7%top_12-name%",
            "&713. &8[&7%top_13-points%&8] %top_13-g-tag% &7%top_13-name%",
            "&714. &8[&7%top_14-points%&8] %top_14-g-tag% &7%top_14-name%",
            "&715. &8[&7%top_15-points%&8] %top_15-g-tag% &7%top_15-name%",
            " ",
            "",
            "   &5\u30fb  &f&lTOPOWI GILDIE  &5\u30fb",
            "         &8(&dPunkty&8)",
            "",
            "&e&l1. &8[&e%g_top_1-points%&8] &e%g_top_1-tag%",
            "&62. &8[&6%g_top_2-points%&8] &6%g_top_2-tag%",
            "&f3. &8[&f%g_top_3-points%&8] &f%g_top_3-tag%",
            "&74. &8[&7%g_top_4-points%&8] &7%g_top_4-tag%",
            "&75. &8[&7%g_top_5-points%&8] &7%g_top_5-tag%",
            "&76. &8[&7%g_top_6-points%&8] &7%g_top_6-tag%",
            "&77. &8[&7%g_top_7-points%&8] &7%g_top_7-tag%",
            "&78. &8[&7%g_top_8-points%&8] &7%g_top_8-tag%",
            "&79. &8[&7%g_top_9-points%&8] &7%g_top_9-tag%",
            "&710. &8[&7%g_top_10-points%&8] &7%g_top_10-tag%",
            "&711. &8[&7%g_top_11-points%&8] &7%g_top_11-tag%",
            "&712. &8[&7%g_top_12-points%&8] &7%g_top_12-tag%",
            "&713. &8[&7%g_top_13-points%&8] &7%g_top_13-tag%",
            "&714. &8[&7%g_top_14-points%&8] &7%g_top_14-tag%",
            "&715. &8[&7%g_top_15-points%&8] &7%g_top_15-tag%",
            "",
            "",
            "    &5\u30fb  &f&lINFORMACJE  &5\u30fb",
            "       &8(&dO serwerze&8)",
            "",
            "&8* &7Zakladanie gildi: &d%enabled-guilds%",
            "&8* &7Kity: &d%enabled-kits%",
            "&8* &7Diax itemy: &d%enabled-diamond%",
            "&7Pierwsze gildie:",
            " &e%g_create-first%&7, &6%g_create-second%&7, &f%g_create-third%",
            "",
            "&7Opoznienie: &d%ping%ms.",
            "&7Tps: &d%tps%",
            "&7Obciazenie: &d%cpu%%",
            "",
            "&7Pamiec ram:",
            "&8* &7Wolna: &d%ram-free% mb.",
            "&8* &7Uzywana: &d%ram-use% mb.",
            "&8* &7Calkowita: &d%ram-total% mb.",
            "&8* &7Maksymalna: &d%ram-max% mb.");

    @Comment("Pierwsze trzy zalozone gildie | system sam nadpisze")
    public String FIRST_GUILD = "Brak";
    public String SECOND_GUILD = "Brak";
    public String THIRD_GUILD = "Brak";

    @Comment({"###", "(4) Actionbar", "###"})
    public String ACTION_BAR_TURBO_DROP = "&6TurboDrop&8: &f%time%";
    public String ACTION_BAR_TURBO_EXP = "&6TurboExp&8: &f%time%";
    public String ACTION_BAR_TURBO_CHEST = "&6TurboChest&8: &f%time%";
    public String ACTION_BAR_TURBO = "&7Drop: &f%drop_time% &7| Exp: &f%exp_time% &7| Chest: &f%chest_time%";

    @Comment({"###", "(5) Scoreboard", "###"})
    public String SCOREBOARD_TITLE = " ";
    public List<String> SCOREBOARD_LINES = Arrays.asList(" ",
            "&8* &7Gildia: &d%g-tag%",
            "&8* &7Punkty: &d%points%",
            "&8* &7Pozycja: &d%position%",
            "                    &0            ",
            "&8* &7Poziom: &d%level%",
            "&8* &7Doswiadczenie: &d%exp%",
            " ");

    @Comment({"###", "(6) AntyLogout", "###"})
    public long ANTY_LOGOUT_TIME = 20;
    public String ANTY_LOGOUT_MESSAGE = "&cAntyLogout: %s█%s█%s█%s█%s█%s█%s█%s█%s█%s█";

    @Comment({"###", "(7) Wydarzenia", "###"})
    public String EVENT_GENERATOR_LIMIT = "\n&7Osiagnieto limit generatorow.\n&7Zakup range &6VIP &7na naszym discordzie, aby zwiekszyc limit do &d50 &7generatorow.";
    public String EVENT_GENERATOR_CREATE_STONE = "\n&7Pomyslnie stworzono generator kamienia.\nPozostala ilosc wolnych generatorow: &d%amount%";
    public String EVENT_GENERATOR_CREATE_OBSIDIAN = "\n&7Pomyslnie stworzono generator obsydianu.\nPozostala ilosc wolnych generatorow: &d%amount%";

    public String EVENT_COMBAT_ARROW = "\n&7Trafiono gracza &d%victimName% &7za pomoca strzaly&8: &cpozostalo &4%health% &chp.";
    public String EVENT_COMBAT_SNOWBALL = "\n&7Trafiono gracza &d%victimName% &7za pomoca sniezki&8: &cpozostalo &4%health% &chp.";
    public String EVENT_COMBAT_START = "&cZostales zaatakowany, zostala nalozona blokada na wychodzenie z gry.\n&8* &cInicjator: &4%attackerName%";
    public String EVENT_COMBAT_END = "&aWalka sie zakonczyla, mozesz sie wylogowac.";

    public String EVENT_DEATH_MESSAGE_BY_PLAYER = "&7Gracz &";

    @Comment({"###", "(8) Wiadomości gildyjne", "###"})
    public String GUILD_HELP =
            "&d/g info [tag] &8- &7Informacje dotyczące gildii\n" +
                    "&d/g pvp &8- &7Zmieniaj tryb pvp w gildii\n" +
                    "&d/g panel &8- &7Panel gildyjny\n" +
                    "&d/g zaloz &8[t&7ag] [nazwa] - Załóż swoją gildie\n" +
                    "&d/g itemy &8- &7Sprawdź wymagane itemy na gildie\n" +
                    "&d/g usun &8- &7Usun gildie\n" +
                    "&d/g oplac &8- &7Oplac gildie\n" +
                    "&d/g powieksz &8- &7Powieksz teren gildii\n" +
                    "&d/g regeneracja &8- &7Zregeneruj teren gildii\n" +
                    "&d/g walka &8[t&7ag] - Zacznij walke z daną gildią\n" +
                    "&d/g wolnemiejsce &8- &7Sprawdź wolne miejsce dla swojej gildii\n" +
                    "&d/g pomoc &8- &7Wyślij prośbe o pomoc do sojuszników";

    public String GUILD_INFO =
            "\n" +
                    "&7Sprawdzasz gildie: &d%tag% &8( &d%name% &8)\n" +
                    "&5[RANKING]\n" +
                    "&8* &7Punkty: &d%points%\n" +
                    "&8* &7Zabojstwa: &d%kills%\n" +
                    "&8* &7smierci: &d%deaths%\n" +
                    "&8* &7Pozycja: &d%position%\n" +
                    "&8* &7K/D Ratio: &d%kdr%\n" +
                    "\n" +
                    "&8* &7Zycie: &d%lives%\n" +
                    "&8* &7Wygasa: &d%expire%\n" +
                    "&8* &7Rozmiar: &d%size%\n" +
                    "\n" +
                    "&5[WOJNA] %war%" +
                    "\n" +
                    "&5[CZŁONKOWIE]\n" +
                    "&8* &7Online: &a%online%&d/%total%\n" +
                    "%users%" +
                    "\n";

    public String GUILD_INFO_WAR =
            "\n" +
                    "&8* &7Atakujaca: &d%attacker% &8( &d%attacker-points% &8)\n" +
                    "&8* &7Broniaca: &d%defender% &8( &d%defender-points% &8)\n" +
                    "&8* &7Start: &d%start%\n" +
                    "&8* &7Koniec: &d%end%";

    public String GUILD_HAVE = "&7Aby uzyc ta komende musisz nalezec do gildii.";
    public String GUILD_INFO_EXISTS = "&7Aby mozna bylo zdobyc informacje na temat gildii musisz byc na jej terenie, albo wpisac jej tag.";

    public String GUILD_CREATE_EXISTS = "&7Gildia o tym tagu juz istnieje.";
    public String GUILD_CREATE_WRONG_TAG = "&7Uzyto niepoprawnego tagu dla gildii. &8( &7Dozwolone sa tylko wielkie litery | Dlugosc tagu musi zmiescic sie pomiedzy &d2, a 4 &7literami &8)";
    public String GUILD_CREATE_WRONG_NAME = "&7Uzyto niepoprawnej nazwy dla gildii. &8( &7Dozwolone sa tylko litery | Dlugosc nazwy musi zmiescic sie pomiedzy &d6, a 24 &7literami &8)";
    public String GUILD_CREATE_EXECUTOR = "\n\n      &d&l!!! GRATULACJE !!!\n" +
            "&7Pomyslnie zalozono gildie o tagu &d%tag% &7i nazwie &d%name%\n" +
            "&7Wygasnie ona za: &d%expire%\n" +
            "&7Aktualny maksymalny limit osob w gildi: &d%membersMax%\n\n" +
            "&7Jest to &d%guilds% &7zalozona gildia." +
            "\n\n";
    public String GUILD_CREATE_ALL = "\n\n&8##########\n&7Gracz &d%playerName% &7zalozyl gildie o tagu &d%tag% &7i nazwie &d%name%\n\n&8#########";

    @Comment({"###", "(9) Otchlan", "###"})

    public ItemStack ABYSS_GUI_PREVIOUS_EXISTS = new ItemHelper(Material.SKULL_ITEM, 1, (short) 3)
            .editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&5* &7Poprzednia strona &5*"));
                itemMeta.setLore(ColorHelper.translateColors(Collections.singletonList("&7Kliknij, aby przejsc na poprzednia strone.")));
            });

    public String ABYSS_GUI_PREVIOUS_EXISTS_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";

    public ItemStack ABYSS_GUI_NEXT_EXISTS = new ItemHelper(Material.SKULL_ITEM, 1, (short) 3)
            .editMeta(itemMeta -> {
                itemMeta.setDisplayName(ColorHelper.translateColors("&5* &7Kolejna strona &5*"));
                itemMeta.setLore(ColorHelper.translateColors(Collections.singletonList("&7Kliknij, aby przejsc na kolejna strone.")));
            });

    public String ABYSS_GUI_NEXT_EXISTS_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTliZjMyOTJlMTI2YTEwNWI1NGViYTcxM2FhMWIxNTJkNTQxYTFkODkzODgyOWM1NjM2NGQxNzhlZDIyYmYifX19";

    public ItemStack ABYSS_GUI_NEXT_EMPTY = new ItemHelper(Material.SKULL_ITEM, 1, (short)3)
            .editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&5* &7Brak kolejnej strony &5*")));

    public String ABYSS_GUI_NEXT_EMPTY_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjJmM2EyZGZjZTBjM2RhYjdlZTEwZGIzODVlNTIyOWYxYTM5NTM0YThiYTI2NDYxNzhlMzdjNGZhOTNiIn19fQ==";

    public ItemStack ABYSS_GUI_PREVIOUS_EMPTY = new ItemHelper(Material.SKULL_ITEM, 1, (short)3)
            .editMeta(itemMeta -> itemMeta.setDisplayName(ColorHelper.translateColors("&5* &7Brak poprzedniej strony &5*")));

    public String ABYSS_GUI_PREVIOUS_EMPTY_HEAD = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIwZjZlOGFmNDZhYzZmYWY4ODkxNDE5MWFiNjZmMjYxZDY3MjZhNzk5OWM2MzdjZjJlNDE1OWZlMWZjNDc3In19fQ==";

    public long ABYSS_COUNTDOWN = 180;
    public long ABYSS_OPEN_TIME = 30;

    public List<String> ABYSS_MESSAGES = Arrays.asList(
            "<180> &5[OTCHLAN] &7Otchlan zostanie otwarta za: &d3 minuty.",
            "<120> &5[OTCHLAN] &7Otchlan zostanie otwarta za: &d2 minuty.",
            "<60> &5[OTCHLAN] &7Otchlan zostanie otwarta za: &d1 minute.",
            "<30> &5[OTCHLAN] &7Otchlan zostanie otwarta za: &d30 sekund.",
            "<15> &5[OTCHLAN] &7Otchlan zostanie otwarta za: &d15 sekund.",
            "<5> &5[OTCHLAN] &7Otchlan zostanie otwarta za: &d5 sekund.",
            "<4> &5[OTCHLAN] &7Do otwarcia pozostalo: &d4",
            "<3> &5[OTCHLAN] &7Do otwarcia pozostalo: &d3",
            "<2> &5[OTCHLAN] &7Do otwarcia pozostalo: &d2",
            "<1> &5[OTCHLAN] &7Do otwarcia pozostalo: &d1",
            "<0> &5[OTCHLAN] &aOtchlan zostala otwarta. Przeniesiono &2%items% &aprzedmiotow, aby ja otworzyc wpisz &2/otchlan.",
            "<-1> &5[OTCHLAN] &cOtchlan zostala zamknieta.");

    public String ABYSS_CLOSED = "\n&7Otchlan jest aktualnie zamknieta.";

    @Comment({"###", "(10) Toolsy", "###"})
    public String COMMAND_REPAIR_MUST_BE_ITEM = "&7Rzecz ktora chcesz naprawic musi byc przedmiotem.";
    public String COMMAND_REPAIR_ONE = "&7Pomyslnie naprawiono &d%item%";

    public String COMMAND_REPAIR_ALL_EMPTY = "&7Brak przedmiotow do naprawy.";
    public String COMMAND_REPAIR_ALL = "&7Pomyslnie naprawiono &d%itemsCount% &7przedmiotow.\n&8[&d%itemsList%&8]";

    @Comment("###")
    public String COMMAND_GAMEMODE_INVALID_MODE = "&7Nie odnaleziono trybu gry o tej nazwie.";

}
