package com.frizmods.upgradedpistons.common.items;

import com.frizmods.upgradedpistons.Main;
import com.frizmods.upgradedpistons.init.ModItems;
import com.frizmods.upgradedpistons.common.util.IHasModel;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel{
	
	public ItemBase(String name)
	{
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(CreativeTabs.REDSTONE);
		
		ModItems.ITEMS.add(this);
	}

	@Override
	public void registerModels() {
		Main.proxy.registerItemRenderer(this, 0, "inventory");
	}

}
