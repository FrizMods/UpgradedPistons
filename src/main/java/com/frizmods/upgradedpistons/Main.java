package com.frizmods.upgradedpistons;

import com.frizmods.upgradedpistons.client.renderer.TileEntityUpgradedPistonRenderer;
//import com.frizmods.upgradedpistons.init.ModRecipes;
import com.frizmods.upgradedpistons.common.CommonProxy;
import com.frizmods.upgradedpistons.common.tileentities.TileEntityUpgradedPiston;
import com.frizmods.upgradedpistons.common.util.Reference;
//import com.frizmods.upgradedpistons.world.ModWorldGen;

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
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main {
	
	@Instance
	public static Main instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@EventHandler
	public static void PreInit(FMLPreInitializationEvent event)
	{
		//GameRegistry.registerWorldGenerator(new ModWorldGen(), 3);
	}
	
	@EventHandler
	public static void init(FMLInitializationEvent event)
	{
		//ModRecipes.init();
		
		TileEntitySpecialRenderer<TileEntityUpgradedPiston> upgradedPistonRenderer = new TileEntityUpgradedPistonRenderer();
		ClientRegistry.registerTileEntity(TileEntityUpgradedPiston.class, "tileentity_upgraded_piston", upgradedPistonRenderer);
	}
	
	@EventHandler
	public static void Postinit(FMLPostInitializationEvent event)
	{

	}

}

