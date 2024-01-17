package me.andreasmelone.glowingeyes.client.ui;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.presets.Preset;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiPresetButton;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesProvider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;
import java.util.HashMap;
import java.util.List;

public class PresetsScreen extends GuiScreen {
    private int guiLeft;
    private int guiTop;

    protected int xSize = 256; // the size of the texture is 256x222
    protected int ySize = 222; // the size of the texture is 256x222

    private final GuiScreen parent;
    private final PresetManager presetManager = PresetManager.getInstance();

    private EntityPlayer player;

    public PresetsScreen(GuiScreen parent) {
        this.parent = parent;
    }

    boolean toggledState = true;
    HashMap<Point, Color> savedPixelMap = new HashMap<>();

    int page = 0;
    int offset = 0;
    int buttons = 0;

    int selectedPreset = -1;

    @Override
    public void initGui() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        // <- and ->
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        this.guiLeft + 20, this.guiTop + 190,
                        20, 20,
                        "<"
                )
        );
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        this.guiLeft + 90, this.guiTop + 190,
                        20, 20,
                        ">"
                )
        );
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        this.guiLeft + this.xSize - 90, this.guiTop + 120,
                        80, 20,
                        "Done"
                )
        );
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        this.guiLeft + this.xSize - 90, this.guiTop + 142,
                        80, 20,
                        "Cancel"
                )
        );

        player = mc.player;
        savedPixelMap.putAll(player.getCapability(GlowingEyesProvider.CAPABILITY, null).getGlowingEyesMap());
        toggledState = player.getCapability(GlowingEyesProvider.CAPABILITY, null).isToggledOn();

        offset = buttons;

        List<Preset> presets = presetManager.getPresets();
        int j = 0;
        for(Preset preset : presets) {
            if(j >= 6) break;
            this.buttonList.add(
                    new GuiPresetButton(
                            j + buttons,
                            this.guiLeft + 10, this.guiTop + 10 + (j * 30),
                            preset
                    )
            );
            j++;
        }
        buttons += j;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        GuiUtil.drawBackground(TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        // draw the fake player above the done button
        GuiInventory.drawEntityOnScreen(
                this.guiLeft + this.xSize - 90 + 30,
                this.guiTop + 160 - 30,
                30,
                (float) (this.guiLeft + this.xSize - 70) - mouseX,
                (float) (this.guiTop + this.ySize - 10 - 30) - mouseY,
                player
        );

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id == 0) {
            switchPage(page - 1);
        } else if(button.id == 1) {
            switchPage(page + 1);
        } else if(button.id == 2) {
            // done
            player.getCapability(GlowingEyesProvider.CAPABILITY, null).setToggledOn(toggledState);
            if(selectedPreset != -1) {
                presetManager.applyPreset(selectedPreset);
            }
            this.mc.displayGuiScreen(parent);
        } else if(button.id == 3) {
            // cancel
            player.getCapability(GlowingEyesProvider.CAPABILITY, null).setGlowingEyesMap(savedPixelMap);
            GlowingEyes.proxy.setPixelMap(savedPixelMap);
            player.getCapability(GlowingEyesProvider.CAPABILITY, null).setToggledOn(toggledState);
            GlowingEyes.proxy.setToggledOn(toggledState);

            this.mc.displayGuiScreen(parent);
        } else if(button instanceof GuiPresetButton) {
            GuiPresetButton presetButton = (GuiPresetButton) button;
            selectedPreset = presetButton.getPreset().getId();
            for(GuiButton b : this.buttonList) {
                if(b instanceof GuiPresetButton) {
                    GuiPresetButton pb = (GuiPresetButton) b;
                    pb.setSelected(pb.getPreset().getId() == selectedPreset);
                    if(pb.getPreset().getId() == selectedPreset) {
                        // we need to set the capability of the fake player here
                        player.getCapability(GlowingEyesProvider.CAPABILITY, null)
                                .setGlowingEyesMap(pb.getPreset().getContent());
                        GlowingEyes.proxy.setPixelMap(pb.getPreset().getContent());
                    }
                }
            }
        }
    }

    private void switchPage(int newPage) {
        // switch the page, if the new page has less elements than 6, then hide the other buttons
        if(presetManager.hasPage(newPage, 6)) {
            List<Preset> presets = presetManager.getPresets();
            page = newPage;
            int j = offset;
            for(int i = 0; i < 6; i++) {
                if((page * 6) + i >= presets.size()) {
                    this.buttonList.get(j).visible = false;
                } else {
                    this.buttonList.get(j).visible = true;
                    GuiPresetButton presetButton = (GuiPresetButton) this.buttonList.get(j);
                    presetButton.setPreset(presets.get((page * 6) + i));
                    presetButton.setSelected(presetButton.getPreset().getId() == selectedPreset);
                }
                j++;
            }
        }
    }
}
