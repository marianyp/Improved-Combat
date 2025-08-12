package dev.mariany.improvedcombat.client;

import dev.mariany.improvedcombat.client.render.entity.ThrownAxeEntityRenderer;
import dev.mariany.improvedcombat.entity.ICEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class ImprovedCombatClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerEntityRenderer();
    }

    private static void registerEntityRenderer() {
        EntityRendererRegistry.register(ICEntityTypes.THROWN_AXE, ThrownAxeEntityRenderer::new);
    }
}
