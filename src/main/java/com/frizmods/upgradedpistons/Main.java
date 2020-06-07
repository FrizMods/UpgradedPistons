package com.frizmods.upgradedpistons;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.frizmods.upgradedpistons.client.renderer.TileEntityUpgradedPistonRenderer;
import com.frizmods.upgradedpistons.client.renderer.TileEntityUpgradedPistonRodRenderer;
//import com.frizmods.upgradedpistons.init.ModRecipes;
import com.frizmods.upgradedpistons.common.CommonProxy;
import com.frizmods.upgradedpistons.common.tileentities.TileEntityUpgradedPistonHead;
import com.frizmods.upgradedpistons.common.tileentities.TileEntityUpgradedPistonRod;
import com.frizmods.upgradedpistons.common.util.Reference;
//import com.frizmods.upgradedpistons.world.ModWorldGen;
import com.frizmods.upgradedpistons.init.ModBlocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main 
{
	@Instance
	public static Main instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
	
	public static Logger logger;
	
	@EventHandler
	public static void PreInit(FMLPreInitializationEvent event)
	{
		//GameRegistry.registerWorldGenerator(new ModWorldGen(), 3);
		logger = event.getModLog();
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
		//ModRecipes.init();
		ClientRegistry.registerTileEntity(TileEntityUpgradedPistonHead.class, "upgradedpistons:tileentity_upgraded_piston_head", new TileEntityUpgradedPistonRenderer());
		ClientRegistry.registerTileEntity(TileEntityUpgradedPistonRod.class, "upgradedpistons:tileentity_upgraded_piston_rod", new TileEntityUpgradedPistonRodRenderer());
	}
	
	@EventHandler
	public static void Postinit(FMLPostInitializationEvent event)
	{

	}
}