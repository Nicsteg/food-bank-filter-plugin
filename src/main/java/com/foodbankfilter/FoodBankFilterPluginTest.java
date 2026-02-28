package com.foodbankfilter;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class FoodBankFilterPluginTest
{
    public static void main(String[] args) throws Exception
    {
        ExternalPluginManager.loadBuiltin(FoodBankFilterPlugin.class);
        RuneLite.main(args);
    }
}
