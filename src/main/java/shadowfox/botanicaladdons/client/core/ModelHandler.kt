package shadowfox.botanicaladdons.client.core

import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelBakery
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.block.statemap.StateMap
import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ModelLoader
import shadowfox.botanicaladdons.common.lib.LibMisc
import java.util.*

/**
 * @author WireSegal
 * Created at 2:12 PM on 3/20/16.
 */
object ModelHandler {

    interface IVariantHolder {
        val customMeshDefinition: ItemMeshDefinition?

        val variants: Array<out String>
    }

    interface IExtraVariantHolder : IVariantHolder {
        val extraVariants: Array<out String>
    }

    interface IBABlock : IVariantHolder {
        val variantEnum: Class<Enum<*>>?
        val ignoredProperties: Array<IProperty<*>>?
        val bareName: String

        fun getBlockRarity(stack: ItemStack): EnumRarity
    }

    interface IColorProvider {
        val color: IItemColor
    }

    val variantCache = ArrayList<IVariantHolder>()

    val resourceLocations = HashMap<String, ModelResourceLocation>()

    fun preInit() {
        for (holder in variantCache) {
            registerModels(holder)
        }
    }

    fun init() {
        val colors = Minecraft.getMinecraft().itemColors
        for (holder in variantCache)
            if (holder is IColorProvider)
                colors.registerItemColorHandler(holder.color, holder as Item);
    }

    // The following is a blatant copy of Psi's ModelHandler.

    fun registerModels(holder: IVariantHolder) {
        val def = holder.customMeshDefinition
        if (def != null) {
            ModelLoader.setCustomMeshDefinition(holder as Item, def)
        } else {
            val i = holder as Item
            registerModels(i, holder.variants, false)
            if (holder is IExtraVariantHolder) {
                registerModels(i, holder.extraVariants, true)
            }
        }

    }

    fun registerModels(item: Item, variants: Array<out String>, extra: Boolean) {
        if (item is ItemBlock && item.getBlock() is IBABlock) {
            val locName = Block.blockRegistry.getNameForObject(Block.getBlockFromItem(item))

            val i = item.getBlock() as IBABlock
            val name = i.variantEnum
            val loc = i.ignoredProperties
            if (loc != null && loc.size > 0) {
                val builder = StateMap.Builder()
                val var7 = loc
                val var8 = loc.size

                for (var9 in 0..var8 - 1) {
                    val p = var7[var9]
                    builder.ignore(p)
                }

                ModelLoader.setCustomStateMapper(i as Block, builder.build())
            }

            if (name != null) {
                registerVariantsDefaulted(locName.toString(), item, name, "variant")
                return
            }
        }

        for (var11 in variants.indices) {
            val var13 = ModelResourceLocation(ResourceLocation(LibMisc.MOD_ID, variants[var11]).toString(), "inventory")
            if (!extra) {
                ModelLoader.setCustomModelResourceLocation(item, var11, var13)
                resourceLocations.put(getKey(item, var11), var13)
            } else {
                ModelBakery.registerItemVariants(item, *arrayOf<ResourceLocation>(var13))
                resourceLocations.put(variants[var11], var13)
            }
        }

    }

    private fun registerVariantsDefaulted(key: String, item: Item, enumclazz: Class<*>, variantHeader: String) {
        if (enumclazz.enumConstants != null)
            for (e in enumclazz.enumConstants) {
                if (e is IStringSerializable && e is Enum<*>) {
                    val variantName = variantHeader + "=" + e.name
                    val loc = ModelResourceLocation(key, variantName)
                    val i = e.ordinal
                    ModelLoader.setCustomModelResourceLocation(item, i, loc)
                    resourceLocations.put(getKey(item, i), loc)
                }
            }

    }

    private fun getKey(item: Item, meta: Int): String {
        return "i_" + item.registryName + "@" + meta
    }
}
