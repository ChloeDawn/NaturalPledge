package shadowfox.botanicaladdons.common.block

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenTrees
import shadowfox.botanicaladdons.api.SaplingVariantRegistry
import shadowfox.botanicaladdons.api.sapling.IIridescentSaplingVariant
import shadowfox.botanicaladdons.api.sapling.IridescentSaplingBaseVariant
import shadowfox.botanicaladdons.common.block.base.BlockModSapling
import shadowfox.botanicaladdons.common.block.colored.BlockIridescentDirt
import shadowfox.botanicaladdons.common.lexicon.LexiconEntries
import vazkii.botania.api.lexicon.ILexiconable
import vazkii.botania.api.lexicon.LexiconEntry
import vazkii.botania.api.state.BotaniaStateProps
import vazkii.botania.api.state.enums.AltGrassVariant
import java.util.*
import vazkii.botania.common.block.ModBlocks as BotaniaBlocks

/**
 * @author WireSegal
 * Created at 4:01 PM on 5/14/16.
 */
class BlockIrisSapling(name: String) : BlockModSapling(name), ILexiconable {

    companion object {
        class IridescentDirtSaplingVariant : IIridescentSaplingVariant {
            override fun getLeaves(soil: IBlockState): IBlockState {
                val dye = soil.getValue(BlockIridescentDirt.COLOR)
                val colorSet = dye.metadata / 4
                val block = ModBlocks.irisLeaves[colorSet]
                return block.defaultState.withProperty(block.COLOR, dye)
            }

            override fun getWood(soil: IBlockState): IBlockState {
                val dye = soil.getValue(BlockIridescentDirt.COLOR)
                val colorSet = dye.metadata / 4
                val block = ModBlocks.irisLogs[colorSet]
                return block.defaultState.withProperty(block.COLOR, dye)
            }

            override fun matchesSoil(soil: IBlockState): Boolean {
                return soil.block == ModBlocks.irisDirt
            }

            override fun getDisplaySoilBlockstates(): MutableList<IBlockState>? {
                return mutableListOf(*Array(16) {
                    ModBlocks.irisDirt.defaultState.withProperty(BlockIridescentDirt.COLOR, EnumDyeColor.byMetadata(it))
                })
            }
        }

        class AltGrassSaplingVariant : IIridescentSaplingVariant {
            override fun getLeaves(soil: IBlockState): IBlockState {
                val variant = soil.getValue(BotaniaStateProps.ALTGRASS_VARIANT)
                val colorSet = variant.ordinal / 4
                val block = ModBlocks.altLeaves[colorSet]
                return block.defaultState.withProperty(block.TYPE, variant)
            }

            override fun getWood(soil: IBlockState): IBlockState {
                val variant = soil.getValue(BotaniaStateProps.ALTGRASS_VARIANT)
                val colorSet = variant.ordinal / 4
                val block = ModBlocks.altLogs[colorSet]
                return block.defaultState.withProperty(block.TYPE, variant)
            }

            override fun matchesSoil(soil: IBlockState): Boolean {
                return soil.block == BotaniaBlocks.altGrass
            }

            override fun getDisplaySoilBlockstates(): MutableList<IBlockState>? {
                return mutableListOf(*Array(6) {
                    BotaniaBlocks.altGrass.defaultState.withProperty(BotaniaStateProps.ALTGRASS_VARIANT, AltGrassVariant.values()[it])
                })
            }
        }
    }

    init {
        SaplingVariantRegistry.registerVariant("irisDirt", IridescentDirtSaplingVariant())
        SaplingVariantRegistry.registerVariant("altGrass", AltGrassSaplingVariant())
        SaplingVariantRegistry.registerVariant("rainbowDirt",
                IridescentSaplingBaseVariant(
                        ModBlocks.rainbowDirt.defaultState,
                        ModBlocks.rainbowLog.defaultState,
                        ModBlocks.rainbowLeaves.defaultState))
    }

    override fun canSustain(state: IBlockState): Boolean {
        return SaplingVariantRegistry.getVariant(state) != null
    }

    override fun generateTree(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        val soil = worldIn.getBlockState(pos.down()) ?: return
        val variant = SaplingVariantRegistry.getVariant(soil) ?: return

        if (!net.minecraftforge.event.terraingen.TerrainGen.saplingGrowTree(worldIn, rand, pos)) return

        worldIn.setBlockState(pos, Blocks.AIR.defaultState, 4)

        if (!WorldGenTrees(true, 4, variant.getWood(soil), variant.getLeaves(soil), false).generate(worldIn, rand, pos)) {
            worldIn.setBlockState(pos, state, 4)
        } else {
            worldIn.setBlockState(pos.down(), soil, 4)
        }
    }

    override fun canGrow(worldIn: World, pos: BlockPos, state: IBlockState, isClient: Boolean): Boolean {
        return canSustain(worldIn.getBlockState(pos.down()))
    }

    override fun getEntry(p0: World?, p1: BlockPos?, p2: EntityPlayer?, p3: ItemStack?): LexiconEntry? {
        return LexiconEntries.sapling
    }
}
