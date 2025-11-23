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
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PresetsScreen extends Screen {
    public static final int EDIT_BUTTON_SPACING = 2;
    private static final int UI_WIDTH = TextureLocations.UI_BACKGROUND_BROAD_WIDTH; // the size of the texture is 256x222
    private static final int UI_HEIGHT = TextureLocations.UI_BACKGROUND_BROAD_HEIGHT; // the size of the texture is 256x222

    private static final int PRESETS_OFFSET_X = 15;
    private static final int PRESETS_OFFSET_Y = 18;
    private static final int PRESETBOX_OFFSET_X = -7;
    private static final int PRESETBOX_OFFSET_Y = -6;

    public static final int PAGE_SIZE = 5;

    private static final int PRESET_BUTTON_SPACING = 1;
    private static final int PRESET_BUTTON_TOTAL_HEIGHT = BigSelectableButton.HEIGHT + PRESET_BUTTON_SPACING;
    private static final int PRESET_BUTTONS_LIST_TOTAL_HEIGHT = PRESET_BUTTON_TOTAL_HEIGHT * PAGE_SIZE;

    private static final int THUMB_WIDTH = 5;
    public static final int THUMB_MIN_HEIGHT = 7;
    public static final int THUMB_X_OFFSET = 2;

    private static final int NORMAL_BUTTON_WIDTH = 80;
    private static final int NORMAL_BUTTON_HEIGHT = 20;

    private static final int BUTTONS_X = UI_WIDTH - 90;
    private static final int BUTTONS_Y = 120;
    private static final int BUTTONS_SPACING = 2;
    private static final int NORMAL_BUTTON_TOTAL_HEIGHT = NORMAL_BUTTON_HEIGHT + BUTTONS_SPACING;

    private static final int EDIT_BUTTON_Y = 5;

    private static final int PLAYERBOX_X = 110;
    private static final float PLAYERBOX_ENTITY_OFFSET = 0.0625f;

    public static final int PLAYERBOX_WIDTH = TextureLocations.UI_PLAYERBOX_WIDTH;
    public static final int PLAYERBOX_HEIGHT = TextureLocations.UI_PLAYERBOX_HEIGHT;

    public static final int PLAYERBOX_FINAL_WIDTH = 50;
    public static final int PLAYERBOX_FINAL_HEIGHT = (int) ((float) PLAYERBOX_FINAL_WIDTH / PLAYERBOX_WIDTH * PLAYERBOX_HEIGHT);
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
    private Button createEditButton, deleteButton;
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
        if(this.parent != null) {
            this.parent.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
            this.parent.clearFocus();
        }
        this.guiLeft = (this.width - UI_WIDTH) / 2;
        this.guiTop = (this.height - UI_HEIGHT) / 2;

        this.presetButtons = new ArrayList<>();

        int buttons = 0;
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.done"),
                                button -> {
                                    if (this.parent != null) {
                                        GlowingEyesComponent.setToggledOn(Minecraft.getInstance().player, this.toggledState);
                                        if (this.selectedPreset != -1) {
                                            this.presetManager.applyPreset(this.selectedPreset);
                                        }
                                        this.presetManager.savePresets();
                                        Minecraft.getInstance().setScreen(this.parent);
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
                                    if (this.parent != null) {
                                        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, this.savedPixelMap);
                                        GlowingEyesComponent.setToggledOn(Minecraft.getInstance().player, this.toggledState);

                                        this.presetManager.savePresets();
                                        Minecraft.getInstance().setScreen(this.parent);
                                    }
                                })
                        .pos(this.guiLeft + BUTTONS_X, this.guiTop + BUTTONS_Y + (buttons * NORMAL_BUTTON_TOTAL_HEIGHT))
                        .size(NORMAL_BUTTON_WIDTH, NORMAL_BUTTON_HEIGHT)
                        .build()
        );
        buttons++;

        this.addRenderableWidget(
                this.createEditButton = Button.builder(Component.translatable("gui.glowingeyes.presets.create"),
                                button -> {
                                    if (this.editing) {
                                        EditPresetScreen.askForName(this, this.presetManager.getPreset(this.selectedPreset).getName()).thenAccept((result) -> {
                                            if (result != null) {
                                                this.presetManager.getPreset(this.selectedPreset).setName(result);
                                                for (PresetButton presetButton : this.presetButtons) {
                                                    if (PresetManager.getInstance().getId(presetButton.getPreset()) == this.selectedPreset) {
                                                        presetButton.setPreset(this.presetManager.getPreset(this.selectedPreset));
                                                    }
                                                }
                                                this.unselectPreset();
                                            }
                                        });
                                    } else Minecraft.getInstance().setScreen(new CreatePresetScreen(this));
                                })
                        .pos(this.guiLeft + PRESETS_OFFSET_X, this.guiTop + EDIT_BUTTON_Y + ((PAGE_SIZE + 1) * PRESET_BUTTON_TOTAL_HEIGHT))
                        .size(BigSelectableButton.WIDTH / 2 - (EDIT_BUTTON_SPACING * 2), NORMAL_BUTTON_HEIGHT)
                        .build()
        );

        this.addRenderableWidget(
                this.deleteButton = Button.builder(Component.translatable("gui.glowingeyes.presets.delete"),
                                button -> {
                                    if (this.selectedPreset != -1) {
                                        ConfirmDeletionScreen.askToDelete(this, this.presetManager.getPreset(this.selectedPreset).getName()).thenAccept((result) -> {
                                            if (result) {
                                                this.presetManager.removePreset(this.selectedPreset);
                                                this.movePresets(-2);
                                                this.unselectPreset();
                                            }
                                        });
                                    }
                                })
                        .pos(this.guiLeft + PRESETS_OFFSET_X + (BigSelectableButton.WIDTH / 2) + (EDIT_BUTTON_SPACING * 2), this.guiTop + EDIT_BUTTON_Y + ((PAGE_SIZE + 1) * PRESET_BUTTON_TOTAL_HEIGHT))
                        .size(BigSelectableButton.WIDTH / 2 - (EDIT_BUTTON_SPACING * 2), NORMAL_BUTTON_HEIGHT)
                        .build()
        );
        this.deleteButton.active = false;

        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.glowingeyes.presets.lock"),
                                button -> {
                                    this.isLocked = !this.isLocked;
                                    button.setMessage(this.isLocked ? Component.translatable("gui.glowingeyes.presets.unlock") : Component.translatable("gui.glowingeyes.presets.lock"));
                                })
                        .pos(this.guiLeft + BUTTONS_X, this.guiTop + BUTTONS_Y + (buttons * NORMAL_BUTTON_TOTAL_HEIGHT))
                        .size(NORMAL_BUTTON_WIDTH, NORMAL_BUTTON_HEIGHT)
                        .build()
        );
        buttons++;


        if (this.savedPixelMap == null) {
            this.savedPixelMap = GlowingEyesComponent.getGlowingEyesMap(Minecraft.getInstance().player);
            this.toggledState = GlowingEyesComponent.isToggledOn(Minecraft.getInstance().player);
        }

        List<Preset> presets = this.presetManager.getPresets();
        for (int i = 0; i < PAGE_SIZE; i++) {
            Preset preset = getOrDefault(presets, i + this.page, null);
            PresetButton presetButton;
            this.presetButtons.add(presetButton = new PresetButton(
                    this.guiLeft + PRESETS_OFFSET_X, this.guiTop + PRESETS_OFFSET_Y + (i * PRESET_BUTTON_TOTAL_HEIGHT),
                    preset, button -> {
                if (PresetManager.getInstance().getId(button.getPreset()) == this.selectedPreset) this.unselectPreset();
                else this.selectPreset(PresetManager.getInstance().getId(button.getPreset()));

                boolean hasSelected = false;
                for (PresetButton b : this.presetButtons) {
                    if(b.getPreset() == null) continue;
                    b.setSelected(PresetManager.getInstance().getId(b.getPreset()) == this.selectedPreset);
                    if (!hasSelected) hasSelected = PresetManager.getInstance().getId(b.getPreset()) == this.selectedPreset;

                    if (PresetManager.getInstance().getId(b.getPreset()) == this.selectedPreset) {
                        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, b.getPreset().getContent());
                    }
                }

                if (!hasSelected) {
                    GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, this.savedPixelMap);
                }
            }
            ));
            presetButton.setSelected(this.presetManager.getId(preset) == this.selectedPreset);
            this.addRenderableWidget(presetButton);
        }
        this.selectPreset(this.selectedPreset);
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        if(this.parent != null) {
            ctx.pose().pushPose();
            ctx.pose().translate(0, 0, -100);
            this.parent.render(ctx, 0, 0, partialTicks);
            ctx.pose().popPose();
            GuiUtil.drawTransparentBlack(ctx);
        } else super.renderBackground(ctx, mouseX, mouseY, partialTicks);
        GuiUtil.drawBackground(
                ctx, TextureLocations.UI_BACKGROUND_BROAD,
                this.guiLeft, this.guiTop,
                UI_WIDTH, UI_HEIGHT
        );

        final int playerBoxX = this.guiLeft + BUTTONS_X + (PLAYERBOX_SCALE / 2);
        final int playerBoxY = this.guiTop + PLAYERBOX_X - (PLAYERBOX_SCALE * 2);

        final int middleX = playerBoxX + (PLAYERBOX_FINAL_WIDTH / 2);
        final int middleY = playerBoxY + (PLAYERBOX_FINAL_HEIGHT / 2);

        ctx.blit(
                TextureLocations.UI_PLAYERBOX,
                playerBoxX, playerBoxY,
                PLAYERBOX_FINAL_WIDTH, PLAYERBOX_FINAL_HEIGHT,
                0, 0,
                PLAYERBOX_WIDTH, PLAYERBOX_HEIGHT,
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
                this.isLocked ? middleX : (float) mouseX,   // mouseX
                this.isLocked ? middleY : (float) mouseY,   // mouseY
                Minecraft.getInstance().player         // entity
        );

        ctx.blit(
                TextureLocations.UI_PRESETBOX,
                this.guiLeft + PRESETS_OFFSET_X + PRESETBOX_OFFSET_X, this.guiTop + PRESETS_OFFSET_Y + PRESETBOX_OFFSET_Y,
                TextureLocations.UI_PRESETBOX_WIDTH, TextureLocations.UI_PRESETBOX_HEIGHT,
                0, 0,
                TextureLocations.UI_PRESETBOX_WIDTH, TextureLocations.UI_PRESETBOX_HEIGHT,
                256, 256
        );

        if(this.presetManager.getPresets().size() > 5) {
            int heightThumb = this.calculateThumbHeight();
            float yThumb = this.guiTop + PRESETS_OFFSET_Y + (PRESET_BUTTONS_LIST_TOTAL_HEIGHT * (float) this.page / this.presetManager.getPresets().size());
            ctx.fill(
                    this.guiLeft + PRESETS_OFFSET_X + BigSelectableButton.WIDTH + THUMB_X_OFFSET,
                    (int) yThumb,
                    this.guiLeft + PRESETS_OFFSET_X + BigSelectableButton.WIDTH + THUMB_X_OFFSET + THUMB_WIDTH,
                    (int) (yThumb + heightThumb),
                    Color.WHITE.getRGB()
            );
        }

        if(GlowingEyes.DEBUG) {
            GuiUtil.drawCursorPos(ctx, this.font, mouseX, mouseY);
        }

        super.render(ctx, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int heightThumb = this.calculateThumbHeight();
        float yThumb = this.guiTop + PRESETS_OFFSET_Y + (PRESET_BUTTONS_LIST_TOTAL_HEIGHT * (float) this.page / this.presetManager.getPresets().size());
        float xThumb = this.guiLeft + PRESETS_OFFSET_X + BigSelectableButton.WIDTH + THUMB_X_OFFSET;
        if(mouseX >= xThumb && mouseX <= xThumb + THUMB_WIDTH && mouseY >= yThumb && mouseY <= yThumb + heightThumb) {
            this.isDraggingThumb = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.isDraggingThumb = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(this.isDraggingThumb) {
            int presets = this.presetManager.getPresets().size();
            int heightThumb = this.calculateThumbHeight();

            double scaledMouse = (mouseY - this.guiTop - PRESETS_OFFSET_Y - ((double) heightThumb / 2));
            this.movePresets(Mth.floor((scaledMouse / PRESET_BUTTONS_LIST_TOTAL_HEIGHT) * presets) - this.page);
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        int xPresets = this.guiLeft + PRESETS_OFFSET_X + PRESETBOX_OFFSET_X;
        int yPresets = this.guiTop + PRESETS_OFFSET_Y + PRESETBOX_OFFSET_Y;

        if(mouseX >= xPresets && mouseX <= xPresets + TextureLocations.UI_PRESETBOX_WIDTH && mouseY >= yPresets && mouseY <= yPresets + TextureLocations.UI_PRESETBOX_HEIGHT) {
            int scroll = (int) -scrollY;
            this.movePresets(scroll);
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, this.savedPixelMap);
        GlowingEyesComponent.setToggledOn(Minecraft.getInstance().player, this.toggledState);

        this.presetManager.savePresets();

        if (this.parent != null) {
            Minecraft.getInstance().setScreen(this.parent);
        }
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTick) {
    }

    private int calculateThumbHeight() {
        return Math.max((int) (PRESET_BUTTONS_LIST_TOTAL_HEIGHT * ((float) PAGE_SIZE / this.presetManager.getPresets().size())), THUMB_MIN_HEIGHT); // min size of 7
    }

    private void selectPreset(int preset) {
        if(preset == -1) {
            this.unselectPreset();
            return;
        }
        this.selectedPreset = preset;
        this.deleteButton.active = true;
        this.setEditing(true);
    }

    private void unselectPreset() {
        this.selectedPreset = -1;
        for (PresetButton presetButton : this.presetButtons) {
            presetButton.setSelected(false);
        }
        this.setEditing(false);
        this.deleteButton.active = false;

        GlowingEyesComponent.setGlowingEyesMap(Minecraft.getInstance().player, this.savedPixelMap);
        GlowingEyesComponent.setToggledOn(Minecraft.getInstance().player, this.toggledState);
    }

    private void setEditing(boolean editing) {
        this.editing = editing;
        if (editing) {
            this.createEditButton.setMessage(Component.translatable("gui.glowingeyes.presets.edit"));
        } else {
            this.createEditButton.setMessage(Component.translatable("gui.glowingeyes.presets.create"));
        }
    }

    private void movePresets(int amount) {
        this.page = Mth.clamp(amount + this.page, 0, this.presetManager.getPresets().size() - PAGE_SIZE);
        List<Preset> presets = this.presetManager.getPresets();
        for (int i = 0; i < PAGE_SIZE; i++) {
            if ((this.page) + i >= presets.size()) {
                this.presetButtons.get(i).visible = false;
            } else {
                this.presetButtons.get(i).visible = true;
                PresetButton presetButton = this.presetButtons.get(i);
                Preset preset = presets.get(this.page + i);
                presetButton.setPreset(preset);
                presetButton.setSelected(PresetManager.getInstance().getId(preset) == this.selectedPreset);
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
