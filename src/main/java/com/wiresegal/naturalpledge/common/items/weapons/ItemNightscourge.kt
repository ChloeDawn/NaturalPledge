package com.wiresegal.naturalpledge.common.items.weapons

import com.google.common.collect.Multimap
import com.teamwizardry.librarianlib.features.base.item.IGlowingItem
import com.teamwizardry.librarianlib.features.base.item.ItemMod
import com.wiresegal.naturalpledge.api.item.IWeightEnchantable
import com.wiresegal.naturalpledge.common.enchantment.EnchantmentWeight
import com.wiresegal.naturalpledge.common.items.base.IPreventBreakInCreative
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.enchantment.Enchantment
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.SharedMonsterAttributes
import net.minecraft.entity.ai.attributes.AttributeModifier
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.EnumAction
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.item.equipment.tool.ToolCommons

/**
 * @author WireSegal
 * Created at 9:20 PM on 5/18/16.
 */
class ItemNightscourge(val name: String) : ItemMod(name), IWeightEnchantable, IPreventBreakInCreative, IGlowingItem {

    val MANA_PER_DAMAGE = 40

    init {
        setMaxStackSize(1)
        maxDamage = 1561
        addPropertyOverride(ResourceLocation("blocking")) {
            stack, _, entityIn ->
            if (entityIn != null && entityIn.isHandActive && (entityIn.heldItemMainhand == stack || entityIn.heldItemOffhand == stack)) 1f else 0f
        }
    }

    @SideOnly(Side.CLIENT)
    override fun transformToGlow(itemStack: ItemStack, model: IBakedModel) = IGlowingItem.Helper.wrapperBake(model, false, 1)
    @SideOnly(Side.CLIENT)
    override fun shouldDisableLightingForGlow(itemStack: ItemStack, model: IBakedModel) = true

    override fun isShield(stack: ItemStack?, entity: EntityLivingBase?): Boolean {
        return true
    }

    private val attackDamage = 1.5f

    override fun getItemUseAction(stack: ItemStack) = EnumAction.BLOCK

    override fun onUpdate(stack: ItemStack, world: World, player: Entity, itemSlot: Int, isSelected: Boolean) {
        if (!world.isRemote && player is EntityPlayer && stack.itemDamage > 0 && ManaItemHandler.requestManaExactForTool(stack, player, MANA_PER_DAMAGE * 2, true))
            stack.itemDamage = stack.itemDamage - 1
    }

    override fun onBlockDestroyed(stack: ItemStack, worldIn: World, blockIn: IBlockState, pos: BlockPos, entityLiving: EntityLivingBase): Boolean {
        if (blockIn.getBlockHardness(worldIn, pos) > 0)
            ToolCommons.damageItem(stack, 2, entityLiving, MANA_PER_DAMAGE)
        return true
    }

    override fun getMaxItemUseDuration(stack: ItemStack): Int {
        return 72000
    }

    override fun hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean {
        ToolCommons.damageItem(stack, 1, attacker, MANA_PER_DAMAGE)
        return super.hitEntity(stack, target, attacker)
    }

    override fun onItemRightClick(worldIn: World, playerIn: EntityPlayer, hand: EnumHand): ActionResult<ItemStack>? {
        playerIn.activeHand = hand
        return super.onItemRightClick(worldIn, playerIn, hand)
    }

    override fun getAttributeModifiers(slot: EntityEquipmentSlot, stack: ItemStack): Multimap<String, AttributeModifier>? {
        val multimap = super.getAttributeModifiers(slot, stack)
        if (slot == EntityEquipmentSlot.MAINHAND) {
            val offset = EnchantmentWeight.getWeight(stack)
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.name, AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", this.attackDamage.toDouble() - offset * 0.7, 0))
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.name, AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -2.0 + offset * -.3, 0))
        }
        return multimap
    }

    override fun getItemEnchantability(): Int = 14

    override fun canApplyWeightEnchantment(stack: ItemStack, ench: Enchantment) = true
}
