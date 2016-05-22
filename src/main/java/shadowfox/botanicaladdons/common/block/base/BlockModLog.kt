package shadowfox.botanicaladdons.common.block.base

import net.minecraft.block.BlockLog
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.util.EnumFacing
import net.minecraft.util.Rotation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * @author WireSegal
 * Created at 10:36 AM on 5/7/16.
 */
open class BlockModLog(name: String, vararg variants: String) : BlockMod(name, Material.WOOD, *variants) {
    companion object {
        val AXIS = PropertyEnum.create("axis", BlockLog.EnumAxis::class.java)
    }

    init {
        blockHardness = 2.0f
        defaultState = defaultState.withProperty(AXIS, BlockLog.EnumAxis.Y)
    }

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val i = 4
        val j = i + 1

        if (worldIn.isAreaLoaded(pos.add(-j, -j, -j), pos.add(j, j, j))) {
            for (blockpos in BlockPos.getAllInBox(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                val iblockstate = worldIn.getBlockState(blockpos)

                if (iblockstate.block.isLeaves(iblockstate, worldIn, blockpos)) {
                    iblockstate.block.beginLeavesDecay(iblockstate, worldIn, blockpos)
                }
            }
        }
    }

    override fun onBlockPlaced(worldIn: World?, pos: BlockPos?, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float, meta: Int, placer: EntityLivingBase?): IBlockState {
        return this.getStateFromMeta(meta).withProperty(AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.axis))
    }

    override fun withRotation(state: IBlockState, rot: Rotation): IBlockState {
        when (rot) {
            Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90 -> {

                when (state.getValue(AXIS)) {
                    BlockLog.EnumAxis.X -> return state.withProperty(AXIS, BlockLog.EnumAxis.Z)
                    BlockLog.EnumAxis.Z -> return state.withProperty(AXIS, BlockLog.EnumAxis.X)
                    else -> return state
                }
            }

            else -> return state
        }
    }

    override fun canSustainLeaves(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun isWood(world: IBlockAccess?, pos: BlockPos?): Boolean {
        return true
    }

    override fun rotateBlock(world: World, pos: BlockPos, axis: EnumFacing?): Boolean {
        val state = world.getBlockState(pos)
        world.setBlockState(pos, state.cycleProperty(AXIS))
        return true
    }

    override fun getStateFromMeta(meta: Int): IBlockState {
        var axis = BlockLog.EnumAxis.Y
        val i = meta and 12

        if (i == 4) {
            axis = BlockLog.EnumAxis.X
        } else if (i == 8) {
            axis = BlockLog.EnumAxis.Z
        }

        return this.defaultState.withProperty(AXIS, axis)
    }

    override fun getMetaFromState(state: IBlockState?): Int {
        var i = 0

        if (state!!.getValue(AXIS) == BlockLog.EnumAxis.X) {
            i = i or 4
        } else if (state.getValue(AXIS) == BlockLog.EnumAxis.Z) {
            i = i or 8
        }

        return i
    }

    override fun createBlockState(): BlockStateContainer? {
        return BlockStateContainer(this, AXIS)
    }

    override fun getHarvestTool(state: IBlockState?): String? {
        return "axe"
    }

    override fun isToolEffective(type: String?, state: IBlockState?): Boolean {
        return type == "axe"
    }
}
