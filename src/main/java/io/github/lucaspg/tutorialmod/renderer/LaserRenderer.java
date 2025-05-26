package io.github.lucaspg.tutorialmod.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Axis;
import io.github.lucaspg.tutorialmod.TutorialMod;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix3f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.function.Function;

import static io.github.lucaspg.tutorialmod.renderer.RenderTypes.LASER_BEAM;

public class LaserRenderer {

    public static final ResourceLocation LASER_BEAM_TEXTURE = ResourceLocation.fromNamespaceAndPath(TutorialMod.MOD_ID, "textures/laser_beam_white.png");

    public static void renderLaserBeam(RenderLevelStageEvent event, Player player, float ticks) {
        Vec3 playerPos = player.getEyePosition(ticks);
        HitResult trace = player.pick(256, ticks, false);

        int r = 0;
        int g = 255;
        int b = 0;

        float distance = (float) Math.max(1, playerPos.subtract(trace.getLocation()).length());

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        PoseStack matrix = event.getPoseStack();

        Vec3 view = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        matrix.pushPose();
        // when in first person, these two translations cancel each other out
        matrix.translate(-view.x(), -view.y(), -view.z());
        matrix.translate(playerPos.x(), playerPos.y(), playerPos.z());
        matrix.mulPose(Axis.YP.rotationDegrees(Mth.lerp(ticks, -player.getYRot(), -player.yRotO)));
        matrix.mulPose(Axis.XP.rotationDegrees(Mth.lerp(ticks, player.getXRot(), player.xRotO)));
        matrix.mulPose(Axis.ZP.rotationDegrees(45f));

        PoseStack.Pose currentPose = matrix.last();
        Matrix3f matrixNormal = currentPose.normal();

        VertexConsumer builder = buffer.getBuffer(laserBeam(LASER_BEAM_TEXTURE));

        float xOffset = 0.0f;
        float yOffset = 0.0f;
        float zOffset = 0.0f;

        Vector3f vector3f = new Vector3f(0.0f, 1.0f, 0.0f);
        vector3f.mul(matrixNormal);

        float startXOffset = -0.3f;
        float startYOffset = 0.25f;
        float startZOffset = 0.60f;

        LocalPlayer localPlayer = Minecraft.getInstance().player;

        float fov = localPlayer.getFieldOfViewModifier();
        float fovSetting = Minecraft.getInstance().options.fov().get();
        float fovSettingAdjuster = 0;

        if (fovSetting != 70)
            fovSettingAdjuster = fovSetting < 70f ? (fovSetting / 70.0f) / 0.5f : -(fovSetting / 70.0f) / 6f;

        if (fov >= 1)
            startZOffset += (1 - fov) * 0.5f + fovSettingAdjuster;
        else
            startZOffset += (1 - fov) * 1.5f + fovSettingAdjuster;

        float thickness = 0.04f;

        // right side
        Vector4f vec1 = new Vector4f(startXOffset,      startYOffset - thickness,       startZOffset,           1.0F);
        Vector4f vec2 = new Vector4f(xOffset,           yOffset - thickness,            distance + zOffset,     1.0F);
        Vector4f vec3 = new Vector4f(xOffset,           yOffset + thickness,            distance + zOffset,     1.0F);
        Vector4f vec4 = new Vector4f(startXOffset,      startYOffset + thickness,       startZOffset,           1.0F);

        // left side
        Vector4f vec5 = new Vector4f(startXOffset + 2*thickness,    startYOffset - thickness,      startZOffset,           1.0F);
        Vector4f vec6 = new Vector4f(xOffset + 2*thickness,         yOffset - thickness,           distance + zOffset,     1.0F);
        Vector4f vec7 = new Vector4f(xOffset + 2*thickness,         yOffset + thickness,           distance + zOffset,     1.0F);
        Vector4f vec8 = new Vector4f(startXOffset + 2*thickness,    startYOffset + thickness,      startZOffset,           1.0F);

        // bottom side
        Vector4f vec9 = new Vector4f(startXOffset,                  startYOffset - thickness,      startZOffset,           1.0F);
        Vector4f vec10 = new Vector4f(xOffset,                      yOffset - thickness,           zOffset + distance,     1.0F);
        Vector4f vec11 = new Vector4f(xOffset + 2*thickness,        yOffset - thickness,           zOffset + distance,     1.0F);
        Vector4f vec12 = new Vector4f(startXOffset + 2*thickness,   startYOffset - thickness,      startZOffset,           1.0F);

        // top side
        Vector4f vec13 = new Vector4f(startXOffset,                 startYOffset + thickness,       startZOffset,           1.0F);
        Vector4f vec14 = new Vector4f(xOffset,                      yOffset + thickness,            distance + zOffset,     1.0F);
        Vector4f vec15 = new Vector4f(xOffset + 2*thickness,        yOffset + thickness,            distance + zOffset,     1.0F);
        Vector4f vec16 = new Vector4f(startXOffset + 2*thickness,   startYOffset + thickness,       startZOffset,           1.0F);

        int alpha = 150;
        renderQuad(builder, currentPose, vector3f, vec1, vec2, vec3, vec4, alpha, r, g, b);
        renderQuad(builder, currentPose, vector3f, vec5, vec6, vec7, vec8, alpha, r, g, b);
        renderQuad(builder, currentPose, vector3f, vec9, vec10, vec11, vec12, alpha, r, g, b);
        renderQuad(builder, currentPose, vector3f, vec13, vec14, vec15, vec16, alpha, r, g, b);

        matrix.popPose();
    }

    private static void renderQuad(VertexConsumer builder, PoseStack.Pose pose, Vector3f normalVector, Vector4f vec1, Vector4f vec2, Vector4f vec3, Vector4f vec4, int alpha, int r, int g, int b) {
        builder.addVertex(pose, vec4.x(), vec4.y(), vec4.z()).setColor(FastColor.ARGB32.color(alpha, r, g, b)).setUv(0, (float) 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x(), normalVector.y(), normalVector.z());
        builder.addVertex(pose, vec3.x(), vec3.y(), vec3.z()).setColor(FastColor.ARGB32.color(alpha, r, g, b)).setUv(0, (float) 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x(), normalVector.y(), normalVector.z());
        builder.addVertex(pose, vec2.x(), vec2.y(), vec2.z()).setColor(FastColor.ARGB32.color(alpha, r, g, b)).setUv(0, (float) 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x(), normalVector.y(), normalVector.z());
        builder.addVertex(pose, vec1.x(), vec1.y(), vec1.z()).setColor(FastColor.ARGB32.color(alpha, r, g, b)).setUv(0, (float) 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(normalVector.x(), normalVector.y(), normalVector.z());
    }

    public static RenderType laserBeam(ResourceLocation location) {
        return LASER_BEAM.apply(location);
    }


}
