package shadowfox.botanicaladdons.client.integration.jei.spellcrafting

import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeWrapper
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import shadowfox.botanicaladdons.api.lib.LibMisc
import shadowfox.botanicaladdons.client.core.TooltipHelper
import shadowfox.botanicaladdons.client.integration.jei.JEIPluginBotanicalAddons
import shadowfox.botanicaladdons.client.integration.jei.treegrowing.TreeGrowingRecipeJEI

object SpellCraftingCategory : IRecipeCategory {

    private val background = JEIPluginBotanicalAddons.helpers.guiHelper.createDrawable(ResourceLocation(LibMisc.MOD_ID, "textures/gui/jei/spell.png"), 0, 0, 108, 30)

    override fun getUid(): String {
        return "${LibMisc.MOD_ID}:spellCrafting"
    }

    override fun getTitle(): String {
        return TooltipHelper.local("jei.${LibMisc.MOD_ID}.recipe.spellCrafting")
    }

    override fun getBackground(): IDrawable {
        return background
    }

    override fun drawExtras(minecraft: Minecraft) {
        // NO-OP
    }

    override fun drawAnimations(minecraft: Minecraft) {
        // NO-OP
    }

    @SuppressWarnings("unchecked")
    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: IRecipeWrapper) {

        recipeLayout.itemStacks.init(INPUT_SLOT, true, 0, 5)
        recipeLayout.itemStacks.init(FOCUS_SLOT, true, 17, 5)
        recipeLayout.itemStacks.init(SPELL_SLOT, false, 56, 5)
        recipeLayout.itemStacks.init(OUTPUT_SLOT, false, 80, 5)

        if (recipeWrapper is SpellCraftingRecipeJEI) {
            recipeLayout.itemStacks.set(INPUT_SLOT, recipeWrapper.getInputsTyped())
            recipeLayout.itemStacks.set(FOCUS_SLOT, recipeWrapper.getFocusTyped())
            recipeLayout.itemStacks.set(SPELL_SLOT, recipeWrapper.getIconTyped())
            recipeLayout.itemStacks.set(OUTPUT_SLOT, recipeWrapper.getOutputsTyped())
        }
    }

    private val INPUT_SLOT = 0
    private val FOCUS_SLOT = 1
    private val SPELL_SLOT = 2
    private val OUTPUT_SLOT = 3
}
