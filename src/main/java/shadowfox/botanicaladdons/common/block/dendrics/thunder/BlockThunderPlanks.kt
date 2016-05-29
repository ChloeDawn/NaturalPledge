package shadowfox.botanicaladdons.common.block.dendrics.thunder

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.event.sound.PlaySoundEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import shadowfox.botanicaladdons.api.sapling.ISealingBlock
import shadowfox.botanicaladdons.common.block.base.BlockModPlanks
import shadowfox.botanicaladdons.common.block.dendrics.thunder.IThunderAbsorber
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.api.lexicon.LexiconEntry

/**
 * @author WireSegal
 * Created at 10:36 PM on 5/27/16.
 */
class BlockThunderPlanks(name: String) : BlockModPlanks(name), IThunderAbsorber, ILexiconable {
    override fun getEntry(p0: World?, p1: BlockPos?, p2: EntityPlayer?, p3: ItemStack?): LexiconEntry? {
        return null //todo
    }
}
