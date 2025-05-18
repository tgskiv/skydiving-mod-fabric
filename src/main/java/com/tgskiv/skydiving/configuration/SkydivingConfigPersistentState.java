package com.tgskiv.skydiving.configuration;

import com.google.gson.Gson;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;
import com.tgskiv.skydiving.network.WindConfigSyncPayload;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.slf4j.LoggerFactory;
import net.minecraft.world.PersistentStateType;

import static com.tgskiv.SkydivingMod.MOD_ID;

public class SkydivingConfigPersistentState extends PersistentState {

    public SkydivingServerConfig skydivingConfig;


    public void updateSettingsWithPayload(WindConfigSyncPayload payload) {

        LoggerFactory.getLogger("skydivingmod").info("updateSettingsWithPayload");

        skydivingConfig.ticksPerWindChange = payload.ticksPerWindChange();
        skydivingConfig.maxSpeedDelta = payload.maxSpeedDelta();
        skydivingConfig.maxWindSpeed = payload.maxWindSpeed();
        skydivingConfig.minWindSpeed = payload.minWindSpeed();
        skydivingConfig.windRotationDegrees = payload.windRotationDegrees();

        this.markDirty();
    }

    public static Codec<SkydivingConfigPersistentState> codec(final ServerWorld world) {
        return Codec.of(new Encoder<>() {
            @Override
            public <T> DataResult<T> encode(SkydivingConfigPersistentState input, DynamicOps<T> ops, T prefix) {
                NbtCompound nbtCompound = new NbtCompound();
                System.out.println("[SkydivingMod] Saving config: " + new Gson().toJson(input));

                Gson gson = new Gson();
                String json = gson.toJson(input);
                nbtCompound.putString("skydivingConfig", json);

                return DataResult.success((T) nbtCompound);
            }
        }, new Decoder<>() {

            @Override
            public <T> DataResult<Pair<SkydivingConfigPersistentState, T>> decode(DynamicOps<T> ops, T input) {
                NbtElement hi = ops.convertTo(NbtOps.INSTANCE, input);
                var hello = SkydivingConfigPersistentState.createFromNbt(hi.asCompound().get(), world.getRegistryManager());
                return DataResult.success(Pair.of(hello, ops.empty()));
            }
        });
    }



    public static SkydivingConfigPersistentState createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {

        SkydivingConfigPersistentState state = new SkydivingConfigPersistentState();
        if (tag.contains("skydivingConfig")) {
            Gson gson = new Gson();
            state.skydivingConfig = gson.fromJson(String.valueOf(tag.getString("skydivingConfig")), SkydivingServerConfig.class);

            System.out.println("[SkydivingMod] Loaded config: " + tag.getString("skydivingConfig"));

        } else {
            state.skydivingConfig = new SkydivingServerConfig(); // fallback

            System.out.println("[SkydivingMod] No config found, using defaults.");
        }
        return state;
    }

    public static SkydivingConfigPersistentState createNew() {
        SkydivingConfigPersistentState state = new SkydivingConfigPersistentState();
        state.skydivingConfig = new SkydivingServerConfig(); // default values
        return state;
    }

//    private static final PersistentStateType<StateSaverAndLoader> type = new PersistentStateType<>(
//            MOD_ID,
//            StateSaverAndLoader::new,
//            StateSaverAndLoader,
//            null
//    );
//
//    private static final PersistentStateType<StateSaverAndLoader> type = new PersistentStateType<>(
//            StateSaverAndLoader::createNew, // If there's no 'StateSaverAndLoader' yet create one and refresh variables
//            StateSaverAndLoader::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
//            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
//    );

    public static SkydivingConfigPersistentState getServerState(MinecraftServer server) {
//        ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
//        assert serverWorld != null;
//
//        return serverWorld.getPersistentStateManager().getOrCreate(new PersistentStateType<>(MOD_ID, StateSaverAndLoader::new,
//                codec(serverWorld), null));

        PersistentStateManager persistentStateManager = server.getOverworld().getPersistentStateManager();
        return persistentStateManager.getOrCreate(new PersistentStateType<>(MOD_ID, SkydivingConfigPersistentState::new,
                codec(server.getOverworld()), null));
    }
}