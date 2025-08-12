package dev.mariany.improvedcombat.client.render.entity;

import dev.mariany.improvedcombat.client.render.entity.state.ThrownAxeEntityRenderState;
import dev.mariany.improvedcombat.entity.custom.ThrownAxeEntity;
import dev.mariany.improvedcombat.tag.ICTags;
import net.minecraft.client.item.ItemModelManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;

public class ThrownAxeEntityRenderer extends EntityRenderer<ThrownAxeEntity, ThrownAxeEntityRenderState> {
    private final Random random = Random.create();
    private final ItemModelManager itemModelManager;

    public ThrownAxeEntityRenderer(EntityRendererFactory.Context context) {
        super(context);
        this.itemModelManager = context.getItemModelManager();
    }

    @Override
    public ThrownAxeEntityRenderState createRenderState() {
        return new ThrownAxeEntityRenderState();
    }

    @Override
    public void updateRenderState(
            ThrownAxeEntity thrownAxeEntity,
            ThrownAxeEntityRenderState thrownAxeEntityRenderState,
            float tickProgress
    ) {
        super.updateRenderState(thrownAxeEntity, thrownAxeEntityRenderState, tickProgress);
        thrownAxeEntityRenderState.yaw = thrownAxeEntity.getLerpedYaw(tickProgress);
        thrownAxeEntityRenderState.pitch = thrownAxeEntity.getLerpedPitch(tickProgress);
        thrownAxeEntityRenderState.enchanted = thrownAxeEntity.isEnchanted();
        thrownAxeEntityRenderState.rightFacing = thrownAxeEntity.getItemStack().isIn(ICTags.Items.RIGHT_FACING);
        thrownAxeEntityRenderState.spinDuration = thrownAxeEntity.getSpinDuration();
        thrownAxeEntityRenderState.update(thrownAxeEntity, thrownAxeEntity.getItemStack(), this.itemModelManager);
    }

    @Override
    public void render(
            ThrownAxeEntityRenderState state,
            MatrixStack matrices,
            VertexConsumerProvider vertexConsumers,
            int light
    ) {
        matrices.push();

        matrices.scale(1.5F, 1.5F, 1.5F);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(90 - state.yaw));
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(state.spinDuration * 15));

        if (!state.rightFacing) {
            matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(180));
        }

        ItemEntityRenderer.renderStack(
                matrices,
                vertexConsumers,
                light,
                state,
                this.random,
                state.itemRenderState.getModelBoundingBox()
        );

        matrices.pop();

        super.render(state, matrices, vertexConsumers, light);
    }
}
