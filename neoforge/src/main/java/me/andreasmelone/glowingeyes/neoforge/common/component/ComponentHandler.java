package me.andreasmelone.glowingeyes.neoforge.common.component;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.component.data.PlayerDataComponent;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.neoforge.common.component.data.IPlayerData;
import me.andreasmelone.glowingeyes.neoforge.common.component.data.PlayerDataComponentImpl;
import me.andreasmelone.glowingeyes.neoforge.common.component.data.PlayerDataImpl;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.GlowingEyesComponentImpl;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.GlowingEyesImpl;
import me.andreasmelone.glowingeyes.neoforge.common.component.eyes.IGlowingEyes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ComponentHandler {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, GlowingEyes.MOD_ID);

    private static final DeferredHolder<AttachmentType<?>, AttachmentType<IGlowingEyes>> GLOWING_EYES = ATTACHMENT_TYPES.register(
            GlowingEyesComponent.IDENTIFIER.getPath(), () -> AttachmentType.serializable(() -> (IGlowingEyes) new GlowingEyesImpl()).build()
    );

    private static final DeferredHolder<AttachmentType<?>, AttachmentType<IPlayerData>> PLAYER_DATA = ATTACHMENT_TYPES.register(
            PlayerDataComponent.IDENTIFIER.getPath(), () -> AttachmentType.builder(() -> (IPlayerData) new PlayerDataImpl()).build()
    );

    public static void register(IEventBus modEventBus) {
        ATTACHMENT_TYPES.register(modEventBus);
        GlowingEyesComponent.setImplementation(new GlowingEyesComponentImpl(GLOWING_EYES));
        PlayerDataComponent.setImplementation(new PlayerDataComponentImpl(PLAYER_DATA));
    }
}
