package me.andreasmelone.glowingeyes.client.gui.preset;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.gui.button.BigSelectableButton;
import me.andreasmelone.glowingeyes.client.gui.button.PresetButton;
import me.andreasmelone.glowingeyes.client.presets.Preset;
import me.andreasmelone.glowingeyes.client.presets.PresetManager;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PresetsScreen extends Screen {
    private static final int TEXTURE_WIDTH = TextureLocations.UI_BACKGROUND_BROAD_WIDTH; // the size of the texture is 256x222
    private static final int TEXTURE_HEIGHT = TextureLocations.UI_BACKGROUND_BROAD_HEIGHT; // the size of the texture is 256x222

    private static final int PRESETS_OFFSET_X = 15;
    private static final int PRESETS_OFFSET_Y = 18;
    private static final int PRESETBOX_OFFSET_X = -7;
    private static final int PRESETBOX_OFFSET_Y = -6;

    public static final int PAGE_SIZE = 5;

    private static final int PRESET_BUTTON_SPACING = 1;
    private static final int PRESET_BUTTON_TOTAL_HEIGHT = BigSelectableButton.WIDTH + PRESET_BUTTON_SPACING;
    private static final int PRESET_BUTTONS_LIST_TOTAL_HEIGHT = PRESET_BUTTON_TOTAL_HEIGHT * PAGE_SIZE;

    private static final int THUMB_WIDTH = 5;

    private static final int NORMAL_BUTTON_WIDTH = 80;
    private static final int NORMAL_BUTTON_HEIGHT = 20;

    private static final int BUTTONS_X = TEXTURE_WIDTH - 90;
    private static final int BUTTONS_Y = 120;
    private static final int BUTTONS_SPACING = 2;
    private static final int NORMAL_BUTTON_TOTAL_HEIGHT = NORMAL_BUTTON_HEIGHT + BUTTONS_SPACING;

    private static final int EDIT_BUTTON_Y = 5;

    private static final int PLAYERBOX_X = 110;
    private static final float PLAYERBOX_ENTITY_OFFSET = 0.0625f;
    public static final int PLAYERBOX_SOURCE_WIDTH = 34;
    public static final int PLAYERBOX_SOURCE_HEIGHT = 45;
    public static final int PLAYERBOX_FINAL_WIDTH = 50;
    public static final int PLAYERBOX_FINAL_HEIGHT = (int) ((float) PLAYERBOX_FINAL_WIDTH / PLAYERBOX_SOURCE_WIDTH * PLAYERBOX_SOURCE_HEIGHT);
    public static final int PLAYERBOX_SCALE = 30;

    private int guiLeft, guiTop;

    private int page = 0;

    private int selectedPreset = -1;

    private boolean toggledState = true;
    private boolean isLocked = false;
    private boolean editing = false;
    private boolean isDraggingThumb = false;

    private Map<Point, Color> savedPixelMap = null;
    private List<PresetButton> presetButtons;
    private Button createEditButton;
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
        super.init();
        if(parent != null) parent.init(minecraft, minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
        this.guiLeft = (this.width - TEXTURE_WIDTH) / 2;
        this.guiTop = (this.height - TEXTURE_HEIGHT) / 2;

        presetButtons = new ArrayList<>();

        int buttons = 0;
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.done"),
                                button -> {
                                    if (parent != null) {
                                        GlowingEyesComponent.setToggledOn(Minecraft.getInstance().player, toggledState);
                                        if (selectedPreset != -1) {
                                            presetManager.applyPreset(selectedPreset);
                                        }
                                        presetManager.savePresets();
                                        Minecraft.getInstance().setScreen(parent);
                                    }
                                })
                        .pos(this.guiLeft + BUTTONS_X, this.guiTop + BUTTONS_Y)
                        .size(NORMAL_BUTTON_WIDTH, NORMAL_BUTTON_HEIGHT)
                        .build()
        );
        buttons++;

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
                        .pos(this.guiLeft + BUTTONS_X, this.guiTop + BUTTONS_Y + (buttons * NORMAL_BUTTON_TOTAL_HEIGHT))
                        .size(NORMAL_BUTTON_WIDTH, NORMAL_BUTTON_HEIGHT)
                        .build()
        );
        buttons++;

        int editButtonDistance = 2;
        this.addRenderableWidget(
                createEditButton = Button.builder(Component.translatable("gui.glowingeyes.presets.create"),
                                button -> {
                                    if (editing) {
                                        EditPresetScreen.askForName(this, presetManager.getPreset(selectedPreset).getName()).thenAccept((result) -> {
                                            if (result != null) {
                                                presetManager.getPreset(selectedPreset).setName(result);
                                                for (PresetButton presetButton : this.presetButtons) {
                                                    if (PresetManager.getInstance().getId(presetButton.getPreset()) == selectedPreset) {
                                                        presetButton.setPreset(presetManager.getPreset(selectedPreset));
                                                    }
                                                }
                                                this.unselectPreset();
                                            }
                                        });
                                    } else Minecraft.getInstance().setScreen(new CreatePresetScreen(this));
                                })
                        .pos(this.guiLeft + PRESETS_OFFSET_X, this.guiTop + EDIT_BUTTON_Y + ((PAGE_SIZE + 1) * PRESET_BUTTON_TOTAL_HEIGHT))
                        .size(BigSelectableButton.WIDTH / 2 - (editButtonDistance * 2), NORMAL_BUTTON_HEIGHT)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.glowingeyes.presets.delete"),
                                button -> {
                                    if (selectedPreset != -1) {
                                        ConfirmDeletionScreen.askToDelete(this, presetManager.getPreset(selectedPreset).getName()).thenAccept((result) -> {
                                            if (result) {
                                                presetManager.removePreset(selectedPreset);
                                                movePresets(-1);
                                            }
                                            this.unselectPreset();
                                        });
                                    }
                                })
                        .pos(this.guiLeft + PRESETS_OFFSET_X + (BigSelectableButton.WIDTH / 2) + (editButtonDistance * 2), this.guiTop + EDIT_BUTTON_Y + ((PAGE_SIZE + 1) * PRESET_BUTTON_TOTAL_HEIGHT))
                        .size(BigSelectableButton.WIDTH / 2 - (editButtonDistance * 2), NORMAL_BUTTON_TOTAL_HEIGHT)
                        .build()
        );

        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.glowingeyes.presets.lock"),
                                button -> {
                                    isLocked = !isLocked;
                                    button.setMessage(isLocked ? Component.translatable("gui.glowingeyes.presets.unlock") : Component.translatable("gui.glowingeyes.presets.lock"));
                                })
                        .pos(this.guiLeft + TEXTURE_WIDTH, this.guiTop + BUTTONS_Y + (buttons * NORMAL_BUTTON_TOTAL_HEIGHT))
                        .size(NORMAL_BUTTON_WIDTH, NORMAL_BUTTON_HEIGHT)
                        .build()
        );
        buttons++;


        if (savedPixelMap == null) {
            savedPixelMap = GlowingEyesComponent.getGlowingEyesMap(Minecraft.getInstance().player);
            toggledState = GlowingEyesComponent.isToggledOn(Minecraft.getInstance().player);
        }

        List<Preset> presets = presetManager.getPresets();
        for (int i = 0; i < PAGE_SIZE; i++) {
            Preset preset = getOrDefault(presets, i + (page * PAGE_SIZE), null);
            presetButtons.add(new PresetButton(
                    this.guiLeft + PRESETS_OFFSET_X, this.guiTop + PRESETS_OFFSET_Y + (i * PRESET_BUTTON_TOTAL_HEIGHT),
                    preset, button -> {
                if (PresetManager.getInstance().getId(button.getPreset()) == selectedPreset) selectedPreset = -1;
                else selectedPreset = PresetManager.getInstance().getId(button.getPreset());

                this.setEditing(selectedPreset != -1);

                boolean hasSelected = false;
                for (PresetButton b : this.presetButtons) {
                    if(b.getPreset() == null) continue;
                    b.setSelected(PresetManager.getInstance().getId(b.getPreset()) == selectedPreset);
                    if (!hasSelected) hasSelected = PresetManager.getInstance().getId(b.getPreset()) == selectedPreset;

                    if (PresetManager.getInstance().getId(b.getPreset()) == selectedPreset) {
                        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, b.getPreset().getContent());
                    }
                }

                if (!hasSelected) {
                    GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, savedPixelMap);
                }
            }
            ));
            presetButtons.forEach(this::addRenderableWidget);
        }
    }

    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        if(parent != null) {
            ctx.pose().pushPose();
            ctx.pose().translate(0, 0, -100);
            parent.render(ctx, 0, 0, partialTicks);
            ctx.pose().popPose();
        }
        super.renderBackground(ctx, mouseX, mouseY, partialTicks);
        GuiUtil.drawBackground(
                ctx, TextureLocations.UI_BACKGROUND_BROAD,
                this.guiLeft, this.guiTop,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        final int playerBoxX = this.guiLeft + BUTTONS_X + (PLAYERBOX_SCALE / 2);
        final int playerBoxY = this.guiTop + PLAYERBOX_X - (PLAYERBOX_SCALE * 2);

        final int middleX = playerBoxX + (PLAYERBOX_FINAL_WIDTH / 2);
        final int middleY = playerBoxY + (PLAYERBOX_FINAL_HEIGHT / 2);

        ctx.blit(
                RenderType::guiTextured,
                TextureLocations.UI_PLAYERBOX,
                playerBoxX, playerBoxY,
                0, 0,
                PLAYERBOX_FINAL_WIDTH, PLAYERBOX_FINAL_HEIGHT,
                PLAYERBOX_SOURCE_WIDTH, PLAYERBOX_SOURCE_HEIGHT,
                64, 64
        );

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                ctx,
                playerBoxX,                            // x1
                playerBoxY,                            // y1
                playerBoxX + PLAYERBOX_FINAL_WIDTH,               // x2
                playerBoxY + PLAYERBOX_FINAL_HEIGHT,              // y2
                PLAYERBOX_SCALE,                                 // scale
                PLAYERBOX_ENTITY_OFFSET,               // offset
                isLocked ? middleX : (float) mouseX,   // mouseX
                isLocked ? middleY : (float) mouseY,   // mouseY
                Minecraft.getInstance().player         // entity
        );

        ctx.blit(
                RenderType::guiTextured,
                TextureLocations.UI_PRESETBOX,
                this.guiLeft + PRESETS_OFFSET_X + PRESETBOX_OFFSET_X, this.guiTop + PRESETS_OFFSET_Y + PRESETBOX_OFFSET_Y,
                0, 0,
                TextureLocations.UI_PRESETBOX_WIDTH, TextureLocations.UI_PRESETBOX_HEIGHT,
                256, 256
        );

        if(presetManager.getPresets().size() > 5) {
            int heightThumb = calculateThumbHeight();
            float yThumb = this.guiTop + PRESETS_OFFSET_Y + (PRESET_BUTTONS_LIST_TOTAL_HEIGHT * (float) page / presetManager.getPresets().size());
            ctx.fill(
                    this.guiLeft + PRESETS_OFFSET_X + BigSelectableButton.WIDTH + 1,
                    (int) yThumb,
                    this.guiLeft + PRESETS_OFFSET_X + BigSelectableButton.WIDTH + 1 + THUMB_WIDTH,
                    (int) (yThumb + heightThumb),
                    0xFFFFFFFF
            );
        }

        if(GlowingEyes.DEBUG) {
            GuiUtil.drawCursorPos(ctx, font, mouseX, mouseY);
        }

        super.render(ctx, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int heightThumb = calculateThumbHeight();
        float yThumb = this.guiTop + PRESETS_OFFSET_Y + (PRESET_BUTTONS_LIST_TOTAL_HEIGHT * (float) page / presetManager.getPresets().size());
        float xThumb = this.guiLeft + PRESETS_OFFSET_X + BigSelectableButton.WIDTH + 1;
        if(mouseX >= xThumb && mouseX <= xThumb + THUMB_WIDTH && mouseY >= yThumb && mouseY <= yThumb + heightThumb) {
            isDraggingThumb = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDraggingThumb = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(isDraggingThumb) {
            int presets = presetManager.getPresets().size();
            int heightThumb = calculateThumbHeight();

            double scaledMouse = (mouseY - this.guiTop - PRESETS_OFFSET_Y - ((double) heightThumb / 2));
            movePresets(Mth.floor((scaledMouse / PRESET_BUTTONS_LIST_TOTAL_HEIGHT) * presets) - page);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int xPresets = this.guiLeft + PRESETS_OFFSET_X + PRESETBOX_OFFSET_X;
        int yPresets = this.guiTop + PRESETS_OFFSET_Y + PRESETBOX_OFFSET_Y;

        if(mouseX >= xPresets && mouseX <= xPresets + TextureLocations.UI_PRESETBOX_WIDTH && mouseY >= yPresets && mouseY <= yPresets + TextureLocations.UI_PRESETBOX_HEIGHT) {
            int scroll = (int) -scrollY;
            movePresets(scroll);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
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

        if (parent != null) {
            Minecraft.getInstance().setScreen(parent);
        }
    }

    @Override
    public void renderBackground(GuiGraphics ctx, int mouseX, int mouseY, float partialTick) {
    }

    private int calculateThumbHeight() {
        return Math.max((int) (150 * ((float) PAGE_SIZE / presetManager.getPresets().size())), 7); // min size of 7
    }

    private void unselectPreset() {
        selectedPreset = -1;
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

    private void movePresets(int amount) {
        page = Mth.clamp(amount + page, 0, presetManager.getPresets().size() - PAGE_SIZE);
        List<Preset> presets = presetManager.getPresets();
        for (int i = 0; i < PAGE_SIZE; i++) {
            if ((page) + i >= presets.size()) {
                presetButtons.get(i).visible = false;
            } else {
                presetButtons.get(i).visible = true;
                PresetButton presetButton = presetButtons.get(i);
                Preset preset = presets.get(page + i);
                presetButton.setPreset(preset);
                presetButton.setSelected(PresetManager.getInstance().getId(preset) == selectedPreset);
            }
        }
    }

    private static <T> T getOrDefault(List<T> list, int index, T defaulT) {
        if (list.size() > index && index >= 0) {
            return list.get(index);
        }
        return defaulT;
    }
}
