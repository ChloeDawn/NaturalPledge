package shadowfox.botanicaladdons.common.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.ItemRenderer
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.RenderItem
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryHelper
import net.minecraft.item.ItemStack
import net.minecraft.stats.StatList
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityHopper
import net.minecraft.util.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import org.lwjgl.opengl.GL11
import shadowfox.botanicaladdons.common.block.base.BlockModContainer
import shadowfox.botanicaladdons.common.block.tile.TileLivingwoodFunnel
import shadowfox.botanicaladdons.common.lexicon.LexiconEntries
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.api.lexicon.LexiconEntry
import vazkii.botania.api.wand.IWandHUD

/**
 * @author L0neKitsune
 * Created on 3/20/16.
 */
class BlockFunnel(name: String): BlockModContainer(name, Material.WOOD), ILexiconable, IWandHUD {
    companion object {
        val FACING = PropertyDirection.create("facing", { facing -> facing != EnumFacing.UP })
        val ENABLED = PropertyBool.create("enabled")

        fun getActiveStateFromMetadata(meta: Int): Boolean = (meta and 8) != 8
        fun getFacing(meta: Int): EnumFacing = EnumFacing.getFront(meta and 7)


        val BASE_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.625, 1.0)
        val SOUTH_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 1.0, 0.125)
        val NORTH_AABB = AxisAlignedBB(0.0, 0.0, 0.875, 1.0, 1.0, 1.0)
        val WEST_AABB = AxisAlignedBB(0.875, 0.0, 0.0, 1.0, 1.0, 1.0)
        val EAST_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 0.125, 1.0, 1.0)

    }

    override val ignoredProperties: Array<IProperty<*>>
        get() = arrayOf(ENABLED)

    init {
        this.defaultState = this.blockState.baseState.withProperty(FACING, EnumFacing.DOWN).withProperty(ENABLED, true)
        blockHardness = 2f
    }


    override fun getBoundingBox(state: IBlockState?, source: IBlockAccess?, pos: BlockPos?): AxisAlignedBB {
        return FULL_BLOCK_AABB
    }

    override fun addCollisionBoxToList(state: IBlockState, worldIn: World, pos: BlockPos, axis: AxisAlignedBB, lists: List<AxisAlignedBB>, collider: Entity?) {
        addCollisionBoxToList(pos, axis, lists, BASE_AABB)
        addCollisionBoxToList(pos, axis, lists, SOUTH_AABB)
        addCollisionBoxToList(pos, axis, lists, NORTH_AABB)
        addCollisionBoxToList(pos, axis, lists, WEST_AABB)
        addCollisionBoxToList(pos, axis, lists, EAST_AABB)
    }

    override fun onBlockPlaced(worldIn: World?, pos: BlockPos?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase?): IBlockState {
        var enumfacing = facing!!.opposite
        if (enumfacing == EnumFacing.UP) {
            enumfacing = EnumFacing.DOWN
        }

        return this.defaultState.withProperty(FACING, enumfacing).withProperty(ENABLED, java.lang.Boolean.valueOf(true))
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity {
        return TileLivingwoodFunnel()
    }

    override fun onBlockPlacedBy(worldIn: World?, pos: BlockPos?, state: IBlockState?, placer: EntityLivingBase?, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        if (stack!!.hasDisplayName()) {
            val tileentity = worldIn!!.getTileEntity(pos)
            if (tileentity is TileEntityHopper) {
                tileentity.setCustomName(stack.displayName)
            }
        }

    }

    override fun isFullyOpaque(state: IBlockState): Boolean {
        return true
    }

    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        this.updateState(worldIn, pos, state)
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block?) {
        this.updateState(worldIn, pos, state)
    }

    private fun updateState(worldIn: World, pos: BlockPos, state: IBlockState) {
        val flag = !worldIn.isBlockPowered(pos)
        if (flag != (state.getValue(ENABLED) as Boolean)) {
            worldIn.setBlockState(pos, state.withProperty(ENABLED, flag), 4)
        }
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tileentity = worldIn.getTileEntity(pos)
        if (tileentity is IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, tileentity)
            worldIn.updateComparatorOutputLevel(pos, this)
        }

        super.breakBlock(worldIn, pos, state)
    }

    override fun getRenderType(state: IBlockState?): EnumBlockRenderType {
        return EnumBlockRenderType.MODEL
    }

    override fun isFullCube(state: IBlockState?): Boolean {
        return false
    }

    override fun isOpaqueCube(state: IBlockState?): Boolean = false

    @SideOnly(Side.CLIENT)
    override fun shouldSideBeRendered(blockState: IBlockState, blockAccess: IBlockAccess, pos: BlockPos, side: EnumFacing): Boolean = true

    fun getFacing(meta: Int): EnumFacing {
        return EnumFacing.getFront(meta and 7)
    }

    fun isEnabled(meta: Int): Boolean {
        return meta and 8 != 8
    }

    override fun hasComparatorInputOverride(state: IBlockState?): Boolean = true

    override fun getComparatorInputOverride(blockState: IBlockState?, worldIn: World?, pos: BlockPos?): Int {
        return Container.calcRedstone(worldIn!!.getTileEntity(pos))
    }

    @SideOnly(Side.CLIENT)
    override fun getBlockLayer(): BlockRenderLayer = BlockRenderLayer.CUTOUT_MIPPED

    override fun getStateFromMeta(meta: Int): IBlockState {
        return this.defaultState.withProperty(FACING, getFacing(meta)).withProperty(ENABLED, isEnabled(meta))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        val i: Int = 0
        var i1 = i or state.getValue(FACING).index
        if (!(state.getValue(ENABLED) as Boolean)) {
            i1 = i1 or 8
        }

        return i1
    }

    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)))
    }

    override fun withMirror(state: IBlockState, mirrorIn: Mirror): IBlockState {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)))
    }

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, FACING, ENABLED)
    }

    override fun getEntry(p0: World?, p1: BlockPos?, p2: EntityPlayer?, p3: ItemStack?): LexiconEntry? {
        return LexiconEntries.funnel
    }

    @SideOnly(Side.CLIENT)
    override fun renderHUD(mc: Minecraft, res: ScaledResolution, world: World, pos: BlockPos) {
        val te = world.getTileEntity(pos) ?: return
        if (te is TileLivingwoodFunnel) {
            val stack = te.unsidedHandler.getStackInSlot(0) ?: return
            if (stack.stackSize > 0) {
                RenderHelper.enableGUIStandardItemLighting()
                mc.renderItem.renderItemIntoGUI(stack, res.scaledWidth / 2, res.scaledHeight / 2)
                RenderHelper.disableStandardItemLighting()
            }
        }
    }
}
