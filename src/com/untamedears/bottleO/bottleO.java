package com.untamedears.bottleO;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class bottleO extends JavaPlugin {
	
    public static Logger log;
    public EventListener listener;
    public static String pluginName = "bottleO";
    public static String version = "0.7.6";
    
	public void onEnable() {
		log = this.getLogger();
		
		listener = new EventListener(this);
		Bukkit.getPluginManager().registerEvents(listener, this);
		
		//add recipes
		ShapelessRecipe expToEmeraldRecipe = new ShapelessRecipe(new ItemStack(Material.EMERALD,1));
		for (int i = 0; i < 9; i++) {
			expToEmeraldRecipe.addIngredient(Material.EXP_BOTTLE);
		}
		Bukkit.addRecipe(expToEmeraldRecipe);
		ShapelessRecipe emeraldToExpRecipe = new ShapelessRecipe(new ItemStack(Material.EXP_BOTTLE,9));
		emeraldToExpRecipe.addIngredient(Material.EMERALD);
		Bukkit.addRecipe(emeraldToExpRecipe);
		
		log.info(pluginName+" v"+version+" enabled!");
	}
	
	public void onDisable() {
		log.info(pluginName+" v"+version+" disabled!");
	}
}
