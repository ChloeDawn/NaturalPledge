package shadowfox.botanicaladdons.common.lexicon.page

import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import shadowfox.botanicaladdons.common.items.bauble.faith.ItemFaithBauble
import vazkii.botania.api.lexicon.LexiconCategory

/**
 * @author WireSegal
 * Created at 1:26 PM on 4/16/16.
 */
class EntryPriestlyKnowledge(unlocName: String, val pendant: Class<out ItemFaithBauble.IFaithVariant>, category: LexiconCategory, icon: ItemStack) : ModEntry(unlocName, category, icon) {

    override fun isVisible(): Boolean {
        val entityPlayer = Minecraft.getMinecraft().thePlayer
        return ItemFaithBauble.getEmblem(entityPlayer, pendant) != null
    }
}
