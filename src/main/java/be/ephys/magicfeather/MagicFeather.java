package be.ephys.magicfeather;

import be.ephys.magicfeather.content.MFCreativeTabs;
import be.ephys.magicfeather.content.MFItems;
import be.ephys.magicfeather.content.util.BeaconRangeCalculator;
import be.ephys.magicfeather.content.util.BeaconTypeHandler;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.InterModProcessEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.HashSet;

@Mod(MagicFeather.MODID)
@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = MagicFeather.MODID)
public class MagicFeather {
  public static final String MODID = "magicfeather";

  private static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, MODID);

  public static final DeferredHolder<PoiType, PoiType> MF_BEACON_POI = POI_TYPES.register("beacon",
          () -> new PoiType(
                  new HashSet<>(Blocks.BEACON.getStateDefinition().getPossibleStates()),
                  0,
                  1
          ));
  public static PoiType AM_BEACON_POI;


  public MagicFeather(IEventBus modEventBus, ModContainer modContainer) {
    MFItems.ITEM_DEFERRED_REGISTER.register(modEventBus);
    MFCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
    POI_TYPES.register(modEventBus);

    modContainer.registerConfig(ModConfig.Type.COMMON, MFConfig.buildSpec());
  }

  @SubscribeEvent
  public static void onRegister(RegisterEvent event) {
    if (event.getRegistryKey().equals(Registries.POINT_OF_INTEREST_TYPE)) {
      ResourceLocation amBeaconId = ResourceLocation.fromNamespaceAndPath("alexsmobs", "am_beacon");
      AM_BEACON_POI = BuiltInRegistries.POINT_OF_INTEREST_TYPE.get(amBeaconId);
    }
  }

  public static PoiType getBeaconPoi() {
    if (AM_BEACON_POI != null) {
      return AM_BEACON_POI;
    }
    return MF_BEACON_POI.get();
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