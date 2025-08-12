package dev.mariany.improvedcombat.client.render.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.state.ItemStackEntityRenderState;

@Environment(EnvType.CLIENT)
public class ThrownAxeEntityRenderState extends ItemStackEntityRenderState {
    public float pitch;
    public float yaw;
    public long spinDuration;
    public boolean enchanted;
    public boolean rightFacing;
}
