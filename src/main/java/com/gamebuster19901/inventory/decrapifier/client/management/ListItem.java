package com.gamebuster19901.inventory.decrapifier.client.management;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.oredict.OreDictionary;

public final class ListItem{
	private final String identifier;
	private final int[] idRange;
	private final boolean isOre;
	private final long startTime = currentTime();
	
	public ListItem(){
		isOre = false;
		identifier = null;
		idRange = null;
	}
	
	private ListItem(String idOrOre, boolean isOre){
		identifier = idOrOre;
		this.isOre = isOre;
		assert this.isOre == true || this.isOre == false;
		idRange = new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE};
	};
	
	private ListItem(String idOrOre, boolean isOre, int[] range){
		identifier = idOrOre;
		this.isOre = isOre;
		assert this.isOre == true || this.isOre == false;
		this.idRange = new int[]{range[0], range[1]};
	};
	
	public int getIndexBasedOffTimer(){
		if (!isOre()){
			long start = getData()[0];
			if(start < 0) {
				return 0;
			}
			long duration = (long)getData()[1] - start + 1l;
			if (duration == 0){
				return 0;
			}
			int ret = (int) (currentTime() % duration + start);
			return ret;
		}
		else{
			long duration = OreDictionary.getOres(identifier).size();
			if (duration == 0){
				throw new AssertionError();
			}
			int ret = (int)(currentTime() % duration);
			return ret;
		}
	}
	
	private static final long currentTime() {
		return System.currentTimeMillis() / 1000l;
	}
	
	@SuppressWarnings("finally")
	public static final ListItem fromString(String idOrOre, boolean isOre){
		if (isOre && OreDictionary.doesOreNameExist(idOrOre)){
			return new ListItem(idOrOre, isOre);
		}
		else if (!isOre){
			String itemString;
			String idRange;
			
			if (idOrOre.indexOf('[') == -1){
				idRange = "[0]";
				itemString = idOrOre;
			}
			else if (idOrOre.indexOf(']') != -1){
				itemString = idOrOre.substring(0, idOrOre.indexOf('['));
				idRange = idOrOre.substring(idOrOre.indexOf('['));
			}
			else{
				return null;
			}
			
			Item item;
			int[] range = null;
			
			item = Item.getByNameOrId(itemString);
			
			if (item == null){
				return null;
			}
			
			if (idRange.equals("[*]")){
				range = new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE};
			}
			else if (idRange.lastIndexOf(',') != -1){
				try{
					int min = Integer.parseInt(idRange.substring(1, idRange.lastIndexOf(',')));
					int max = Integer.parseInt(idRange.substring(idRange.lastIndexOf(',') + 1, idRange.length() - 1));
					if (min <= max){
						range = new int[]{min, max};
					}
					else{
						return null;
					}
				}
				catch (Exception e){
					e.printStackTrace();
					try{
						Integer.parseInt(idRange.substring(1, idRange.lastIndexOf(',')));
					}
					catch (Exception e1){
						e.printStackTrace();
					}
					try{
						Integer.parseInt(idRange.substring(idRange.lastIndexOf(',') + 1, idRange.length() -1));
					}
					catch (Exception e2){

					}
					finally{
						return null;
					}
				}
			}
			else{
				try{
					int x = Integer.parseInt(idRange.substring(1, idRange.indexOf(']')));
					range = new int[]{x,x};
				}
				catch (Exception e2){
					return null;
				}
			}
			return new ListItem(itemString, isOre, range);
		}
		return null;
	}
	
	public final boolean contains(Object o){
		if (o instanceof ItemStack){
			if (isOre){
				List<ItemStack> ores = OreDictionary.getOres(identifier);
				for(ItemStack ore : ores){
					if (((ItemStack) o).getItem().getRegistryName().equals(ore.getItem().getRegistryName())){
						return true;
					}
				}
			}
			else if (!isOre){
				Item i;
				if (identifier.contains("[")){
					i = Item.getByNameOrId(identifier.substring(0, identifier.indexOf('[')));
				}
				else{
					i = Item.getByNameOrId(identifier);
				}
				if (((ItemStack)o).getItem().getRegistryName().equals(i.getRegistryName())){
					if (idRange[0] <= ((ItemStack)o).getItemDamage() && idRange[1] >= ((ItemStack)o).getItemDamage()){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public final Item getItem(){
		return Item.getByNameOrId(identifier);
	}
	
	public final Item getItem(int i){
		return OreDictionary.getOres(identifier).get(i).getItem();
	}
	
	public final int[] getData(){
		if (!isOre){
			return idRange;
		}
		else{
			return null;
		}
	}
	
	public final String getDataAsString(){
		String ret;
		if (idRange[0] == Integer.MIN_VALUE && idRange[1] == Integer.MAX_VALUE){
			if (isOre){
				return "";
			}
			return "[*]";
		}
		if (idRange[0] == idRange[1]){
			if (new String("[" + idRange[0] + "]").length() < 5){
				return "[" + idRange[0] + "]";
			}
			return "[...]";
		}
		if (new String("[" + idRange[0] + "," + idRange[1] + "]").length() < 5){
			return "[" + idRange[0] + "," + idRange[1] + "]";
		}
		return "[...]";
	}
	
	@Override
	public final String toString(){
		if(isOre){
			return identifier;
		}
		else{
			if (idRange[0] == Integer.MIN_VALUE && idRange[1] == Integer.MAX_VALUE){
				return identifier + "[*]";
			}
			if (idRange[0] == idRange[1]){
				if (idRange[0] != 0){
					return identifier + "[" + idRange[0] + "]";
				}
				return identifier;
			}
			return identifier + "[" + idRange[0] + "," + idRange[1] + "]";
		}
	}
	
	public ItemStack getPolyStack(){
		if (!isOre()){
			return new ItemStack(getItem(), 1, getIndexBasedOffTimer());
		}
		else{
			return new ItemStack(getItem(0));
		}
	}
	
	@Override
	public boolean equals(Object o){
		ListItem l = (ListItem)o;
		if (o instanceof ListItem){
			if (this.identifier.equals(l.identifier) && this.idRange[0] == l.idRange[0] && this.idRange[1] == l.idRange[1] && this.isOre == l.isOre){
				return true;
			}
			else{

			}
		}
		return false;
	}
	
	public final boolean isOre(){
		return isOre;
	}
}
