package shadowfox.botanicaladdons.common.items.bauble.faith

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraft.util.math.MathHelper
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.player.AttackEntityEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import shadowfox.botanicaladdons.api.item.IPriestlyEmblem
import shadowfox.botanicaladdons.api.priest.IFaithVariant
import shadowfox.botanicaladdons.common.lib.LibNames
import shadowfox.botanicaladdons.common.potions.ModPotions
import shadowfox.botanicaladdons.common.potions.base.ModPotionEffect
import vazkii.botania.api.mana.ManaItemHandler

/**
 * @author WireSegal
 * Created at 4:38 PM on 4/13/16.
 */
class PriestlyEmblemNjord : IFaithVariant {

    override fun getName(): String = "njord"

    override fun hasSubscriptions(): Boolean = true

    override fun getSpells(stack: ItemStack, player: EntityPlayer): MutableList<String> {
        return mutableListOf(LibNames.SPELL_LEAP, LibNames.SPELL_INTERDICT, LibNames.SPELL_PUSH, LibNames.SPELL_NJORD_INFUSION)
    }

    override fun punishTheFaithless(stack: ItemStack, player: EntityPlayer) {
        player.addPotionEffect(ModPotionEffect(ModPotions.featherweight, 600))
    }

    @SubscribeEvent
    fun floatInWater(e: TickEvent.PlayerTickEvent) {
        val player = e.player
        val world = player.worldObj
        val emblem = ItemFaithBauble.getEmblem(player, PriestlyEmblemNjord::class.java) ?: return

        if (player.isSneaking && world.containsAnyLiquid(player.entityBoundingBox.offset(0.0, 0.175, 0.0)) && player.motionY > -0.5)
            if (ManaItemHandler.requestManaExact(emblem, player, 2, true)) {
                player.motionY -= 0.15
                player.fallDistance = 0f
            }

        if (player.capabilities.isFlying || player.isSneaking) return

        if (world.containsAnyLiquid(player.entityBoundingBox.offset(0.0, 0.175, 0.0)) && player.motionY < 0.5) {
            if (ManaItemHandler.requestManaExact(emblem, player, 2, true)) {
                player.motionY += 0.15
                player.fallDistance = 0f
            }
        } else if (world.containsAnyLiquid(player.entityBoundingBox.offset(0.0, -0.175, 0.0))) {
            if (player.motionY < 0.0 && ManaItemHandler.requestManaExact(emblem, player, 2, true)) {
                player.motionY = 0.0
                player.fallDistance = 0f
                player.onGround = true
            }
        } else if (world.containsAnyLiquid(player.entityBoundingBox.offset(0.0, -0.15 + player.motionY, 0.0))) {
            if (ManaItemHandler.requestManaExact(emblem, player, 2, true)) {
                player.motionY += 0.15
                player.fallDistance = 0f
            }
        }
    }

    @SubscribeEvent
    fun onPlayerAttack(e: AttackEntityEvent) {
        val emblem = ItemFaithBauble.getEmblem(e.entityPlayer, PriestlyEmblemNjord::class.java) ?: return
        val entity = e.target
        if (entity is EntityLivingBase)
            if (ManaItemHandler.requestManaExact(emblem, e.entityPlayer, 20, true))
                entity.knockBack(e.entityPlayer, if ((emblem.item as IPriestlyEmblem).isAwakened(emblem)) 1.5f else 1f,
                        MathHelper.sin(e.entityPlayer.rotationYaw * Math.PI.toFloat() / 180).toDouble(),
                        -MathHelper.cos(e.entityPlayer.rotationYaw * Math.PI.toFloat() / 180).toDouble())
    }

    @SubscribeEvent
    fun onPlayerFall(e: LivingAttackEvent) {
        val player = e.entityLiving
        if (player is EntityPlayer) {
            val emblem = ItemFaithBauble.getEmblem(player, PriestlyEmblemNjord::class.java) ?: return
            if (e.source == DamageSource.fall || e.source == DamageSource.flyIntoWall) {
                if ((emblem.item as IPriestlyEmblem).isAwakened(emblem))
                    e.isCanceled = true
                else if (e.amount > 4f && ManaItemHandler.requestManaExact(emblem, player, 10, true)) {
                    e.isCanceled = true
                    player.attackEntityFrom(e.source, 4f)
                }
            }
        }
    }
}
