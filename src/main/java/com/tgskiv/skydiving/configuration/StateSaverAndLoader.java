package com.tgskiv.skydiving.configuration;

import com.google.gson.Gson;
import com.tgskiv.SkydivingMod;
import com.tgskiv.skydiving.network.WindConfigSyncPayload;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import org.slf4j.LoggerFactory;

public class StateSaverAndLoader extends PersistentState {

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

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {

        System.out.println("[SkydivingMod] Saving config: " + new Gson().toJson(skydivingConfig));

        Gson gson = new Gson();
        String json = gson.toJson(skydivingConfig);
        nbt.putString("skydivingConfig", json);

        return nbt;
    }

    // STATIC

    public static StateSaverAndLoader createFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {

        StateSaverAndLoader state = new StateSaverAndLoader();
        if (tag.contains("skydivingConfig")) {
            Gson gson = new Gson();
            state.skydivingConfig = gson.fromJson(tag.getString("skydivingConfig"), SkydivingServerConfig.class);

            System.out.println("[SkydivingMod] Loaded config: " + tag.getString("skydivingConfig"));

        } else {
            state.skydivingConfig = new SkydivingServerConfig(); // fallback

            System.out.println("[SkydivingMod] No config found, using defaults.");
        }
        return state;
    }

    public static StateSaverAndLoader createNew() {
        StateSaverAndLoader state = new StateSaverAndLoader();
        state.skydivingConfig = new SkydivingServerConfig(); // default values
        return state;
    }

    private static final Type<StateSaverAndLoader> type = new Type<>(
            StateSaverAndLoader::createNew, // If there's no 'StateSaverAndLoader' yet create one and refresh variables
            StateSaverAndLoader::createFromNbt, // If there is a 'StateSaverAndLoader' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static StateSaverAndLoader getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        ServerWorld serverWorld = server.getWorld(World.OVERWORLD);
        assert serverWorld != null;



        // The first time the following 'getOrCreate' function is called, it creates a brand new 'StateSaverAndLoader' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'StateSaverAndLoader' NBT on disk to our function 'StateSaverAndLoader::createFromNbt'.
        StateSaverAndLoader state = serverWorld.getPersistentStateManager().getOrCreate(type, SkydivingMod.MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }
}