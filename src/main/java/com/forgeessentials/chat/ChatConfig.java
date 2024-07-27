package com.forgeessentials.chat;

import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Set;

import net.minecraftforge.common.config.Configuration;

import com.forgeessentials.core.moduleLauncher.config.ConfigLoaderBase;
import com.forgeessentials.util.output.LoggingHandler;

public class ChatConfig extends ConfigLoaderBase
{

    private static final String CATEGORY = ModuleChat.CONFIG_CATEGORY;

    private static final String CAT_GM = CATEGORY + ".Gamemodes";

    private static final String CAT_LOG = CATEGORY + ".Logging";

    public static final String CHAT_FORMAT_HELP = "Format for chat. Always needs to contain all 5 \"%s\" placeholders like the default!";

    private static final String MUTEDCMD_HELP = "All commands in here will be blocked if the player is muted.";

    private static final String WELCOME_MESSAGE = "Welcome messages for new players. Can be color formatted (supports script arguments)";

    private static final String LOGIN_MESSAGE = "Login message shown each time the player logs in (supports script arguments)";

    private static final String DEFAULT_WELCOME_MESSAGE = "New player @player joined the server!";

    private static final String[] DEFAULT_LOGIN_MESSAGE = new String[] { "Welcome @player.", "This server is running ForgeEssentials" };

    public static String gamemodeCreative;

    public static String gamemodeAdventure;

    public static String gamemodeSurvival;

    public static String chatFormat = "%s%s<%s>%s%s ";

    public static String welcomeMessage;

    public static String[] loginMessage;

    public static Set<String> mutedCommands = new HashSet<>();

    public static boolean logChat;

    public static boolean logTells;

    public static boolean logGroupChat;

    public static boolean logIRC;

    public static boolean logDiscord;

    @Override
    public void load(Configuration config, boolean isReload)
    {
        config.addCustomCategoryComment("Chat", "Chat configuration");

        try
        {
            chatFormat = config.get("Chat", "ChatFormat", "%s%s<%s>%s%s ", CHAT_FORMAT_HELP).getString();
            String.format(chatFormat, "", "", "", "", "");
        }
        catch (IllegalFormatException e)
        {
            LoggingHandler.felog.error("Invalid chat format specified in chat config!");
            chatFormat = "%s%s<%s>%s%s ";
        }

        welcomeMessage = config.get("Chat", "WelcomeMessage", DEFAULT_WELCOME_MESSAGE, WELCOME_MESSAGE).getString();
        loginMessage = config.get("Chat", "LoginMessage", DEFAULT_LOGIN_MESSAGE, LOGIN_MESSAGE).getStringList();

        config.addCustomCategoryComment(CAT_GM, "Gamemode names");
        gamemodeSurvival = config.get(CAT_GM, "Survival", "survival").getString();
        gamemodeCreative = config.get(CAT_GM, "Creative", "creative").getString();
        gamemodeAdventure = config.get(CAT_GM, "Adventure", "adventure").getString();

        mutedCommands.clear();
        for (String cmd : config.get("Chat.mute", "mutedCommands", new String[] { "me" }, MUTEDCMD_HELP).getStringList())
            mutedCommands.add(cmd);

        logChat = config.get(CAT_LOG, "LogChat", true, "Log all publicly visible chat messages.").getBoolean(true);
        logDiscord = config.get(CAT_LOG, "LogDiscord", true, "If Discord is enabled, then log Discord chat too.").getBoolean(true);
        logGroupChat = config.get(CAT_LOG, "LogGroupChats", true, "Log all chats sent to groups.").getBoolean(true);
        logIRC = config.get(CAT_LOG, "LogIRC", true, "If IRC is enabled, then log IRC chat too. Excludes /ircpm, unless LogTells is also enabled.").getBoolean(true);
        logTells = config.get(CAT_LOG, "LogTells", true, "Log all /msg, /pm, /tell, /r chats. Includes /ircpm, if LogIRC is also enabled.").getBoolean(true);
        ModuleChat.instance.setChatLogging();
    }

    @Override
    public void save(Configuration config)
    {
    }

}
