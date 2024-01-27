package me.andreasmelone.glowingeyes.client.ui.preset;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.presets.Preset;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.ui.EyesEditScreen;
import me.andreasmelone.glowingeyes.client.ui.buttons.GuiPresetButton;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesProvider;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


// I have been told my code looks like it was obfuscated, compiled, decompiled and obfuscated.
public class PresetsScreen extends GuiScreen {
    private int guiLeft;
    private int guiTop;

    private int middleX;
    private int middleY;

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

    int pageSize;

    boolean isLocked = false;

    @Override
    public void initGui() {
        buttons = 0;

        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.middleX = this.guiLeft + this.xSize / 2;
        this.middleY = this.guiTop + this.ySize / 2;

        pageSize = 5;

        int leftButtonX = (int) (this.guiLeft + 128 * ((double)1 / 4));
        int rightButtonX = (int) (this.guiLeft + 128 * ((double)3 / 4));

        // <- and ->
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        leftButtonX, this.guiTop - 20 + ((pageSize + 1) * 30),
                        20, 20,
                        "<"
                )
        );
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        rightButtonX, this.guiTop - 20 + ((pageSize + 1) * 30),
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
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        this.guiLeft + 10, this.guiTop + 5 + ((pageSize + 1) * 30),
                        128 / 2 - 3, 20,
                        "Create"
                )
        );
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        this.guiLeft + 10 + (128 / 2) + (3 * 2), this.guiTop + 5 + ((pageSize + 1) * 30),
                        128 / 2 - 3, 20,
                        "Delete"
                )
        );
        this.buttonList.add(
                new GuiButton(
                        buttons++,
                        this.guiLeft + this.xSize - 90, this.guiTop + 164,
                        80, 20,
                        "Lock"
                )
        );

        player = mc.player;
        savedPixelMap.putAll(player.getCapability(GlowingEyesProvider.CAPABILITY, null).getGlowingEyesMap());
        toggledState = player.getCapability(GlowingEyesProvider.CAPABILITY, null).isToggledOn();

        offset = buttons;

        List<Preset> presets = new ArrayList<>(presetManager.getPresets().values());
        int j = 0;
        for(Preset preset : presets) {
            if(j >= pageSize) break;
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

        final int sourceWidth = 34;
        final int sourceHeight = 45;
        final int finalWidth = 50;
        final int finalHeight = (int) ((float) finalWidth / sourceWidth * sourceHeight);

        final int scale = 30;

        mc.getTextureManager().bindTexture(TextureLocations.UI_PLAYERBOX);
        GuiUtil.drawScaledCustomSizeModalRect(
                this.guiLeft + this.xSize - 90 + (scale / 2), this.guiTop + 110 - (scale * 2),
                0, 0,
                sourceWidth, sourceHeight,
                finalWidth, finalHeight,
                64, 64
        );

        // account for the preset menu and draw it below an already rendered texture
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(GL11.GL_LEQUAL);

        GuiInventory.drawEntityOnScreen(
                this.guiLeft + this.xSize - 90 + 40,
                this.guiTop + 110,
                scale,
                (float) (isLocked ? 0 : (double) (this.guiLeft + this.xSize - 90 + 40) - mouseX),
                (float) (isLocked ? 0 : (double) (this.guiTop + 110 - 20) - mouseY),
                player
        );

        GlStateManager.disableDepth();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private void unselectPreset() {
        selectedPreset = -1;
        for(GuiButton b : this.buttonList) {
            if(b instanceof GuiPresetButton) {
                GuiPresetButton pb = (GuiPresetButton) b;
                pb.setSelected(false);
            }
        }
        this.buttonList.get(4).displayString = "Create";
    }

    private void setEditing(boolean editing) {
        this.editing = editing;
        if(editing) {
            this.buttonList.get(4).displayString = "Edit";
        } else {
            this.buttonList.get(4).displayString = "Create";
        }
    }

    boolean editing = false;

    @Override
    protected void actionPerformed(GuiButton button) {
        if(button.id == 0) {
            switchPage(page - 1);
        } else if(button.id == 1) {
            switchPage(page + 1);
        } else if(button.id == 2) {
            player.getCapability(GlowingEyesProvider.CAPABILITY, null).setToggledOn(toggledState);
            if(selectedPreset != -1) {
                presetManager.applyPreset(selectedPreset);
            }
            if(parent instanceof EyesEditScreen)
                ((EyesEditScreen)parent).openAsParent();
            else
                this.mc.displayGuiScreen(parent);
        } else if(button.id == 3) {
            player.getCapability(GlowingEyesProvider.CAPABILITY, null).setGlowingEyesMap(savedPixelMap);
            GlowingEyes.proxy.setPixelMap(savedPixelMap);
            player.getCapability(GlowingEyesProvider.CAPABILITY, null).setToggledOn(toggledState);
            GlowingEyes.proxy.setToggledOn(toggledState);

            this.mc.displayGuiScreen(parent);
        } else if (button.id == 4) {
            if(editing) {
                EditPresetScreen.askForName(this, presetManager.getPreset(selectedPreset).getName()).thenAccept((result) -> {
                    if(result != null) {
                        presetManager.getPreset(selectedPreset).setName(result);
                        switchPage(page);
                        this.unselectPreset();
                    }
                });
            }
            else this.mc.displayGuiScreen(new CreatePresetScreen(mc, this));
        } else if(button.id == 5) {
            if(selectedPreset != -1) {
                ConfirmDeletionScreen.askToDelete(this, presetManager.getPreset(selectedPreset).getName()).thenAccept((result) -> {
                    if(result) {
                        presetManager.removePreset(selectedPreset);
                        if(presetManager.hasPage(page, pageSize))
                            switchPage(page);
                        else {
                            page--;
                            switchPage(page);
                        }
                    }
                    unselectPreset();
                });
            }
        } else if(button.id == 6) {
            isLocked = !isLocked;
            button.displayString = isLocked ? "Unlock" : "Lock";
        } else if(button instanceof GuiPresetButton) {
            GuiPresetButton presetButton = (GuiPresetButton) button;
            if(!presetManager.hasPreset(presetButton.getPreset().getId())) {
                // we need to like update the page or sum
                return;
            }
            if(presetButton.getPreset().getId() == selectedPreset) selectedPreset = -1;
            else selectedPreset = presetButton.getPreset().getId();

            this.setEditing(selectedPreset != -1);

            for(GuiButton b : this.buttonList) {
                if(b instanceof GuiPresetButton) {
                    GuiPresetButton pb = (GuiPresetButton) b;
                    pb.setSelected(pb.getPreset().getId() == selectedPreset);
                    if(pb.getPreset().getId() == selectedPreset) {
                        // we need to set the capability of the player here
                        player.getCapability(GlowingEyesProvider.CAPABILITY, null)
                                .setGlowingEyesMap(pb.getPreset().getContent());
                        GlowingEyes.proxy.setPixelMap(pb.getPreset().getContent());
                    }
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        player.getCapability(GlowingEyesProvider.CAPABILITY, null).setGlowingEyesMap(savedPixelMap);
        GlowingEyes.proxy.setPixelMap(savedPixelMap);
        player.getCapability(GlowingEyesProvider.CAPABILITY, null).setToggledOn(toggledState);
        GlowingEyes.proxy.setToggledOn(toggledState);

        presetManager.savePresets();
    }

    private void switchPage(int newPage) {
        // switch the page, if the new page has less elements than pageSize, then hide the other buttons
        if(presetManager.hasPage(newPage, pageSize)) {
            List<Preset> presets = presetManager.getPresetList();
            page = newPage;
            int j = offset;
            for(int i = 0; i < pageSize; i++) {
                if((page * pageSize) + i >= presets.size()) {
                    this.buttonList.get(j).visible = false;
                } else {
                    this.buttonList.get(j).visible = true;
                    GuiPresetButton presetButton = (GuiPresetButton) this.buttonList.get(j);
                    presetButton.setPreset(presets.get((page * pageSize) + i));
                    presetButton.setSelected(presetButton.getPreset().getId() == selectedPreset);
                }
                j++;
            }
        }
    }
}
