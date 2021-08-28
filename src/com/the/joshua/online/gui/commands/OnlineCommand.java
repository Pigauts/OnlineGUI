package com.the.joshua.online.gui.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.the.joshua.online.gui.GUIPlugin;
import com.the.joshua.online.gui.helper.Util;

public class OnlineCommand implements CommandExecutor {
	private GUIPlugin plugin;

	public OnlineCommand(GUIPlugin plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(Util.translate("&cThe commands only can be used by players!"));
			return true;
		}
		Player p = (Player) sender;
		plugin.getLogicHandler().openMenu(p, 0);
		return true;
	}
}
