package be.ephys.magicfeather.content;

import be.ephys.magicfeather.MagicFeather;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class MFCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicFeather.MODID);

    public static final Supplier<CreativeModeTab> MF_TAB = CREATIVE_MODE_TABS.register("magicfeather", () -> CreativeModeTab.builder()
            .title(Component.literal("Magic Feather"))
            .icon(() -> MFItems.MAGIC_FEATHER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(MFItems.MAGIC_FEATHER.get().getDefaultInstance());
                output.accept(MFItems.ARCANE_FEATHER.get().getDefaultInstance());
                output.accept(MFItems.PRIMEVAL_FEATHER.get().getDefaultInstance());
                output.accept(MFItems.STYGIAN_FEATHER.get().getDefaultInstance());
            }).build());

}
