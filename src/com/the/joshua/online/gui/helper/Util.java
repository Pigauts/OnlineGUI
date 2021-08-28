package com.the.joshua.online.gui.helper;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;

public class Util {
	
	public static String format(String string) {
		return string.toUpperCase().replace(' ', '_');
	}
	
	public static String translate(String msg) {
	    return ChatColor.translateAlternateColorCodes('&', msg);
	}
	
	public static int getPages(int slots, int players) {
		int pages = 0; 
		for (int i = 1; i <= players; i++) {
			if (i == slots) {
				pages++; 
				slots += slots;
			}
		}
		if (pages == 0) 
			return 1;
		return pages;
	}
	
	public static Inventory clone(Inventory inventory) {
		return inventory;
	}
	
	public static void send(String s) {
		Bukkit.broadcastMessage(s);
	}
}
