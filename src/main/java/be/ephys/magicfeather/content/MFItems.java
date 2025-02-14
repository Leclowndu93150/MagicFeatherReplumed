package be.ephys.magicfeather.content;

import be.ephys.magicfeather.MagicFeather;
import be.ephys.magicfeather.content.item.ArcaneFeatherItem;
import be.ephys.magicfeather.content.item.MagicFeatherItem;
import be.ephys.magicfeather.content.item.PrimevalFeatherItem;
import be.ephys.magicfeather.content.item.StygianFeatherItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MagicFeather.MODID)
public class MFItems {
  private static final DeferredRegister<Item> ITEM_DEFERRED_REGISTER = DeferredRegister.create(Registries.ITEM, MagicFeather.MODID);
  public static final RegistryObject<Item> MAGIC_FEATHER = ITEM_DEFERRED_REGISTER.register("magic_feather", () -> new MagicFeatherItem(new Item.Properties().stacksTo(1)));

  public static final RegistryObject<Item> ARCANE_FEATHER = ITEM_DEFERRED_REGISTER.register("arcane_feather", () -> new ArcaneFeatherItem(new Item.Properties().stacksTo(1)));

  public static final RegistryObject<Item> PRIMEVAL_FEATHER = ITEM_DEFERRED_REGISTER.register("primeval_feather", PrimevalFeatherItem::new);
  public static final RegistryObject<Item> STYGIAN_FEATHER = ITEM_DEFERRED_REGISTER.register("stygian_feather", () -> new StygianFeatherItem(new Item.Properties().stacksTo(1)));


  @SubscribeEvent
  public static void onConstructMod(final FMLConstructModEvent evt) {
    ITEM_DEFERRED_REGISTER.register(FMLJavaModLoadingContext.get().getModEventBus());
    MinecraftForge.EVENT_BUS.addListener(MagicFeatherItem::onPlayerTick);
    MinecraftForge.EVENT_BUS.addListener(ArcaneFeatherItem::onPlayerTick);
    MinecraftForge.EVENT_BUS.addListener(PrimevalFeatherItem::onPlayerTick);
    MinecraftForge.EVENT_BUS.addListener(StygianFeatherItem::onPlayerTick);
  }

  @SubscribeEvent
  public static void sendInterComms(InterModEnqueueEvent event) {
    InterModComms.sendTo("curios", SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
  }
}
