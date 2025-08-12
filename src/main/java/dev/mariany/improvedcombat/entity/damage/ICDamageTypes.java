package dev.mariany.improvedcombat.entity.damage;

import dev.mariany.improvedcombat.ImprovedCombat;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public interface ICDamageTypes {
    RegistryKey<DamageType> THROWN_AXE = RegistryKey.of(
            RegistryKeys.DAMAGE_TYPE,
            ImprovedCombat.id("thrown_axe")
    );

    static void bootstrap(Registerable<DamageType> damageTypeRegisterable) {
        damageTypeRegisterable.register(THROWN_AXE, new DamageType("thrown_axe", 0.1F));
    }
}