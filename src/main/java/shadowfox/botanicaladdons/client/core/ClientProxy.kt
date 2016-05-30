package shadowfox.botanicaladdons.client.core

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import shadowfox.botanicaladdons.client.render.entity.RenderSealedArrow
import shadowfox.botanicaladdons.client.render.tile.RenderTileFrozenStar
import shadowfox.botanicaladdons.common.block.dendrics.thunder.ThunderEventHandler
import shadowfox.botanicaladdons.common.block.tile.TileStar
import shadowfox.botanicaladdons.common.core.CommonProxy
import shadowfox.botanicaladdons.common.entity.EntitySealedArrow
import vazkii.botania.client.core.handler.ClientTickHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.Vector3
import java.awt.Color
import java.util.*

/**
 * @author WireSegal
 * Created at 8:37 AM on 3/20/16.
 */
class ClientProxy : CommonProxy() {
    override fun pre(e: FMLPreInitializationEvent) {
        super.pre(e)
        RenderingRegistry.registerEntityRenderingHandler(EntitySealedArrow::class.java, { RenderSealedArrow(it) })
        ModelHandler.preInit()
    }

    override fun init(e: FMLInitializationEvent) {
        super.init(e)
        ModelHandler.init()
        ClientRegistry.bindTileEntitySpecialRenderer(TileStar::class.java, RenderTileFrozenStar())
    }

    override fun particleEmission(world: World, pos: Vector3, color: Int, probability: Float) {
        if (world.isRemote && Math.random() < probability) {
            val v = 0.1F

            var r = (color shr 16 and 0xFF) / 0xFF.toFloat()
            var g = (color shr 8 and 0xFF) / 0xFF.toFloat()
            var b = (color and 0xFF) / 0xFF.toFloat()

            val luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b // Standard relative luminance calculation

            if (luminance < v) {
                r += Math.random().toFloat() * 0.125F
                g += Math.random().toFloat() * 0.125F
                b += Math.random().toFloat() * 0.125F
            }

            val w = 0.15F
            val h = 0.05F
            val x = pos.x + 0.5 + (Math.random() - 0.5) * w
            val y = pos.y + 0.25 + (Math.random() - 0.5) * h
            val z = pos.z + 0.5 + (Math.random() - 0.5) * w

            val s = 0.2F + Math.random().toFloat() * 0.1F
            val m = 0.03F + Math.random().toFloat() * 0.015F

            Botania.proxy.wispFX(world, x, y, z, r, g, b, s, -m)
        }
    }

    override fun particleStream(world: World, from: Vector3, to: Vector3, color: Int) {
        val motionVec = to.copy().sub(from).multiply(0.04)
        val c = Color(color)

        val r = c.red / 255f
        val g = c.green / 255f
        val b = c.blue / 255f

        Botania.proxy.wispFX(world, from.x, from.y, from.z, r, g, b, 0.4f, motionVec.x.toFloat(), motionVec.y.toFloat(), motionVec.z.toFloat())
    }

    override fun pulseColor(color: Color): Color {
        val add = (Math.sin(ClientTickHandler.ticksInGame * 0.2) * 24).toInt()
        val newColor = Color(Math.max(Math.min(color.red + add, 255), 0),
                Math.max(Math.min(color.green + add, 255), 0),
                Math.max(Math.min(color.blue + add, 255), 0))
        return newColor
    }

    override fun rainbow(saturation: Float) = Color(Color.HSBtoRGB((Botania.proxy.worldElapsedTicks * 2L % 360L).toFloat() / 360.0f, saturation, 1.0f))

    override fun rainbow(pos: BlockPos, saturation: Float): Color {
        val ticks = ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks
        val seed = (pos.x xor pos.y xor pos.z).toLong()
        val index = Random(seed).nextInt(100000)
        return Color(Color.HSBtoRGB((index + ticks) * 0.005F, saturation, 1F))
    }

    override fun wireFrameRainbow(saturation: Float) = Color(Color.HSBtoRGB(ClientTickHandler.ticksInGame % 200 / 200f, saturation, 1f))
}
