package shadowfox.botanicaladdons.client.core

import baubles.api.BaublesApi
import net.minecraft.client.Minecraft
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import shadowfox.botanicaladdons.api.lib.LibMisc
import shadowfox.botanicaladdons.client.render.entity.RenderSealedArrow
import shadowfox.botanicaladdons.client.render.tile.RenderTileFrozenStar
import shadowfox.botanicaladdons.common.BotanicalAddons
import shadowfox.botanicaladdons.common.block.tile.TileStar
import shadowfox.botanicaladdons.common.core.CommonProxy
import shadowfox.botanicaladdons.common.core.helper.BALogger
import shadowfox.botanicaladdons.common.entity.EntitySealedArrow
import vazkii.botania.client.core.handler.ClientTickHandler
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.Vector3
import java.awt.Color

/**
 * @author WireSegal
 * Created at 8:37 AM on 3/20/16.
 */
class ClientProxy : CommonProxy() {
    override fun pre(e: FMLPreInitializationEvent) {
        super.pre(e)
        RenderingRegistry.registerEntityRenderingHandler(EntitySealedArrow::class.java, { RenderSealedArrow(it) })
        ModelHandler.preInit(LibMisc.MOD_ID, BotanicalAddons.DEV_ENVIRONMENT, BALogger.coreLog)
    }

    override fun init(e: FMLInitializationEvent) {
        super.init(e)
        ModelHandler.init()
        ClientRegistry.bindTileEntitySpecialRenderer(TileStar::class.java, RenderTileFrozenStar())
    }

    override fun particleEmission(pos: Vector3, color: Int, probability: Float) {
        if (Math.random() < probability) {
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

            Botania.proxy.wispFX(x, y, z, r, g, b, s, -m)
        }
    }

    override fun particleStream(from: Vector3, to: Vector3, color: Int) {
        val motionVec = to.subtract(from).multiply(0.04)
        val c = Color(color)

        val r = c.red / 255f
        val g = c.green / 255f
        val b = c.blue / 255f

        Botania.proxy.wispFX(from.x, from.y, from.z, r, g, b, 0.4f, motionVec.x.toFloat(), motionVec.y.toFloat(), motionVec.z.toFloat())
    }

    override fun wispLine(start: Vector3, line: Vector3, color: Int, stepsPerBlock: Double, time: Int) {
        val len = line.mag()
        val ray = line.multiply(1 / len)
        val steps = (len * stepsPerBlock).toInt()

        for (i in 0..steps - 1) {
            val extended = ray.multiply(i / stepsPerBlock)
            val x = start.x + extended.x
            val y = start.y + extended.y
            val z = start.z + extended.z

            val c = Color(color)

            val r = c.red.toFloat() / 255.0f
            val g = c.green.toFloat() / 255.0f
            val b = c.blue.toFloat() / 255.0f

            Botania.proxy.wispFX(x, y, z, r, g, b, time * 0.0125f)
        }
    }

    override fun particleRing(x: Double, y: Double, z: Double, range: Double, r: Float, g: Float, b: Float) {
        val m = 0.15F
        val mv = 0.35F
        for (i in 0..359 step 8) {
            val rad = i.toDouble() * Math.PI / 180.0
            val dispx = x + 0.5 - Math.cos(rad) * range.toFloat()
            val dispy = y + 0.5
            val dispz = z + 0.5 - Math.sin(rad) * range.toFloat()

            Botania.proxy.wispFX(dispx, dispy, dispz, r, g, b, 0.2F, (Math.random() - 0.5).toFloat() * m, (Math.random() - 0.5).toFloat() * mv, (Math.random() - 0.5F).toFloat() * m)
        }
    }

    override fun pulseColor(color: Color): Color {
        val add = (Math.sin(ClientTickHandler.ticksInGame * 0.2) * 24).toInt()
        val newColor = Color(Math.max(Math.min(color.red + add, 255), 0),
                Math.max(Math.min(color.green + add, 255), 0),
                Math.max(Math.min(color.blue + add, 255), 0))
        return newColor
    }

    override fun rainbow(saturation: Float) = Color(Color.HSBtoRGB((Botania.proxy.worldElapsedTicks * 2L % 360L).toFloat() / 360.0f, saturation, 1.0f))

    override fun rainbow2(speed: Float, saturation: Float): Color {
        val time = ClientTickHandler.ticksInGame.toFloat() + ClientTickHandler.partialTicks
        return Color.getHSBColor(time * speed, saturation, 1.0f)
    }

    override fun rainbow(pos: BlockPos, saturation: Float): Color {
        val ticks = ClientTickHandler.ticksInGame + ClientTickHandler.partialTicks
        val seed = (pos.x xor pos.y xor pos.z) * 255 xor pos.hashCode()
        return Color(Color.HSBtoRGB((seed + ticks) * 0.005F, saturation, 1F))
    }

    override fun wireFrameRainbow(saturation: Float) = Color(Color.HSBtoRGB(ClientTickHandler.ticksInGame % 200 / 200f, saturation, 1f))

    override fun playerHasMonocle(): Boolean {
        BaublesApi.getBaublesHandler(Minecraft.getMinecraft().thePlayer) ?: return false
        return Botania.proxy.isClientPlayerWearingMonocle
    }
}
