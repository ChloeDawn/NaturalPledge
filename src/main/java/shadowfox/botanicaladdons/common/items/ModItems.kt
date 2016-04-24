package shadowfox.botanicaladdons.common.items

import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.oredict.OreDictionary
import shadowfox.botanicaladdons.common.BotanicalAddons
import shadowfox.botanicaladdons.common.items.base.ItemMod
import shadowfox.botanicaladdons.common.items.bauble.ItemSymbol
import shadowfox.botanicaladdons.common.items.bauble.faith.ItemFaithBauble
import shadowfox.botanicaladdons.common.items.sacred.ItemDagger
import shadowfox.botanicaladdons.common.items.sacred.ItemMjolnir
import shadowfox.botanicaladdons.common.lib.LibMisc
import shadowfox.botanicaladdons.common.lib.LibNames

import vazkii.botania.common.item.ModItems as BotaniaItems

/**
 * @author WireSegal
 * Created at 5:39 PM on 4/13/16.
 */
object ModItems {
    val emblem: ItemMod
    val symbol: ItemMod
    val spellIcon: ItemMod
    val spellFocus: ItemMod

    val mjolnir: ItemMod
    val dagger: ItemMod
    val mjolnirMaterial: Item.ToolMaterial
    val daggerMaterial: Item.ToolMaterial

    lateinit var gaiaKiller: ItemMod

    init {
        mjolnirMaterial = EnumHelper.addToolMaterial("${LibMisc.MOD_ID}:MJOLNIR", 3, 1561, 9.0f, 8.0f, 26).setRepairItem(ItemStack(Items.iron_ingot))
        daggerMaterial = EnumHelper.addToolMaterial("${LibMisc.MOD_ID}:DAGGER", 3, 1561, 9.0f, 0.0f, 14).setRepairItem(ItemStack(BotaniaItems.manaResource, 1, 7)) // Elementium

        emblem = ItemFaithBauble(LibNames.PRIESTLY_EMBLEM)
        symbol = ItemSymbol(LibNames.HOLY_SYMBOL)
        spellIcon = ItemSpellIcon(LibNames.SPELL_ICON)
        spellFocus = ItemTerrestrialFocus(LibNames.SPELL_FOCUS)

        mjolnir = ItemMjolnir(LibNames.MJOLNIR, mjolnirMaterial)
        dagger = ItemDagger(LibNames.DAGGER, daggerMaterial)

        if (BotanicalAddons.isDevEnv)
            gaiaKiller = ItemGaiaSlayer("gaiaKiller")

        OreDictionary.registerOre("holySymbol", symbol)
    }
}
