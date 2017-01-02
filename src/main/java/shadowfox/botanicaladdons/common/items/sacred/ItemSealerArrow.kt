package shadowfox.botanicaladdons.common.items.sacred

import com.teamwizardry.librarianlib.common.base.IVariantHolder
import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import com.teamwizardry.librarianlib.common.base.item.IModItemProvider
import com.teamwizardry.librarianlib.common.util.VariantHelper
import com.teamwizardry.librarianlib.common.util.currentModId
import net.minecraft.block.BlockDispenser
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.dispenser.BehaviorProjectileDispense
import net.minecraft.dispenser.IPosition
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.IProjectile
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.projectile.EntityArrow
import net.minecraft.inventory.IInventory
import net.minecraft.item.EnumRarity
import net.minecraft.item.Item
import net.minecraft.item.ItemArrow
import net.minecraft.item.ItemStack
import net.minecraft.stats.Achievement
import net.minecraft.world.World
import shadowfox.botanicaladdons.common.achievements.ModAchievements
import shadowfox.botanicaladdons.common.entity.EntitySealedArrow
import vazkii.botania.api.BotaniaAPI
import vazkii.botania.common.achievement.ICraftAchievement

/**
 * @author WireSegal
 * Created at 11:49 AM on 5/22/16.
 */
class ItemSealerArrow(name: String, vararg variants: String) : ItemArrow(), IModItemProvider, IVariantHolder, ICraftAchievement {

    init {
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(this, object : BehaviorProjectileDispense() {
            override fun getProjectileEntity(worldIn: World, position: IPosition, stackIn: ItemStack): IProjectile {
                val arrow = EntitySealedArrow(worldIn, position.x, position.y, position.z)
                arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED
                return arrow
            }
        })
    }

    override val variants: Array<out String>

    private val bareName: String
    private val modId: String

    override val providedItem: Item
        get() = this

    init {
        modId = currentModId
        bareName = name
        this.variants = VariantHelper.setupItem(this, name, variants, creativeTab)
    }

    override fun setUnlocalizedName(name: String): Item {
        VariantHelper.setUnlocalizedNameForItem(this, modId, name)
        return super.setUnlocalizedName(name)
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        val dmg = stack.itemDamage
        val variants = this.variants
        val name = if (dmg >= variants.size) this.bareName else variants[dmg]

        return "item.$modId:$name"
    }

    override fun getSubItems(itemIn: Item, tab: CreativeTabs?, subItems: MutableList<ItemStack>) {
        variants.indices.mapTo(subItems) { ItemStack(itemIn, 1, it) }
    }

    /**
     * Override this to have a custom creative tab. Leave blank to have a default tab (or none if no default tab is set).
     */
    val creativeTab: ModCreativeTab?
        get() = ModCreativeTab.defaultTabs[modId]

    override fun createArrow(world: World, stack: ItemStack, player: EntityLivingBase?): EntityArrow? {
        return EntitySealedArrow(world, player)
    }

    override fun getRarity(stack: ItemStack?): EnumRarity? {
        return BotaniaAPI.rarityRelic
    }

    override fun isInfinite(stack: ItemStack?, bow: ItemStack?, player: EntityPlayer?) = false

    override fun getAchievementOnCraft(p0: ItemStack?, p1: EntityPlayer?, p2: IInventory?): Achievement? {
        return ModAchievements.sacredAqua
    }
}
