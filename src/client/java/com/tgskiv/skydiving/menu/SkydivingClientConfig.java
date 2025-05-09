package com.tgskiv.skydiving.menu;


import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "skydivingmod")
public class SkydivingClientConfig implements ConfigData  {
    public static int ticksPerWindChange = 1200;
    public static double windRotationDegrees = 15.0;
    public static double maxSpeedDelta = 0.025;
    public static double maxWindSpeed = 0.1;
    public static double minWindSpeed = 0.00;

}
