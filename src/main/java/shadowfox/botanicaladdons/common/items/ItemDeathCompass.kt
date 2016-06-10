package shadowfox.botanicaladdons.common.items

import net.minecraft.client.renderer.color.IItemColor
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ActionResult
import net.minecraft.util.EnumHand
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.GameRules
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.event.entity.player.PlayerDropsEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import shadowfox.botanicaladdons.api.lib.LibMisc
import shadowfox.botanicaladdons.client.core.ModelHandler
import shadowfox.botanicaladdons.common.BotanicalAddons
import shadowfox.botanicaladdons.common.items.base.ItemMod
import vazkii.botania.api.sound.BotaniaSoundEvents
import vazkii.botania.api.wand.ICoordBoundItem
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.core.helper.Vector3
import java.util.*

/**
 * @author WireSegal
 * Created at 10:02 PM on 6/8/16.
 */
class ItemDeathCompass(name: String) : ItemMod(name), ICoordBoundItem, ModelHandler.IColorProvider {

    init {
        setMaxStackSize(1)
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SideOnly(Side.CLIENT)
    override fun getColor(): IItemColor? {
        return IItemColor { itemStack, i ->
            if (i == 1)
                BotanicalAddons.proxy.rainbow(0.25f).rgb
            else 0xFFFFFF
        }
    }

    override fun addInformation(stack: ItemStack, playerIn: EntityPlayer, tooltip: MutableList<String>, advanced: Boolean) {
        val dirVec = getDirVec(stack, playerIn)
        val distance = Math.round((dirVec ?: Vector3.zero).mag()).toInt()
        if (getBinding(stack) != null) {
            if (distance < 5)
                addToTooltip(tooltip, "misc.${LibMisc.MOD_ID}.trackingBlockClose")
            else
                addToTooltip(tooltip, "misc.${LibMisc.MOD_ID}.trackingBlock", distance)
        }
    }

    override fun onUpdate(stack: ItemStack, worldIn: World, entityIn: Entity, itemSlot: Int, isSelected: Boolean) {
        if (!worldIn.isRemote || entityIn !is EntityLivingBase || entityIn.heldItemMainhand != stack && entityIn.heldItemOffhand != stack) return

        val startVec = Vector3.fromEntityCenter(entityIn)
        val dirVec = getDirVec(stack, entityIn) ?: return
        val endVec = startVec.copy().add(dirVec.copy().normalize().multiply(Math.min(dirVec.mag(), 10.0)))

        BotanicalAddons.proxy.particleStream(worldIn, startVec.copy().add(dirVec.copy().normalize()).add(0.0, 0.5, 0.0), endVec, BotanicalAddons.proxy.wireFrameRainbow().rgb)
    }

    override fun onItemRightClick(stack: ItemStack, worldIn: World, player: EntityPlayer, hand: EnumHand?): ActionResult<ItemStack>? {
        if (player.isSneaking && getBinding(stack) != null) {
            ItemNBTHelper.removeEntry(stack, TAG_X)
            ItemNBTHelper.removeEntry(stack, TAG_Y)
            ItemNBTHelper.removeEntry(stack, TAG_Z)
            worldIn.playSound(player, player.posX, player.posY, player.posZ, BotaniaSoundEvents.ding, SoundCategory.PLAYERS, 1f, 5f)
        }

        return super.onItemRightClick(stack, worldIn, player, hand)
    }

    fun getDirVec(stack: ItemStack, player: Entity): Vector3? {
        val pos = getEndVec(stack) ?: return null

        val entityPos = Vector3.fromEntityCenter(player).sub(Vector3(0.5, 0.5, 0.5))
        return pos.copy().sub(entityPos)
    }

    fun getEndVec(stack: ItemStack): Vector3? {
        return Vector3.fromBlockPos(getBinding(stack) ?: return null)
    }

    override fun getBinding(stack: ItemStack): BlockPos? {
        val x = ItemNBTHelper.getInt(stack, TAG_X, 0)
        val y = ItemNBTHelper.getInt(stack, TAG_Y, Int.MIN_VALUE)
        val z = ItemNBTHelper.getInt(stack, TAG_Z, 0)
        return if (y == Int.MIN_VALUE) null else BlockPos(x, y, z)
    }

    @SubscribeEvent
    fun onPlayerDeath(event: LivingDeathEvent) {
        val entity = event.entityLiving
        if (entity is EntityPlayer && entity.worldObj.gameRules.getBoolean("keepInventory")) {
            for (i in 0..entity.inventory.sizeInventory-1) {
                val stack = entity.inventory.getStackInSlot(i)
                if (stack != null && stack.item == this) {
                    ItemNBTHelper.setInt(stack, TAG_X, (entity.posX - 0.5).toInt())
                    ItemNBTHelper.setInt(stack, TAG_Y, (entity.posY - 0.5).toInt())
                    ItemNBTHelper.setInt(stack, TAG_Z, (entity.posZ - 0.5).toInt())
                }
            }
        }
    }

    @SubscribeEvent
    fun onPlayerDrops(event: PlayerDropsEvent) {
        val keeps = ArrayList<EntityItem>()
        for (item in event.drops) {
            val stack = item.entityItem
            if (stack != null && stack.item == this) {
                keeps.add(item)
                ItemNBTHelper.setInt(stack, TAG_X, (event.entityPlayer.posX - 0.5).toInt())
                ItemNBTHelper.setInt(stack, TAG_Y, (event.entityPlayer.posY - 0.5).toInt())
                ItemNBTHelper.setInt(stack, TAG_Z, (event.entityPlayer.posZ - 0.5).toInt())
            }
        }

        if (event.entityPlayer.worldObj.gameRules.getBoolean("keepInventory"))
            return

        if (keeps.size > 0) {
            event.drops.removeAll(keeps)

            val cmp = NBTTagCompound()
            cmp.setInteger(TAG_DROP_COUNT, keeps.size)

            var i = 0
            for (keep in keeps) {
                val stack = keep.entityItem
                val cmp1 = NBTTagCompound()
                stack.writeToNBT(cmp1)
                cmp.setTag(TAG_DROP_PREFIX + i, cmp1)
                i++
            }

            val data = event.entityPlayer.entityData
            if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG))
                data.setTag(EntityPlayer.PERSISTED_NBT_TAG, NBTTagCompound())

            val persist = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
            persist.setTag(TAG_PLAYER_KEPT_DROPS, cmp)
        }
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        val data = event.player.entityData
        if (data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            val cmp = data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG)
            val cmp1 = cmp.getCompoundTag(TAG_PLAYER_KEPT_DROPS)

            val count = cmp1.getInteger(TAG_DROP_COUNT)
            for (i in 0..count - 1) {
                val cmp2 = cmp1.getCompoundTag(TAG_DROP_PREFIX + i)
                val stack = ItemStack.loadItemStackFromNBT(cmp2)
                if (stack != null) {
                    val copy = stack.copy()
                    event.player.inventory.addItemStackToInventory(copy)
                }
            }

            cmp.setTag(TAG_PLAYER_KEPT_DROPS, NBTTagCompound())
        }
    }

    companion object {
        val TAG_PLAYER_KEPT_DROPS = "${LibMisc.MOD_ID}_playerKeptDrops"
        val TAG_DROP_COUNT = "dropCount"
        val TAG_DROP_PREFIX = "dropPrefix"

        val TAG_X = "x"
        val TAG_Y = "y"
        val TAG_Z = "z"
    }
}
