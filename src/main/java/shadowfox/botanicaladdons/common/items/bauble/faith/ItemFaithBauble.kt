package shadowfox.botanicaladdons.common.items.bauble.faith

import baubles.api.BaubleType
import baubles.common.lib.PlayerHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionEffect
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.Style
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import shadowfox.botanicaladdons.common.items.base.ItemModBauble
import shadowfox.botanicaladdons.common.lib.LibMisc
import shadowfox.botanicaladdons.common.potions.ModPotions
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.common.core.helper.ItemNBTHelper

/**
 * @author WireSegal
 * Created at 1:50 PM on 4/13/16.
 */
open class ItemFaithBauble(name: String) : ItemModBauble(name, *Array(variants.size, { "emblem${variants[it].name.capitalizeFirst()}" })), IBaubleRender {

    interface IFaithVariant {
        val name: String

        val hasSubscriptions: Boolean
            get() = false

        fun onUpdate(stack: ItemStack, player: EntityPlayer) {
        }

        fun onAwakenedUpdate(stack: ItemStack, player: EntityPlayer) {
            onUpdate(stack, player)
        }

        fun onRenderTick(stack: ItemStack, player: EntityPlayer, render: IBaubleRender.RenderType, renderTick: Float) {
        }

        fun addToTooltip(stack: ItemStack, player: EntityPlayer?, tooltip: MutableList<String>, advanced: Boolean) {
        }

        fun punishTheFaithless(stack: ItemStack, player: EntityPlayer)

    }

    companion object {

        fun String.capitalizeFirst(): String {
            if (this.length == 0) return this
            return this.slice(0..0).capitalize() + this.slice(1..this.length - 1)
        }

        val TAG_PENDANT = "pendant"
        val TAG_AWAKENED = "awakened"

        val variants = arrayOf(
                PriestlyEmblemNjord(),
                PriestlyEmblemIdunn(),
                PriestlyEmblemThor()
        )

        init {
            for (variant in variants)
                if (variant.hasSubscriptions)
                    MinecraftForge.EVENT_BUS.register(variant)
        }

        fun getEmblem(player: EntityPlayer, variant: Class<out IFaithVariant>): ItemStack? {

            if (isFaithless(player)) return null

            var baubles = PlayerHandler.getPlayerBaubles(player)
            var stack = baubles.getStackInSlot(0)
            if (stack != null && stack.item is ItemFaithBauble) {
                val variantInstance = (stack.item as ItemFaithBauble).getVariant(stack)
                if (variantInstance != null && variant.isInstance(variantInstance))
                    return stack
            }
            return null
        }

        fun isFaithless(player: EntityPlayer): Boolean {
            return ModPotions.faithlessness.hasEffect(player)
        }

        fun isAwakened(stack: ItemStack) = ItemNBTHelper.getBoolean(stack, TAG_AWAKENED, false)
        fun setAwakened(stack: ItemStack, state: Boolean) = ItemNBTHelper.setBoolean(stack, TAG_AWAKENED, state)

        fun getVariantBase(stack: ItemStack) = if (variants.size == 0) null else variants[stack.itemDamage % variants.size]
    }

    init {
        addPropertyOverride(ResourceLocation(LibMisc.MOD_ID, TAG_PENDANT)) {
            stack, world, entity ->
            if (ItemNBTHelper.getBoolean(stack, TAG_PENDANT, false)) 1f else 0f
        }
        addPropertyOverride(ResourceLocation(LibMisc.MOD_ID, TAG_AWAKENED)) {
            stack, world, entity ->
            if (ItemNBTHelper.getBoolean(stack, TAG_AWAKENED, false)) 1f else 0f
        }
    }

    override fun getRarity(stack: ItemStack) = if (isAwakened(stack)) BotaniaAPI.rarityRelic else super.getRarity(stack)

    override fun getBaubleType(stack: ItemStack) = BaubleType.AMULET

    override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
        super.onWornTick(stack, player)

        val variant = getVariant(stack)
        if (variant != null && player is EntityPlayer && !isFaithless(player)) {
            if (isAwakened(stack)) {
                variant.onAwakenedUpdate(stack, player)
            } else {
                variant.onUpdate(stack, player)
            }
        }
    }

    override fun onPlayerBaubleRender(stack: ItemStack, player: EntityPlayer, render: IBaubleRender.RenderType, renderTick: Float) {
        val variant = getVariant(stack) ?: return

        GlStateManager.pushMatrix()
        if (render == IBaubleRender.RenderType.BODY) {
            if (player.isSneaking) {
                GlStateManager.translate(0.0, 0.3, 0.0)
                GlStateManager.rotate(90 / Math.PI.toFloat(), 1.0f, 0.0f, 0.0f)
            }
            val armor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null
            GlStateManager.rotate(180F, 1F, 0F, 0F)
            GlStateManager.translate(0.0, -0.3, if (armor) 0.175 else 0.05)

            val renderStack = stack.copy()
            ItemNBTHelper.setBoolean(renderStack, TAG_PENDANT, true)

            Minecraft.getMinecraft().renderItem.renderItem(renderStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
        }
        GlStateManager.popMatrix()

        if (!isFaithless(player))
            variant.onRenderTick(stack, player, render, renderTick)

    }

    override fun onUpdate(stack: ItemStack, worldIn: World?, entityIn: Entity?, itemSlot: Int, isSelected: Boolean) {
        if (isAwakened(stack)) setAwakened(stack, false)
    }

    open fun getVariant(stack: ItemStack): IFaithVariant? {
        return getVariantBase(stack)
    }

    override fun canUnequip(stack: ItemStack, player: EntityLivingBase) = !isAwakened(stack)
    override fun onEquipped(stack: ItemStack, player: EntityLivingBase?) {
        super.onEquipped(stack, player)
        setAwakened(stack, false)
    }

    override fun onUnequipped(stack: ItemStack, player: EntityLivingBase) {
        super.onUnequipped(stack, player)
        val variant = getVariant(stack)
        if (variant != null && player is EntityPlayer) {
            player.addPotionEffect(PotionEffect(ModPotions.faithlessness, 600))
            if (player.worldObj.isRemote)
                player.addChatComponentMessage(TextComponentTranslation((stack.unlocalizedName + ".angry")).setChatStyle(Style().setColor(TextFormatting.RED)))
            variant.punishTheFaithless(stack, player)
        }
    }

    override fun addHiddenTooltip(stack: ItemStack, player: EntityPlayer?, tooltip: MutableList<String>, advanced: Boolean) {
        super.addHiddenTooltip(stack, player, tooltip, advanced)
        val variant = getVariant(stack) ?: return
        variant.addToTooltip(stack, player, tooltip, advanced)
    }
}
