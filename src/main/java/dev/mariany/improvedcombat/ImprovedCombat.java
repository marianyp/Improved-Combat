package dev.mariany.improvedcombat;

import dev.mariany.improvedcombat.component.ICEnchantmentEffectComponentTypes;
import dev.mariany.improvedcombat.entity.ICEntityTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImprovedCombat implements ModInitializer {
    public static final String MOD_ID = "improvedcombat";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static Identifier id(String resource) {
        return Identifier.of(MOD_ID, resource);
    }

    @Override
    public void onInitialize() {
        ICEntityTypes.bootstrap();
        ICEnchantmentEffectComponentTypes.bootstrap();
    }
}