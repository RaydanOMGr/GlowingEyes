package me.andreasmelone.glowingeyes.client.gui.preset;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.client.gui.EyesEditorScreen;
import me.andreasmelone.glowingeyes.client.gui.button.PresetButton;
import me.andreasmelone.glowingeyes.client.presets.Preset;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesCapability;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class PresetsScreen extends Screen {
    private int guiLeft, guiTop;
    private int middleX, middleY;

    protected int xSize = 256; // the size of the texture is 256x222
    protected int ySize = 222; // the size of the texture is 256x222

    int page = 0;
    int offset = 0;

    int selectedPreset = -1;
    int pageSize;

    boolean toggledState = true;
    boolean isLocked = false;
    boolean editing = false;

    HashMap<Point, Color> savedPixelMap = new HashMap<>();
    List<PresetButton> presetButtons;
    Button createEditButton;
    private Screen parent;
    private final PresetManager presetManager = PresetManager.getInstance();

    public PresetsScreen() {
        super(Component.empty());
    }

    public PresetsScreen(Screen parent) {
        super(Component.empty());
        this.parent = parent;
    }

    @Override
    public void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        this.middleX = this.guiLeft + this.xSize / 2;
        this.middleY = this.guiTop + this.ySize / 2;

        presetButtons = new ArrayList<>();
        pageSize = 5;

        int leftButtonX = (int) (this.guiLeft + 128 * ((double)1 / 4));
        int rightButtonX = (int) (this.guiLeft + 128 * ((double)3 / 4));

        this.addRenderableWidget(new Button(
                leftButtonX, this.guiTop - 20 + ((pageSize + 1) * 30),
                20, 20,
                Component.literal("<"),
                button -> {
                    switchPage(page - 1);
                }
        ));
        this.addRenderableWidget(new Button(
                rightButtonX, this.guiTop - 20 + ((pageSize + 1) * 30),
                20, 20,
                Component.literal(">"),
                button -> {
                    switchPage(page + 1);
                }
        ));
        this.addRenderableWidget(new Button(
                this.guiLeft + this.xSize - 90, this.guiTop + 120,
                80, 20,
                Component.translatable("gui.done"),
                button -> {
                    if(parent != null) {
                        GlowingEyesCapability.setToggledOn(getMinecraft().player, toggledState);
                        if(selectedPreset != -1) {
                            presetManager.applyPreset(selectedPreset);
                        }
                        presetManager.savePresets();
                        if(parent instanceof EyesEditorScreen)
                            ((EyesEditorScreen)parent).openAsParent();
                        else
                            getMinecraft().setScreen(parent);
                    }
                }
        ));
        this.addRenderableWidget(new Button(
                this.guiLeft + this.xSize - 90, this.guiTop + 142,
                80, 20,
                Component.translatable("gui.cancel"),
                button -> {
                    if(parent != null) {
                        GlowingEyesCapability.setGlowingEyesMap(getMinecraft().player, savedPixelMap);
                        GlowingEyesCapability.setToggledOn(getMinecraft().player, toggledState);

                        presetManager.savePresets();
                        getMinecraft().setScreen(parent);
                    }
                }
        ));
        this.addRenderableWidget(createEditButton = new Button(
                this.guiLeft + 10, this.guiTop + 5 + ((pageSize + 1) * 30),
                128 / 2 - 3, 20,
                Component.translatable("gui.presets.create"),
                button -> {
                    if(editing) {
                        EditPresetScreen.askForName(this, presetManager.getPreset(selectedPreset).getName()).thenAccept((result) -> {
                            if(result != null) {
                                presetManager.getPreset(selectedPreset).setName(result);
                                for(PresetButton b : this.presetButtons) {
                                    if (b.getPreset().getId() == selectedPreset) {
                                        b.setPreset(presetManager.getPreset(selectedPreset));
                                    }
                                }
                                this.unselectPreset();
                            }
                        });
                    } else getMinecraft().setScreen(new CreatePresetScreen(this));
                }
        ));
        this.addRenderableWidget(new Button(
                this.guiLeft + 10 + (128 / 2) + (3 * 2), this.guiTop + 5 + ((pageSize + 1) * 30),
                128 / 2 - 3, 20,
                Component.translatable("gui.presets.delete"),
                button -> {
                    if(selectedPreset != -1) {
                        ConfirmDeletionScreen.askToDelete(this, presetManager.getPreset(selectedPreset).getName()).thenAccept((result) -> {
                            if (result) {
                                presetManager.removePreset(selectedPreset);
                                switchPage(1);
                            }
                            this.unselectPreset();
                        });
                    }
                }
        ));
        this.addRenderableWidget(new Button(
                this.guiLeft + this.xSize - 90, this.guiTop + 164,
                80, 20,
                Component.translatable("gui.presets.lock"),
                button -> {
                    isLocked = !isLocked;
                    button.setMessage(isLocked ? Component.translatable("gui.presets.unlock") : Component.translatable("gui.presets.lock"));
                }
        ));

        savedPixelMap = GlowingEyesCapability.getGlowingEyesMap(getMinecraft().player);
        toggledState = GlowingEyesCapability.isToggledOn(getMinecraft().player);

        List<Preset> presets = new ArrayList<>(presetManager.getPresets().values());
        int j;
        for(j = 0; j < pageSize; j++) {
            if(!presetManager.hasPreset(j + (page * pageSize))) {
                break;
            }
            Preset preset = presets.get(j + (page * pageSize));
            PresetButton current;
            presetButtons.add(current = new PresetButton(
                    this.guiLeft + 10, this.guiTop + 10 + (j * 30),
                    preset, button -> {
                        if (button.getPreset().getId() == selectedPreset) selectedPreset = -1;
                        else selectedPreset = button.getPreset().getId();

                        this.setEditing(selectedPreset != -1);

                        for (PresetButton b : this.presetButtons) {
                            b.setSelected(b.getPreset().getId() == selectedPreset);
                            if (b.getPreset().getId() == selectedPreset) {
                                GlowingEyesCapability.setGlowingEyesMap(getMinecraft().player, b.getPreset().getContent());
                            }
                        }
                    }
            ));
            presetButtons.forEach(this::addRenderableWidget);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        GuiUtil.drawBackground(
                poseStack, TextureLocations.UI_BACKGROUND_BROAD,
                this.guiLeft, this.guiTop,
                this.xSize, this.ySize
        );

        final int sourceWidth = 34;
        final int sourceHeight = 45;
        final int finalWidth = 50;
        final int finalHeight = (int) ((float) finalWidth / sourceWidth * sourceHeight);

        final int scale = 30;

        RenderSystem.setShaderTexture(0, TextureLocations.UI_PLAYERBOX);
        blit(
                poseStack,
                this.guiLeft + this.xSize - 90 + (scale / 2), this.guiTop + 110 - (scale * 2),
                0, 0,
                sourceWidth, sourceHeight,
                finalWidth, finalHeight,
                64, 64
        );

        // account for the preset menu and draw it below an already rendered texture
        GlStateManager._enableDepthTest();
        GlStateManager._depthFunc(GL11.GL_LEQUAL);

        InventoryScreen.renderEntityInInventory(
                this.guiLeft + this.xSize - 90 + 40,
                this.guiTop + 110,
                scale,
                (float) (isLocked ? 0 : (double) (this.guiLeft + this.xSize - 90 + 40) - mouseX),
                (float) (isLocked ? 0 : (double) (this.guiTop + 110 - 20) - mouseY),
                getMinecraft().player
        );

        GlStateManager._disableDepthTest();

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if(parent != null) {
                this.onClose();
                getMinecraft().setScreen(parent);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        GlowingEyesCapability.setGlowingEyesMap(getMinecraft().player, savedPixelMap);
        GlowingEyesCapability.setToggledOn(getMinecraft().player, toggledState);

        presetManager.savePresets();
    }

    private void unselectPreset() {
        selectedPreset = -1;
        for(PresetButton b : this.presetButtons) {
            b.setSelected(false);
        }
        this.setEditing(false);
    }

    private void setEditing(boolean editing) {
        this.editing = editing;
        if(editing) {
            createEditButton.setMessage(Component.translatable("gui.presets.edit"));
        } else {
            createEditButton.setMessage(Component.translatable("gui.presets.create"));
        }
    }

    private void switchPage(int newPage) {
        if(presetManager.hasPage(newPage, pageSize)) {
            List<Preset> presets = presetManager.getPresetList();
            page = newPage;
            int j = offset;
            for(int i = 0; i < pageSize; i++) {
                if((page * pageSize) + i >= presets.size()) {
                    presetButtons.get(i).visible = false;
                } else {
                    presetButtons.get(i).visible = true;
                    PresetButton presetButton = presetButtons.get(i);
                    presetButton.setPreset(presets.get((page * pageSize) + i));
                    presetButton.setSelected(presetButton.getPreset().getId() == selectedPreset);
                }
                j++;
            }
        }
    }
}
