package com.tgskiv.skydiving;


import com.tgskiv.skydiving.network.WindConfigSyncPayload;

public class SkydivingServerConfig  {
    public static int ticksPerWindChange = 1200;
    public static double windRotationDegrees = 15.0;
    public static double maxSpeedDelta = 0.025;
    public static double maxWindSpeed = 0.1;
    public static double minWindSpeed = 0.00;


    public static int FORECAST_MIN_SIZE = 5;
    public static int FORECAST_DISPLAY_COUNT = 5;


    public static void updateSettings(WindConfigSyncPayload payload) {
        SkydivingServerConfig.ticksPerWindChange = payload.ticksPerWindChange();
        SkydivingServerConfig.maxSpeedDelta = payload.maxSpeedDelta();
        SkydivingServerConfig.maxWindSpeed = payload.maxWindSpeed();
        SkydivingServerConfig.minWindSpeed = payload.minWindSpeed();
        SkydivingServerConfig.windRotationDegrees = payload.windRotationDegrees();
    }
}
