package com.frizmods.upgradedpistons.common.blocks;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.frizmods.upgradedpistons.Main;
import com.frizmods.upgradedpistons.common.util.IHasModel;
import com.frizmods.upgradedpistons.init.ModBlocks;
import com.frizmods.upgradedpistons.init.ModItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockUpgradedPistonRod extends BlockDirectional
{
    public static final PropertyBool SHORT = PropertyBool.create("short");
    
    //used for piston rod
    protected static final AxisAlignedBB UP_ARM_AABB = new AxisAlignedBB(0.375D, -0.25D, 0.375D, 0.625D, 0.75D, 0.625D);
    protected static final AxisAlignedBB DOWN_ARM_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.25D, 0.625D);
    protected static final AxisAlignedBB SOUTH_ARM_AABB = new AxisAlignedBB(0.375D, 0.375D, -0.25D, 0.625D, 0.625D, 0.75D);
    protected static final AxisAlignedBB NORTH_ARM_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.25D, 0.625D, 0.625D, 1.25D);
    protected static final AxisAlignedBB EAST_ARM_AABB = new AxisAlignedBB(-0.25D, 0.375D, 0.375D, 0.75D, 0.625D, 0.625D);
    protected static final AxisAlignedBB WEST_ARM_AABB = new AxisAlignedBB(0.25D, 0.375D, 0.375D, 1.25D, 0.625D, 0.625D);
    
    //used for short version of piston rod
    protected static final AxisAlignedBB SHORT_UP_ARM_AABB = new AxisAlignedBB(0.375D, 0.0D, 0.375D, 0.625D, 0.75D, 0.625D);
    protected static final AxisAlignedBB SHORT_DOWN_ARM_AABB = new AxisAlignedBB(0.375D, 0.25D, 0.375D, 0.625D, 1.0D, 0.625D);
    protected static final AxisAlignedBB SHORT_SOUTH_ARM_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.0D, 0.625D, 0.625D, 0.75D);
    protected static final AxisAlignedBB SHORT_NORTH_ARM_AABB = new AxisAlignedBB(0.375D, 0.375D, 0.25D, 0.625D, 0.625D, 1.0D);
    protected static final AxisAlignedBB SHORT_EAST_ARM_AABB = new AxisAlignedBB(0.0D, 0.375D, 0.375D, 0.75D, 0.625D, 0.625D);
    protected static final AxisAlignedBB SHORT_WEST_ARM_AABB = new AxisAlignedBB(0.25D, 0.375D, 0.375D, 1.0D, 0.625D, 0.625D);

    public BlockUpgradedPistonRod(String name)
    {
    	super(Material.PISTON);
    	setRegistryName(name);//sets the registry name of the new block
    	this.setSoundType(SoundType.STONE);
        this.setHardness(0.5F);
        
        this.setDefaultState(this.blockState.getBaseState()
        		.withProperty(FACING, EnumFacing.NORTH)
        		.withProperty(SHORT, Boolean.valueOf(false)));
        
        ModBlocks.BLOCKS.add(this);
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
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
        pos = pos.offset(enumfacing);
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

        if (iblockstate.getBlock() instanceof BlockUpgradedPistonBase)
        {
            iblockstate.neighborChanged(worldIn, blockpos, blockIn, fromPos);
        }
        else
        {
            worldIn.setBlockToAir(pos);
        }
    }

    @Nullable
    public static EnumFacing getFacing(int meta)
    {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
    	//TODO: make it so even if extended the rod can find the base of the piston and return the base item
        return null;//new ItemStack(state.getValue(TYPE) == BlockUpgradedPistonRod.EnumPistonType.STICKY ? ModBlocks.UPGRADED_STICKY_PISTON : ModBlocks.UPGRADED_PISTON);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
    	//TODO: test this
        return this.getDefaultState()
        		.withProperty(FACING, getFacing(meta));
        		//.withProperty(TYPE, (meta & 8) > 0 ? BlockUpgradedPistonRod.EnumPistonType.STICKY : BlockUpgradedPistonRod.EnumPistonType.DEFAULT);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

        //if (state.getValue(TYPE) == BlockUpgradedPistonRod.EnumPistonType.STICKY)
        //{
        //    i |= 8;
        //}

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
        return new BlockStateContainer(this, new IProperty[] {FACING, SHORT});
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
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face == state.getValue(FACING) ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    public static enum EnumPistonType implements IStringSerializable
    {
        DEFAULT("normal"),
        STICKY("sticky");

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