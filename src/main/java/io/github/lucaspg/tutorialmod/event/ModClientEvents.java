package io.github.lucaspg.tutorialmod.event;

import io.github.lucaspg.tutorialmod.TutorialMod;
import io.github.lucaspg.tutorialmod.item.ModItems;
import io.github.lucaspg.tutorialmod.item.custom.RadiationStaffItem;
import io.github.lucaspg.tutorialmod.renderer.CubeRenderer;
import io.github.lucaspg.tutorialmod.renderer.LaserRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;

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
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) return;

        List<AbstractClientPlayer> players = Minecraft.getInstance().level.players();
        var localPlayer = Minecraft.getInstance().player;

        for (Player player : players) {
            // Skip rendering if the player is further than 500 blocks from local player
            if (player.distanceTo(localPlayer) > 500) {
                continue;
            }

            ItemStack heldItem = player.getMainHandItem();
            if (player.isUsingItem() && heldItem.getItem() instanceof RadiationStaffItem) {
                LaserRenderer.renderLaserBeam(event, player, event.getPartialTick().getGameTimeDeltaPartialTick(false));
            }
        }

        CubeRenderer.renderCube(event, localPlayer, event.getPartialTick().getGameTimeDeltaPartialTick(false));
    }
}
