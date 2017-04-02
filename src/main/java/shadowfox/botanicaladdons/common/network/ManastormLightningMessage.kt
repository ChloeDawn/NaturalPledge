package shadowfox.botanicaladdons.common.network

import com.teamwizardry.librarianlib.common.network.PacketBase
import com.teamwizardry.librarianlib.common.util.autoregister.PacketRegister
import com.teamwizardry.librarianlib.common.util.saving.Save
import com.teamwizardry.librarianlib.common.util.vec
import net.minecraft.util.math.MathHelper
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
class ManastormLightningMessage(@Save var pos: Vec3d = Vec3d.ZERO, @Save var vecsTo: Array<Vec3d> = arrayOf()) : PacketBase() {
    override fun handle(ctx: MessageContext) {
        for (vecTo in vecsTo)
            Botania.proxy.lightningFX(Vector3(pos), Vector3(vecTo), 1f, 0xFF0000, 0)
    }
}
