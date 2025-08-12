package dev.mariany.improvedcombat.datagen;

import dev.mariany.improvedcombat.tag.ICTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ICItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public ICItemTagProvider(
            FabricDataOutput output,
            CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture
    ) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(ICTags.Items.ARCHERY_ENCHANTABLE)
                .addOptionalTag(ItemTags.BOW_ENCHANTABLE)
                .addOptionalTag(ItemTags.CROSSBOW_ENCHANTABLE);

        valueLookupBuilder(ICTags.Items.KNOCKBACK_ENCHANTABLE)
                .addOptionalTag(ItemTags.SWORD_ENCHANTABLE)
                .addOptionalTag(ItemTags.AXES);

        valueLookupBuilder(ICTags.Items.LOOTING_ENCHANTABLE)
                .addOptionalTag(ItemTags.SWORD_ENCHANTABLE)
                .addOptionalTag(ItemTags.AXES);

        valueLookupBuilder(ICTags.Items.LOYALTY_ENCHANTABLE)
                .addOptionalTag(ItemTags.TRIDENT_ENCHANTABLE)
                .addOptionalTag(ItemTags.AXES);

        valueLookupBuilder(ItemTags.FIRE_ASPECT_ENCHANTABLE)
                .addOptionalTag(ItemTags.AXES);

        valueLookupBuilder(ICTags.Items.RIGHT_FACING);
    }
}
