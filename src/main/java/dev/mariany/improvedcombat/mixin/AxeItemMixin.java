package dev.mariany.improvedcombat.mixin;

import dev.mariany.improvedcombat.component.ICEnchantmentEffectComponentTypes;
import dev.mariany.improvedcombat.entity.custom.ThrownAxeEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ProjectileItem;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Position;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AxeItem.class)
public class AxeItemMixin extends Item implements ProjectileItem {
    public AxeItemMixin(Item.Settings settings) {
        super(settings);
    }

    @Override
    public ProjectileEntity createEntity(World world, Position pos, ItemStack stack, Direction direction) {
        ThrownAxeEntity thrownAxeEntity = new ThrownAxeEntity(
                world,
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                stack.copyWithCount(1)
        );
        thrownAxeEntity.pickupType = PersistentProjectileEntity.PickupPermission.ALLOWED;
        return thrownAxeEntity;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 72000;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        boolean canThrow = EnchantmentHelper.hasAnyEnchantmentsWith(
                itemStack,
                ICEnchantmentEffectComponentTypes.ALLOW_AXE_THROW
        );

        if (!canThrow || itemStack.willBreakNextUse() || user.getAttackCooldownProgress(0) < 1) {
            return ActionResult.FAIL;
        }

        user.setCurrentHand(hand);

        return ActionResult.CONSUME;
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player) {
            int elapsedUseTicks = this.getMaxUseTime(stack, user) - remainingUseTicks;

            if (elapsedUseTicks < 5) {
                return false;
            }

            if (stack.willBreakNextUse()) {
                return false;
            }

            player.incrementStat(Stats.USED.getOrCreateStat(this));

            if (world instanceof ServerWorld serverWorld) {
                stack.damage(1, player);
                ItemStack itemStack = stack.splitUnlessCreative(1, player);

                ThrownAxeEntity thrownAxeEntity = ProjectileEntity.spawnWithVelocity(
                        (spawnWorld, shooter, thrownStack) -> new ThrownAxeEntity(
                                spawnWorld,
                                shooter,
                                thrownStack,
                                getSelectedSlot(player)
                        ),
                        serverWorld,
                        itemStack,
                        player,
                        0F,
                        2.5F,
                        1F
                );

                if (player.isInCreativeMode()) {
                    thrownAxeEntity.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }

                world.playSoundFromEntity(
                        null,
                        thrownAxeEntity,
                        SoundEvents.ITEM_TRIDENT_THROW.value(),
                        SoundCategory.PLAYERS,
                        1F,
                        1F
                );

                return true;
            }
        }

        return false;
    }

    @Unique
    private static int getSelectedSlot(PlayerEntity player) {
        if (player.getActiveHand().equals(Hand.OFF_HAND)) {
            return PlayerInventory.OFF_HAND_SLOT;
        }

        return player.getInventory().getSelectedSlot();
    }
}
