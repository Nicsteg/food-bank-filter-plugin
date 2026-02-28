package com.foodbankfilter;

import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.bank.BankSearch;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;

@Slf4j
@PluginDescriptor(
    name = "Food Bank Filter",
    description = "Bank UI toggle that shows only edible food items",
    tags = {"bank", "food", "filter"}
)
public class FoodBankFilterPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private ClientThread clientThread;

    @Inject
    private ItemManager itemManager;

    @Inject
    private FoodBankFilterConfig config;

    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private BankSearch bankSearch;

    private NavigationButton navButton;
    private FoodBankFilterPanel panel;
    private boolean filterEnabled;
    private boolean lastBankOpen;
    private boolean sortByHealing;
    private final List<int[]> compactSlotPositions = new ArrayList<>();
    private final Map<Integer, Integer> gePriceCache = new HashMap<>();
    private final Map<Integer, Integer> healingCache = new HashMap<>();

    @Provides
    FoodBankFilterConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(FoodBankFilterConfig.class);
    }

    @Override
    protected void startUp()
    {
        filterEnabled = false;
        sortByHealing = !config.sortByGePrice();
        panel = new FoodBankFilterPanel(
            () -> clientThread.invokeLater(this::toggleFilter),
            () -> clientThread.invokeLater(this::toggleSortMode)
        );
        panel.setFilterEnabled(false);
        panel.setSortMode(sortByHealing);

        navButton = NavigationButton.builder()
            .tooltip("Food Bank Filter")
            .icon(createIcon())
            .priority(6)
            .panel(panel)
            .build();

        clientToolbar.addNavigation(navButton);
    }

    @Override
    protected void shutDown()
    {
        clientThread.invokeLater(this::unhideAndRelayoutBankItems);

        if (navButton != null)
        {
            clientToolbar.removeNavigation(navButton);
            navButton = null;
        }

        panel = null;
        filterEnabled = false;
        lastBankOpen = false;
        sortByHealing = false;
        compactSlotPositions.clear();
        gePriceCache.clear();
        healingCache.clear();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        switch (event.getGameState())
        {
            case HOPPING:
            case LOGIN_SCREEN:
            case CONNECTION_LOST:
                gePriceCache.clear();
                healingCache.clear();
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onClientTick(ClientTick tick)
    {
        boolean bankOpen = isBankOpen();

        if (panel != null && bankOpen != lastBankOpen)
        {
            panel.setBankOpen(bankOpen);
            lastBankOpen = bankOpen;
        }

        if (!bankOpen)
        {
            if (filterEnabled)
            {
                filterEnabled = false;
                if (panel != null)
                {
                    panel.setFilterEnabled(false);
                }
                unhideAndRelayoutBankItems();
            }
            return;
        }

        if (filterEnabled)
        {
            applyFoodFilterCompact();
        }
    }

    private void toggleFilter()
    {
        if (!isBankOpen())
        {
            return;
        }

        filterEnabled = !filterEnabled;

        if (panel != null)
        {
            panel.setFilterEnabled(filterEnabled);
        }

        if (filterEnabled)
        {
            captureCompactSlots();
        }
        else
        {
            unhideAndRelayoutBankItems();
        }
    }

    private void toggleSortMode()
    {
        sortByHealing = !sortByHealing;
        if (panel != null)
        {
            panel.setSortMode(sortByHealing);
        }

        if (filterEnabled && isBankOpen())
        {
            applyFoodFilterCompact();
        }
    }

    private boolean isBankOpen()
    {
        Widget bankContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
        return bankContainer != null && !bankContainer.isHidden();
    }

    private void applyFoodFilterCompact()
    {
        Widget bankContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
        if (bankContainer == null)
        {
            return;
        }

        Widget[] children = bankContainer.getDynamicChildren();
        if (children == null || children.length == 0)
        {
            return;
        }

        if (compactSlotPositions.isEmpty())
        {
            captureCompactSlots();
        }

        List<Widget> itemWidgets = new ArrayList<>();
        for (Widget child : children)
        {
            if (child != null && child.getItemId() > 0)
            {
                itemWidgets.add(child);
            }
        }

        if (itemWidgets.isEmpty() || compactSlotPositions.isEmpty())
        {
            return;
        }

        itemWidgets.sort(Comparator.comparingInt(Widget::getIndex));

        List<Widget> foodWidgets = new ArrayList<>();
        for (Widget widget : itemWidgets)
        {
            if (isFood(widget.getItemId()))
            {
                foodWidgets.add(widget);
            }
            else
            {
                widget.setHidden(true);
            }
        }

        if (sortByHealing)
        {
            foodWidgets.sort(
                Comparator.comparingInt((Widget w) -> getHealingValue(w.getItemId())).reversed()
                    .thenComparing(Comparator.comparingInt((Widget w) -> getGePrice(w.getItemId())).reversed())
                    .thenComparingInt(Widget::getItemId)
            );
        }
        else
        {
            foodWidgets.sort(
                Comparator.comparingInt((Widget w) -> getGePrice(w.getItemId())).reversed()
                    .thenComparing(Comparator.comparingInt((Widget w) -> getHealingValue(w.getItemId())).reversed())
                    .thenComparingInt(Widget::getItemId)
            );
        }

        for (int i = 0; i < foodWidgets.size(); i++)
        {
            Widget widget = foodWidgets.get(i);
            if (i >= compactSlotPositions.size())
            {
                widget.setHidden(true);
                continue;
            }

            int[] pos = compactSlotPositions.get(i);
            widget.setHidden(false);
            widget.setForcedPosition(pos[0], pos[1]);
        }
    }

    private void captureCompactSlots()
    {
        compactSlotPositions.clear();

        Widget bankContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
        if (bankContainer == null)
        {
            return;
        }

        Widget[] children = bankContainer.getDynamicChildren();
        if (children == null || children.length == 0)
        {
            return;
        }

        List<Widget> itemWidgets = new ArrayList<>();
        for (Widget child : children)
        {
            if (child != null && child.getItemId() > 0)
            {
                itemWidgets.add(child);
            }
        }

        itemWidgets.sort(Comparator
            .comparingInt(Widget::getRelativeY)
            .thenComparingInt(Widget::getRelativeX));

        for (Widget widget : itemWidgets)
        {
            compactSlotPositions.add(new int[] {widget.getRelativeX(), widget.getRelativeY()});
        }
    }

    private void unhideAndRelayoutBankItems()
    {
        Widget bankContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
        if (bankContainer != null)
        {
            Widget[] children = bankContainer.getDynamicChildren();
            if (children != null)
            {
                for (Widget child : children)
                {
                    if (child != null)
                    {
                        child.setHidden(false);
                        child.setForcedPosition(child.getOriginalX(), child.getOriginalY());
                    }
                }
            }
        }

        compactSlotPositions.clear();
        bankSearch.layoutBank();
    }

    private boolean isFood(int itemId)
    {
        int canonicalId = itemManager.canonicalize(itemId);
        ItemComposition composition = itemManager.getItemComposition(canonicalId);
        if (composition == null)
        {
            return false;
        }

        String[] actions = composition.getInventoryActions();
        if (actions == null)
        {
            return false;
        }

        return Arrays.stream(actions)
            .anyMatch(action -> action != null && action.equalsIgnoreCase("Eat"));
    }

    private int getGePrice(int itemId)
    {
        int canonicalId = itemManager.canonicalize(itemId);
        return gePriceCache.computeIfAbsent(canonicalId, id ->
        {
            try
            {
                return Math.max(0, itemManager.getItemPrice(id));
            }
            catch (Exception e)
            {
                return 0;
            }
        });
    }

    private int getHealingValue(int itemId)
    {
        int canonicalId = itemManager.canonicalize(itemId);
        return healingCache.computeIfAbsent(canonicalId, id ->
        {
            ItemComposition comp = itemManager.getItemComposition(id);
            if (comp == null || comp.getName() == null)
            {
                return 0;
            }

            String name = comp.getName().toLowerCase();

            if (name.contains("anglerfish")) return 22;
            if (name.contains("manta ray")) return 22;
            if (name.contains("dark crab")) return 22;
            if (name.contains("sea turtle")) return 21;
            if (name.contains("shark")) return 20;
            if (name.contains("potato with cheese")) return 16;
            if (name.contains("curry")) return 19;
            if (name.contains("stew")) return 11;
            if (name.contains("monkfish")) return 16;
            if (name.contains("karambwan")) return 18;
            if (name.contains("anchovy pizza")) return 18;
            if (name.contains("pineapple pizza")) return 22;
            if (name.contains("meat pizza")) return 16;
            if (name.contains("plain pizza")) return 14;
            if (name.contains("tuna potato")) return 22;
            if (name.contains("egg potato")) return 16;
            if (name.contains("chilli potato")) return 14;
            if (name.contains("baked potato")) return 11;
            if (name.contains("bass")) return 13;
            if (name.contains("swordfish")) return 14;
            if (name.contains("lobster")) return 12;
            if (name.contains("trout")) return 7;
            if (name.contains("salmon")) return 9;
            if (name.contains("cake")) return 12;
            if (name.contains("chocolate cake")) return 15;
            if (name.contains("apple pie")) return 14;
            if (name.contains("meat pie")) return 12;
            if (name.contains("garden pie")) return 12;
            if (name.contains("summer pie")) return 22;
            if (name.contains("wild pie")) return 22;
            if (name.contains("admiral pie")) return 16;
            if (name.contains("fish pie")) return 12;
            if (name.contains("redberry pie")) return 10;
            if (name.contains("jugbler")) return 15;
            if (name.contains("jangerberries")) return 2;
            if (name.contains("strawberry")) return 1;
            if (name.contains("watermelon")) return 5;
            if (name.contains("banana")) return 2;
            if (name.contains("tomato")) return 4;
            if (name.contains("cabbage")) return 1;

            return 0;
        });
    }

    private BufferedImage createIcon()
    {
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);

        int transparent = 0x00000000;
        int outline = 0xFF2F2A26;
        int white = 0xFFF6F0E5;
        int beak = 0xFFF2B233;
        int comb = 0xFFD94B48;
        int eye = 0xFF121212;

        int[][] pixels = {
            {transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent},
            {transparent, transparent, transparent, transparent, transparent, transparent, comb, comb, comb, transparent, transparent, transparent, transparent, transparent, transparent, transparent},
            {transparent, transparent, transparent, transparent, transparent, comb, comb, comb, comb, comb, transparent, transparent, transparent, transparent, transparent, transparent},
            {transparent, transparent, transparent, transparent, outline, outline, white, white, white, outline, outline, transparent, transparent, transparent, transparent, transparent},
            {transparent, transparent, transparent, outline, white, white, white, white, white, white, outline, outline, transparent, transparent, transparent, transparent},
            {transparent, transparent, outline, white, white, white, white, white, white, white, white, outline, transparent, transparent, transparent, transparent},
            {transparent, transparent, outline, white, white, white, white, white, white, white, white, outline, beak, outline, transparent, transparent},
            {transparent, outline, outline, white, white, white, white, white, white, white, white, outline, beak, beak, outline, transparent},
            {transparent, outline, white, white, white, white, white, white, white, white, white, outline, beak, outline, transparent, transparent},
            {transparent, outline, white, white, white, white, white, white, white, white, outline, outline, outline, transparent, transparent, transparent},
            {transparent, outline, white, white, white, white, white, white, white, outline, transparent, transparent, transparent, transparent, transparent, transparent},
            {transparent, transparent, outline, white, white, white, white, white, outline, transparent, transparent, transparent, transparent, transparent, transparent, transparent},
            {transparent, transparent, transparent, outline, outline, white, outline, outline, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent},
            {transparent, transparent, transparent, transparent, outline, transparent, outline, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent},
            {transparent, transparent, transparent, transparent, outline, transparent, outline, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent},
            {transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent, transparent}
        };

        for (int y = 0; y < 16; y++)
        {
            for (int x = 0; x < 16; x++)
            {
                image.setRGB(x, y, pixels[y][x]);
            }
        }

        image.setRGB(9, 5, eye);
        return image;
    }
}
