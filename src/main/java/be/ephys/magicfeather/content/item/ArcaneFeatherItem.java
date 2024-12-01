package be.ephys.magicfeather.content.item;

import be.ephys.magicfeather.MFConfig;
import be.ephys.magicfeather.content.MFItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;
import java.util.WeakHashMap;

public class ArcaneFeatherItem extends AbstractFeatherItem {
    public ArcaneFeatherItem(Properties properties) {
        super(properties);
    }

    public static final WeakHashMap<Player, ArcaneFeatherData> GLOBAL_PLAYER_DATA = new WeakHashMap<>();


    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltip, tooltipFlag);

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            if (requiresCurios() && !isCuriosEquipped(player, MFItems.ARCANE_FEATHER.get())) {
                tooltip.add(
                        Component.translatable(getDescriptionId(stack) + ".tooltip.requires_curios")
                                .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
                );
            }

            tooltip.add(
                    Component.translatable(getDescriptionId(stack) + ".tooltip.description")
                            .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
            );
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Pre event) {
        if(event.getEntity().level().isClientSide()) {
            return;
        }

        Player player = event.getEntity();

        ArcaneFeatherData data = GLOBAL_PLAYER_DATA.get(player);
        // if the player instance changes, we have to rebuild this.
        if (data == null || data.player != player) {
            data = new ArcaneFeatherData(player);
            GLOBAL_PLAYER_DATA.put(player, data);
        }

        data.onTick();
    }
    private static class ArcaneFeatherData {
        private final Player player;
        private boolean isSoftLanding = false;
        private boolean wasGrantedFlight = false;
        private boolean isSlowFalling = false;

        private int checkTick = 0;

        public ArcaneFeatherData(Player player) {
            this.player = player;
        }

        public void onTick() {
            if (player.isSpectator()) {
                return;
            }

            boolean mayFly = player.isCreative() || (hasItem(player, MFItems.ARCANE_FEATHER.get()));

            if (mayFly) {
                setMayFly(player, true);
                isSoftLanding = false;
            } else {
                // we only remove the fly ability if we are the one who granted it.
                if (wasGrantedFlight) {
                    isSoftLanding = true;
                }
            }

            if (isSoftLanding) {
                if (this.softLand()) {
                    isSoftLanding = false;
                }
            }

            wasGrantedFlight = mayFly;
        }

        private boolean softLand() {
            if (MFConfig.fallStyle.get() == FallStyle.SLOW_FALL) {
                return this.slowFall();
            } else {
                return this.negateFallDamage();
            }
        }

        private boolean slowFall() {
            // SOFT LANDING:
            // on item removal, we disable flying until the player hits the ground
            // and only then do we remove the creative flight ability

            Abilities abilities = player.getAbilities();
            if (abilities.flying) {
                this.isSlowFalling = true;
                abilities.flying = false;
            }

            abilities.mayfly = false;
            player.onUpdateAbilities();

            boolean isPlayerOnGround = player.onGround() && player.fallDistance < 1F;
            if (isPlayerOnGround) {
                this.isSlowFalling = false;
            } else if (this.isSlowFalling) {
                if (checkTick++ % 5 != 0) {
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 10, 0, false, false));
                }
            }

            return isPlayerOnGround;
        }

        private boolean negateFallDamage() {
            boolean isPlayerOnGround = player.onGround() && player.fallDistance < 1F;

            if (isPlayerOnGround) {
                setMayFly(player, false);

                // softland complete
                return true;
            } else {
                if (player.getAbilities().flying) {
                    player.getAbilities().flying = false;
                    player.onUpdateAbilities();
                }

                // softland in progress
                return false;
            }
        }
    }
}
