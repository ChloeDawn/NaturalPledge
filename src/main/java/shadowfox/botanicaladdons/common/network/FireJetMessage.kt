package shadowfox.botanicaladdons.common.network

import com.teamwizardry.librarianlib.common.network.PacketBase
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.util.math.Vec3d
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side
import shadowfox.botanicaladdons.common.BotanicalAddons
import shadowfox.botanicaladdons.common.block.trap.BlockBaseTrap
import shadowfox.botanicaladdons.common.block.trap.BlockBaseTrap.Companion.B
import shadowfox.botanicaladdons.common.block.trap.BlockBaseTrap.Companion.G
import shadowfox.botanicaladdons.common.block.trap.BlockBaseTrap.Companion.R
import vazkii.botania.common.Botania
import vazkii.botania.common.core.helper.Vector3

/**
 * @author WireSegal
 * Created at 8:15 PM on 3/25/17.
 */
@PacketRegister(Side.CLIENT)
class FireJetMessage(@Save var pos: Vec3d = Vec3d.ZERO, @Save var vecTo: Vec3d = Vec3d.ZERO) : PacketBase() {
    override fun handle(ctx: MessageContext) {
        val maxDist = pos.distanceTo(vecTo)
        val to = vecTo.subtract(pos)
        val ray = to.normalize()
        var disturbedRay = vec(ray.xCoord, ray.yCoord + 1, ray.zCoord).crossProduct(ray).scale(0.2)
        if (disturbedRay.xCoord == 0.0 && disturbedRay.yCoord == 0.0 && disturbedRay.zCoord == 0.0)
            disturbedRay = vec(0.2, 0, 0)
        for (distX5 in 0 until (maxDist * 5).toInt()) {
            val dist = distX5 / 5.0
            val position = pos.add(ray.scale(dist))
            val angle = Math.random() * 120
            var rotated = Vector3(disturbedRay).rotate(angle, Vector3(ray)).toVec3D()
            Botania.proxy.wispFX(position.xCoord + rotated.xCoord, position.yCoord + rotated.yCoord, position.zCoord + rotated.zCoord, R, G, B, 0.5f)
            rotated = Vector3(rotated).rotate(120.0, Vector3(ray)).toVec3D()
            Botania.proxy.wispFX(position.xCoord + rotated.xCoord, position.yCoord + rotated.yCoord, position.zCoord + rotated.zCoord, R, G, B, 0.5f)
            rotated = Vector3(rotated).rotate(120.0, Vector3(ray)).toVec3D()
            Botania.proxy.wispFX(position.xCoord + rotated.xCoord, position.yCoord + rotated.yCoord, position.zCoord + rotated.zCoord, R, G, B, 0.5f)
        }
    }
}
