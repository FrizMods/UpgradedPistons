package com.frizmods.upgradedpistons.common.blocks;

import java.util.List;

import javax.annotation.Nullable;

import com.frizmods.upgradedpistons.Main;
import com.frizmods.upgradedpistons.common.blocks.state.BlockUpgradedPistonStructureHelper;
import com.frizmods.upgradedpistons.common.tileentities.TileEntityUpgradedPistonHead;
import com.frizmods.upgradedpistons.common.util.IHasModel;
import com.frizmods.upgradedpistons.init.ModBlocks;
import com.frizmods.upgradedpistons.init.ModItems;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockUpgradedPistonBase extends BlockDirectional implements IHasModel
{        
    public static final PropertyBool EXTENDED = PropertyBool.create("extended");
    protected static final AxisAlignedBB PISTON_BASE_EAST_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.75D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_WEST_AABB = new AxisAlignedBB(0.25D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_SOUTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.75D);
    protected static final AxisAlignedBB PISTON_BASE_NORTH_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.25D, 1.0D, 1.0D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_UP_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D);
    protected static final AxisAlignedBB PISTON_BASE_DOWN_AABB = new AxisAlignedBB(0.0D, 0.25D, 0.0D, 1.0D, 1.0D, 1.0D);
    /** This piston is the sticky one? */
    private final boolean isSticky;
    //TODO: number of blocks to stick due to magnetic addon, 1 is default
    private final int stickyOffset = 1;
    //TODO: extension length parameter, 1 is default
    private final int extensionOffset = 1;

    public BlockUpgradedPistonBase(String name, boolean isSticky)
    {
        super(Material.PISTON);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EXTENDED, Boolean.valueOf(false)));
        this.isSticky = isSticky;
        this.setSoundType(SoundType.STONE);
        this.setHardness(0.5F);
        this.setCreativeTab(CreativeTabs.REDSTONE);
        
        setUnlocalizedName(name);//sets the localization of the new block
		setRegistryName(name);//sets the registry name of the new block
		
		ModBlocks.BLOCKS.add(this);
		ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }
    
    @Override
	public void registerModels() 
	{
		Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
	}

    public boolean causesSuffocation(IBlockState state)
    {
        return !((Boolean)state.getValue(EXTENDED)).booleanValue();
    }
    
    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
    	RayTraceResult raytraceresult = this.rayTrace(pos, start, end, blockState.getBoundingBox(worldIn, pos));
    	if(raytraceresult == null)
    	{
    		EnumFacing enumfacing = (EnumFacing)blockState.getValue(FACING);
    		BlockPos pistonHeadBlockPos = pos.offset(enumfacing, 1);
    		raytraceresult = worldIn.getBlockState(pistonHeadBlockPos).collisionRayTrace(worldIn, pistonHeadBlockPos, start, end);
    	}
        return raytraceresult;
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if (((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
            switch ((EnumFacing)state.getValue(FACING))
            {
                case DOWN:
                    return PISTON_BASE_DOWN_AABB;
                case UP:
                default:
                    return PISTON_BASE_UP_AABB;
                case NORTH:
                    return PISTON_BASE_NORTH_AABB;
                case SOUTH:
                    return PISTON_BASE_SOUTH_AABB;
                case WEST:
                    return PISTON_BASE_WEST_AABB;
                case EAST:
                    return PISTON_BASE_EAST_AABB;
            }
        }
        else
        {
            return FULL_BLOCK_AABB;
        }
    }

    /**
     * Determines if the block is solid enough on the top side to support other blocks, like redstone components.
     */
    public boolean isTopSolid(IBlockState state)
    {
        return true;
    }

    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, state.getBoundingBox(worldIn, pos));
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        worldIn.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);

        if (!worldIn.isRemote)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null)
        {
            this.checkForMove(worldIn, pos, state);
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)).withProperty(EXTENDED, Boolean.valueOf(false));
    }

    private void checkForMove(World worldIn, BlockPos pos, IBlockState state)
    {
        EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
        boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

        if (flag && !((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
        	//push out
            if ((new BlockUpgradedPistonStructureHelper(worldIn, pos, enumfacing, true, this.extensionOffset)).canMove())
            {
                worldIn.addBlockEvent(pos, this, 0, enumfacing.getIndex());
            }
        }
        else if (!flag && ((Boolean)state.getValue(EXTENDED)).booleanValue())
        {
        	//pull in
            worldIn.addBlockEvent(pos, this, 1, enumfacing.getIndex());
        }
    }

    private boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing)
    {
        for (EnumFacing enumfacing : EnumFacing.values())
        {
            if (enumfacing != facing && worldIn.isSidePowered(pos.offset(enumfacing), enumfacing))
            {
                return true;
            }
        }

        if (worldIn.isSidePowered(pos, EnumFacing.DOWN))
        {
            return true;
        }
        else
        {
            BlockPos blockpos = pos.up();

            for (EnumFacing enumfacing1 : EnumFacing.values())
            {
                if (enumfacing1 != EnumFacing.DOWN && worldIn.isSidePowered(blockpos.offset(enumfacing1), enumfacing1))
                {
                    return true;
                }
            }

            return false;
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

        if (!worldIn.isRemote)
        {
            boolean flag = this.shouldBeExtended(worldIn, pos, enumfacing);

            if (flag && id == 1)
            {
                worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 2);
                return false;
            }

            if (!flag && id == 0)
            {
                return false;
            }
        }
        
        if (id == 0)
        {
        	//push out
            if (!this.doMove(worldIn, pos, enumfacing, true))
            {
                return false;
            }

            worldIn.setBlockState(pos, state.withProperty(EXTENDED, Boolean.valueOf(true)), 3);
            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PISTON_EXTEND, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
        }
        else if (id == 1)
        {
        	//pull in
            TileEntity tileentity1 = worldIn.getTileEntity(pos.offset(enumfacing));

            if (tileentity1 instanceof TileEntityUpgradedPistonHead)
            {
                ((TileEntityUpgradedPistonHead)tileentity1).clearPistonTileEntity();
            }

            worldIn.setBlockState(pos, ModBlocks.UPGRADED_PISTON_MOVING.getDefaultState()
            		.withProperty(BlockUpgradedPistonMoving.FACING, enumfacing)
            		.withProperty(BlockUpgradedPistonMoving.TYPE, this.isSticky ? BlockUpgradedPistonHead.EnumPistonType.STICKY : BlockUpgradedPistonHead.EnumPistonType.DEFAULT), 3);
            
            worldIn.setTileEntity(pos, BlockUpgradedPistonMoving.createTilePistonHead(this.getStateFromMeta(param), enumfacing, false, true));
            //worldIn.setTileEntity(pos, BlockUpgradedPistonMoving.createTilePistonRod(this.getStateFromMeta(param), enumfacing, false, true));

            if (this.isSticky)
            {
                BlockPos blockpos = pos.add(enumfacing.getFrontOffsetX() * 2, enumfacing.getFrontOffsetY() * 2, enumfacing.getFrontOffsetZ() * 2);
                IBlockState iblockstate = worldIn.getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                boolean flag1 = false;

                if (block instanceof BlockUpgradedPistonMoving)
                {
                    TileEntity tileentity = worldIn.getTileEntity(blockpos);

                    if (tileentity instanceof TileEntityUpgradedPistonHead)
                    {
                        TileEntityUpgradedPistonHead TileEntityUpgradedPiston = (TileEntityUpgradedPistonHead)tileentity;

                        if (TileEntityUpgradedPiston.getFacing() == enumfacing && TileEntityUpgradedPiston.isExtending())
                        {
                            TileEntityUpgradedPiston.clearPistonTileEntity();
                            flag1 = true;
                        }
                    }
                }

                if (!flag1 && !iblockstate.getBlock().isAir(iblockstate, worldIn, blockpos) && canPush(iblockstate, worldIn, blockpos, enumfacing.getOpposite(), false, enumfacing) && (iblockstate.getMobilityFlag() == EnumPushReaction.NORMAL || block == ModBlocks.UPGRADED_PISTON || block == ModBlocks.UPGRADED_STICKY_PISTON))
                {
                    this.doMove(worldIn, pos, enumfacing, false);
                }
            }
            else
            {
                worldIn.setBlockToAir(pos.offset(enumfacing));
            }

            worldIn.playSound((EntityPlayer)null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
        }

        return true;
    }

    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Nullable
    public static EnumFacing getFacing(int meta)
    {
        int i = meta & 7;
        return i > 5 ? null : EnumFacing.getFront(i);
    }

    /**
     * Checks if the piston can push the given BlockState.
     */
    public static boolean canPush(IBlockState blockStateIn, World worldIn, BlockPos pos, EnumFacing facing, boolean destroyBlocks, EnumFacing p_185646_5_)
    {
        Block block = blockStateIn.getBlock();

        if (block == Blocks.OBSIDIAN)
        {
            return false;
        }
        else if (!worldIn.getWorldBorder().contains(pos))
        {
            return false;
        }
        else if (pos.getY() >= 0 && (facing != EnumFacing.DOWN || pos.getY() != 0))
        {
            if (pos.getY() <= worldIn.getHeight() - 1 && (facing != EnumFacing.UP || pos.getY() != worldIn.getHeight() - 1))
            {
            	if(!(block instanceof BlockUpgradedPistonBase) && !(block instanceof BlockPistonBase))
            	{
                    if (blockStateIn.getBlockHardness(worldIn, pos) == -1.0F)
                    {
                        return false;
                    }

                    switch (blockStateIn.getMobilityFlag())
                    {
                        case BLOCK:
                            return false;
                        case DESTROY:
                            return destroyBlocks;
                        case PUSH_ONLY:
                            return facing == p_185646_5_;
                    }
                }
                else if (((Boolean)blockStateIn.getValue(EXTENDED)).booleanValue())
                {
                    return false;
                }

                return !block.hasTileEntity(blockStateIn);
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    private boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending)
    {
        if (!extending)
        {
            worldIn.setBlockToAir(pos.offset(direction));
        }

        BlockUpgradedPistonStructureHelper BlockUpgradedPistonStructureHelper = new BlockUpgradedPistonStructureHelper(worldIn, pos, direction, extending, this.extensionOffset);

        if (!BlockUpgradedPistonStructureHelper.canMove())
        {
            return false;
        }
        else
        {
            List<BlockPos> blocksToMove = BlockUpgradedPistonStructureHelper.getBlocksToMove();
            List<IBlockState> blocksToMoveStates = Lists.<IBlockState>newArrayList();

            for (int i = 0; i < blocksToMove.size(); ++i)
            {
                BlockPos blockpos = blocksToMove.get(i);
                blocksToMoveStates.add(worldIn.getBlockState(blockpos).getActualState(worldIn, blockpos));
            }

            List<BlockPos> blocksToDestroy = BlockUpgradedPistonStructureHelper.getBlocksToDestroy();
            int numBlocksEffected = blocksToMove.size() + blocksToDestroy.size();
            IBlockState[] allBlocksEffectedStates = new IBlockState[numBlocksEffected];
            EnumFacing enumfacing = extending ? direction : direction.getOpposite();

            for (int j = blocksToDestroy.size() - 1; j >= 0; --j)
            {
                BlockPos currentBlockPos = blocksToDestroy.get(j);
                IBlockState currentBlockState = worldIn.getBlockState(currentBlockPos);
                // Forge: With our change to how snowballs are dropped this needs to disallow to mimic vanilla behavior.
                float chance = currentBlockState.getBlock() instanceof BlockSnow ? -1.0f : 1.0f;
                currentBlockState.getBlock().dropBlockAsItemWithChance(worldIn, currentBlockPos, currentBlockState, chance, 0);
                worldIn.setBlockState(currentBlockPos, Blocks.AIR.getDefaultState(), 4);
                --numBlocksEffected;
                allBlocksEffectedStates[numBlocksEffected] = currentBlockState;
            }

            for (int l = blocksToMove.size() - 1; l >= 0; --l)
            {
                BlockPos currentBlockPos = blocksToMove.get(l);
                IBlockState currentBlockState = worldIn.getBlockState(currentBlockPos);
                worldIn.setBlockState(currentBlockPos, Blocks.AIR.getDefaultState(), 2);
                currentBlockPos = currentBlockPos.offset(enumfacing, this.extensionOffset);
                worldIn.setBlockState(currentBlockPos, ModBlocks.UPGRADED_PISTON_MOVING.getDefaultState().withProperty(FACING, direction), 4);
                worldIn.setTileEntity(currentBlockPos, BlockUpgradedPistonMoving.createTilePistonHead(blocksToMoveStates.get(l), direction, extending, false));
                //worldIn.setTileEntity(currentBlockPos, BlockUpgradedPistonMoving.createTilePistonRod(blocksToMoveStates.get(l), direction, extending, false));
                --numBlocksEffected;
                allBlocksEffectedStates[numBlocksEffected] = currentBlockState;
            }

            BlockPos pistonHeadPos = pos.offset(direction, this.extensionOffset);//TODO: this can be used for multi length extension

            if (extending)
            {
                BlockUpgradedPistonHead.EnumPistonType isStickyEnum = this.isSticky ? BlockUpgradedPistonHead.EnumPistonType.STICKY : BlockUpgradedPistonHead.EnumPistonType.DEFAULT;
                IBlockState pistonHeadBlockState = ModBlocks.UPGRADED_PISTON_HEAD.getDefaultState()
                		.withProperty(BlockUpgradedPistonHead.FACING, direction)
                		.withProperty(BlockUpgradedPistonHead.TYPE, isStickyEnum);
                IBlockState pistonMovingBlockState = ModBlocks.UPGRADED_PISTON_MOVING.getDefaultState()
                		.withProperty(BlockUpgradedPistonMoving.FACING, direction)
                		.withProperty(BlockUpgradedPistonMoving.TYPE, isStickyEnum);
                worldIn.setBlockState(pistonHeadPos, pistonMovingBlockState, 4);
                worldIn.setTileEntity(pistonHeadPos, BlockUpgradedPistonMoving.createTilePistonHead(pistonHeadBlockState, direction, true, true));
                //worldIn.setTileEntity(pistonHeadPos, BlockUpgradedPistonMoving.createTilePistonRod(pistonHeadBlockState, direction, true, true));
            }

            for (int i1 = blocksToDestroy.size() - 1; i1 >= 0; --i1)
            {
                worldIn.notifyNeighborsOfStateChange(blocksToDestroy.get(i1), allBlocksEffectedStates[numBlocksEffected++].getBlock(), false);
            }

            for (int j1 = blocksToMove.size() - 1; j1 >= 0; --j1)
            {
                worldIn.notifyNeighborsOfStateChange(blocksToMove.get(j1), allBlocksEffectedStates[numBlocksEffected++].getBlock(), false);
            }

            if (extending)
            {
                worldIn.notifyNeighborsOfStateChange(pistonHeadPos, ModBlocks.UPGRADED_PISTON_HEAD, false);
            }

            return true;
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(EXTENDED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | ((EnumFacing)state.getValue(FACING)).getIndex();

        if (((Boolean)state.getValue(EXTENDED)).booleanValue())
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
        return new BlockStateContainer(this, new IProperty[] {FACING, EXTENDED});
    }

    /* ======================================== FORGE START =====================================*/
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis)
    {
        IBlockState state = world.getBlockState(pos);
        return !state.getValue(EXTENDED) && super.rotateBlock(world, pos, axis);
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
        state = this.getActualState(state, worldIn, pos);
        return BlockFaceShape.SOLID;
    }
}

