package com.tgskiv.skydiving.menu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.tgskiv.skydiving.network.WindConfigSyncPayload;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.LoggerFactory;


@Environment(EnvType.CLIENT)
public class SkydivingModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.literal("Skydiving Mod Settings"));

            ConfigCategory general = builder.getOrCreateCategory(Text.literal("Wind Settings"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            general.addEntry(entryBuilder.startIntField(Text.literal("Ticks Per Wind Change"), SkydivingClientConfig.ticksPerWindChange)
                    .setDefaultValue(300)
                    .setSaveConsumer(newValue -> SkydivingClientConfig.ticksPerWindChange = newValue)
                    .build());

            general.addEntry(entryBuilder.startDoubleField(Text.literal("Wind Rotation Degrees"), SkydivingClientConfig.windRotationDegrees)
                    .setDefaultValue(20.0)
                    .setSaveConsumer(newValue -> SkydivingClientConfig.windRotationDegrees = newValue)
                    .build());

            general.addEntry(entryBuilder.startDoubleField(Text.literal("Max Speed Delta"), SkydivingClientConfig.maxSpeedDelta)
                    .setDefaultValue(0.025)
                    .setSaveConsumer(newValue -> SkydivingClientConfig.maxSpeedDelta = newValue)
                    .build());

            general.addEntry(entryBuilder.startDoubleField(Text.literal("Max Wind Speed"), SkydivingClientConfig.maxWindSpeed)
                    .setDefaultValue(0.1)
                    .setSaveConsumer(newValue -> SkydivingClientConfig.maxWindSpeed = newValue)
                    .build());

            general.addEntry(entryBuilder.startDoubleField(Text.literal("Min Wind Speed"), SkydivingClientConfig.minWindSpeed)
                    .setDefaultValue(0.0)
                    .setSaveConsumer(newValue -> SkydivingClientConfig.minWindSpeed = newValue)
                    .build());

            builder.setSavingRunnable(() -> {

                if (MinecraftClient.getInstance().world == null) {

                    LoggerFactory.getLogger("SkydivingMod").warn("Failed to send configuration to server: No connection available");
                    return;
                }
                try {
                    // Send config to server
                    ClientPlayNetworking.send(new WindConfigSyncPayload(
                            SkydivingClientConfig.ticksPerWindChange,
                            SkydivingClientConfig.windRotationDegrees,
                            SkydivingClientConfig.maxSpeedDelta,
                            SkydivingClientConfig.maxWindSpeed,
                            SkydivingClientConfig.minWindSpeed
                    ));
                } catch (Exception e) {
                    LoggerFactory.getLogger("SkydivingMod").warn("Failed to send message to server: No connection available", e);
                }

            });

            LoggerFactory.getLogger("SkydivingMod").info("Building the skydivingmod settings screen");

            return builder.build();
        };
    }
}
