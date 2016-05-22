package shadowfox.botanicaladdons.common.items.bauble.faith

import net.minecraft.block.BlockSapling
import net.minecraft.block.IGrowable
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.SoundEvents
import net.minecraft.item.ItemStack
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import shadowfox.botanicaladdons.api.SpellRegistry
import shadowfox.botanicaladdons.api.priest.IFaithVariant
import shadowfox.botanicaladdons.api.sapling.ISaplingBlock
import shadowfox.botanicaladdons.common.items.ItemResource
import shadowfox.botanicaladdons.common.items.ItemResource.Variants.LIFE_ROOT
import shadowfox.botanicaladdons.common.items.ItemSpellIcon
import shadowfox.botanicaladdons.common.items.ItemSpellIcon.Variants.LIFEMAKER
import shadowfox.botanicaladdons.common.lib.LibNames
import shadowfox.botanicaladdons.common.potions.ModPotions
import shadowfox.botanicaladdons.common.potions.base.ModPotionEffect
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.api.mana.ManaItemHandler
import vazkii.botania.common.core.helper.ItemNBTHelper
import vazkii.botania.common.lib.LibOreDict
import java.util.*

/**
 * @author WireSegal
 * Created at 7:24 AM on 4/14/16.
 */
class PriestlyEmblemIdunn : IFaithVariant {

    init {
        SpellRegistry.registerSpell(LibNames.SPELL_PROTECTION, Spells.Idunn.Ironroot())
        SpellRegistry.registerSpell(LibNames.SPELL_IDUNN_INFUSION,
                Spells.ObjectInfusion(LIFEMAKER, LibOreDict.LIVING_WOOD,
                        LIFE_ROOT, 150, 0x0FF469))
    }

    override fun getName(): String = "idunn"

    override fun hasSubscriptions(): Boolean = true

    override fun getSpells(stack: ItemStack, player: EntityPlayer): MutableList<String> {
        return mutableListOf(LibNames.SPELL_PROTECTION, LibNames.SPELL_IDUNN_INFUSION)
    }

    val RANGE = 5
    override fun onUpdate(stack: ItemStack, player: EntityPlayer) {
        val saplings = ArrayList<Pair<BlockPos, IBlockState>>()
        val world = player.worldObj

        if (!world.isRemote) {
            val cooldown = ItemNBTHelper.getInt(stack, TAG_COOLDOWN, 0)
            if (cooldown > 0) ItemNBTHelper.setInt(stack, TAG_COOLDOWN, cooldown - 1)
        }

        if (!ManaItemHandler.requestManaExact(stack, player, 10, false)) return

        if (world.totalWorldTime % 40 == 0L)
            for (x in -RANGE..RANGE)
                for (y in -RANGE..RANGE)
                    for (z in -RANGE..RANGE) {
                        val pos = BlockPos(player.posX + x, player.posY + y, player.posZ + z)
                        val state = world.getBlockState(pos)
                        val block = state.block
                        if (block is BlockSapling || block is ISaplingBlock)
                            saplings.add(Pair(pos, state))

                    }

        if (saplings.size == 0) return

        val pair = saplings[world.rand.nextInt(saplings.size)]

        val pos = pair.first
        val state = pair.second
        val block = state.block

        if (block is IGrowable && block.canGrow(world, pos, state, world.isRemote)) {
            if (world.isRemote)
                world.playEvent(2005, pos, 0)
            else if (block.canUseBonemeal(world, world.rand, pos, state) && ManaItemHandler.requestManaExact(stack, player, 10, true))
                grow(player, block, world, pos, state)
        }
    }

    fun grow(player: EntityPlayer, block: IGrowable, world: World, pos: BlockPos, state: IBlockState) {
        block.grow(world, world.rand, pos, state)
        world.playSound(player, pos, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 1f, 0.1f)
    }

    override fun punishTheFaithless(stack: ItemStack, player: EntityPlayer) {
        player.addPotionEffect(ModPotionEffect(ModPotions.rooted, 600))
    }

    val TAG_COOLDOWN = "cooldown"
    val COOLDOWN_LENGTH = 30

    @SubscribeEvent
    fun onClick(e: PlayerInteractEvent.RightClickBlock) {
        val emblem = ItemFaithBauble.getEmblem(e.entityPlayer, PriestlyEmblemIdunn::class.java) ?: return

        val cooldown = ItemNBTHelper.getInt(emblem, TAG_COOLDOWN, 0)

        if (cooldown == 0 && e.entityPlayer.isSneaking && ManaItemHandler.requestManaExact(emblem, e.entityPlayer, 50, false) && e.itemStack == null) {
            val world = e.world
            val pos = e.pos
            val state = e.world.getBlockState(pos)
            val block = state.block
            if (block is IGrowable && block.canGrow(world, pos, state, world.isRemote)) {
                if (world.isRemote)
                    world.playEvent(2005, pos, 0)
                else if (block.canUseBonemeal(world, world.rand, pos, state) && ManaItemHandler.requestManaExact(emblem, e.entityPlayer, 50, true)) {
                    grow(e.entityPlayer, block, world, pos, state)
                    ItemNBTHelper.setInt(emblem, TAG_COOLDOWN, COOLDOWN_LENGTH)
                }
            }
        }
    }
}
