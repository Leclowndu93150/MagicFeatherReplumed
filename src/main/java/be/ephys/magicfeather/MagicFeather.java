package be.ephys.magicfeather;

import be.ephys.magicfeather.content.util.BeaconRangeCalculator;
import be.ephys.magicfeather.content.util.BeaconTypeHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashSet;

@Mod(MagicFeather.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MagicFeather.MODID)
public class MagicFeather {
  public static final String MODID = "magicfeather";

  private static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, MODID);

  public static final RegistryObject<PoiType> MF_BEACON_POI = POI_TYPES.register("beacon",
          () -> new PoiType(
                  new HashSet<>(Blocks.BEACON.getStateDefinition().getPossibleStates()),
                  0,
                  1
          ));
  public static PoiType AM_BEACON_POI;

  public MagicFeather() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, MFConfig.buildSpec());

    POI_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onRegister);
  }

  private void onRegister(RegisterEvent event) {
    if (event.getRegistryKey().equals(ForgeRegistries.Keys.POI_TYPES)) {
      ResourceLocation amBeaconId = new ResourceLocation("alexsmobs", "am_beacon");
      AM_BEACON_POI = ForgeRegistries.POI_TYPES.getValue(amBeaconId);
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