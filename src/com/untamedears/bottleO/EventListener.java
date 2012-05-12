package com.untamedears.bottleO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ExpBottleEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EventListener implements Listener {
	
	protected static int minXP = 25;
	protected static int maxXP = 25;
	protected static int costXP = 25;
	protected static Random rand;
	
	private float getNextXpJump(int level) {
		return (float)(3.5*level) + (float)(6.7);
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onExpBottleEvent(ExpBottleEvent e) {
		int xp = minXP;
		if (maxXP > minXP) {
			xp += rand.nextInt(maxXP-minXP);
		}
		e.setExperience(xp);
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onPlayerInteractEvent(PlayerInteractEvent e) {
		
		if (e.getMaterial() == Material.GLASS_BOTTLE && e.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE) {
			Player p = e.getPlayer();
			Integer initialAmount = p.getItemInHand().getAmount();
			Integer amount = initialAmount;
			Integer totalXP = p.getTotalExperience();
			Integer totalCost = amount*costXP;
			PlayerInventory inventory = p.getInventory();
			Integer newTotalXP;
			
			if (costXP > 0) {
				if (totalXP < totalCost) {
					if (totalXP >= costXP) {
						totalCost = totalXP;
						totalCost -= totalXP % costXP;
						amount = totalCost / costXP;
					} else {
						amount = 0;
					}
				}
				if (amount > 0) {
					ItemStack stack = p.getItemInHand();
					stack.setAmount(initialAmount-amount);
					p.setItemInHand(stack);
					float remainingXP = totalXP-totalCost;
					int levels = 0;
					float next = getNextXpJump(levels);
					while (remainingXP >= next) {
						levels++;
						remainingXP-=next;
						next=getNextXpJump(levels);
					}
					
					newTotalXP = totalXP-totalCost;
					p.setTotalExperience(0);
					p.setLevel(0);
					p.setExp(0);
					p.giveExp(newTotalXP);
					HashMap<Integer, ItemStack> hash = inventory.addItem(new ItemStack(Material.EXP_BOTTLE, amount));
					if (!hash.isEmpty()) {
						Iterator<Integer> it = hash.keySet().iterator();
						if (it.hasNext()) {
							p.setItemInHand(hash.get(it.next()));
						}
					}
					
				}
			}
		}
	}
}
