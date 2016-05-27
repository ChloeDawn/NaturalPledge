package shadowfox.botanicaladdons.common.crafting.recipe

import net.minecraft.block.Block
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.oredict.ShapelessOreRecipe

/**
 * @author WireSegal
 * Created at 1:45 PM on 5/23/16.
 */
class RecipeItemDuplication(val resource: Any, val product: ItemStack) : ShapelessOreRecipe(product, resource, product) {

    constructor(resource: Any, product: Item) : this(resource, ItemStack(product))

    constructor(resource: Any, product: Block) : this(resource, ItemStack(product))

    override fun getRemainingItems(inv: InventoryCrafting): Array<out ItemStack?>? {
        val ret = arrayOfNulls<ItemStack>(inv.sizeInventory)
        for (i in ret.indices) {
            val stack = inv.getStackInSlot(i)
            if (stack != null && stack.item == product.item && stack.itemDamage == product.itemDamage) {
                val newStack = stack.copy()
                newStack.stackSize = 1
                ret[i] = newStack
            } else
                ret[i] = ForgeHooks.getContainerItem(stack)
        }
        return ret
    }
}
