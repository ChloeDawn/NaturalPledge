package shadowfox.botanicaladdons.common.block

import net.minecraft.block.properties.PropertyEnum
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import shadowfox.botanicaladdons.api.lib.LibMisc
import shadowfox.botanicaladdons.common.block.alt.BlockAltLeaves
import shadowfox.botanicaladdons.common.block.alt.BlockAltLog
import shadowfox.botanicaladdons.common.block.alt.BlockAltPlanks
import shadowfox.botanicaladdons.common.block.base.BlockMod
import shadowfox.botanicaladdons.common.block.colored.*
import shadowfox.botanicaladdons.common.block.colored.BlockIridescentPlanks.BlockRainbowPlanks
import shadowfox.botanicaladdons.common.block.dendrics.circuit.*
import shadowfox.botanicaladdons.common.block.dendrics.sealing.*
import shadowfox.botanicaladdons.common.block.dendrics.thunder.*
import shadowfox.botanicaladdons.common.block.tile.TilePrismFlame
import shadowfox.botanicaladdons.common.block.tile.TileStar
import shadowfox.botanicaladdons.common.lib.LibNames
import shadowfox.botanicaladdons.common.lib.LibOreDict
import vazkii.botania.api.state.enums.AltGrassVariant
import vazkii.botania.common.block.ModBlocks as BotaniaBlocks

/**
 * @author WireSegal
 * Created at 2:46 PM on 4/17/16.
 */
object ModBlocks {
    val awakenerCore: BlockMod
    val star: BlockMod
    val flame: BlockMod
    val irisDirt: BlockMod
    val rainbowDirt: BlockMod
    val irisPlanks: BlockMod
    val irisLogs: Array<BlockIridescentLog>
    val rainbowPlanks: BlockMod
    val rainbowLog: BlockMod
    val irisLeaves: Array<BlockIridescentLeaves>
    val rainbowLeaves: BlockMod
    val irisSapling: BlockMod
    val altLogs: Array<BlockAltLog>
    val altLeaves: Array<BlockAltLeaves>
    val altPlanks: BlockMod
    val storage: BlockMod

    val sealSapling: BlockMod
    val sealPlanks: BlockMod
    val sealLeaves: BlockMod
    val sealLog: BlockMod

    val thunderSapling: BlockMod
    val thunderPlanks: BlockMod
    val thunderLeaves: BlockMod
    val thunderLog: BlockMod

    val circuitSapling: BlockMod
    val circuitPlanks: BlockMod
    val circuitLeaves: BlockMod
    val circuitLog: BlockMod

    init {
        awakenerCore = BlockAwakenerCore(LibNames.AWAKENER)
        star = BlockFrozenStar(LibNames.STAR)
        flame = BlockPrismFlame(LibNames.PRISM_FLAME)
        irisDirt = BlockIridescentDirt(LibNames.IRIS_DIRT)
        rainbowDirt = BlockRainbowDirt(LibNames.RAINBOW_DIRT)
        irisPlanks = BlockIridescentPlanks(LibNames.IRIS_PLANKS)
        irisLogs = Array(4) { object : BlockIridescentLog(LibNames.IRIS_LOG, it) {
            override val COLOR: PropertyEnum<EnumDyeColor>
                get() = BlockIridescentLog.COLOR_PROPS[it]
        }}
        rainbowPlanks = BlockRainbowPlanks(LibNames.RAINBOW_PLANKS)
        rainbowLog = BlockRainbowLog(LibNames.RAINBOW_LOG)
        irisLeaves = Array(4) { object : BlockIridescentLeaves(LibNames.IRIS_LEAVES, it) {
            override val COLOR: PropertyEnum<EnumDyeColor>
                get() = BlockIridescentLeaves.COLOR_PROPS[it]
        }}
        rainbowLeaves = BlockRainbowLeaves(LibNames.RAINBOW_LEAVES)
        irisSapling = BlockIrisSapling(LibNames.IRIS_SAPLING)
        altLogs = Array(2) { object : BlockAltLog(LibNames.ALT_LOG, it) {
            override val TYPE: PropertyEnum<AltGrassVariant>
                get() = BlockAltLog.TYPE_PROPS[it]
        }}
        altLeaves = Array(2) { object : BlockAltLeaves(LibNames.ALT_LEAVES, it) {
            override val TYPE: PropertyEnum<AltGrassVariant>
                get() = BlockAltLeaves.TYPE_PROPS[it]
        }}
        altPlanks = BlockAltPlanks(LibNames.ALT_PLANKS)
        storage = BlockStorage(LibNames.STORAGE)

        SoundSealEventHandler
        sealSapling = BlockSealSapling(LibNames.SEAL_SAPLING)
        sealPlanks = BlockSealPlanks(LibNames.SEAL_PLANKS)
        sealLeaves = BlockSealLeaves(LibNames.SEAL_LEAVES)
        sealLog = BlockSealingLog(LibNames.SEAL_LOG)

        ThunderEventHandler
        thunderSapling = BlockThunderSapling(LibNames.THUNDER_SAPLING)
        thunderPlanks = BlockThunderPlanks(LibNames.THUNDER_PLANKS)
        thunderLeaves = BlockThunderLeaves(LibNames.THUNDER_LEAVES)
        thunderLog = BlockThunderLog(LibNames.THUNDER_LOG)

        circuitSapling = BlockCircuitSapling(LibNames.CIRCUIT_SAPLING)
        circuitPlanks = BlockCircuitPlanks(LibNames.CIRCUIT_PLANKS)
        circuitLeaves = BlockCircuitLeaves(LibNames.CIRCUIT_LEAVES)
        circuitLog = BlockCircuitLog(LibNames.CIRCUIT_LOG)

        GameRegistry.registerTileEntity(TileStar::class.java, ResourceLocation(LibMisc.MOD_ID, LibNames.STAR).toString())
        GameRegistry.registerTileEntity(TilePrismFlame::class.java, ResourceLocation(LibMisc.MOD_ID, LibNames.PRISM_FLAME).toString())

        OreDictionary.registerOre(LibOreDict.BLOCK_AQUAMARINE, ItemStack(storage, 1, BlockStorage.Variants.AQUAMARINE.ordinal))
        OreDictionary.registerOre(LibOreDict.BLOCK_THUNDERSTEEL, ItemStack(storage, 1, BlockStorage.Variants.THUNDERSTEEL.ordinal))

        OreDictionary.registerOre("treeSapling", ItemStack(sealSapling, 1, OreDictionary.WILDCARD_VALUE))
        OreDictionary.registerOre("treeSapling", ItemStack(irisSapling, 1, OreDictionary.WILDCARD_VALUE))

        OreDictionary.registerOre("logWood", ItemStack(rainbowLog, 1, OreDictionary.WILDCARD_VALUE))
        OreDictionary.registerOre("logWood", ItemStack(sealLog, 1, OreDictionary.WILDCARD_VALUE))
        for (log in irisLogs) OreDictionary.registerOre("logWood", ItemStack(log, 1, OreDictionary.WILDCARD_VALUE))
        for (log in altLogs) OreDictionary.registerOre("logWood", ItemStack(log, 1, OreDictionary.WILDCARD_VALUE))

        OreDictionary.registerOre("plankWood", ItemStack(irisPlanks, 1, OreDictionary.WILDCARD_VALUE))
        OreDictionary.registerOre("plankWood", ItemStack(rainbowPlanks, 1, OreDictionary.WILDCARD_VALUE))
        OreDictionary.registerOre("plankWood", ItemStack(altPlanks, 1, OreDictionary.WILDCARD_VALUE))

        OreDictionary.registerOre(LibOreDict.DYES[16], ItemStack(BotaniaBlocks.bifrostPerm, 1, OreDictionary.WILDCARD_VALUE))
    }
}
