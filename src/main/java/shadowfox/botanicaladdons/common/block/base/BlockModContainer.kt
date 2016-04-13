package shadowfox.botanicaladdons.common.block.base

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * @author WireSegal
 * Created at 5:45 PM on 3/20/16.
 */
open class BlockModContainer(name: String, materialIn: Material, vararg variants: String) : BlockMod(name, materialIn, *variants), ITileEntityProvider {
    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        super.breakBlock(worldIn, pos, state)
        worldIn.removeTileEntity(pos)
    }

    override fun onBlockEventReceived(worldIn: World?, pos: BlockPos?, state: IBlockState?, eventID: Int, eventParam: Int): Boolean {
        super.onBlockEventReceived(worldIn, pos, state, eventID, eventParam)
        val tileentity = worldIn!!.getTileEntity(pos)
        return if (tileentity == null) false else tileentity.receiveClientEvent(eventID, eventParam)
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return null
    }
}
