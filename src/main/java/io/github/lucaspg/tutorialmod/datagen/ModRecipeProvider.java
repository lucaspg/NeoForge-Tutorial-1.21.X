package io.github.lucaspg.tutorialmod.datagen;

import io.github.lucaspg.tutorialmod.TutorialMod;
import io.github.lucaspg.tutorialmod.block.ModBlocks;
import io.github.lucaspg.tutorialmod.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {

    protected ModRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    public static class Runner extends RecipeProvider.Runner {

        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new ModRecipeProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "My Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        List<ItemLike> BISMUTH_SMELTABLES = List.of(ModItems.RAW_BISMUTH, ModBlocks.BISMUTH_ORE, ModBlocks.BISMUTH_DEEPSLATE_ORE);

        shaped(RecipeCategory.MISC, ModBlocks.BISMUTH_BLOCK.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', ModItems.BISMUTH.get())
                .unlockedBy("has_bismuth", has(ModItems.BISMUTH))
                .save(this.output);

        shapeless(RecipeCategory.MISC, ModItems.BISMUTH.get(), 9)
                .requires(ModBlocks.BISMUTH_BLOCK.get())
                .unlockedBy("has_bismuth_block", has(ModBlocks.BISMUTH_BLOCK))
                .save(this.output);

        shapeless(RecipeCategory.MISC, ModItems.BISMUTH.get(), 18)
                .requires(ModBlocks.MAGIC_BLOCK.get())
                .unlockedBy("has_magic_block", has(ModBlocks.MAGIC_BLOCK))
                .save(this.output, "tutorialmod:bismuth_from_magic_block");

        oreSmelting(this.output, BISMUTH_SMELTABLES, RecipeCategory.MISC, ModItems.BISMUTH.get(), 0.25f, 200, "bismuth");
        oreBlasting(this.output, BISMUTH_SMELTABLES, RecipeCategory.MISC, ModItems.BISMUTH.get(), 0.25f, 100, "bismuth");

        stairBuilder(ModBlocks.BISMUTH_STAIRS.get(), Ingredient.of(ModItems.BISMUTH)).group("bismuth")
                .unlockedBy("has_bismuth", has(ModItems.BISMUTH)).save(this.output);
        slab(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BISMUTH_SLAB.get(), ModItems.BISMUTH.get());

        buttonBuilder(ModBlocks.BISMUTH_BUTTON.get(), Ingredient.of(ModItems.BISMUTH)).group("bismuth")
                .unlockedBy("has_bismuth", has(ModItems.BISMUTH)).save(this.output);
        pressurePlate(ModBlocks.BISMUTH_PRESSURE_PLATE.get(), ModItems.BISMUTH.get());

        fenceBuilder(ModBlocks.BISMUTH_FENCE.get(), Ingredient.of(ModItems.BISMUTH)).group("bismuth")
                .unlockedBy("has_bismuth", has(ModItems.BISMUTH)).save(this.output);
        fenceGateBuilder(ModBlocks.BISMUTH_FENCE_GATE.get(), Ingredient.of(ModItems.BISMUTH)).group("bismuth")
                .unlockedBy("has_bismuth", has(ModItems.BISMUTH)).save(this.output);
        wall(RecipeCategory.BUILDING_BLOCKS, ModBlocks.BISMUTH_WALL.get(), ModItems.BISMUTH.get());

        doorBuilder(ModBlocks.BISMUTH_DOOR.get(), Ingredient.of(ModItems.BISMUTH)).group("bismuth")
                .unlockedBy("has_bismuth", has(ModItems.BISMUTH)).save(this.output);
        trapdoorBuilder(ModBlocks.BISMUTH_TRAPDOOR.get(), Ingredient.of(ModItems.BISMUTH)).group("bismuth")
                .unlockedBy("has_bismuth", has(ModItems.BISMUTH)).save(this.output);

        trimSmithing(ModItems.KAUPEN_SMITHING_TEMPLATE.get(), ResourceKey.create(Registries.RECIPE,
                ResourceLocation.parse(getItemName(ModItems.KAUPEN_SMITHING_TEMPLATE) + "_smithing_trim")));
    }

    protected void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTIme, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected void oreBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        oreCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected <T extends AbstractCookingRecipe> void oreCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                       List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for (ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory)
                    .group(pGroup)
                    .unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, TutorialMod.MOD_ID + ":" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }
}
