package shadowfox.botanicaladdons.client.integration.jei.treegrowing

import mezz.jei.api.recipe.IRecipeHandler
import mezz.jei.api.recipe.IRecipeWrapper
import shadowfox.botanicaladdons.api.lib.LibMisc
import shadowfox.botanicaladdons.client.integration.jei.treegrowing.TreeGrowingRecipeJEI

object TreeGrowingRecipeHandler : IRecipeHandler<TreeGrowingRecipeJEI> {
    override fun getRecipeClass(): Class<TreeGrowingRecipeJEI> {
        return TreeGrowingRecipeJEI::class.java
    }

    override fun getRecipeCategoryUid(): String {
        return "${LibMisc.MOD_ID}:treeGrowing"
    }

    override fun getRecipeWrapper(recipe: TreeGrowingRecipeJEI): IRecipeWrapper {
        return recipe
    }

    override fun isRecipeValid(recipe: TreeGrowingRecipeJEI): Boolean {
        return true
    }
}
