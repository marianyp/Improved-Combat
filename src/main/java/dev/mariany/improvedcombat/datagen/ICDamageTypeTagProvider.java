package dev.mariany.improvedcombat.datagen;

import dev.mariany.improvedcombat.entity.damage.ICDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.DamageTypeTags;

import java.util.concurrent.CompletableFuture;

public class ICDamageTypeTagProvider extends FabricTagProvider<DamageType> {
    public ICDamageTypeTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, RegistryKeys.DAMAGE_TYPE, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        this.builder(DamageTypeTags.IS_PROJECTILE).add(ICDamageTypes.THROWN_AXE);
        this.builder(DamageTypeTags.ALWAYS_KILLS_ARMOR_STANDS).add(ICDamageTypes.THROWN_AXE);
        this.builder(DamageTypeTags.PANIC_CAUSES).add(ICDamageTypes.THROWN_AXE);
    }
}
