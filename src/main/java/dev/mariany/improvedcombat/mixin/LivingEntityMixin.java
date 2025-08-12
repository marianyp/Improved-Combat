package dev.mariany.improvedcombat.mixin;

import dev.mariany.improvedcombat.entity.ModifiableLivingEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ModifiableLivingEntity {
    @Shadow
    protected int itemUseTimeLeft;

    @Override
    public void improvedcombat$setItemUseTimeLeft(int ticks) {
        this.itemUseTimeLeft = ticks;
    }
}
