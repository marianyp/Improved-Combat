package dev.mariany.improvedcombat.entity;

import dev.mariany.improvedcombat.ImprovedCombat;
import dev.mariany.improvedcombat.entity.custom.ThrownAxeEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;

public class ICEntityTypes {
    public static final EntityType<ThrownAxeEntity> THROWN_AXE = register("thrown_axe",
            EntityType.Builder.<ThrownAxeEntity>create(ThrownAxeEntity::new, SpawnGroup.MISC)
                    .dropsNothing()
                    .dimensions(0.5F, 0.5F)
                    .eyeHeight(0.13F)
                    .maxTrackingRange(4)
                    .trackingTickInterval(20)
    );

    private static RegistryKey<EntityType<?>> keyOf(String id) {
        return RegistryKey.of(RegistryKeys.ENTITY_TYPE, ImprovedCombat.id(id));
    }

    private static <T extends Entity> EntityType<T> register(String id, EntityType.Builder<T> type) {
        return register(keyOf(id), type);
    }

    private static <T extends Entity> EntityType<T> register(
            RegistryKey<EntityType<?>> key,
            EntityType.Builder<T> type
    ) {
        return Registry.register(Registries.ENTITY_TYPE, key, type.build(key));
    }

    public static void bootstrap() {
        ImprovedCombat.LOGGER.info("Registering Entities for " + ImprovedCombat.MOD_ID);
    }
}
