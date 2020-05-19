package com.frizmods.upgradedpistons.init;

import java.util.ArrayList;
import java.util.List;

import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonBase;
import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonHead;
import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonMoving;
import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonRod;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class ModBlocks 
{
	public static final List<Block> BLOCKS = new ArrayList<Block>();
	
	//Full Blocks
	public static final Block UPGRADED_PISTON = new BlockUpgradedPistonBase("upgraded_piston", false);
	public static final Block UPGRADED_STICKY_PISTON = new BlockUpgradedPistonBase("upgraded_sticky_piston", true);
	
	//Sub Blocks
	public static final Block UPGRADED_PISTON_HEAD = new BlockUpgradedPistonHead("upgraded_piston_head").setUnlocalizedName("upgraded_piston_head");
	public static final Block UPGRADED_PISTON_ROD = new BlockUpgradedPistonRod("upgraded_piston_rod").setUnlocalizedName("upgraded_piston_rod");
	public static final Block UPGRADED_PISTON_MOVING = new BlockUpgradedPistonMoving().setUnlocalizedName("upgraded_piston_moving");
}
