package shadowfox.botanicaladdons.common.items.armor

import com.google.common.collect.Multimap
import com.teamwizardry.librarianlib.client.util.TooltipHelper
import com.teamwizardry.librarianlib.common.util.ItemNBTHelper
import net.minecraft.enchantment.EnchantmentHelper
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.EnumCreatureAttribute
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.boss.EntityDragonPart
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.init.MobEffects
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.item.ItemSword
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.stats.AchievementList
import net.minecraft.stats.StatList
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumHand
import net.minecraft.util.EnumParticleTypes
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.WorldServer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import shadowfox.botanicaladdons.common.items.ModItems
import shadowfox.botanicaladdons.common.items.ModItems.FENRIS
import shadowfox.botanicaladdons.common.items.armor.ItemFenrisArmor.Companion.TAG_ACTIVE
import shadowfox.botanicaladdons.common.items.base.ItemBaseArmor
import java.util.*

/**
 * @author WireSegal
 * Created at 5:09 PM on 4/2/17.
 */
class ItemFenrisArmor(name: String, type: EntityEquipmentSlot) : ItemBaseArmor(name, type, FENRIS) {
    companion object {
        val TAG_ACTIVE = "active"

        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        @SubscribeEvent
        fun onLivingAttack(e: LivingAttackEvent) {
            val attacker = e.source.entity
            if (e.source.damageType == "player" && attacker is EntityPlayer && ModItems.fenrisHelm.hasFullSet(attacker) && attacker.heldItemMainhand.isEmpty) {
                e.source.setDamageBypassesArmor()
            }
        }
    }

    override val armorTexture: String
        get() = armorMaterial.getName()

    override val armorSetStacks: ArmorSet by lazy {
        ArmorSet(ModItems.fenrisHelm, ModItems.fenrisChest, ModItems.fenrisLegs, ModItems.fenrisBoots)
    }

    override val manaDiscount: Float
        get() = 0.2f

    override fun addArmorSetDescription(list: MutableList<String>) {
        TooltipHelper.addToTooltip(list, "$modId.armorset.$matName.desc")
        TooltipHelper.addToTooltip(list, "$modId.armorset.$matName.desc1")
    }

    override fun onArmorTick(world: World, player: EntityPlayer, stack: ItemStack) {
        super.onArmorTick(world, player, stack)
        if (!world.isRemote) {
            if (hasFullSet(player))
                ItemNBTHelper.setBoolean(stack, TAG_ACTIVE, true)
            else
                ItemNBTHelper.setBoolean(stack, TAG_ACTIVE, false)
        }
    }

    override fun onUpdate(stack: ItemStack, world: World, player: Entity, par4: Int, par5: Boolean) {
        super.onUpdate(stack, world, player, par4, par5)
        if (!world.isRemote && par4 < 100)
            ItemNBTHelper.setBoolean(stack, TAG_ACTIVE, false)
    }

    override fun getAttributeModifiers(slot: EntityEquipmentSlot?, stack: ItemStack): Multimap<String, AttributeModifier> {
        val map = super.getAttributeModifiers(slot, stack)

        if (slot == armorType && ItemNBTHelper.getBoolean(stack, TAG_ACTIVE, false)) {
		    val uuid = UUID((unlocalizedName + slot.toString()).hashCode().toLong(), 0L)
            map.put(SharedMonsterAttributes.ATTACK_DAMAGE.name, AttributeModifier(uuid, "Fenris modifier " + slot?.name, 0.5, 0))
        }

        return map
    }
}
