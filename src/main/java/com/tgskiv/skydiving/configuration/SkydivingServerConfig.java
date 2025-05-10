package com.tgskiv.skydiving.configuration;


import com.tgskiv.skydiving.network.WindConfigSyncPayload;

public class SkydivingServerConfig  {

    public int ticksPerWindChange = 1200;
    public double windRotationDegrees = 15.0;
    public double maxSpeedDelta = 0.025;
    public double maxWindSpeed = 0.1;
    public double minWindSpeed = 0.00;


    public int FORECAST_MIN_SIZE = 5;
    public int FORECAST_DISPLAY_COUNT = 5;

}
