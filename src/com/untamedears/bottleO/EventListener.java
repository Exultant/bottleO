package com.untamedears.bottleO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventListener implements Listener {
	
	/*
	 * TODO:
	 * Change xp growth rate
	 * modify the amount of xp set after bottling
	 * implement old available enchants
	 * raise level cap back to 50
	 * disable mining and smelting xp sources
	 * ---
	 * add emeralds as dense storage for xp
	 * add additional enchants beyond old system
	 */
	
	protected static int xpPerBottle = 25;
	protected static long waitTime = 10000;
	protected static Random rand;
	//cool-down timers
	protected static HashMap<String,Long> playerWaitHash = new HashMap<String,Long>(100);
	
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
	
	//calculate new total xp
	private int getNewTotalXP(int levels, float exp) {
		if (levels > 16) {
			int a1 = 20;
			int n = levels-16;
			int an = 17 + (n)*3;
			int d = 3;
			Float progression = (float)(n*(a1 + an)/2);
			Float remainder = (exp*getNewNextXpJump(levels));
			return (int)(272 + progression + remainder);
		} else if (levels >= 0) {
			return (int)((17*levels) + exp*getNewNextXpJump(levels));
		} else {
			return 0;
		}
	}
	
	//calculate new xp required to get to next level
	private float getNewNextXpJump(int levels) {
		if (levels < 17) {
			return 17;
		} else if (levels >= 17){
			return 17 + (float)((levels-16)*3);
		} else {
			return 0;
		}
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
		e.setExperience(xpPerBottle);
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
		//check the event isn't cancelled, the player is holding glass bottles and they have clicked on an enchanting table
		if (!e.isCancelled() && e.getMaterial() == Material.GLASS_BOTTLE && e.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) {
			Player p = e.getPlayer();
			//check player has waited for the required amount of time
			if (!playerWaitHash.containsKey(p.getName())) {
				//if there is no time recorded, add the current time
				bottleO.log.info("no record of "+p.getName()+" logging in!");
				playerWaitHash.put(p.getName(), System.currentTimeMillis());
			}
			long loginTime = playerWaitHash.get(p.getName());
			long timeDiff = System.currentTimeMillis() - (loginTime+waitTime);
			if (timeDiff < 0) {
				bottleO.log.info(p.getName()+" must wait "+(float)(-timeDiff)/1000+" seconds!");
				p.sendMessage(ChatColor.RED+"["+bottleO.pluginName+"]"+ChatColor.WHITE+" you must wait "+ChatColor.RED+(float)(-timeDiff)/1000+ChatColor.WHITE+" seconds to make more exp bottles.");
				return;
			}
			
			int initialAmount = p.getItemInHand().getAmount();
			int amount = initialAmount;
			int totalXP = getLegacyTotalXP(p.getLevel(),p.getExp());
			int otherTotalXP = p.getTotalExperience();
			
			//sanity checking
			if (totalXP != otherTotalXP || totalXP < 0) {
				String infoMessage = "invalid xp values for "+p.getName()+", calculated xp:"+totalXP+", reported xp:"+otherTotalXP+", level:"+p.getLevel()+", progress:"+p.getExp()+", ";
				if (totalXP < otherTotalXP) {
					bottleO.log.info(infoMessage+"calculated xp lower, ignoring reported xp.");
				} else if (totalXP > otherTotalXP){
					bottleO.log.info(infoMessage+"reported xp lower, stopping.");
					return;
				} else {
					bottleO.log.info(infoMessage+"impossible xp value, stopping.");
					return;
				}
			}
			
			int totalCost = amount*xpPerBottle;
			PlayerInventory inventory = p.getInventory();
			int newTotalXP;
			
			//sanity checking
			if (xpPerBottle > 0 && amount > 0 && amount <= 64) {
				if (totalXP < totalCost) {
					if (totalXP >= xpPerBottle) {
						totalCost = totalXP;
						totalCost -= (totalXP % xpPerBottle);
						amount = totalCost / xpPerBottle;
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
					p.setTotalExperience(0);
					p.setLevel(0);
					p.setExp(0);
					p.giveExp(newTotalXP);
					int finalXP = getLegacyTotalXP(p.getLevel(),p.getExp());
					
					//sanity checking
					if (finalXP == newTotalXP && finalXP == (totalXP - totalCost)) {
						//try to put xp bottles in inventory
						HashMap<Integer, ItemStack> hash = inventory.addItem(new ItemStack(Material.EXP_BOTTLE, amount));
						//otherwise replace glass bottles in hand
						if (!hash.isEmpty()) {
							Iterator<Integer> it = hash.keySet().iterator();
							if (it.hasNext()) {
								p.setItemInHand(hash.get(it.next()));
							}
						}
						//restart cool-down timer
						playerWaitHash.put(p.getName(), System.currentTimeMillis());
						//add slowness potion effect because it looks cool
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 40, 3));
						bottleO.log.info("success! "+p.getName()+", init:"+totalXP+", final:"+finalXP+", cost:"+totalCost+", bottles:"+(totalCost/xpPerBottle));
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
		}
	}
	
	//record login time
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerLoginEvent(PlayerLoginEvent e) {
		Player p = e.getPlayer();
		playerWaitHash.put(p.getName(), System.currentTimeMillis());
	}
}
