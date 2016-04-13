package shadowfox.botanicaladdons.common.core

import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import shadowfox.botanicaladdons.common.items.ModItems

/**
 * @author WireSegal
 * Created at 5:07 PM on 4/12/16.
 */
open class CommonProxy {
    open fun pre(e: FMLPreInitializationEvent) {
        ModItems
    }

    open fun init(e: FMLInitializationEvent) {

    }

    open fun post(e: FMLPostInitializationEvent) {

    }
}
