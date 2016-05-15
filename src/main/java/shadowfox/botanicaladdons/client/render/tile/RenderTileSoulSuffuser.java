package shadowfox.botanicaladdons.client.render.tile;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import shadowfox.botanicaladdons.common.block.tile.TileSuffuser;
import vazkii.botania.client.core.handler.ClientTickHandler;
import vazkii.botania.client.core.helper.RenderHelper;

public class RenderTileSoulSuffuser extends TileEntitySpecialRenderer<TileSuffuser> {

    @Override
    public void renderTileEntityAt(TileSuffuser altar, double x, double y, double z, float partticks, int digProgress) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1F, 1F, 1F, 1F);
        GlStateManager.translate(x, y, z);

        int items = 0;
        for (int i = 0; i < altar.getSizeInventory(); i++)
            if (altar.getItemHandler().getStackInSlot(i) == null)
                break;
            else items++;
        float[] angles = new float[altar.getSizeInventory()];

        float anglePer = 360F / items;
        float totalAngle = 0F;
        for (int i = 0; i < angles.length; i++)
            angles[i] = totalAngle += anglePer;

        double time = ClientTickHandler.ticksInGame + partticks;

        for (int i = 0; i < altar.getSizeInventory(); i++) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(1F, 2.5F, 1F);
            GlStateManager.rotate(angles[i] + (float) time, 0F, 1F, 0F);
            GlStateManager.translate(1F, 0F, 0.5F);
            GlStateManager.rotate(90F, -1F, 1F, 0.5F);
            GlStateManager.translate(0D, 0.075 * Math.sin((time + i * 10) / 5D), 0F);
            ItemStack stack = altar.getItemHandler().getStackInSlot(i);
            Minecraft mc = Minecraft.getMinecraft();
            if (stack != null) {
                mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);

                mc.getRenderItem().renderItem(stack, ItemCameraTransforms.TransformType.GROUND);

            }
            GlStateManager.popMatrix();
        }

        GlStateManager.translate(0F, 0.2F, 0F);
        float scale = altar.getTargetMana() == 0 ? 0 : (float) altar.getCurrentMana() / (float) altar.getTargetMana() / 75F;

        if (scale != 0) {
            int seed = altar.getPos().getX() ^ altar.getPos().getY() ^ altar.getPos().getZ();
            GlStateManager.translate(0.5F, 0.7F, 0.5F);
            RenderHelper.renderStar(0x00E4D7, scale, scale, scale, seed);
        }
        GlStateManager.enableAlpha();

        GlStateManager.popMatrix();
    }
}
