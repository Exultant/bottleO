package com.untamedears.bottleO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

public class Enchantments {
	
	private List<EnchantmentPair> possibleEnchantments = new ArrayList<EnchantmentPair>();
	private Map<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
	

	public Enchantments(int level, Material material) {
		
		int enchantability;
		
		switch(material) {
		case WOOD:
		case WOOD_AXE:
		case WOOD_HOE:
		case WOOD_PICKAXE:
		case WOOD_SPADE:
		case WOOD_SWORD:
		case LEATHER:
		case LEATHER_BOOTS:
		case LEATHER_CHESTPLATE:
		case LEATHER_HELMET:
		case LEATHER_LEGGINGS:
			enchantability = 15;
			break;
		
		case STONE:
		case STONE_AXE:
		case STONE_HOE:
		case STONE_PICKAXE:
		case STONE_SPADE:
		case STONE_SWORD:
			enchantability = 5;
			break;
			
		case IRON_AXE:
		case IRON_HOE:
		case IRON_PICKAXE:
		case IRON_SPADE:
		case IRON_SWORD:
			enchantability = 14;
			break;
		
		case IRON_BOOTS:
		case IRON_CHESTPLATE:
		case IRON_HELMET:
		case IRON_LEGGINGS:
			enchantability = 9;
			break;
		
		case CHAINMAIL_BOOTS:
		case CHAINMAIL_CHESTPLATE:
		case CHAINMAIL_HELMET:
		case CHAINMAIL_LEGGINGS:
			enchantability = 12;
			break;
			
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_PICKAXE:
		case DIAMOND_SPADE:
		case DIAMOND_SWORD:
		case DIAMOND_BOOTS:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_HELMET:
		case DIAMOND_LEGGINGS:
			enchantability = 10;
			break;
		
		case GOLD_AXE:
		case GOLD_HOE:
		case GOLD_PICKAXE:
		case GOLD_SPADE:
		case GOLD_SWORD:
			enchantability = 22;
			break;
		
		case GOLD_BOOTS:
		case GOLD_CHESTPLATE:
		case GOLD_HELMET:
		case GOLD_LEGGINGS:
			enchantability = 25;
			break;
		
		case BOW:
			enchantability = 1;
			
		default:
			// Raise an error?
			enchantability = 0;
		}
		
		Random rnd = new Random();
		int modified_level = (int)((float)(level + rnd.nextInt(enchantability) + 1) * (0.75f + (rnd.nextFloat() / 2.0f)));

		switch (material) {
		case LEATHER:
		case LEATHER_BOOTS:
		case LEATHER_CHESTPLATE:
		case LEATHER_HELMET:
		case LEATHER_LEGGINGS:
		case IRON_BOOTS:
		case IRON_CHESTPLATE:
		case IRON_HELMET:
		case IRON_LEGGINGS:
		case CHAINMAIL_BOOTS:
		case CHAINMAIL_CHESTPLATE:
		case CHAINMAIL_HELMET:
		case CHAINMAIL_LEGGINGS:
		case DIAMOND_BOOTS:
		case DIAMOND_CHESTPLATE:
		case DIAMOND_HELMET:
		case DIAMOND_LEGGINGS:
		case GOLD_BOOTS:
		case GOLD_CHESTPLATE:
		case GOLD_HELMET:
		case GOLD_LEGGINGS:
			for (int i = 0; i < 10; i++) {
				
				if (modified_level >= 1 && modified_level <= 21) {
					this.add(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
				}
				
				if (modified_level >= 17 && modified_level <= 37) {
					this.add(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
				}
				
				if (modified_level >= 33 && modified_level <= 53) {
					this.add(Enchantment.PROTECTION_ENVIRONMENTAL, 3);
				}
				
				if (modified_level >= 49 && modified_level <= 69) {
					this.add(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
				}
			}
			
			for (int i = 0; i < 5; i++) {
				if (modified_level >= 10 && modified_level <= 22) {
					this.add(Enchantment.PROTECTION_FIRE, 1);
				}
	
				if (modified_level >= 18 && modified_level <= 30) {
					this.add(Enchantment.PROTECTION_FIRE, 2);
				}
	
				if (modified_level >= 26 && modified_level <= 38) {
					this.add(Enchantment.PROTECTION_FIRE, 3);
				}
	
				if (modified_level >= 34 && modified_level <= 46) {
					this.add(Enchantment.PROTECTION_FIRE, 4);
				}
	
				if (modified_level >= 5 && modified_level <= 15) {
					this.add(Enchantment.PROTECTION_FALL, 1);
				}
	
				if (modified_level >= 11 && modified_level <= 21) {
					this.add(Enchantment.PROTECTION_FALL, 2);
				}
				
				if (modified_level >= 17 && modified_level <= 27) {
					this.add(Enchantment.PROTECTION_FALL, 3);
				}
	
				if (modified_level >= 23 && modified_level <= 33) {
					this.add(Enchantment.PROTECTION_FALL, 4);
				}
				
				if (modified_level >= 3 && modified_level <= 18) {
					this.add(Enchantment.PROTECTION_PROJECTILE, 1);
				}
				
				if (modified_level >= 9 && modified_level <= 24) {
					this.add(Enchantment.PROTECTION_PROJECTILE, 2);
				}
				
				if (modified_level >= 15 && modified_level <= 30) {
					this.add(Enchantment.PROTECTION_PROJECTILE, 3);
				}
				
				if (modified_level >= 21 && modified_level <= 36) {
					this.add(Enchantment.PROTECTION_PROJECTILE, 4);
				}
			}

			for (int i = 0; i < 2; i++) {
				if (modified_level >= 5 && modified_level <= 17) {
					this.add(Enchantment.PROTECTION_EXPLOSIONS, 1);
				}
				
				if (modified_level >= 13 && modified_level <= 25) {
					this.add(Enchantment.PROTECTION_EXPLOSIONS, 2);
				}
				
				if (modified_level >= 21 && modified_level <= 33) {
					this.add(Enchantment.PROTECTION_EXPLOSIONS, 3);
				}
	
				if (modified_level >= 29 && modified_level <= 41) {
					this.add(Enchantment.PROTECTION_EXPLOSIONS, 4);
				}
						
				if (modified_level >= 10 && modified_level <= 40) {
					this.add(Enchantment.OXYGEN, 1);
				}
	
				if (modified_level >= 20 && modified_level <= 50) {
					this.add(Enchantment.OXYGEN, 2);
				}
				
				if (modified_level >= 30 && modified_level <= 60) {
					this.add(Enchantment.OXYGEN, 3);
				}
	
				if (modified_level >= 1 && modified_level <= 41) {
					this.add(Enchantment.WATER_WORKER, 1);
				}
			}
			
			break;
		
		case WOOD_SWORD:
		case STONE_SWORD:
		case IRON_SWORD:
		case DIAMOND_SWORD:
		case GOLD_SWORD:
			
			for (int i = 0; i < 10; i++) {
				if (modified_level >= 1 && modified_level <= 21) {
					this.add(Enchantment.DAMAGE_ALL, 1);
				}
				
				if (modified_level >= 17 && modified_level <= 37) {
					this.add(Enchantment.DAMAGE_ALL, 2);
				}
				
				if (modified_level >= 33 && modified_level <= 53) {
					this.add(Enchantment.DAMAGE_ALL, 3);
				}
				
				if (modified_level >= 49 && modified_level <= 69) {
					this.add(Enchantment.DAMAGE_ALL, 4);
				}
	
				if (modified_level >= 65 && modified_level <= 85) {
					this.add(Enchantment.DAMAGE_ALL, 5);
				}
			}

			for (int i = 0; i < 5; i++) {
				if (modified_level >= 5 && modified_level <= 25) {
					this.add(Enchantment.DAMAGE_UNDEAD, 1);
				}
				
				if (modified_level >= 13 && modified_level <= 33) {
					this.add(Enchantment.DAMAGE_UNDEAD, 2);
				}
				
				if (modified_level >= 21 && modified_level <= 41) {
					this.add(Enchantment.DAMAGE_UNDEAD, 3);
				}
				
				if (modified_level >= 29 && modified_level <= 49) {
					this.add(Enchantment.DAMAGE_UNDEAD, 4);
				}
	
				if (modified_level >= 37 && modified_level <= 57) {
					this.add(Enchantment.DAMAGE_UNDEAD, 5);
				}
	
				if (modified_level >= 5 && modified_level <= 25) {
					this.add(Enchantment.DAMAGE_ARTHROPODS, 1);
				}
				
				if (modified_level >= 13 && modified_level <= 33) {
					this.add(Enchantment.DAMAGE_ARTHROPODS, 2);
				}
				
				if (modified_level >= 21 && modified_level <= 41) {
					this.add(Enchantment.DAMAGE_ARTHROPODS, 3);
				}
				
				if (modified_level >= 29 && modified_level <= 49) {
					this.add(Enchantment.DAMAGE_ARTHROPODS, 4);
				}
	
				if (modified_level >= 37 && modified_level <= 57) {
					this.add(Enchantment.DAMAGE_ARTHROPODS, 5);
				}
	
				if (modified_level >= 5 && modified_level <= 55) {
					this.add(Enchantment.KNOCKBACK, 1);
				}
				
				if (modified_level >= 25 && modified_level <= 75) {
					this.add(Enchantment.KNOCKBACK, 2);
				}
			}

			for (int i = 0; i < 2; i++) {
				if (modified_level >= 10 && modified_level <= 60) {
					this.add(Enchantment.FIRE_ASPECT, 1);
				}
				
				if (modified_level >= 30 && modified_level <= 80) {
					this.add(Enchantment.FIRE_ASPECT, 2);
				}
				
				if (modified_level >= 20 && modified_level <= 70) {
					this.add(Enchantment.LOOT_BONUS_MOBS, 1);
				}
				
				if (modified_level >= 32 && modified_level <= 82) {
					this.add(Enchantment.LOOT_BONUS_MOBS, 2);
				}
				
				if (modified_level >= 44 && modified_level <= 94) {
					this.add(Enchantment.LOOT_BONUS_MOBS, 3);
				}
			}
			break;
			
		case BOW:
			
			for (int i = 0; i < 10; i++) {
				if (modified_level >= 1 && modified_level <= 16) {
					this.add(Enchantment.ARROW_DAMAGE, 1);
				}
				
				if (modified_level >= 11 && modified_level <= 26) {
					this.add(Enchantment.ARROW_DAMAGE, 2);
				}
				
				if (modified_level >= 21 && modified_level <= 36) {
					this.add(Enchantment.ARROW_DAMAGE, 3);
				}
				
				if (modified_level >= 41 && modified_level <= 46) {
					this.add(Enchantment.ARROW_DAMAGE, 4);
				}
				
				if (modified_level >= 51 && modified_level <= 56) {
					this.add(Enchantment.ARROW_DAMAGE, 5);
				}
			}
			
			for (int i = 0; i < 2; i++) {
				if (modified_level >= 12 && modified_level <= 37) {
					this.add(Enchantment.ARROW_KNOCKBACK, 1);
				}
				
				if (modified_level >= 32 && modified_level <= 57) {
					this.add(Enchantment.ARROW_KNOCKBACK, 2);
				}
	
				if (modified_level >= 20 && modified_level <= 50) {
					this.add(Enchantment.ARROW_FIRE, 1);
				}
			}
			
			if (modified_level >= 20 && modified_level <= 50) {
				this.add(Enchantment.ARROW_INFINITE, 1);
			}

			break;
		
		case WOOD:
		case WOOD_AXE:
		case WOOD_HOE:
		case WOOD_PICKAXE:
		case WOOD_SPADE:
		case STONE:
		case STONE_AXE:
		case STONE_HOE:
		case STONE_PICKAXE:
		case STONE_SPADE:
		case IRON_AXE:
		case IRON_HOE:
		case IRON_PICKAXE:
		case IRON_SPADE:
		case DIAMOND_AXE:
		case DIAMOND_HOE:
		case DIAMOND_PICKAXE:
		case DIAMOND_SPADE:
			
			for (int i = 0; i < 10; i++) {
				if (modified_level >= 1 && modified_level <= 51) {
					this.add(Enchantment.DIG_SPEED, 1);
				}
				
				if (modified_level >= 16 && modified_level <= 66) {
					this.add(Enchantment.DIG_SPEED, 2);
				}
				
				if (modified_level >= 31 && modified_level <= 81) {
					this.add(Enchantment.DIG_SPEED, 3);
				}
				
				if (modified_level >= 46 && modified_level <= 96) {
					this.add(Enchantment.DIG_SPEED, 4);
				}
				
				if (modified_level >= 61 && modified_level <= 111) {
					this.add(Enchantment.DIG_SPEED, 5);
				}
			}
			
			if (modified_level >= 25 && modified_level <= 75) {
				this.add(Enchantment.SILK_TOUCH, 1);
			}
			
			for (int i = 0; i < 5; i++) {
				if (modified_level >= 5 && modified_level <= 55) {
					this.add(Enchantment.DURABILITY, 1);
				}
				
				if (modified_level >= 15 && modified_level <= 65) {
					this.add(Enchantment.DURABILITY, 2);
				}
				
				if (modified_level >= 25 && modified_level <= 75) {
					this.add(Enchantment.DURABILITY, 3);
				}
			}
			
			for (int i = 0; i < 2; i++) {
				if (modified_level >= 20 && modified_level <= 70) {
					this.add(Enchantment.LOOT_BONUS_BLOCKS, 1);
				}
				
				if (modified_level >= 32 && modified_level <= 82) {
					this.add(Enchantment.LOOT_BONUS_BLOCKS, 2);
				}
				
				if (modified_level >= 44 && modified_level <= 94) {
					this.add(Enchantment.LOOT_BONUS_BLOCKS, 3);
				}
			}
			
			break;
		
		default:
			// Raise an error?
		}
		
		do {
			if (this.possibleEnchantments.size() <= 0) {
				return;
			}
			
			int index = rnd.nextInt(this.possibleEnchantments.size());
			EnchantmentPair pair = this.possibleEnchantments.get(index);

			//System.out.println("Choosing enchantment '" + pair.enchantment.toString() + "', level " + pair.level);
			this.enchantments.put(pair.enchantment, pair.level);

			for (ListIterator<EnchantmentPair> iter = this.possibleEnchantments.listIterator(this.possibleEnchantments.size()); iter.hasPrevious();) {
			    EnchantmentPair otherPair = iter.previous();
				
				if (otherPair.enchantment.conflictsWith(pair.enchantment)) {
					//System.out.println("Removing conflicting enchantment '" + otherPair.enchantment.toString() + "', level " + otherPair.level);
					iter.remove();
				}
			}
			
			modified_level = (int)Math.floor((float)modified_level / 2.0f);
			
		} while ((float)(modified_level + 1) / 50.0f > rnd.nextFloat());
			
	}
	
	
	private void add(Enchantment enchantment, int level) {
		this.possibleEnchantments.add(new EnchantmentPair(enchantment, level));
		//System.out.println("Possible enchantment '" + enchantment.toString() + "', level " + level);
	}
	
	public int getCount() {
		return this.enchantments.size();
	}


	public void applyEnchantments(Map<Enchantment, Integer> enchantsToAdd) {
		enchantsToAdd.clear();
		
		Iterator<?> iter = this.enchantments.entrySet().iterator();
	    while (iter.hasNext()) {
	        Entry<Enchantment, Integer> pairs = (Entry<Enchantment, Integer>)iter.next();
	        enchantsToAdd.put(pairs.getKey(), pairs.getValue());
	    }
	}
	
	
	
}