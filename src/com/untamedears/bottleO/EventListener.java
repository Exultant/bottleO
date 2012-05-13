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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EventListener implements Listener {
	
	protected static int minXP = 25;
	protected static int maxXP = 25;
	protected static int costXP = 25;
	protected static long waitTime = 30000;
	protected static Random rand;
	protected static HashMap<String,Long> playerWaitHash = new HashMap<String,Long>(100);
	
	//calculate total xp
	private int getTotalXP(int levels, float exp) {
		float result = (float) (1.75*levels*levels + 5*levels);
		result = Math.round(result);
		float remainder = Math.round(getNextXpJump(levels))*exp;
		return (int)(remainder+result);
	}
	
	//calculate xp required to get to next level
	private float getNextXpJump(int level) {
		return (float)(3.5*level) + (float)(6.7);
	}
	
	//change xp yield from bottle
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onExpBottleEvent(ExpBottleEvent e) {
		int xp = minXP;
		//if (maxXP > minXP) {
		//	xp += rand.nextInt(maxXP-minXP);
		//}
		e.setExperience(xp);
	}
	
	//generate xp bottles
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		if (!e.isCancelled() && e.getMaterial() == Material.GLASS_BOTTLE && e.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE && e != null) {
			Player p = e.getPlayer();
			//check player has waited for the required amount of time
			if (!playerWaitHash.containsKey(p.getName())) {
				bottleO.log.info("no record of "+p.getName()+" logging in!");
				playerWaitHash.put(p.getName(), System.currentTimeMillis());
				
			}
			long loginTime = playerWaitHash.get(p.getName());
			long timeDiff = System.currentTimeMillis() - (loginTime+waitTime);
			if (timeDiff < 0) {
				bottleO.log.info(p.getName()+" has not waited long enough!");
				p.sendMessage(ChatColor.RED+"["+bottleO.pluginName+"] "+ChatColor.WHITE+" you must wait "+(-(float)(timeDiff)/1000)+" more seconds to make more exp bottles.");
				return;
			}
			
			int initialAmount = p.getItemInHand().getAmount();
			int amount = initialAmount;
			int totalXP = getTotalXP(p.getLevel(),p.getExp());
			int otherTotalXP = p.getTotalExperience();
			
			//sanity checking
			if (totalXP != otherTotalXP) {
				bottleO.log.info("invalid xp values! "+p.getName()+", "+totalXP+", "+otherTotalXP);
				return;
			}
			
			int totalCost = amount*costXP;
			PlayerInventory inventory = p.getInventory();
			int newTotalXP;
			
			//sanity checking
			if (costXP > 0 && amount > 0 && amount <= 64) {
				if (totalXP < totalCost) {
					if (totalXP >= costXP) {
						totalCost = totalXP;
						totalCost -= (totalXP % costXP);
						amount = totalCost / costXP;
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
					if (finalXP == newTotalXP && finalXP == totalXP - totalCost) {
						//try to put xp bottles in inventory
						HashMap<Integer, ItemStack> hash = inventory.addItem(new ItemStack(Material.EXP_BOTTLE, amount));
						//otherwise replace glass bottles in hand
						if (!hash.isEmpty()) {
							Iterator<Integer> it = hash.keySet().iterator();
							if (it.hasNext()) {
								p.setItemInHand(hash.get(it.next()));
							}
						}
						playerWaitHash.put(p.getName(), System.currentTimeMillis());
						p.sendMessage(ChatColor.RED+"["+bottleO.pluginName+"]"+ChatColor.WHITE+" you have made "+amount+" exp bottles");
						bottleO.log.info("success! "+p.getName()+", "+totalXP+", "+finalXP+", "+totalCost);
					}
					// this should never happen
					else {
						p.setTotalExperience(0);
						p.setLevel(0);
						p.setExp(0);
						bottleO.log.info("panic! "+p.getName()+", "+totalXP+", "+finalXP+", "+totalCost);
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
