package com.untamedears.bottleO;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class bottleO extends JavaPlugin {
	
	/*
	 * DONE:
	 * Change xp growth rate
	 * modify the amount of xp set after bottling
	 * implement old available enchants
	 * raise level cap back to 50
	 * TODO:
	 * disable mining and smelting xp sources
	 * TEST EVERYTHING
	 * ---
	 * add emeralds as dense storage for xp
	 * add additional enchants beyond old system
	 */
	
    public static Logger log;
    public EventListener listener;
    public static String pluginName = "bottleO";
    public static String version = "0.6";
    
	public void onEnable() {
		log = this.getLogger();
		
		listener = new EventListener();
		Bukkit.getPluginManager().registerEvents(listener, this);
		ShapelessRecipe recipe1 = new ShapelessRecipe(new ItemStack(Material.EMERALD,1));
		for (int i = 0; i < 9; i++) {
			recipe1.addIngredient(Material.EXP_BOTTLE);
		}
		Bukkit.addRecipe(recipe1);
		ShapelessRecipe recipe2 = new ShapelessRecipe(new ItemStack(Material.EXP_BOTTLE,9));
		recipe2.addIngredient(Material.EMERALD);
		Bukkit.addRecipe(recipe2);
		
		log.info(pluginName+" v"+version+" enabled!");
	}
	
	public void onDisable() {
		log.info(pluginName+" v"+version+" disabled!");
	}
}
