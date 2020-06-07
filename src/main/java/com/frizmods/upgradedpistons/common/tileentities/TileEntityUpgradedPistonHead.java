package com.frizmods.upgradedpistons.common.tileentities;

import java.util.List;

import javax.annotation.Nullable;

import com.frizmods.upgradedpistons.Main;
import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonBase;
import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonHead;
import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonMoving;
import com.frizmods.upgradedpistons.common.blocks.BlockUpgradedPistonRod;
import com.frizmods.upgradedpistons.init.ModBlocks;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityUpgradedPistonHead extends TileEntity implements ITickable
{
	
	
	private IBlockState pistonState;
    private EnumFacing pistonFacing;
    /** if this piston is extending or not */
    private boolean extending;
    private boolean isPistonExtension; //determines if this tile entity instance is the piston extension or a block to move
    private boolean isPistonExtensionHead; //determines if the tile entity instance in the piston extension head, should only be true if the isPistonExtension is true
    private int extensionLength = 1; //determines the total length the extension is from the piston base  
    private int extensionOffset = 0; //determines the extension offset from the piston base, default is the head of the piston extension to have 0 extensionLength, then piston rods will have a greater than 0 offset
    
    
    private static final ThreadLocal<EnumFacing> MOVING_ENTITY = new ThreadLocal<EnumFacing>()
    {
        protected EnumFacing initialValue()
        {
            return null;
        }
    };
    
    private float progress;
    /** the progress in (de)extending */
    private float lastProgress;

    public TileEntityUpgradedPistonHead()
    {
    }

    public TileEntityUpgradedPistonHead(IBlockState pistonStateIn, EnumFacing pistonFacingIn, boolean extendingIn, boolean isPistonExtensionIn, int extendedLengthIn, int extensionOffsetIn)
    {
        this.pistonState = pistonStateIn;
        this.pistonFacing = pistonFacingIn;
        this.extending = extendingIn;
        this.isPistonExtension = isPistonExtensionIn;
        this.extensionLength = extendedLengthIn;
        this.extensionOffset = extensionOffsetIn;
        this.isPistonExtensionHead = Boolean.valueOf(isPistonExtensionIn && extensionOffsetIn == extendedLengthIn);
    }

    public IBlockState getPistonState()
    {
        return this.pistonState;
    }
    
    public int getExtensionLength()
    {
    	return this.extensionLength;
    }
    
    public int getExtensionOffset()
    {
    	return this.extensionOffset;
    }

    public NBTTagCompound getUpdateTag()
    {
        return this.writeToNBT(new NBTTagCompound());
    }

    public int getBlockMetadata()
    {
        return 0;
    }

    /**
     * Returns true if a piston is extending
     */
    public boolean isExtending()
    {
        return this.extending;
    }

    public EnumFacing getFacing()
    {
        return this.pistonFacing;
    }

    public boolean isPistonExtension()
    {
        return this.isPistonExtension;
    }
    
    public boolean isPistonExtensionHead()
    {
        return this.isPistonExtensionHead;
    }

    /**
     * Get interpolated progress value (between lastProgress and progress) given the fractional time between ticks as an
     * argument
     */
    @SideOnly(Side.CLIENT)
    public float getProgress(float ticks)
    {
        if (ticks > 1.0F)
        {
            ticks = 1.0F;
        }

        return this.lastProgress + (this.progress - this.lastProgress) * ticks;
    }
    
    @SideOnly(Side.CLIENT)
    public float getExtendedProgressAbs(float ticks)
    {
        return this.getProgress(ticks)*extensionLength;
    }

    @SideOnly(Side.CLIENT)
    public float getOffsetX(float ticks)
    {
        return (float)this.pistonFacing.getFrontOffsetX() * this.getExtendedProgress(this.getProgress(ticks));
    }

    @SideOnly(Side.CLIENT)
    public float getOffsetY(float ticks)
    {
        return (float)this.pistonFacing.getFrontOffsetY() * this.getExtendedProgress(this.getProgress(ticks));
    }

    @SideOnly(Side.CLIENT)
    public float getOffsetZ(float ticks)
    {
        return (float)this.pistonFacing.getFrontOffsetZ() * this.getExtendedProgress(this.getProgress(ticks));
    }

    private float getExtendedProgress(float progress)
    {
        return this.extending ? (progress*(float)this.extensionLength) - (float)this.extensionLength : (float)this.extensionLength - (progress*(float)this.extensionLength);
    }
    
    
    
    
	/*
	 * Called from the moving piston block
	 */
    public AxisAlignedBB getAABB(IBlockAccess source, BlockPos pos)
    {
        return this.getAABB(source, pos, this.progress).union(this.getAABB(source, pos, this.lastProgress));
    }

    public AxisAlignedBB getAABB(IBlockAccess source, BlockPos pos, float currentProgress)
    {
        currentProgress = this.getExtendedProgress(currentProgress);
        IBlockState iblockstate = !this.isExtending() && this.isPistonExtension() 
        		? ModBlocks.UPGRADED_PISTON_HEAD.getDefaultState()
        		.withProperty(BlockUpgradedPistonHead.TYPE, this.pistonState.getBlock() == ModBlocks.UPGRADED_STICKY_PISTON ? BlockUpgradedPistonHead.EnumPistonType.STICKY : BlockUpgradedPistonHead.EnumPistonType.DEFAULT)
        		.withProperty(BlockUpgradedPistonHead.FACING, this.pistonState.getValue(BlockUpgradedPistonBase.FACING))
        		.withProperty(BlockUpgradedPistonHead.SHORT, true ? Boolean.valueOf(false) : Boolean.valueOf(true))
        		: this.pistonState;
        return iblockstate.getBoundingBox(source, pos).offset((double)(currentProgress * (float)this.pistonFacing.getFrontOffsetX()), (double)(currentProgress * (float)this.pistonFacing.getFrontOffsetY()), (double)(currentProgress * (float)this.pistonFacing.getFrontOffsetZ()));
    }
    
    /**
     * removes a piston's tile entity (and if the piston is moving, stops it)
     */
    public void clearPistonTileEntity()
    {
        if (this.lastProgress < 1.0F && this.world != null)
        {
            this.progress = 1.0F;
            this.lastProgress = this.progress;
            this.world.removeTileEntity(this.pos);
            this.invalidate();

            if (this.world.getBlockState(this.pos).getBlock() == ModBlocks.UPGRADED_PISTON_MOVING)
            {
                this.world.setBlockState(this.pos, this.pistonState, 3);
                this.world.neighborChanged(this.pos, this.pistonState.getBlock(), this.pos);
            }
        }
    }

    /**
     * Like the old updateEntity(), except more generic.
     */
    public void update()
    {
        this.lastProgress = this.progress;

        if (this.lastProgress >= 1.0F)
        {
            this.world.removeTileEntity(this.pos);
            this.invalidate();

			if (this.world.getBlockState(this.pos).getBlock() == ModBlocks.UPGRADED_PISTON_MOVING)
            {
				//this.progress = 0;
                this.world.setBlockState(this.pos, this.pistonState, 3);
                this.world.neighborChanged(this.pos, this.pistonState.getBlock(), this.pos);
                
                //this.world.addBlockEvent(this.pos, this, 1, pistonFacing.getIndex());
            }
        }
        else
        {
        	Main.logger.info("TileEntityUpgradedPistonHead:update: progress="+this.progress+" pos="+this.pos);
        	for (int i = this.extensionOffset - 1; i > 0; i--)
                {
        	if (this.progress >= ((float)i / (float)this.extensionOffset) && this.extending)
        	{
        		BlockPos rodBlockPos = this.pos.offset(this.pistonFacing.getOpposite(), i);
            	TileEntity tempTileEntity = world.getTileEntity(rodBlockPos);
            	TileEntityUpgradedPistonRod rodTileEntity = null;
            	if (tempTileEntity instanceof TileEntityUpgradedPistonRod)
            	{
            		rodTileEntity = (TileEntityUpgradedPistonRod)tempTileEntity;
            		//this.world.setBlockState(rodBlockPos, rodTileEntity.getPistonState(), 3);
            		//pistonArmBlockList.add(rodTileEntity.getBlockType());
            		//this.renderStateModel(rodBlockPos, rodTileEntity.getPistonState(), bufferbuilder, world, false);
            	}
        		
        		
//        		BlockPos blockpos = this.pos.offset(this.pistonFacing, (int)this.getExtendedProgress(this.progress));
//        		
//        		BlockUpgradedPistonHead.EnumPistonType isStickyEnum = this.pistonState.getBlock() == ModBlocks.UPGRADED_STICKY_PISTON ? BlockUpgradedPistonHead.EnumPistonType.STICKY : BlockUpgradedPistonHead.EnumPistonType.DEFAULT;
//                IBlockState pistonHeadBlockState = ModBlocks.UPGRADED_PISTON_ROD.getDefaultState()
//                		.withProperty(BlockUpgradedPistonRod.FACING, this.pistonFacing)
//                		.withProperty(BlockUpgradedPistonRod.SHORT, true);
//                IBlockState pistonMovingBlockState = ModBlocks.UPGRADED_PISTON_MOVING.getDefaultState()
//                		.withProperty(BlockUpgradedPistonMoving.FACING, this.pistonFacing)
//                		.withProperty(BlockUpgradedPistonMoving.TYPE, isStickyEnum);
//                
//        		this.world.setBlockState(blockpos, pistonMovingBlockState, 3);
//        		this.world.setTileEntity(blockpos, BlockUpgradedPistonMoving.createTilePistonRod(pistonHeadBlockState, this.pistonFacing, this.extending, true));
//        		this.world.notifyNeighborsOfStateChange(blockpos, ModBlocks.UPGRADED_PISTON_ROD, false);
        		
        		//this.world.setBlockState(blockpos, this.pistonState, 3);
        		//this.world.neighborChanged(blockpos, this.pistonState.getBlock(), this.pos);
        	}
                }
        	
            float f = this.progress + 0.5F;//TODO: use this to slow down the piston extension
            //this.moveCollidedEntities(f);
            this.progress = f;
            
            if (this.progress >= 1.0F)
            {
                this.progress = 1.0F;
            }
        }
    }
    
    /*
     * Called from the moving piston block
     */
    public void addCollissionAABBs(World worldIn, BlockPos blockPosIn, AxisAlignedBB axisAlignedBB, List<AxisAlignedBB> listAxisAlignedBB, @Nullable Entity entityIn)
    {
        if (!this.extending && this.isPistonExtension)
        {
            this.pistonState.withProperty(BlockUpgradedPistonBase.EXTENDED, Boolean.valueOf(true)).addCollisionBoxToList(worldIn, blockPosIn, axisAlignedBB, listAxisAlignedBB, entityIn, false);
        }

        EnumFacing enumfacing = MOVING_ENTITY.get();

        if ((double)this.progress >= 1.0D || enumfacing != (this.extending ? this.pistonFacing : this.pistonFacing.getOpposite()))
        {
            int i = listAxisAlignedBB.size();
            IBlockState iblockstate;

            if (this.isPistonExtension())
            {
                iblockstate = ModBlocks.UPGRADED_PISTON_HEAD.getDefaultState()
                		.withProperty(BlockUpgradedPistonHead.FACING, this.pistonFacing)
                		.withProperty(BlockUpgradedPistonHead.SHORT, Boolean.valueOf(true));
            }
            else
            {
                iblockstate = this.pistonState;
            }
            
            float f = this.getExtendedProgress(this.progress);
            double d0 = (double)((float)this.pistonFacing.getFrontOffsetX() * f);
            double d1 = (double)((float)this.pistonFacing.getFrontOffsetY() * f);
            double d2 = (double)((float)this.pistonFacing.getFrontOffsetZ() * f);
            iblockstate.addCollisionBoxToList(worldIn, blockPosIn, axisAlignedBB.offset(-d0, -d1, -d2), listAxisAlignedBB, entityIn, true);
            
            for (int j = i; j < listAxisAlignedBB.size(); ++j)
            {
                listAxisAlignedBB.set(j, ((AxisAlignedBB)listAxisAlignedBB.get(j)).offset(d0, d1, d2));
            }
        }
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        this.pistonState = Block.getBlockById(compound.getInteger("blockId")).getStateFromMeta(compound.getInteger("blockData"));
        this.pistonFacing = EnumFacing.getFront(compound.getInteger("facing"));
        this.progress = compound.getFloat("progress");
        this.lastProgress = this.progress;
        this.extending = compound.getBoolean("extending");
        this.isPistonExtension = compound.getBoolean("source");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setInteger("blockId", Block.getIdFromBlock(this.pistonState.getBlock()));
        compound.setInteger("blockData", this.pistonState.getBlock().getMetaFromState(this.pistonState));
        compound.setInteger("facing", this.pistonFacing.getIndex());
        compound.setFloat("progress", this.lastProgress);
        compound.setBoolean("extending", this.extending);
        compound.setBoolean("source", this.isPistonExtension);
        return compound;
    }
}
