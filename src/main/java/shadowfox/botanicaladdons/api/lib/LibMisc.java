package shadowfox.botanicaladdons.api.lib;

/**
 * @author WireSegal
 *         Created at 3:12 PM on 5/14/16.
 */
public final class LibMisc {
    public static final String MOD_NAME = "Natural Pledge";
    public static final String MOD_ID = "botanicaladdons";

    public static final String BUILD = "2";
    public static final String VERSIONID = "2";
    public static final String VERSION = VERSIONID + "." + BUILD;
    public static final String DEPENDENCIES = "required-after:forge@[13.20.0.2259,);required-after:botania;required-after:librarianlib;";

    public static final String PROXY_COMMON = "shadowfox.botanicaladdons.common.core.CommonProxy";
    public static final String PROXY_CLIENT = "shadowfox.botanicaladdons.client.core.ClientProxy";
}
