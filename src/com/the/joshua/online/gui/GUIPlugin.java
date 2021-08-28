package com.the.joshua.online.gui;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.the.joshua.online.gui.service.ConfigReader;
import com.the.joshua.online.gui.service.LogicHandler;
import com.the.joshua.online.gui.commands.OnlineCommand;
import com.the.joshua.online.gui.commands.ReloadCommand;
import com.the.joshua.online.gui.helper.Util;

public class GUIPlugin extends JavaPlugin {

	public static ConsoleCommandSender log;

	public static boolean placeHolderAPIHook;
	public static boolean vanishNoPacketHook;

	private ConfigReader configReader;
	private LogicHandler logicHandler;

	public void onEnable() {
		log = Bukkit.getConsoleSender();
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) placeHolderAPIHook = true;
		if (Bukkit.getPluginManager().getPlugin("VanishNoPacket") != null) vanishNoPacketHook = true;

		saveDefaultConfig();
		configReader = new ConfigReader(this);
		logicHandler = new LogicHandler(this);
		getServer().getPluginManager().registerEvents(logicHandler, this);
		getCommand("online").setExecutor(new OnlineCommand(this));
		getCommand("onlineguireload").setExecutor(new ReloadCommand(this));

		log.sendMessage("");
		log.sendMessage(Util.translate("  &eAOnlineGUI Enabled!"));
		log.sendMessage(Util.translate("    &aVersion: &b" + getDescription().getVersion()));
		log.sendMessage(Util.translate("    &aAuthors: &b" + (String) getDescription().getAuthors().get(0) + ", " + (String) getDescription().getAuthors().get(1)));
		if (placeHolderAPIHook) {
			log.sendMessage(Util.translate("    &2PlaceHolderAPI Hook was successful!"));
		} else {
			log.sendMessage(Util.translate("    &cPlaceHolderAPI Hook was unsuccessful!"));
		}
		if (vanishNoPacketHook) {
			log.sendMessage(Util.translate("    &2VanishNoPacket Hook was successful!"));
		} else {
			log.sendMessage(Util.translate("    &cVanishNoPacket Hook was unsuccessful!"));
		}
		log.sendMessage("");

//		Bukkit.getScheduler().runTaskLaterAsynchronously((Plugin) this, () -> {
//			try {
//				log.sendMessage(Util.translate(""));
//				log.sendMessage(Util.translate("  &eAOnlineGUI checking updates:"));
//			} catch (Exception e) {
//				log.sendMessage(Util.translate("    &cCould not proceed update-checking"));
//				log.sendMessage(Util.translate(""));
//			}
//		}, 10L);
	}

	public void onDisable() {
		log.sendMessage("");
		log.sendMessage(Util.translate("  &eAOnlineGUI Disabled!"));
		log.sendMessage(Util.translate("    &aVersion: &b" + getDescription().getVersion()));
		log.sendMessage(Util.translate("    &aAuthors: &b" + (String) getDescription().getAuthors().get(0) + ", " + (String) getDescription().getAuthors().get(1)));
		log.sendMessage("");
	}

	public ConfigReader getConfigReader() {
		return configReader;
	}

	public LogicHandler getLogicHandler() {
		return logicHandler;
	}

	public void reload() {
		saveDefaultConfig();
		reloadConfig();
		configReader = new ConfigReader(this);
		logicHandler.reload(this);
	}
}
