package dev.mariany.improvedcombat.tag;

import dev.mariany.improvedcombat.ImprovedCombat;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public final class ICTags {
    public static final class Items {
        public static final TagKey<Item> ARCHERY_ENCHANTABLE = createTag("enchantable/archery");
        public static final TagKey<Item> KNOCKBACK_ENCHANTABLE = createTag("enchantable/knockback");
        public static final TagKey<Item> LOOTING_ENCHANTABLE = createTag("enchantable/looting");
        public static final TagKey<Item> LOYALTY_ENCHANTABLE = createTag("enchantable/loyalty");
        public static final TagKey<Item> RIGHT_FACING = createTag("right_facing");

        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, ImprovedCombat.id(name));
        }
    }
}
