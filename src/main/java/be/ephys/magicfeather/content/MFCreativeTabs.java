package be.ephys.magicfeather.content;

import be.ephys.magicfeather.MagicFeather;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MagicFeather.MODID)
public class MFCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MagicFeather.MODID);

    public static final RegistryObject<CreativeModeTab> MF_TAB = CREATIVE_MODE_TABS.register("magicfeather", () -> CreativeModeTab.builder()
            .title(Component.literal("Magic Feather"))
            .icon(() -> MFItems.MAGIC_FEATHER.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(MFItems.MAGIC_FEATHER.get().getDefaultInstance());
                output.accept(MFItems.ARCANE_FEATHER.get().getDefaultInstance());
                output.accept(MFItems.PRIMEVAL_FEATHER.get().getDefaultInstance());
                output.accept(MFItems.STYGIAN_FEATHER.get().getDefaultInstance());
            }).build());

    @SubscribeEvent
    public static void onConstructMod(final FMLConstructModEvent evt) {
        CREATIVE_MODE_TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
