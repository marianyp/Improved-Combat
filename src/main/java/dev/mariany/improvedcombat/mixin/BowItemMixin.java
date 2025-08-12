package dev.mariany.improvedcombat.mixin;

import dev.mariany.improvedcombat.entity.ModifiableLivingEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BowItem.class)
public abstract class BowItemMixin extends Item {
    @Shadow
    public abstract int getMaxUseTime(ItemStack stack, LivingEntity user);

    public BowItemMixin(Settings settings) {
        super(settings);
    }

    @Inject(method = "use", at = @At(value = "RETURN"))
    public void injectOnUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (cir.getReturnValue().isAccepted()) {
            ItemStack stack = user.getStackInHand(hand);
            int maxUseTime = getMaxUseTime(stack, user);
            int additionalTicks = getAdditionalTicks(stack, user, 1.1F);

            ((ModifiableLivingEntity) user).improvedcombat$setItemUseTimeLeft(maxUseTime - additionalTicks);
        }
    }

    @Unique
    private static int getAdditionalTicks(ItemStack stack, LivingEntity user, float baseSeconds) {
        float chargeTime = EnchantmentHelper.getCrossbowChargeTime(stack, user, baseSeconds);
        return MathHelper.floor((baseSeconds - chargeTime) * 20);
    }
}
