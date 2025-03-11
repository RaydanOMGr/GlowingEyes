package me.andreasmelone.glowingeyes.client.gui.preset;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.client.gui.button.PresetButton;
import me.andreasmelone.glowingeyes.client.presets.Preset;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PresetsScreen extends Screen {
    private int guiLeft, guiTop;

    protected int xSize = 256; // the size of the texture is 256x222
    protected int ySize = 222; // the size of the texture is 256x222

    int page = 0;

    ResourceLocation selectedPreset = null;
    int pageSize;

    boolean toggledState = true;
    boolean isLocked = false;
    boolean editing = false;

    Map<Point, Color> savedPixelMap = null;
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

        presetButtons = new ArrayList<>();
        pageSize = 5;

        int leftButtonX = (int) (this.guiLeft + 128 * ((double) 1 / 4));
        int rightButtonX = (int) (this.guiLeft + 128 * ((double) 3 / 4));

        this.addRenderableWidget(
            Button.builder(Component.literal("<"),
                    button -> {
                        switchPage(page - 1);
                    })
                .pos(leftButtonX, this.guiTop - 20 + ((pageSize + 1) * 30))
                .size(20, 20)
                .build()
        );

        this.addRenderableWidget(
            Button.builder(Component.literal(">"),
                    button -> {
                        switchPage(page + 1);
                    })
                .pos(rightButtonX, this.guiTop - 20 + ((pageSize + 1) * 30))
                .size(20, 20)
                .build()
        );

        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.done"),
                    button -> {
                        if (parent != null) {
                            GlowingEyesComponent.setToggledOn(Minecraft.getInstance().player, toggledState);
                            if (selectedPreset != null) {
                                presetManager.applyPreset(selectedPreset);
                            }
                            presetManager.savePresets();
                            Minecraft.getInstance().setScreen(parent);
                        }
                    })
                .pos(this.guiLeft + this.xSize - 90, this.guiTop + 120)
                .size(80, 20)
                .build()
        );

        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.glowingeyes.cancel"),
                    button -> {
                        if (parent != null) {
                            GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, savedPixelMap);
                            GlowingEyesComponent.setToggledOn(Minecraft.getInstance().player, toggledState);

                            presetManager.savePresets();
                            Minecraft.getInstance().setScreen(parent);
                        }
                    })
                .pos(this.guiLeft + this.xSize - 90, this.guiTop + 142)
                .size(80, 20)
                .build()
        );

        this.addRenderableWidget(
            createEditButton = Button.builder(Component.translatable("gui.glowingeyes.presets.create"),
                button -> {
                    if (editing) {
                        EditPresetScreen.askForName(this, presetManager.getPreset(selectedPreset).getName()).thenAccept((result) -> {
                            if (result != null) {
                                presetManager.getPreset(selectedPreset).setName(result);
                                for (PresetButton presetButton : this.presetButtons) {
                                    if (presetButton.getPreset().getId() == selectedPreset) {
                                        presetButton.setPreset(presetManager.getPreset(selectedPreset));
                                    }
                                }
                                this.unselectPreset();
                            }
                        });
                    } else Minecraft.getInstance().setScreen(new CreatePresetScreen(this));
                })
            .pos(this.guiLeft + 10, this.guiTop + 5 + ((pageSize + 1) * 30))
            .size(128 / 2 - 3, 20)
            .build()
        );

        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.glowingeyes.presets.delete"),
                button -> {
                    if (selectedPreset != null) {
                        ConfirmDeletionScreen.askToDelete(this, presetManager.getPreset(selectedPreset).getName()).thenAccept((result) -> {
                            if (result) {
                                presetManager.removePreset(selectedPreset);
                                if (presetManager.hasPage(page, pageSize)) {
                                    updatePage();
                                } else {
                                    switchPage(page - 1);
                                }
                            }
                            this.unselectPreset();
                        });
                    }
                })
            .pos(this.guiLeft + 10 + (128 / 2) + (3 * 2), this.guiTop + 5 + ((pageSize + 1) * 30))
            .size(128 / 2 - 3, 20)
            .build()
        );

        this.addRenderableWidget(
            Button.builder(Component.translatable("gui.glowingeyes.presets.lock"),
                button -> {
                    isLocked = !isLocked;
                    button.setMessage(isLocked ? Component.translatable("gui.glowingeyes.presets.unlock") : Component.translatable("gui.glowingeyes.presets.lock"));
                })
            .pos(this.guiLeft + this.xSize - 90, this.guiTop + 164)
            .size(80, 20)
            .build()
        );


        if(savedPixelMap == null) {
            savedPixelMap = GlowingEyesComponent.getGlowingEyesMap(Minecraft.getInstance().player);
            toggledState = GlowingEyesComponent.isToggledOn(Minecraft.getInstance().player);
        }

        List<Preset> presets = presetManager.getPresets();
        for (int i = 0; i < pageSize; i++) {
            Preset preset = getOrDefault(presets, i + (page * pageSize), null);
            presetButtons.add(new PresetButton(
                    this.guiLeft + 10, this.guiTop + 10 + (i * 30),
                    preset, button -> {
                if (button.getPreset().getId() == selectedPreset) selectedPreset = null;
                else selectedPreset = button.getPreset().getId();

                this.setEditing(selectedPreset != null);

                for (PresetButton b : this.presetButtons) {
                    if(b.getPreset() == null) continue;
                    b.setSelected(b.getPreset().getId() == selectedPreset);
                    if (b.getPreset().getId() == selectedPreset) {
                        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, b.getPreset().getContent());
                    }
                }
            }
            ));
            presetButtons.forEach(this::addRenderableWidget);
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        if(this.parent != null) {
            parent.render(poseStack, 0, 0, partialTicks);
        }
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

        final int x = this.guiLeft + this.xSize - 90 + (scale / 2);
        final int y = this.guiTop + 110 - (scale * 2);

        final int middleX = x + (finalWidth / 2);

        //     public void blit(
        //     ResourceLocation atlasLocation,
        //     int x,
        //     int y,
        //     int width,
        //     int height,
        //     float uOffset,
        //     float vOffset,
        //     int uWidth,
        //     int vHeight,
        //     int textureWidth,
        //     int textureHeight)
        RenderSystem.setShaderTexture(0, TextureLocations.UI_PLAYERBOX);
        blit(
                poseStack,
                this.guiLeft + this.xSize - 90 + (scale / 2), this.guiTop + 110 - (scale * 2),
                finalWidth, finalHeight,
                0, 0,
                sourceWidth, sourceHeight,
                64, 64
        );

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                poseStack,
                middleX, y + finalHeight - 5,
                scale,
                (float) (isLocked ? 0 : (double) (this.guiLeft + this.xSize - 90 + 40) - mouseX),
                (float) (isLocked ? 0 : (double) (this.guiTop + 110 - 20) - mouseY),
                Minecraft.getInstance().player
        );

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (parent != null) {
                this.onClose();
                Minecraft.getInstance().setScreen(parent);
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
        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, savedPixelMap);
        GlowingEyesComponent.setToggledOn(Minecraft.getInstance().player, toggledState);

        presetManager.savePresets();
    }

    private void unselectPreset() {
        selectedPreset = null;
        for (PresetButton presetButton : this.presetButtons) {
            presetButton.setSelected(false);
        }
        this.setEditing(false);

        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, savedPixelMap);
        GlowingEyesComponent.setToggledOn(Minecraft.getInstance().player, toggledState);
    }

    private void setEditing(boolean editing) {
        this.editing = editing;
        if (editing) {
            createEditButton.setMessage(Component.translatable("gui.glowingeyes.presets.edit"));
        } else {
            createEditButton.setMessage(Component.translatable("gui.glowingeyes.presets.create"));
        }
    }

    private void switchPage(int newPage) {
        if (presetManager.hasPage(newPage, pageSize)) {
            page = newPage;
            updatePage();
        }
    }

    private void updatePage() {
        List<Preset> presets = presetManager.getPresets();
        boolean hasSelectedPreset = false;
        for (int i = 0; i < pageSize; i++) {
            if ((page * pageSize) + i >= presets.size()) {
                presetButtons.get(i).visible = false;
            } else {
                presetButtons.get(i).visible = true;
                PresetButton presetButton = presetButtons.get(i);
                presetButton.setPreset(presets.get((page * pageSize) + i));
                if (presetButton.getPreset().getId() == selectedPreset) {
                    presetButton.setSelected(true);
                    hasSelectedPreset = true;
                }
            }
        }
        if (!hasSelectedPreset) {
            unselectPreset();
        }
    }

    private static <T> T getOrDefault(List<T> list, int index, T defaulT) {
        if(list.size() > index && index >= 0) {
            return list.get(index);
        }
        return defaulT;
    }
}
