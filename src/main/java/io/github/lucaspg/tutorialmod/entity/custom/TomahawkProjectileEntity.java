package io.github.lucaspg.tutorialmod.entity.custom;

import io.github.lucaspg.tutorialmod.entity.ModEntities;
import io.github.lucaspg.tutorialmod.item.ModItems;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class TomahawkProjectileEntity extends AbstractArrow {
    private float rotation;
    public Vec2 groundedOffset;
    public double translateYOffset = -1.0f;

    public TomahawkProjectileEntity(EntityType<? extends AbstractArrow> entityType, Level level) {
        super(entityType, level);
    }

    public TomahawkProjectileEntity(LivingEntity shooter, Level level) {
        super(ModEntities.TOMAHAWK.get(), shooter, level, new ItemStack(ModItems.TOMAHAWK.get()), null);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(ModItems.TOMAHAWK.get());
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if (rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    public boolean isGrounded() {
        return inGround;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        Entity entity = result.getEntity();
        entity.hurt(this.damageSources().thrown(this, this.getOwner()), 4);

        if (!this.level().isClientSide) {
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        if(result.getDirection() == Direction.SOUTH) {
            groundedOffset = new Vec2(215f,180f);
        }
        if(result.getDirection() == Direction.NORTH) {
            groundedOffset = new Vec2(215f, 0f);
        }
        if(result.getDirection() == Direction.EAST) {
            groundedOffset = new Vec2(215f,-90f);
        }
        if(result.getDirection() == Direction.WEST) {
            groundedOffset = new Vec2(215f,90f);
        }

        if(result.getDirection() == Direction.DOWN) {
            float yOffset = getYOffset(result);
            translateYOffset = -0.2f;
            groundedOffset = new Vec2(115f,yOffset);
        }
        if(result.getDirection() == Direction.UP) {
            float yOffset = getYOffset(result);
            translateYOffset = 0.2f;
            groundedOffset = new Vec2(285f,yOffset);
        }
    }

    private float getYOffset(BlockHitResult result) {
        Vec3 impactPos = result.getLocation();
        Vec3 previousPos = this.position().subtract(this.getDeltaMovement().normalize());

        double deltaX = Math.abs(previousPos.x - impactPos.x);
        double deltaZ = Math.abs(previousPos.z - impactPos.z);

        float yOffset;
        if (deltaX > deltaZ) {
            yOffset = (previousPos.x > impactPos.x) ? -90f : 90f;
        } else {
            yOffset = (previousPos.z > impactPos.z) ? 180f : 0f;
        }
        return yOffset;
    }
}
