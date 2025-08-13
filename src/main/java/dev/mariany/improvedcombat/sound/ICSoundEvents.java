package dev.mariany.improvedcombat.sound;

import dev.mariany.improvedcombat.ImprovedCombat;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ICSoundEvents {
    public static final SoundEvent ITEM_AXE_HIT = register("item.axe.hit");
    public static final SoundEvent ITEM_AXE_HIT_GROUND = register("item.axe.hit_ground");
    public static final SoundEvent ITEM_AXE_RETURN = register("item.axe.return");
    public static final SoundEvent ITEM_AXE_THROW = register("item.axe.throw");

    private static SoundEvent register(String id) {
        return register(ImprovedCombat.id(id));
    }

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    public static void bootstrap() {
        ImprovedCombat.LOGGER.info("Registering Sound Events for " + ImprovedCombat.MOD_ID);
    }
}
