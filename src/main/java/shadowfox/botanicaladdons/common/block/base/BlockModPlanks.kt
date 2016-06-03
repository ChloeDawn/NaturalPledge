package shadowfox.botanicaladdons.common.block.base

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraftforge.oredict.OreDictionary

/**
 * @author WireSegal
 * Created at 10:29 PM on 5/27/16.
 */
open class BlockModPlanks(name: String, vararg variants: String) : BlockMod(name, Material.WOOD, *variants) {
    init {
        soundType = SoundType.WOOD
        setHardness(2f)
        setResistance(5f)
        if (hasItem)
            OreDictionary.registerOre("plankWood", ItemStack(this, 1, OreDictionary.WILDCARD_VALUE))
    }

    override fun getHarvestTool(state: IBlockState?): String? {
        return "axe"
    }

    override fun isToolEffective(type: String?, state: IBlockState?): Boolean {
        return type == "axe"
    }

    override fun getFlammability(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = 20
    override fun getFireSpreadSpeed(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?) = 5
}
