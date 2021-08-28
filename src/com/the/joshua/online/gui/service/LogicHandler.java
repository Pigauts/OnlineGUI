package com.the.joshua.online.gui.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import com.the.joshua.online.gui.GUIPlugin;
import com.the.joshua.online.gui.helper.ClickAction;
import com.the.joshua.online.gui.helper.Util;
import com.the.joshua.online.gui.service.Sounds.MySound;

import me.clip.placeholderapi.PlaceholderAPI;

@SuppressWarnings("deprecation")
public class LogicHandler implements Listener {

	private GUIPlugin plugin;
	
	private Inventory templateMenu;

	private HashMap<UUID, Integer> openMenus = new HashMap<UUID, Integer>();
	private HashMap<ClickAction, Set<Short>> slotClickAction;
	private HashMap<MySound, Set<Short>> slotClickSounds;

	private List<Integer> playerSlots;
	private List<Inventory> menuPages;

	public LogicHandler(GUIPlugin plugin) {
		this.plugin = plugin;

		reload(plugin);
		updateMenus();

		Bukkit.getScheduler().runTaskTimer(plugin, new Runnable() {
			@Override
			public void run() {
				if (!openMenus.isEmpty()) {
					updateMenus();
					for (Map.Entry<UUID, Integer> entry : openMenus.entrySet()) {
						if (Bukkit.getOfflinePlayer(entry.getKey()).isOnline()) 
						Bukkit.getPlayer(entry.getKey()).getOpenInventory().getTopInventory().setContents(menuPages.get(entry.getValue()).getContents());
					}
				}
			}
		}, plugin.getConfig().getLong("update"), plugin.getConfig().getLong("update"));
	}
	
	public void reload(GUIPlugin plugin) {
		this.plugin = plugin;
		templateMenu = plugin.getConfigReader().getTemplateMenu();
		slotClickAction = new HashMap<ClickAction, Set<Short>>();
		slotClickSounds = new HashMap<MySound, Set<Short>>();
		playerSlots = plugin.getConfig().getIntegerList("menu.player-slots");
		for (String action : plugin.getConfig().getConfigurationSection("menu.actions").getKeys(false))
			slotClickAction.put(ClickAction.valueOf(Util.format(action)), new HashSet<Short>(plugin.getConfig().getShortList("menu.actions." + action)));
		for (String sound : plugin.getConfig().getConfigurationSection("menu.sounds").getKeys(false))
			slotClickSounds.put(MySound.valueOf(Util.format(sound)), new HashSet<Short>(plugin.getConfig().getShortList("menu.sounds." + sound)));
	}
	
	
	public void updateMenus() {
		List<ItemStack> playerItems = new ArrayList<ItemStack>();
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.hasPermission(plugin.getConfig().getString("permission"))) {
				if (GUIPlugin.vanishNoPacketHook) {
					try {
						if (VanishNoPacket.isVanished(player.getName()) && plugin.getConfig().getBoolean("hide-vanished-players", false)) continue;
					} catch (VanishNotLoadedException e) {
					}
				}
				if (GUIPlugin.placeHolderAPIHook) {
					List<String> lore = new ArrayList<String>();
					for (String line : plugin.getConfig().getStringList("player-item.lore")) {
						lore.add(PlaceholderAPI.setPlaceholders(player, Util.translate(line)));
					}
					playerItems.add(getPlayerHead(player.getName(), PlaceholderAPI.setPlaceholders(player, Util.translate(plugin.getConfig().getString("player-item.name"))), lore));
				} else {
					List<String> lore = new ArrayList<String>();
					for (String line : plugin.getConfig().getStringList("player-item.lore")) {
						lore.add(Util.translate(line));
					}
					playerItems.add(getPlayerHead(player.getName(), Util.translate(plugin.getConfig().getString("player-item.name")), lore));
				}
			}
		}
		int pages = Util.getPages(playerSlots.size(), playerItems.size());
		menuPages = new ArrayList<Inventory>();
		
		for (int i = 0; i < pages; i++) {
			Inventory currentMenu = Bukkit.createInventory(null, plugin.getConfig().getInt("menu.rows", 1) * 9, Util.translate(plugin.getConfig().getString("menu.name", "&4NO_NAME_SET")));
			currentMenu.setContents(templateMenu.getContents());
			
			for (int z = 0; z < playerSlots.size(); z++) {
				if (playerItems.size() > 0) {
					currentMenu.setItem(playerSlots.get(z), playerItems.get(0));
					playerItems.remove(playerItems.get(0));
				}
			}
			menuPages.add(currentMenu);
		}
	}

	public void openMenu(Player player, int page) {
		updateMenus();
		player.openInventory(menuPages.get(page));
		openMenus.put(player.getUniqueId(), 0);
		Sounds.playSound(player, MySound.valueOf(Util.format(plugin.getConfig().getString("open-sound"))), (float) plugin.getConfig().getDouble("sound-options.volume"), (float) plugin.getConfig().getDouble("sound-options.pitch"));
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		updateMenus();
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (openMenus.containsKey(event.getPlayer().getUniqueId())) openMenus.remove(event.getPlayer().getUniqueId());
		updateMenus();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (openMenus.containsKey(player.getUniqueId())) {
			event.setCancelled(true);
			for (Map.Entry<ClickAction, Set<Short>> entry : slotClickAction.entrySet()) {
				if (entry.getValue().contains((short) event.getRawSlot())) {
					int currentPage = openMenus.get(player.getUniqueId());
					switch (entry.getKey()) {
					case CLOSE_MENU:
						openMenus.remove(player.getUniqueId());
						player.closeInventory();
						break;
					case NEXT_PAGE:
						if (currentPage < menuPages.size() - 1) {
							openMenus.put(player.getUniqueId(), currentPage + 1);
						}
						break;
					case PREVIOUS_PAGE:
						if (currentPage > 0) {
							openMenus.put(player.getUniqueId(), currentPage - 1);
						}
						break;
					default:
						break;
					}
				}
			}

			for (Map.Entry<MySound, Set<Short>> entry : slotClickSounds.entrySet()) {
				if (entry.getValue().contains((short) event.getRawSlot())) {
					Sounds.playSound(player, entry.getKey(), (float) plugin.getConfig().getDouble("sound-options.volume"), (float) plugin.getConfig().getDouble("sound-options.pitch"));
				}
			}
		}
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event) {
		if (openMenus.containsKey(event.getPlayer().getUniqueId())) {
			if (plugin.getConfig().getBoolean("cancel-escape")) {
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					@Override
					public void run() {
						event.getPlayer().openInventory(event.getInventory());
					}
				}, 1L);
			} else {
				openMenus.remove(event.getPlayer().getUniqueId());
			}
		}
	}

	private ItemStack getPlayerHead(String pName, String iName, List<String> lore) {
		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta meta = (SkullMeta) head.getItemMeta();
		meta.setOwner(pName);
		meta.setDisplayName(iName);
		meta.setLore(lore);
		head.setItemMeta(meta);
		return head;
	}

	public void closeAllMenus() {
		for (Map.Entry<UUID, Integer> entry : openMenus.entrySet()) {
			Bukkit.getPlayer(entry.getKey()).closeInventory();
		}
	}
}
