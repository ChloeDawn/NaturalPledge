package shadowfox.botanicaladdons.common.items.bauble

import baubles.api.BaubleType
import baubles.api.BaublesApi
import com.teamwizardry.librarianlib.common.base.item.ItemModBauble
import com.teamwizardry.librarianlib.common.network.PacketHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.MobEffects
import net.minecraft.init.SoundEvents
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraft.util.EntityDamageSource
import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundCategory
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingAttackEvent
import net.minecraftforge.event.entity.living.LivingFallEvent
import net.minecraftforge.fml.common.eventhandler.EventPriority
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import shadowfox.botanicaladdons.api.lib.LibMisc
import shadowfox.botanicaladdons.common.core.helper.BAMethodHandles
import shadowfox.botanicaladdons.common.network.SetPositionMessage
import vazkii.botania.api.item.IBaubleRender
import vazkii.botania.client.model.ModelCloak

/**
 * @author WireSegal
 * Created at 9:50 PM on 10/21/16.
 */
class ItemDivineCloak(name: String) : ItemModBauble(name, *variants), IBaubleRender {
    companion object {
        val variants = arrayOf("cloakNjord",
                "cloakIdunn",
                "cloakThor",
                "cloakHeimdall")

        init {
            MinecraftForge.EVENT_BUS.register(this)
        }

        fun damageSourceEarthquake(e: EntityPlayer? = null): DamageSource {
            return if (e == null) DamageSource("${LibMisc.MOD_ID}.earthquake") else EntityDamageSource("${LibMisc.MOD_ID}.earthquake", e)
        }

        @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
        fun onFall(e: LivingFallEvent) {
            val player = e.entityLiving
            if (player is EntityPlayer) {
                val baubles = BaublesApi.getBaublesHandler(player)
                val body = baubles.getStackInSlot(BaubleType.BODY.validSlots[0])
                if (!body.isEmpty && body.item is ItemDivineCloak) {
                    if (body.itemDamage == 2) {
                        val jump = player.getActivePotionEffect(MobEffects.JUMP_BOOST)
                        val f = if (jump == null) 0.0f else (jump.amplifier + 1).toFloat()
                        val damage = Math.min((e.distance - 3.0f - f) * e.damageMultiplier, 5.0f)
                        if (damage > 0.0f) {
                            e.isCanceled = true
                            val entities = player.world.getEntitiesWithinAABB(EntityLivingBase::class.java, player.entityBoundingBox.expand(4.0, 3.0, 4.0))
                            entities
                                    .filter { it != player && it.onGround }
                                    .forEach { it.attackEntityFrom(damageSourceEarthquake(player), damage * 2) }

                            player.attackEntityFrom(damageSourceEarthquake(), 0.00005f)
                            for (pos in BlockPos.getAllInBoxMutable(BlockPos(player.positionVector).add(-1, -1, -1), BlockPos(player.positionVector).add(1, -1, 1))) {
                                val state = player.world.getBlockState(pos)
                                if (state.isFullCube)
                                    player.world.playEvent(2001, pos, state.block.getMetaFromState(state))
                            }
                        }
                    } else if (body.itemDamage == 0) e.isCanceled = true
                }
            }
        }

        val epsilon = Math.cos(Math.PI / 6)
        val inverseEpsilon = Math.sin(Math.PI / 6)

        private var no = false

        @SubscribeEvent
        fun onDamage(e: LivingAttackEvent) {
            if (no) return
            val player = e.entityLiving
            if (player is EntityPlayer) {
                val baubles = BaublesApi.getBaublesHandler(player)
                val body = baubles.getStackInSlot(BaubleType.BODY.validSlots[0])
                if (!body.isEmpty && body.item is ItemDivineCloak && body.itemDamage == 1 && e.source.entity != null) {
                    val look = player.lookVec.normalize()
                    val origin = e.source.entity!!
                    val dir = player.positionVector.subtract(origin.positionVector).normalize()
                    val dot = look.dotProduct(dir)
                    if (dot < inverseEpsilon) {
                        e.isCanceled = true
                        no = true
                        player.attackEntityFrom(e.source, if (dot > epsilon) e.amount else 0.00005f)
                        no = false
                    }
                }
            }
        }
    }

    override fun getBaubleType(stack: ItemStack) = BaubleType.BODY

    override fun onWornTick(stack: ItemStack, player: EntityLivingBase) {
        super.onWornTick(stack, player)
        if (stack.itemDamage == 0) {
            if ((player !is EntityPlayer || !player.capabilities.isFlying) && !player.onGround && !player.isElytraFlying && !player.isInWater)
                player.motionY += 0.05
            player.fallDistance = 0f
        } else if (stack.itemDamage == 3) {
            if (player.world.isRemote && player.isSprinting && BAMethodHandles.getJumpTicks(player) == 10) {
                val look = player.lookVec
                val dist = 6.0
                val vec = Vec3d(player.posX + look.xCoord * dist, player.posY + look.yCoord * dist, player.posZ + look.zCoord * dist)
                val blockAt = BlockPos(vec)
                if (!player.world.getBlockState(blockAt).isFullCube && !player.world.getBlockState(blockAt.up()).isFullCube) {
                    BAMethodHandles.setIsJumping(player, false)
                    PacketHandler.NETWORK.sendToServer(SetPositionMessage(vec))
                    player.world.playSound(vec.xCoord, vec.yCoord, vec.zCoord, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f, false)
                }
            }
        }
    }

    private var model: Any? = null

    private val cloakNjord = ResourceLocation(LibMisc.MOD_ID, "textures/model/njordCloak.png")
    private val cloakIdunn = ResourceLocation(LibMisc.MOD_ID, "textures/model/idunnCloak.png")
    private val cloakThor = ResourceLocation(LibMisc.MOD_ID, "textures/model/thorCloak.png")
    private val cloakHeimdall = ResourceLocation(LibMisc.MOD_ID, "textures/model/heimdallCloak.png")

    private val cloakNjordGlow = ResourceLocation(LibMisc.MOD_ID, "textures/model/njordCloakGlow.png")
    private val cloakIdunnGlow = ResourceLocation(LibMisc.MOD_ID, "textures/model/idunnCloakGlow.png")
    private val cloakThorGlow = ResourceLocation(LibMisc.MOD_ID, "textures/model/thorCloakGlow.png")
    private val cloakHeimdallGlow = ResourceLocation(LibMisc.MOD_ID, "textures/model/heimdallCloakGlow.png")

    fun getCloakTexture(stack: ItemStack): ResourceLocation {
        return when (stack.itemDamage) {
            0 -> cloakNjord
            1 -> cloakIdunn
            2 -> cloakThor
            else -> cloakHeimdall
        }
    }

    fun getCloakGlowTexture(stack: ItemStack): ResourceLocation {
        return when (stack.itemDamage) {
            0 -> cloakNjordGlow
            1 -> cloakIdunnGlow
            2 -> cloakThorGlow
            else -> cloakHeimdallGlow
        }
    }

    @SideOnly(Side.CLIENT)
    override fun onPlayerBaubleRender(stack: ItemStack, player: EntityPlayer, type: IBaubleRender.RenderType, partialTicks: Float) {
        if (model == null) model = ModelCloak()
        if (type == vazkii.botania.api.item.IBaubleRender.RenderType.BODY) {
            IBaubleRender.Helper.rotateIfSneaking(player)
            val armor = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null
            GlStateManager.translate(0.0f, if (armor) -0.07f else -0.01f, 0.0f)
            val s = 0.0625f
            GlStateManager.scale(s, s, s)

            GlStateManager.enableLighting()
            GlStateManager.enableRescaleNormal()
            Minecraft.getMinecraft().renderEngine.bindTexture(this.getCloakTexture(stack))
            (model as ModelCloak).render(1.0f)
            val light = 0xF000F0
            val lightmapX = light % 0x10000
            val lightmapY = light / 0x10000
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightmapX.toFloat(), lightmapY.toFloat())
            Minecraft.getMinecraft().renderEngine.bindTexture(this.getCloakGlowTexture(stack))
            (model as ModelCloak).render(1.0f)
        }

    }
}
