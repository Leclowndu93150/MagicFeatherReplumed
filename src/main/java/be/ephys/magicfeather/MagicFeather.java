package be.ephys.magicfeather;

import be.ephys.magicfeather.content.MFCreativeTabs;
import be.ephys.magicfeather.content.MFItems;
import be.ephys.magicfeather.content.util.BeaconRangeCalculator;
import be.ephys.magicfeather.content.util.BeaconTypeHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;

@Mod(MagicFeather.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = MagicFeather.MODID)
public class MagicFeather {
  public static final String MODID = "magicfeather";

  public MagicFeather(IEventBus modEventBus, ModContainer modContainer) {
    MFItems.ITEM_DEFERRED_REGISTER.register(modEventBus);
    MFCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    modContainer.registerConfig(ModConfig.Type.COMMON, MFConfig.buildSpec());
  }

  @SubscribeEvent
  public static void processInterComms(InterModProcessEvent event) {
    event.getIMCStream(method -> method.equals("add-beacon-handler")).forEach(msg -> {
      Object data = msg.messageSupplier().get();

      if (data instanceof BeaconTypeHandler) {
        BeaconRangeCalculator.registerBeaconType((BeaconTypeHandler) data);
      }
    });
  }
}