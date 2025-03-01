package io.github.lucaspg.tutorialmod.datagen;

import io.github.lucaspg.tutorialmod.TutorialMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = TutorialMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        LootTableProvider lootTableProvider = new LootTableProvider(
                packOutput,
                Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)),
                lookupProvider
        );
        generator.addProvider(event.includeServer(), lootTableProvider);

        ModRecipeProvider modRecipeProvider = new ModRecipeProvider(packOutput, lookupProvider);
        generator.addProvider(event.includeServer(), modRecipeProvider);

        BlockTagsProvider blockTagsProvider = new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);

        ModItemTagProvider modItemTagProvider = new ModItemTagProvider(packOutput, lookupProvider,
                blockTagsProvider.contentsGetter(), existingFileHelper);
        generator.addProvider(event.includeServer(), modItemTagProvider);

        ModItemModelProvider modItemModelProvider = new ModItemModelProvider(packOutput, existingFileHelper);
        generator.addProvider(event.includeClient(), modItemModelProvider);

        ModBlockStateProvider modBlockStateProvider = new ModBlockStateProvider(packOutput, existingFileHelper);
        generator.addProvider(event.includeClient(), modBlockStateProvider);

        ModDataMapProvider modDataMapProvider = new ModDataMapProvider(packOutput, lookupProvider);
        generator.addProvider(event.includeServer(), modDataMapProvider);

        generator.addProvider(event.includeServer(), new ModDatapackProvider(packOutput, lookupProvider));
        generator.addProvider(event.includeServer(), new ModGlobalLootModifierProvider(packOutput, lookupProvider));
    }
}
