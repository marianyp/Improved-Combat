package dev.mariany.improvedcombat;

import dev.mariany.improvedcombat.datagen.*;
import dev.mariany.improvedcombat.enchantment.ICEnchantments;
import dev.mariany.improvedcombat.entity.damage.ICDamageTypes;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKeys;
import org.jetbrains.annotations.Nullable;

public class ImprovedCombatDataGenerator implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        pack.addProvider(ICItemTagProvider::new);
        pack.addProvider(ICEnchantmentProvider::new);
        pack.addProvider(ICEnchantmentTagProvider::new);
        pack.addProvider(ICDamageTypeProvider::new);
        pack.addProvider(ICDamageTypeTagProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder) {
        registryBuilder.addRegistry(RegistryKeys.ENCHANTMENT, ICEnchantments::bootstrap);
        registryBuilder.addRegistry(RegistryKeys.DAMAGE_TYPE, ICDamageTypes::bootstrap);
    }

    @Nullable
    public String getEffectiveModId() {
        return ImprovedCombat.MOD_ID;
    }
}
