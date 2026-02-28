package com.foodbankfilter;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("foodbankfilter")
public interface FoodBankFilterConfig extends Config
{
    @ConfigItem(
        keyName = "sortByGePrice",
        name = "Sort by GE price",
        description = "Sort visible food by GE value (highest first)"
    )
    default boolean sortByGePrice()
    {
        return true;
    }
}
