package com.foodbankfilter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.runelite.client.ui.PluginPanel;

class FoodBankFilterPanel extends PluginPanel
{
    private final JLabel statusLabel = new JLabel();
    private final JLabel bankLabel = new JLabel();
    private final JLabel sortLabel = new JLabel();
    private final JButton toggleButton = new JButton();
    private final JButton sortModeButton = new JButton();

    FoodBankFilterPanel(Runnable onToggleFilter, Runnable onToggleSortMode)
    {
        setLayout(new BorderLayout(0, 8));

        JPanel content = new JPanel(new GridLayout(0, 1, 0, 6));
        content.add(statusLabel);
        content.add(bankLabel);
        content.add(sortLabel);

        toggleButton.addActionListener(e -> onToggleFilter.run());
        content.add(toggleButton);

        sortModeButton.addActionListener(e -> onToggleSortMode.run());
        content.add(sortModeButton);

        add(content, BorderLayout.NORTH);

        setFilterEnabled(false);
        setBankOpen(false);
        setSortMode(false);
    }

    void setFilterEnabled(boolean enabled)
    {
        statusLabel.setText(enabled ? "Food filter: ON" : "Food filter: OFF");
        toggleButton.setText(enabled ? "Disable filter" : "Enable filter");
    }

    void setBankOpen(boolean open)
    {
        bankLabel.setText(open ? "Bank open: yes" : "Bank open: no");
        toggleButton.setEnabled(open);
        sortModeButton.setEnabled(open);
    }

    void setSortMode(boolean healingMode)
    {
        sortLabel.setText(healingMode ? "Sort: highest healing" : "Sort: highest GE price");
        sortModeButton.setText(healingMode ? "Switch to GE sort" : "Switch to healing sort");
    }
}
