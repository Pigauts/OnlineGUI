package com.the.joshua.online.gui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.the.joshua.online.gui.GUIPlugin;
import com.the.joshua.online.gui.helper.Util;

public class ConfigReader {

	private GUIPlugin plugin;
	private Inventory templateMenu;
	
	public ConfigReader(GUIPlugin plugin) {
		this.plugin = plugin;
		templateMenu = loadTemplateMenu();
	}
	
	public Inventory getTemplateMenu() {
		return templateMenu;
	}
	
	public Inventory loadTemplateMenu() {
		HashMap<String, ItemStack> items = new HashMap<String, ItemStack>();
		for (String itemName : plugin.getConfig().getConfigurationSection("items").getKeys(false)) {
			
			ItemStack item;
			String[] split;
			if (plugin.getConfig().getString("items." + itemName + ".material").contains(":")) {
				split = plugin.getConfig().getString("items." + itemName + ".material").split(":");
				item = new ItemStack(Material.valueOf(Util.format(split[0])), plugin.getConfig().getInt("items." + itemName + ".amount", 1), Short.parseShort(split[1]));
			} else {
				item = new ItemStack(Material.valueOf(Util.format(plugin.getConfig().getString("items." + itemName + ".material"))), plugin.getConfig().getInt("items." + itemName + ".amount", 1));
			}
			
			ItemMeta meta = item.getItemMeta();

			if (plugin.getConfig().isSet("items." + itemName + ".name")) {
				meta.setDisplayName(Util.translate(plugin.getConfig().getString("items." + itemName + ".name")));
			}

			if (plugin.getConfig().isSet("items." + itemName + ".lore")) {
				List<String> lore = new ArrayList<String>();
				for (String loreLine : plugin.getConfig().getStringList("items." + itemName + ".lore")) {
					lore.add(Util.translate(loreLine));
				}
				meta.setLore(lore);
			}

			if (plugin.getConfig().getBoolean("items." + itemName + ".enchanted", false)) {
				meta.addEnchant(Enchantment.DAMAGE_ALL, 1, false);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
			item.setItemMeta(meta);
			items.put(itemName, item);
		}
		
		Inventory inventory = Bukkit.createInventory(null, plugin.getConfig().getInt("menu.rows", 1) * 9, Util.translate(plugin.getConfig().getString("menu.name", "&4NO_NAME_SET")));
		for (String item : plugin.getConfig().getConfigurationSection("items").getKeys(false)) {
			for (int slot : plugin.getConfig().getIntegerList("menu.items." + item)) {
				inventory.setItem(slot, items.get(item));	
			}
		}
		return inventory;
	}
}
