package com.untamedears.bottleO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventListener implements Listener {
	
	protected static int XP_PER_BOTTLE = 25;
	protected static long WAIT_TIME_MILLIS = 5000;
	protected static int MAX_BOOKSHELVES = 30;
	protected static int BOTTLES_PER_EMERALD = 9;
	protected static int EMERALDS_PER_BLOCK = 9;
	protected static Random rand;
	//cool-down timers
	protected static HashMap<String,Long> playerWaitHash = new HashMap<String,Long>(100);
	
	protected bottleO plugin;
	
	public EventListener(bottleO plugin) {
		this.plugin = plugin;
	}
	
	//calculate legacy total xp
	private int getLegacyTotalXP(int levels, float exp) {
		float result = (float) ((1.75*levels*levels) + (5*levels));
		result = Math.round(result);
		float remainder = Math.round(Math.round(getLegacyNextXpJump(levels))*exp);
		return (int)(remainder+result);
	}
	
	//calculate legacy xp required to get to next level
	private float getLegacyNextXpJump(int level) {
		return (float)(3.5*level) + (float)(6.7);
	}
	
	//calculate legacy level from xp
	private int getLegacyLevel(int totalXP) {
		if (totalXP > 0) {
			int level = 1;
			int xpGuess = getLegacyTotalXP(level,0);
			while (xpGuess < totalXP) {
				level++;
				xpGuess = getLegacyTotalXP(level,0);
			}
			return level-1;
		}
		return 0;
	}
	
	//calculate legacy exp from level and totalXP
	private float getLegacyExp(int level, int totalXP) {
		int levelXP = Math.round(getLegacyTotalXP(level,0));
		int remainder = totalXP-levelXP;
		float required = Math.round(getLegacyNextXpJump(level));
		float exp = (remainder/required);
		return exp;
	}
	
	//change xp yield from bottle
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onExpBottleEvent(ExpBottleEvent e) {
		e.setExperience(XP_PER_BOTTLE);
	}
	
	//modify xp growth rate
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerExpChangeEvent(PlayerExpChangeEvent e) {
		Player p  = e.getPlayer();
		int levels = p.getLevel();
		int amount = e.getAmount();
		float exp = p.getExp();
		Integer legacyTotalXp = getLegacyTotalXP(levels,exp) + amount;
		Integer legacyLevel = getLegacyLevel(legacyTotalXp);
		Float legacyExp = getLegacyExp(legacyLevel, legacyTotalXp);
		
		e.setAmount(0);
		p.setTotalExperience(0);
		p.setLevel(legacyLevel);
		p.setExp(legacyExp);
	}
	
	
	//generate xp bottles
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		//check the event isn't cancelled and they have clicked on an enchanting table
		if (!e.isCancelled() && e.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) {
			//if the player is holding glass bottles
			if (e.getMaterial() == Material.GLASS_BOTTLE) {
				Player p = e.getPlayer();
				//check player has waited for the required amount of time
				if (!playerWaitHash.containsKey(p.getName())) {
					//if there is no time recorded, add the current time
					bottleO.log.info("no record of "+p.getName()+" logging in!");
					playerWaitHash.put(p.getName(), System.currentTimeMillis());
				}
				long loginTime = playerWaitHash.get(p.getName());
				long timeDiff = System.currentTimeMillis() - (loginTime+WAIT_TIME_MILLIS);
				if (timeDiff < 0) {
					bottleO.log.info(p.getName()+" must wait "+(float)(-timeDiff)/1000+" seconds!");
					p.sendMessage(ChatColor.RED+"["+bottleO.pluginName+"]"+ChatColor.WHITE+" you must wait "+ChatColor.RED+(float)(-timeDiff)/1000+ChatColor.WHITE+" seconds to make more exp bottles.");
					return;
				}
				
				int initialAmount = p.getItemInHand().getAmount();
				int amount = initialAmount;
				int totalXP = getLegacyTotalXP(p.getLevel(),p.getExp());
				
				if (totalXP < 0) {
					String infoMessage = "invalid xp values for "+p.getName()+", calculated xp:"+totalXP+", level:"+p.getLevel()+", progress:"+p.getExp()+", ";
					bottleO.log.info(infoMessage+"impossible xp value, stopping.");
					return;
				}
				
				int totalCost = amount*XP_PER_BOTTLE;
				PlayerInventory inventory = p.getInventory();
				int newTotalXP;
				
				//sanity checking
				if (XP_PER_BOTTLE > 0 && amount > 0 && amount <= 64) {
					if (totalXP < totalCost) {
						if (totalXP >= XP_PER_BOTTLE) {
							totalCost = totalXP;
							totalCost -= (totalXP % XP_PER_BOTTLE);
							amount = totalCost / XP_PER_BOTTLE;
						} else {
							amount = 0;
						}
					}
					//if there is enough xp and bottles
					if (amount > 0) {
						//remove some glass bottles from hand
						ItemStack stack = p.getItemInHand();
						stack.setAmount(initialAmount-amount);
						p.setItemInHand(stack);
						
						//set the new xp value and check it is correct
						newTotalXP = totalXP-totalCost;
						int legacyLevel = getLegacyLevel(newTotalXP);
						p.setTotalExperience(0);
						p.setLevel(legacyLevel);
						p.setExp(getLegacyExp(legacyLevel, newTotalXP));
						//p.giveExp(newTotalXP);
						int finalXP = getLegacyTotalXP(p.getLevel(),p.getExp());
						
						//sanity checking
						if (finalXP == newTotalXP && finalXP == (totalXP - totalCost)) {
							//try to put xp bottles in inventory
							HashMap<Integer, ItemStack> hash = inventory.addItem(new ItemStack(Material.EXP_BOTTLE, amount));
							//otherwise replace glass bottles in hand and drop glass bottles
							if (!hash.isEmpty()) {
								Iterator<Integer> it = hash.keySet().iterator();
								if (it.hasNext()) {
									ItemStack glassStack = p.getItemInHand().clone();
									p.setItemInHand(hash.get(it.next()));
									p.getWorld().dropItem(p.getLocation(), glassStack);
								}
							}
							//restart cool-down timer
							playerWaitHash.put(p.getName(), System.currentTimeMillis());
							//add slowness potion effect because it looks cool
							p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 3));
							bottleO.log.info("success! "+p.getName()+", init:"+totalXP+", final:"+finalXP+", cost:"+totalCost+", bottles:"+(totalCost/XP_PER_BOTTLE));
						}
						// this should never happen
						else {
							p.setTotalExperience(0);
							p.setLevel(0);
							p.setExp(0);
							bottleO.log.info("panic! "+p.getName()+", init:"+totalXP+", final:"+finalXP+", attempted cost:"+totalCost);
						}
					}
				}
			} else if ((e.getMaterial() == Material.EMERALD || e.getMaterial() == Material.EMERALD_BLOCK) && !e.getPlayer().isSneaking()) {
				Player p = e.getPlayer();
				//check player has waited for the required amount of time
				if (!playerWaitHash.containsKey(p.getName())) {
					//if there is no time recorded, add the current time
					bottleO.log.info("no record of "+p.getName()+" logging in!");
					playerWaitHash.put(p.getName(), System.currentTimeMillis());
				}
				long loginTime = playerWaitHash.get(p.getName());
				long timeDiff = System.currentTimeMillis() - (loginTime+WAIT_TIME_MILLIS);
				if (timeDiff < 0) {
					bottleO.log.info(p.getName()+" must wait "+(float)(-timeDiff)/1000+" seconds!");
					p.sendMessage(ChatColor.RED+"["+bottleO.pluginName+"]"+ChatColor.WHITE+" you must wait "+ChatColor.RED+(float)(-timeDiff)/1000+ChatColor.WHITE+" seconds to recover more exp.");
					return;
				}
				
				//calculate amount of xp
				ItemStack i = p.getItemInHand();
				Material m = e.getMaterial();
				int amount = i.getAmount();
				int xp = amount*BOTTLES_PER_EMERALD*XP_PER_BOTTLE;
				if (m == Material.EMERALD_BLOCK) {
					xp *= EMERALDS_PER_BLOCK;
				}
				PlayerExpChangeEvent event = new PlayerExpChangeEvent(p, xp);
				Bukkit.getPluginManager().callEvent(event);
				
				//destroy items
				p.setItemInHand(new ItemStack(Material.AIR));
				
				p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 3));
				
				//restart cool-down timer
				playerWaitHash.put(p.getName(), System.currentTimeMillis());
				
				bottleO.log.info("xp recovered! "+m.toString()+", "+amount+", "+xp);
			}
		}
	}
	
	//record login time
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerLoginEvent(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		playerWaitHash.put(p.getName(), System.currentTimeMillis());
	}
	
	

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPrepareItemEnchant(PrepareItemEnchantEvent e) {
		
		int level; 
		
		if (e.getEnchanter().getGameMode() == GameMode.CREATIVE) {
			level = 50;
		}
		else {
			level = e.getEnchanter().getLevel();
		}
		
		int[] levels = e.getExpLevelCostsOffered();
		
		Random rnd = new Random();
		
		//number of bookshelves around the enchanting table
		int bonus = e.getEnchantmentBonus();
		
		//limit bookshelves to 30
		if (bonus > MAX_BOOKSHELVES) {
			bonus = MAX_BOOKSHELVES;
		}
		
		if (bonus <= 0) {
			levels[0] = rnd.nextInt(5) / 2;
			levels[1] = rnd.nextInt(5) * 2 / 3;
			levels[2] = Math.min(4, level);
		}
		else {
			levels[0] = (rnd.nextInt(5) + 2 + (bonus / 2) + rnd.nextInt(bonus)) / 2;
			levels[1] = (rnd.nextInt(5) + 2 + (bonus / 2) + rnd.nextInt(bonus)) * 2 / 3;
			levels[2] = Math.min(5 + (bonus / 2) + bonus, level);
		}
		
		for (int i = 0; i < 3; i++) {
			if (levels[i] < 1) {
				levels[i] = 1;
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onEnchantItem(EnchantItemEvent e) {
		 
		Enchantments enchantments = new Enchantments(e.getExpLevelCost(), e.getItem().getType());
		
		if (enchantments.getCount() <= 0) {
			e.setCancelled(true);
			return;
		}
		
		enchantments.applyEnchantments(e.getEnchantsToAdd());
	}
}
