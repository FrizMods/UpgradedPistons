package com.frizmods.upgradedpistons.client.renderer;

import java.util.List;

import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonBase;
import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonHead;
import com.frizmods.upgradedpistons.common.tileentities.TileEntityUpgradedPistonHead;
import com.frizmods.upgradedpistons.common.tileentities.TileEntityUpgradedPistonRod;
import com.frizmods.upgradedpistons.init.ModBlocks;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityUpgradedPistonRenderer extends TileEntitySpecialRenderer<TileEntityUpgradedPistonHead>
{	
	private BlockRendererDispatcher blockRenderer;
	
	private final List<Block> pistonArmBlockList = Lists.<Block>newArrayList();
	
	public void render(TileEntityUpgradedPistonHead te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
		World world = this.getWorld();
		
        if (blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher(); //Forge: Delay this from constructor to allow us to change it later
        BlockPos headBlockPos = te.getPos();
        IBlockState iblockstate = te.getPistonState();
        Block block = iblockstate.getBlock();
        int extensionLength = te.getExtensionLength();
        int extensionOffset = te.getExtensionOffset();
        boolean isPistonExtension = te.isPistonExtension();        

        if (iblockstate.getMaterial() != Material.AIR && te.getProgress(partialTicks) < 1.0F)
        {
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferbuilder = tessellator.getBuffer();
            this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.disableCull();

            if (Minecraft.isAmbientOcclusionEnabled())
            {
                GlStateManager.shadeModel(7425);
            }
            else
            {
                GlStateManager.shadeModel(7424);
            }

            bufferbuilder.begin(7, DefaultVertexFormats.BLOCK);
            bufferbuilder.setTranslation(x - (double)headBlockPos.getX() + (double)te.getOffsetX(partialTicks), y - (double)headBlockPos.getY() + (double)te.getOffsetY(partialTicks), z - (double)headBlockPos.getZ() + (double)te.getOffsetZ(partialTicks));
            
    		if (isPistonExtension)
    		{
    			if (te.isExtending())
	            {
    				if (te.isPistonExtensionHead())
    				{
	    				iblockstate = iblockstate.withProperty(BlockUpgradedPistonHead.SHORT, Boolean.valueOf(te.getProgress(partialTicks) <= 0.25F));
	                    this.renderStateModel(headBlockPos, iblockstate, bufferbuilder, world, true);
    				}
    				else if (te.getExtendedProgressAbs(partialTicks) >= extensionLength - extensionOffset)
    				{
    					iblockstate = iblockstate.withProperty(BlockUpgradedPistonHead.SHORT, Boolean.valueOf(te.getExtendedProgressAbs(partialTicks) <= (float)(extensionLength - extensionOffset) + 0.25F));
	                    this.renderStateModel(headBlockPos, iblockstate, bufferbuilder, world, true);
    				}
	            }
    			else
    			{
    				if (te.isPistonExtensionHead())
    				{
		    			BlockUpgradedPistonHead.EnumPistonType BlockUpgradedPistonExtension$enumpistontype = block == ModBlocks.UPGRADED_STICKY_PISTON ? BlockUpgradedPistonHead.EnumPistonType.STICKY : BlockUpgradedPistonHead.EnumPistonType.DEFAULT;
		                IBlockState iblockstate1 = ModBlocks.UPGRADED_PISTON_HEAD.getDefaultState()
		                		.withProperty(BlockUpgradedPistonHead.TYPE, BlockUpgradedPistonExtension$enumpistontype)
		                		.withProperty(BlockUpgradedPistonHead.FACING, iblockstate.getValue(BlockUpgradedPistonBase.FACING))
		                		.withProperty(BlockUpgradedPistonHead.SHORT, Boolean.valueOf(te.getProgress(partialTicks) >= 0.75F));
		                //sets the moving head extension sstate on pullin
		                this.renderStateModel(headBlockPos, iblockstate1, bufferbuilder, world, true);
		                
		                //sets the block base location during the movment
		                bufferbuilder.setTranslation(x - (double)headBlockPos.getX(), y - (double)headBlockPos.getY(), z - (double)headBlockPos.getZ());
		                //sets the base block render state on pull in
		                iblockstate = iblockstate.withProperty(BlockUpgradedPistonBase.EXTENDED, Boolean.valueOf(true));
		                this.renderStateModel(headBlockPos, iblockstate, bufferbuilder, world, true);
    				}
    				else
    				{
    					
    				}
    			}
    		}
    		else
    		{
    			//renders the head of the piston moving to blockpos from current position found in blockstate
                this.renderStateModel(headBlockPos, iblockstate, bufferbuilder, world, false);
    		}

            bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
            tessellator.draw();
            RenderHelper.enableStandardItemLighting();
        }
    }

    private boolean renderStateModel(BlockPos pos, IBlockState state, BufferBuilder buffer, World p_188186_4_, boolean checkSides)
    {
        return this.blockRenderer.getBlockModelRenderer().renderModel(p_188186_4_, this.blockRenderer.getModelForState(state), state, pos, buffer, checkSides);
    }
}
