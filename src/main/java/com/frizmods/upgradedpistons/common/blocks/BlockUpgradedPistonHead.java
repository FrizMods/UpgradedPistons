package com.frizmods.upgradedpistons.common.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.frizmods.upgradedpistons.Main;
import com.frizmods.upgradedpistons.common.tileentities.TileEntityUpgradedPistonHead;
import com.frizmods.upgradedpistons.common.tileentities.TileEntityUpgradedPistonRod;
import com.frizmods.upgradedpistons.common.util.IHasModel;
import com.frizmods.upgradedpistons.init.ModBlocks;
import com.frizmods.upgradedpistons.init.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockUpgradedPistonHead extends BlockDirectional
{
    public static final PropertyEnum<BlockUpgradedPistonHead.EnumPistonType> TYPE = PropertyEnum.<BlockUpgradedPistonHead.EnumPistonType>create("type", BlockUpgradedPistonHead.EnumPistonType.class);
    public static final PropertyBool SHORT = PropertyBool.create("short");
    public static final PropertyInteger EXT_LENGTH = PropertyInteger.create("ext_length", 0, 1024);
    public static final PropertyInteger EXT_OFFSET = PropertyInteger.create("ext_offset", 0, 1024);
    
    //used for piston head collision box
    protected static final AxisAlignedBB PISTON_EXTENSION_EAST_AABB = new AxisAlignedBB(0.75D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_WEST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.25D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.75D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.25D);
    protected static final AxisAlignedBB PISTON_EXTENSION_UP_AABB = new AxisAlignedBB(0.0D, 0.75D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_EXTENSION_DOWN_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
    
    //used for piston rod
    protected static final AxisAlignedBB UP_ARM_AABB = new AxisAlignedBB(0.375D, -0.25D, 0.375D, 0.625D, 0.75D, 0.625D);
    protected static final AxisAlignedBB DOWN_ARM_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.25D, 0.625D);
    protected static final AxisAlignedBB SOUTH_ARM_AABB = new AxisAlignedBB(0.375D, 0.375D, -0.25D, 0.625D, 0.625D, 0.75D);
    protected static final AxisAlignedBB NORTH_ARM_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.25D, 0.625D, 0.625D, 1.25D);
    protected static final AxisAlignedBB EAST_ARM_AABB = new AxisAlignedBB(-0.25D, 0.375D, 0.375D, 0.75D, 0.625D, 0.625D);
    protected static final AxisAlignedBB WEST_ARM_AABB = new AxisAlignedBB(0.25D, 0.375D, 0.375D, 1.25D, 0.625D, 0.625D);
    
    //used for short version of piston rod
    protected static final AxisAlignedBB SHORT_UP_ARM_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 0.75D, 0.625D);
    protected static final AxisAlignedBB SHORT_DOWN_ARM_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 0.75D, 0.625D);
    protected static final AxisAlignedBB SHORT_SOUTH_ARM_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.25D, 0.625D, 0.625D, 0.75D);
    protected static final AxisAlignedBB SHORT_NORTH_ARM_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.25D, 0.625D, 0.625D, 0.75D);
    protected static final AxisAlignedBB SHORT_EAST_ARM_AABB = new AxisAlignedBB(0.25D, 0.375D, 0.375D, 0.75D, 0.625D, 0.625D);
    protected static final AxisAlignedBB SHORT_WEST_ARM_AABB = new AxisAlignedBB(0.25D, 0.375D, 0.375D, 0.75D, 0.625D, 0.625D);
    
    private AxisAlignedBB lastLookedAtBoundingBox = new AxisAlignedBB(0,0,0,0,0,0);
    
    //TODO: extension length parameter, 1 is default
    private int extensionLength = 1;
    private int extensionOffset = 1;

    public BlockUpgradedPistonHead(String name)
    {
    	super(Material.PISTON);
    	setRegistryName(name);//sets the registry name of the new block
    	this.setSoundType(SoundType.STONE);
        this.setHardness(0.5F);
        
        this.setDefaultState(this.blockState.getBaseState()
        		.withProperty(FACING, EnumFacing.NORTH)
        		.withProperty(TYPE, BlockUpgradedPistonHead.EnumPistonType.DEFAULT)
        		.withProperty(SHORT, Boolean.valueOf(false)));
        
        ModBlocks.BLOCKS.add(this);
    }
    
    /*
     * This is overiding the default ray check to fix the issue of the piston arm not having a boundingbox
     */
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
    	RayTraceResult raytraceresult = this.rayTrace(pos, start, end, blockState.getBoundingBox(worldIn, pos));
    	if(raytraceresult == null)
    	{
    		raytraceresult = this.rayTrace(pos, start, end, this.getArmShape(blockState));
    		if(raytraceresult != null)
    		{
    			lastLookedAtBoundingBox = this.getArmShape(blockState);
    		}
    	}
    	else
    	{
    		lastLookedAtBoundingBox = blockState.getBoundingBox(worldIn, pos);
    	}
        return raytraceresult;
    }
    
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
    	return this.lastLookedAtBoundingBox.offset(pos);
    }
    
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {    	
        switch ((EnumFacing)state.getValue(FACING))
        {
            case DOWN:
            default:
                return PISTON_EXTENSION_DOWN_AABB;
            case UP:
                return PISTON_EXTENSION_UP_AABB;
            case NORTH:
                return PISTON_EXTENSION_NORTH_AABB;
            case SOUTH:
                return PISTON_EXTENSION_SOUTH_AABB;
            case WEST:
                return PISTON_EXTENSION_WEST_AABB;
            case EAST:
                return PISTON_EXTENSION_EAST_AABB;
        }
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getBoundingBox(worldIn, pos));//head collision box
        addCollisionBoxToList(pos, entityBox, collidingBoxes, this.getArmShape(state));//rod collision box
    }

    private AxisAlignedBB getArmShape(IBlockState state)
    {
        boolean flag = ((Boolean)state.getValue(SHORT)).booleanValue();;

        switch ((EnumFacing)state.getValue(FACING))
        {
            case DOWN:
            default:
                return flag ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB;
            case UP:
                return flag ? SHORT_UP_ARM_AABB : UP_ARM_AABB;
            case NORTH:
                return flag ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB;
            case SOUTH:
                return flag ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB;
            case WEST:
                return flag ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB;
            case EAST:
                return flag ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB;
        }
    }

    /**
     * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
     */
    public boolean isTopSolid(IBlockState state)
    {
        return state.getValue(FACING) == EnumFacing.UP;
    }

    /**
     * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually
     * collect this block
     */
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode)
        {
            BlockPos blockpos = pos.offset(((EnumFacing)state.getValue(FACING)).getOpposite());
            Block block = worldIn.getBlockState(blockpos).getBlock();

            if (block instanceof BlockUpgradedPistonBase)
            {
                worldIn.setBlockToAir(blockpos);
            }
        }

        super.onBlockHarvested(worldIn, pos, state, player);
    }

    /**
     * Called serverside after this block is replaced with another in Chunk, but before the Tile Entity is updated
     */
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        super.breakBlock(worldIn, pos, state);
        EnumFacing enumfacing = ((EnumFacing)state.getValue(FACING)).getOpposite();
        pos = pos.offset(enumfacing, this.extensionOffset);
        IBlockState iblockstate = worldIn.getBlockState(pos);

        if ((iblockstate.getBlock() instanceof BlockUpgradedPistonBase) && ((Boolean)iblockstate.getValue(BlockUpgradedPistonBase.EXTENDED)).booleanValue())
        {
            iblockstate.getBlock().dropBlockAsItem(worldIn, pos, iblockstate, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /**
     * Checks if this block can be placed exactly at the given position.
     */
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return false;
    }

    /**
     * Check whether this Block can be placed at pos, while aiming at the specified side of an adjacent block
     */
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side)
    {
        return false;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random)
    {
        return 0;
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {    	
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        BlockPos blockpos = pos.offset(enumfacing.getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() instanceof BlockUpgradedPistonBase || iblockstate.getBlock() instanceof BlockUpgradedPistonHead || iblockstate.getBlock() instanceof BlockUpgradedPistonMoving)
        {
            iblockstate.neighborChanged(worldIn, blockpos, blockIn, fromPos);
        }
        else
        {
            worldIn.setBlockToAir(pos);
        }
    }
    
    /**
     * Called on server when World#addBlockEvent is called. If server returns true, then also called on the client. On
     * the Server, this may perform additional changes to the world, like pistons replacing the block with an extended
     * base. On the client, the update may involve replacing tile entities or effects such as sounds or particles
     */
    public boolean eventReceived(IBlockState state, World worldIn, BlockPos pos, int id, int param)
    {
    	EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
    	Main.logger.info("BlockUpgradedPistonHead:eventReceived: "+pos+" orientation="+enumfacing+" param="+param+" remote="+worldIn.isRemote);
        return true;
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Nullable
    public static EnumFacing getFacing(int meta)
    {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(state.getValue(TYPE) == BlockUpgradedPistonHead.EnumPistonType.STICKY ? ModBlocks.UPGRADED_STICKY_PISTON : ModBlocks.UPGRADED_PISTON);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(TYPE, (meta & 8) > 0 ? BlockUpgradedPistonHead.EnumPistonType.STICKY : BlockUpgradedPistonHead.EnumPistonType.DEFAULT);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (state.getValue(TYPE) == BlockUpgradedPistonHead.EnumPistonType.STICKY)
        {
            i |= 8;
        }

        return i;
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate((EnumFacing)state.getValue(FACING)));
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     */
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation((EnumFacing)state.getValue(FACING)));
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, new IProperty[] {FACING, SHORT, TYPE});
    }

    /**
     * Get the geometry of the queried face at the given position and state. This is used to decide whether things like
     * buttons are allowed to be placed on the face, or how glass panes connect to the face, among other things.
     * <p>
     * Common values are {@code SOLID}, which is the default, and {@code UNDEFINED}, which represents something that
     * does not fit the other descriptions and will generally cause other things not to connect to the face.
     * 
     * @return an approximation of the form of the given face
     */
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing enumfacing)
    {
    	BlockPos pistonBaseBlockPos = pos.offset(enumfacing.getOpposite(), 1);
        return enumfacing == state.getValue(FACING) && pos != pistonBaseBlockPos ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    public static enum EnumPistonType implements IStringSerializable
    {
        DEFAULT("normal"),
        STICKY("sticky"),
        ROD_ONLY("rod_only");

        private final String VARIANT;

        private EnumPistonType(String name)
        {
            this.VARIANT = name;
        }

        public String toString()
        {
            return this.VARIANT;
        }

        public String getName()
        {
            return this.VARIANT;
        }
    }
}