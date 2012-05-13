package com.untamedears.bottleO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class EventListener implements Listener {
	
	protected static int xpPerBottle = 25;
	protected static long waitTime = 30000;
	protected static Random rand;
	//cool-down timers
	protected static HashMap<String,Long> playerWaitHash = new HashMap<String,Long>(100);
	
	//calculate total xp
	private int getTotalXP(int levels, float exp) {
		float result = (float) ((1.75*levels*levels) + (5*levels));
		result = Math.round(result);
		float remainder = Math.round(Math.round(getNextXpJump(levels))*exp);
		return (int)(remainder+result);
	}
	
	//calculate xp required to get to next level
	private float getNextXpJump(int level) {
		return (float)(3.5*level) + (float)(6.7);
	}
	
	//change xp yield from bottle
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onExpBottleEvent(ExpBottleEvent e) {
		e.setExperience(xpPerBottle);
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
			int totalXP = getTotalXP(p.getLevel(),p.getExp());
			int otherTotalXP = p.getTotalExperience();
			
			//sanity checking
			if (totalXP != otherTotalXP || totalXP < 0) {
				p.sendMessage(ChatColor.RED+"["+bottleO.pluginName+"]"+ChatColor.WHITE+" an error has occured: invalid xp values.");
				bottleO.log.info("invalid xp values! "+p.getName()+", calculated xp:"+totalXP+", reported xp:"+otherTotalXP);
				return;
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
					int finalXP = getTotalXP(p.getLevel(),p.getExp());
					
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
