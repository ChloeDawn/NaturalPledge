package shadowfox.botanicaladdons.common.enchantment

import com.google.common.collect.Lists
import net.minecraft.enchantment.Enchantment
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.enchantment.EnumEnchantmentType
import net.minecraft.entity.EntityLivingBase
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.registry.GameRegistry
import shadowfox.botanicaladdons.api.lib.LibMisc

/**
 * @author WireSegal
 * Created at 9:37 AM on 5/19/16.
 */
open class EnchantmentMod(name: String, rarity: Rarity, type: EnumEnchantmentType, vararg applicableSlots: EntityEquipmentSlot) : Enchantment(rarity, type, applicableSlots) {
    init {
        setName("${LibMisc.MOD_ID}.$name")
        GameRegistry.register(this, ResourceLocation(LibMisc.MOD_ID, name))
    }

    open val applicableSlots: Array<EntityEquipmentSlot>
        get() = arrayOf()

    private fun getEntityEquipmentForLevel(entityIn: EntityLivingBase): Iterable<ItemStack>? {
        val list = Lists.newArrayList<ItemStack>()

        for (entityequipmentslot in applicableSlots) {
            val itemstack = entityIn.getItemStackFromSlot(entityequipmentslot)

            if (itemstack != null) {
                list.add(itemstack)
            }
        }

        return if (list.size > 0) list else null
    }

    fun getMaxLevel(entity: EntityLivingBase): Int {
        val iterable = getEntityEquipmentForLevel(entity) ?: return 0

        var i = 0
        for (itemstack in iterable) {
            val j = EnchantmentHelper.getEnchantmentLevel(this, itemstack)
            if (j > i) i = j
        }
        return i
    }

    fun getTotalLevel(entity: EntityLivingBase): Int {
        val iterable = getEntityEquipmentForLevel(entity) ?: return 0

        var i = 0
        for (itemstack in iterable) {
            val j = EnchantmentHelper.getEnchantmentLevel(this, itemstack)
            i += j
        }
        return i
    }
}
