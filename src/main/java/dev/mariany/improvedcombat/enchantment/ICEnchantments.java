package dev.mariany.improvedcombat.enchantment;

import dev.mariany.improvedcombat.ImprovedCombat;
import dev.mariany.improvedcombat.component.ICEnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.ItemTags;

public class ICEnchantments {
    public static final RegistryKey<Enchantment> HURL = of("hurl");

    public static void bootstrap(Registerable<Enchantment> registry) {
        RegistryEntryLookup<Item> itemRegistry = registry.getRegistryLookup(RegistryKeys.ITEM);

        register(
                registry,
                HURL,
                Enchantment.builder(
                                Enchantment.definition(
                                        itemRegistry.getOrThrow(ItemTags.AXES),
                                        15,
                                        1,
                                        Enchantment.constantCost(20),
                                        Enchantment.constantCost(50),
                                        8,
                                        AttributeModifierSlot.MAINHAND
                                )
                        )
                        .addEffect(ICEnchantmentEffectComponentTypes.ALLOW_AXE_THROW)
        );
    }

    private static void register(
            Registerable<Enchantment> registry,
            RegistryKey<Enchantment> key,
            Enchantment.Builder builder
    ) {
        registry.register(key, builder.build(key.getValue()));
    }

    private static RegistryKey<Enchantment> of(String name) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, ImprovedCombat.id(name));
    }
}
