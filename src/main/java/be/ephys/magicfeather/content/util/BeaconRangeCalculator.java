package be.ephys.magicfeather.content.util;

import be.ephys.magicfeather.MFConfig;
import be.ephys.magicfeather.MagicFeather;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.WeakHashMap;

public final class BeaconRangeCalculator {

    private static final WeakHashMap<Entity, BeaconRangeCache> PLAYER_BEACON_CACHE = new WeakHashMap<>();
    private static final WeakHashMap<Class<? extends BlockEntity>, BeaconTypeHandler> GLOBAL_BEACON_HANDLERS = new WeakHashMap<>();

    public enum BeaconVerticalRangeType {
        JAVA(0, 256),
        FULL_HEIGHT(0, 0);

        private final int downRangeExtension;
        private final int upRangeExtension;

        BeaconVerticalRangeType(int downRangeExtension, int upRangeExtension) {
            this.downRangeExtension = downRangeExtension;
            this.upRangeExtension = upRangeExtension;
        }
    }

    private static class BeaconRangeCache {
        private final BlockPos position;
        private final int dimension;
        private final boolean inRange;
        private final long expireTime;

        public BeaconRangeCache(BlockPos position, int dimension, boolean inRange, long expireTime) {
            this.position = position;
            this.dimension = dimension;
            this.inRange = inRange;
            this.expireTime = expireTime;
        }

        public boolean isValid(Entity entity, long gameTime) {
            BlockPos currentPos = entity.blockPosition();
            int currentDimension = entity.level().dimension().hashCode();

            return position.distManhattan(currentPos) <= 5
                    && dimension == currentDimension
                    && gameTime < expireTime;
        }
    }

    public static void registerBeaconType(BeaconTypeHandler data) {
        GLOBAL_BEACON_HANDLERS.put(data.getTargetClass(), data);
    }

    public static boolean isInBeaconRange(Entity entity) {
        long gameTime = entity.level().getGameTime();
        BeaconRangeCache cache = PLAYER_BEACON_CACHE.get(entity);
        if (cache != null && cache.isValid(entity, gameTime)) {
            return cache.inRange;
        }

        if (!(entity.level() instanceof ServerLevel)) {
            return false;
        }

        ServerLevel world = (ServerLevel) entity.level();
        Vec3 entityPos = entity.position();
        BlockPos playerPos = entity.blockPosition();
        BeaconVerticalRangeType verticalRangeType = MFConfig.verticalRangeType.get();

        int maxRange = MFConfig.baseRange.get() + (4 * MFConfig.rangeStep.get());

        PoiManager poiManager = world.getPoiManager();

        Optional<BlockPos> foundBeaconPos = poiManager.find(
                holder -> holder.value() == MagicFeather.getBeaconPoi(),
                (pos) -> {
                    BlockEntity blockEntityAtPos = world.getBlockEntity(pos);

                    if (blockEntityAtPos == null) {
                        return false;
                    }

                    int radius = getBeaconRange(blockEntityAtPos);
                    if (radius == 0) {
                        return false;
                    }

                    int x = pos.getX();
                    int y = pos.getY();
                    int z = pos.getZ();

                    if (entityPos.x < (x - radius) || entityPos.x > (x + radius)) {
                        return false;
                    }

                    if (entityPos.z < (z - radius) || entityPos.z > (z + radius)) {
                        return false;
                    }

                    if (verticalRangeType != BeaconVerticalRangeType.FULL_HEIGHT) {
                        if (entityPos.y < (y - radius - verticalRangeType.downRangeExtension)
                                || entityPos.y > (y + radius + verticalRangeType.upRangeExtension)) {
                            return false;
                        }
                    }

                    return true;
                },
                playerPos,
                maxRange,
                PoiManager.Occupancy.ANY
        );

        boolean inRange = foundBeaconPos.isPresent();

        PLAYER_BEACON_CACHE.put(entity, new BeaconRangeCache(
                playerPos,
                world.dimension().hashCode(),
                inRange,
                inRange ? gameTime + 20 : gameTime + 10
        ));

        return inRange;
    }

    private static int getBeaconRange(BlockEntity te) {
        BeaconTypeHandler handler = GLOBAL_BEACON_HANDLERS.get(te.getClass());
        if (handler != null) {
            return handler.getFlightRangeAroundBeacon(te);
        }

        if (!(te instanceof BeaconBlockEntity beacon)) {
            return 0;
        }

        if (beacon.getBeamSections().isEmpty()) {
            return 0;
        }

        int level = beacon.levels;
        if (level == 0) {
            return 0;
        }

        return MFConfig.baseRange.get() + (level * MFConfig.rangeStep.get());
    }
}