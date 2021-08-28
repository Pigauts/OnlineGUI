package com.the.joshua.online.gui.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.the.joshua.online.gui.GUIPlugin;

public class ReloadCommand implements CommandExecutor{

	private GUIPlugin plugin;
	
	public ReloadCommand(GUIPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender.hasPermission("online.gui.reload")) {
			plugin.reload();
		}
		return false;
	}

}
