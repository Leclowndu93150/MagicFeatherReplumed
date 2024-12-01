package be.ephys.magicfeather.content;

import be.ephys.magicfeather.MagicFeather;
import be.ephys.magicfeather.content.item.ArcaneFeatherItem;
import be.ephys.magicfeather.content.item.MagicFeatherItem;
import be.ephys.magicfeather.content.item.PrimevalFeatherItem;
import be.ephys.magicfeather.content.item.StygianFeatherItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = MagicFeather.MODID)
public class MFItems {
  public static final DeferredRegister.Items ITEM_DEFERRED_REGISTER = DeferredRegister.createItems(MagicFeather.MODID);
  public static final DeferredItem<MagicFeatherItem> MAGIC_FEATHER = ITEM_DEFERRED_REGISTER.register("magic_feather", () -> new MagicFeatherItem(new Item.Properties().stacksTo(1)));

  public static final DeferredItem<ArcaneFeatherItem> ARCANE_FEATHER = ITEM_DEFERRED_REGISTER.register("arcane_feather", () -> new ArcaneFeatherItem(new Item.Properties().stacksTo(1)));

  public static final DeferredItem<PrimevalFeatherItem> PRIMEVAL_FEATHER = ITEM_DEFERRED_REGISTER.register("primeval_feather", PrimevalFeatherItem::new);
  public static final DeferredItem<StygianFeatherItem> STYGIAN_FEATHER = ITEM_DEFERRED_REGISTER.register("stygian_feather", () -> new StygianFeatherItem(new Item.Properties().stacksTo(1)));


  @SubscribeEvent
  public static void onConstructMod(final FMLConstructModEvent evt) {
    NeoForge.EVENT_BUS.addListener(MagicFeatherItem::onPlayerTick);
    NeoForge.EVENT_BUS.addListener(ArcaneFeatherItem::onPlayerTick);
    NeoForge.EVENT_BUS.addListener(PrimevalFeatherItem::onPlayerTick);
    NeoForge.EVENT_BUS.addListener(StygianFeatherItem::onPlayerTick);
  }
}
