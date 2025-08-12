package dev.mariany.improvedcombat.entity.custom;

import dev.mariany.improvedcombat.entity.ICEntityTypes;
import dev.mariany.improvedcombat.entity.damage.ICDamageTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableFloat;
import org.jetbrains.annotations.Nullable;

public class ThrownAxeEntity extends PersistentProjectileEntity {
    public static final String SLOT_KEY = "Slot";
    public static final String SPIN_DURATION_KEY = "SpinDuration";

    private static final TrackedData<Byte> LOYALTY = DataTracker.registerData(
            ThrownAxeEntity.class,
            TrackedDataHandlerRegistry.BYTE
    );
    private static final TrackedData<ItemStack> ITEM = DataTracker.registerData(
            ThrownAxeEntity.class,
            TrackedDataHandlerRegistry.ITEM_STACK
    );

    private int slot;
    private long spinDuration;
    private boolean dealtDamage = false;
    private int returnTimer;

    public ThrownAxeEntity(EntityType<? extends ThrownAxeEntity> entityType, World world) {
        super(entityType, world);
    }

    public ThrownAxeEntity(World world, LivingEntity owner, ItemStack stack, int slot) {
        super(ICEntityTypes.THROWN_AXE, owner, world, stack, null);
        this.slot = slot;
        this.syncEnchantData(stack);
    }

    public ThrownAxeEntity(World world, double x, double y, double z, ItemStack stack) {
        super(ICEntityTypes.THROWN_AXE, x, y, z, world, stack, null);
        this.syncEnchantData(stack);
    }

    private void syncEnchantData(ItemStack stack) {
        this.dataTracker.set(LOYALTY, getLoyalty(stack));
        this.dataTracker.set(ITEM, stack);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        super.initDataTracker(builder);

        builder.add(LOYALTY, (byte) 0);
        builder.add(ITEM, this.getDefaultItemStack());
    }

    @Override
    protected SoundEvent getHitSound() {
        return SoundEvents.ITEM_TRIDENT_HIT_GROUND;
    }

    @Override
    public void tick() {
        if (!this.isOnGround() && this.inGroundTime <= 0 && !this.horizontalCollision && !this.verticalCollision) {
            ++this.spinDuration;
        }

        if (this.inGroundTime > 4) {
            this.dealtDamage = true;
        }

        Entity entity = this.getOwner();
        int loyaltyLevel = this.dataTracker.get(LOYALTY);

        if (loyaltyLevel > 0 && (this.dealtDamage || this.isNoClip()) && entity != null) {
            if (!this.isOwnerAlive()) {
                if (this.getWorld() instanceof ServerWorld serverWorld &&
                        this.pickupType == PersistentProjectileEntity.PickupPermission.ALLOWED) {
                    this.dropStack(serverWorld, this.asItemStack(), 0.1F);
                }

                this.discard();
            } else {
                if (!(entity instanceof PlayerEntity) &&
                        this.getPos().distanceTo(entity.getEyePos()) < entity.getWidth() + 1) {
                    this.discard();
                    return;
                }

                this.setNoClip(true);

                Vec3d offset = entity.getEyePos().subtract(this.getPos());
                this.setPos(this.getX(), this.getY() + offset.y * 0.015 * loyaltyLevel, this.getZ());

                double multiplier = 0.05 * loyaltyLevel;
                this.setVelocity(this.getVelocity().multiply(0.95).add(offset.normalize().multiply(multiplier)));

                if (this.returnTimer == 0) {
                    this.playSound(SoundEvents.ITEM_TRIDENT_RETURN, 10F, 1F);
                }

                this.returnTimer++;
            }
        }

        super.tick();
    }

    private boolean isOwnerAlive() {
        Entity entity = this.getOwner();
        return entity != null && entity.isAlive() && (!(entity instanceof ServerPlayerEntity) || !entity.isSpectator());
    }

    public boolean isEnchanted() {
        return this.dataTracker.get(ITEM).hasGlint();
    }

    public long getSpinDuration() {
        return this.spinDuration;
    }

    @Override
    @Nullable
    protected EntityHitResult getEntityCollision(Vec3d currentPosition, Vec3d nextPosition) {
        return this.dealtDamage ? null : super.getEntityCollision(currentPosition, nextPosition);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        Entity victim = entityHitResult.getEntity();
        Entity owner = this.getOwner();
        ItemStack weapon = this.getItemStack();

        MutableFloat damage = new MutableFloat(this.damage);

        weapon.applyAttributeModifier(
                AttributeModifierSlot.MAINHAND,
                (entry, modifier, display) -> {
                    if (entry.equals(EntityAttributes.ATTACK_DAMAGE)) {
                        damage.add(modifier.value());
                    }
                }
        );

        DamageSource damageSource = this.getDamageSources().create(
                ICDamageTypes.THROWN_AXE,
                this,
                (owner == null ? this : owner)
        );

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            damage.setValue(EnchantmentHelper.getDamage(serverWorld, weapon, victim, damageSource, damage.getValue()));
            damage.add(weapon.getItem().getBonusAttackDamage(victim, damage.getValue(), damageSource));
        }

        this.dealtDamage = true;

        if (this.getWorld() instanceof ServerWorld serverWorld) {
            EnchantmentHelper.onTargetDamaged(serverWorld, victim, damageSource, this.getItemStack(),
                    item -> this.kill(serverWorld));
        }

        if (victim.sidedDamage(damageSource, damage.getValue())) {
            if (victim.getType() == EntityType.ENDERMAN) {
                return;
            }

            if (victim instanceof LivingEntity livingEntity) {
                this.knockback(livingEntity, damageSource);
                this.onHit(livingEntity);
            }
        }

        this.deflect(ProjectileDeflection.SIMPLE, victim, this.getOwner(), false);
        this.setVelocity(this.getVelocity().multiply(0.02, 0.2, 0.02));
        this.playSound(SoundEvents.ITEM_TRIDENT_HIT, 1F, 1F);
    }

    @Override
    protected void knockback(LivingEntity target, DamageSource source) {
        double multiplier = this.getWorld() instanceof ServerWorld serverWorld
                ? EnchantmentHelper.modifyKnockback(serverWorld, this.getItemStack(), target, source, 0.0F)
                : 0;

        if (multiplier > 0) {
            double resistance = Math.max(0, 1 - target.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE));
            Vec3d knockback = this.getVelocity()
                    .multiply(1, 0, 1)
                    .normalize()
                    .multiply(multiplier * 0.6 * resistance);

            if (knockback.lengthSquared() > 0) {
                target.addVelocity(knockback.x, 0.1, knockback.z);
            }
        }
    }

    @Override
    public ItemStack getItemStack() {
        return this.dataTracker.get(ITEM);
    }

    @Override
    protected void setStack(ItemStack stack) {
        super.setStack(stack);
        this.dataTracker.set(ITEM, super.getItemStack());
    }

    @Override
    public ItemStack getWeaponStack() {
        return this.getItemStack();
    }

    @Override
    protected ItemStack getDefaultItemStack() {
        return Items.WOODEN_AXE.getDefaultStack();
    }

    @Override
    protected void onBlockHitEnchantmentEffects(ServerWorld world, BlockHitResult blockHitResult,
                                                ItemStack weaponStack) {
        Vec3d vec3d = blockHitResult.getBlockPos().clampToWithin(blockHitResult.getPos());
        EnchantmentHelper.onHitBlock(world, weaponStack,
                this.getOwner() instanceof LivingEntity livingEntity ? livingEntity : null, this, null, vec3d,
                world.getBlockState(blockHitResult.getBlockPos()), item -> this.kill(world));
    }

    @Override
    protected boolean tryPickup(PlayerEntity player) {
        if (isOwner(player)) {
            PlayerInventory inventory = player.getInventory();

            ItemStack stack = inventory.getStack(this.slot);

            if (stack.isEmpty()) {
                inventory.setStack(this.slot, this.asItemStack());
                return true;
            }
        }

        return super.tryPickup(player) ||
                this.isNoClip() && this.isOwner(player) && player.getInventory().insertStack(this.asItemStack());
    }

    @Override
    public void onPlayerCollision(PlayerEntity player) {
        if (this.isOwner(player) || this.getOwner() == null) {
            super.onPlayerCollision(player);
        }
    }

    @Override
    public void age() {
    }

    private byte getLoyalty(ItemStack stack) {
        return this.getWorld() instanceof ServerWorld serverWorld ?
                (byte) MathHelper.clamp(EnchantmentHelper.getTridentReturnAcceleration(serverWorld, stack, this),
                        0,
                        127
                ) : 0;
    }

    @Override
    protected void readCustomData(ReadView view) {
        super.readCustomData(view);

        this.slot = view.getInt(SLOT_KEY, 0);
        this.spinDuration = view.getLong(SPIN_DURATION_KEY, 0);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        super.writeCustomData(view);

        view.putInt(SLOT_KEY, this.slot);
        view.putLong(SPIN_DURATION_KEY, this.spinDuration);
    }
}
