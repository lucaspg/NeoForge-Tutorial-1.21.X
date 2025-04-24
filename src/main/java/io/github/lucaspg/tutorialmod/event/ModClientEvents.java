package io.github.lucaspg.tutorialmod.event;

import io.github.lucaspg.tutorialmod.TutorialMod;
import io.github.lucaspg.tutorialmod.item.ModItems;
import io.github.lucaspg.tutorialmod.item.custom.RadiationStaffItem;
import io.github.lucaspg.tutorialmod.util.RenderUtils;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = TutorialMod.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ModClientEvents {

    @SubscribeEvent
    public static void onComputeFovModifierEvent(ComputeFovModifierEvent event) {
        if(event.getPlayer().isUsingItem() && event.getPlayer().getUseItem().getItem() == ModItems.KAUPEN_BOW.get()) {
            float fovModifier = 1f;
            int ticksUsingItem = event.getPlayer().getTicksUsingItem();
            float deltaTicks = (float)ticksUsingItem / 20f;
            if(deltaTicks > 1f) {
                deltaTicks = 1f;
            } else {
                deltaTicks *= deltaTicks;
            }
            fovModifier *= 1f - deltaTicks * 0.15f;
            event.setNewFovModifier(fovModifier);
        }
    }

    @SubscribeEvent
    public static void onRenderWorld(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        var client = Minecraft.getInstance();
        var player = client.player;

        if (player == null) return;

        if (client.options.getCameraType() != CameraType.FIRST_PERSON) return;

        ItemStack heldItem = player.getMainHandItem();

        if (player.isUsingItem() && heldItem.getItem() instanceof RadiationStaffItem) {
            RenderUtils.renderLaserBeam(event, player, event.getPartialTick().getGameTimeDeltaPartialTick(false));
        }
    }
}
