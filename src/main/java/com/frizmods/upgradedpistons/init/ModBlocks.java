package com.frizmods.upgradedpistons.init;

import java.util.ArrayList;
import java.util.List;

import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonBase;
import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonExtension;
import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonMoving;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlocks 
{
	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	//Blocks
	public static final Block UPGRADED_PISTON = new BlockUpgradedPistonBase("upgraded_piston", false);
	public static final Block UPGRADED_STICKY_PISTON = new BlockUpgradedPistonBase("upgraded_sticky_piston", true);
	
	public static final Block UPGRADED_PISTON_HEAD = new BlockUpgradedPistonExtension("upgraded_piston_head").setUnlocalizedName("upgraded_piston_head");
	public static final Block UPGRADED_PISTON_EXTENSION = new BlockUpgradedPistonMoving("upgraded_piston_extension").setUnlocalizedName("upgraded_piston_head");
}
