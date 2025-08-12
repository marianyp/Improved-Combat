package dev.mariany.improvedcombat.component;

import dev.mariany.improvedcombat.ImprovedCombat;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;

import java.util.function.UnaryOperator;

public interface ICEnchantmentEffectComponentTypes {
    ComponentType<Unit> ALLOW_AXE_THROW = register(
            "allow_axe_throw",
            builder -> builder.codec(Unit.CODEC)
    );

    private static <T> ComponentType<T> register(String name, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(
                Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE,
                ImprovedCombat.id(name),
                builderOperator.apply(ComponentType.builder()).build()
        );
    }

    static void bootstrap() {
        ImprovedCombat.LOGGER.info("Registering Enchantment Effect Component Types for " + ImprovedCombat.MOD_ID);
    }
}
