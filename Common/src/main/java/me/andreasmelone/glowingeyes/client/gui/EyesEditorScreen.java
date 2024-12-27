package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.gui.preset.PresetsScreen;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.util.color.ColorType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.*;

import java.awt.*;
import java.util.Map;
import java.util.EnumMap;
import java.util.HashMap;

public class EyesEditorScreen extends Screen {
    private int guiLeft, guiTop;
    private int headX, headY;
    private int endHeadX, endHeadY;
    private boolean displaySecondLayer = false;

    Mode mode = Mode.BRUSH;
    Map<Point, Color> pixels = new HashMap<>();
    Map<Mode, Button> modeButtons = new EnumMap<>(Mode.class);
    Color headBackgroundColor = new Color(160, 160, 160, 255);

    private final int xSize = 256;
    private final int ySize = 222;
    private final ClientModContext mod;
    public EyesEditorScreen(ClientModContext mod) {
        super(Component.empty());
        this.mod = mod;
    }

    @Override
    protected void init() {
        super.init();
        GL.createCapabilities();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        Player player = Minecraft.getInstance().player;
        if(player != null) {
            pixels = GlowingEyesComponent.getGlowingEyesMap(player);
        } else {
            LogUtils.getLogger().error("Could not load glowing eyes map from player capability");
        }

        // the color picker button
        this.addRenderableWidget(new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 30,
                20, 20,
                0, 0, 20,
                TextureLocations.COLOR_PICKER_BUTTON,
                64, 64,
                button -> Minecraft.getInstance().setScreen(new ColorPickerScreen(mod, this)),
                (button, poseStack, mouseX, mouseY) -> {
                    this.renderTooltip(
                            poseStack,
                            Component.translatable("gui.editor.button.colorpicker.tooltip"),
                            mouseX, mouseY
                    );
                },
                CommonComponents.EMPTY
        ));

        // the preset menu button
        this.addRenderableWidget(new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 55,
                20, 20,
                0, 0, 20,
                TextureLocations.PRESET_MENU_BUTTON,
                64, 64,
                button -> Minecraft.getInstance().setScreen(new PresetsScreen(this)),
                (button, poseStack, mouseX, mouseY) -> {
                    this.renderTooltip(
                            poseStack,
                            Component.translatable("gui.editor.button.presetsmenu.tooltip"),
                            mouseX, mouseY
                    );
                },
                CommonComponents.EMPTY
        ));

        // the 2nd layer toggle button
        this.addRenderableWidget(new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 80,
                20, 20,
                0, 0, 20,
                TextureLocations.SECOND_LAYER_TOGGLE_BUTTON,
                64, 64,
                button -> {
                    displaySecondLayer = !displaySecondLayer;
                    button.active = displaySecondLayer;
                },
                (button, poseStack, mouseX, mouseY) -> {
                    this.renderTooltip(
                            poseStack,
                            Component.translatable("gui.editor.button.layertoggle.tooltip"),
                            mouseX, mouseY
                    );
                },
                CommonComponents.EMPTY
        ));

        this.modeButtons.clear();

        this.createModeButton(8, 70, Mode.BRUSH.getTexture(), Mode.BRUSH);
        this.createModeButton(8, 95, Mode.ERASER.getTexture(), Mode.ERASER);
        this.createModeButton(8, 120, Mode.PICKER.getTexture(), Mode.PICKER);

        this.modeButtons.get(Mode.BRUSH).onPress();
        this.modeButtons.forEach((mode, button) -> this.addRenderableWidget(button));
    }

    /**
     * The method that renders the screen
     * @param poseStack The PoseStack, a stack of transformations to apply to the rendering
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     * @param deltaTime The time since the last frame
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float deltaTime) {
        this.renderBackground(poseStack);

        GuiUtil.drawBackground(poseStack,
                TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        int spaceBetweenPixels = 2;
        int pixelSize = 16;
        final int headSize = 8;

        this.calculateHeadSize(headSize, pixelSize, spaceBetweenPixels);

        Gui.fill(
                poseStack,
                headX - spaceBetweenPixels, headY - spaceBetweenPixels,
                endHeadX + spaceBetweenPixels, endHeadY + spaceBetweenPixels,
                headBackgroundColor.getRGB()
        );

        RenderSystem.setShaderTexture(0, Minecraft.getInstance().player.getSkinTextureLocation());

        for (int y = 0; y < headSize; y++) {
            for (int x = 0; x < headSize; x++) {
                Point point = new Point(x, y);
                Gui.blit(
                        poseStack,
                        headX + x * pixelSize + x * spaceBetweenPixels,
                        headY + y * pixelSize + y * spaceBetweenPixels,
                        pixelSize,
                        pixelSize,
                        8f + x, 8f + y,
                        1, 1,
                        64, 64
                );

                if(pixels.containsKey(point)) {
                    pixels.get(point).getColorSpace();
                    Gui.fill(
                            poseStack,
                            headX + x * pixelSize + x * spaceBetweenPixels - 1,
                            headY + y * pixelSize + y * spaceBetweenPixels - 1,
                            headX + x * pixelSize + x * spaceBetweenPixels + pixelSize + 1,
                            headY + y * pixelSize + y * spaceBetweenPixels + pixelSize + 1,
                            pixels.get(point).getRGB()
                    );
                }

                if(displaySecondLayer) {
                    Gui.blit(
                            poseStack,
                            headX + x * pixelSize + x * spaceBetweenPixels,
                            headY + y * pixelSize + y * spaceBetweenPixels,
                            pixelSize, pixelSize,
                            40f + x, 8f + y,
                            1, 1,
                            64, 64
                    );
                }
            }
        }

        if(mode == Mode.PICKER && mouseX >= headX && mouseX <= endHeadX && mouseY >= headY && mouseY <= endHeadY) {
            Color color = this.getPixelColor(mouseX, mouseY);

            Gui.fill(poseStack, mouseX - 10, mouseY - 10 - 25, mouseX + 10, mouseY + 10 - 25, color.getRGB());
            Gui.drawCenteredString(poseStack, minecraft.font, ColorType.HEX.get(color),
                    mouseX, mouseY - 10, 0xFFFFFF);
        }

        super.render(poseStack, mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX >= headX && mouseX <= endHeadX && mouseY >= headY && mouseY <= endHeadY) {
            mode.onButtonPress(this, mouseX, mouseY, button);
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        return this.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        Player player = Minecraft.getInstance().player;
        if(player != null) {
            GlowingEyesComponent.setGlowingEyesMap(player, pixels);
            ClientGlowingEyesComponent.sendUpdate();
        } else {
            LogUtils.getLogger().error("Could not save glowing eyes map to player capability");
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private Color getPixelColor(double x, double y) {
        Window window = minecraft.getWindow();
        if (x < 0 || x > window.getWidth()) {
            throw new IllegalArgumentException("x must be within the screen width: 0 to " + window.getWidth() + ". Provided: " + x);
        }
        if (y < 0 || y > window.getHeight()) {
            throw new IllegalArgumentException("y must be within the screen height: 0 to " + window.getHeight() + ". Provided: " + y);
        }

        float[] pixel = new float[3];

        // Divides the actual width/height by the scaled width/height to find out by what factor it was scaled
        double scaleX = (double) window.getWidth() / window.getGuiScaledWidth();
        double scaleY = (double) window.getHeight() / window.getGuiScaledHeight();
        // Calculates the actual position of the pixel
        int pixelX = (int) (x * scaleX);
        int pixelY = (int) ((window.getGuiScaledHeight() - y) * scaleY); // The y value needs
                                                                         // to be inverted relative to the height
                                                                         // since minecraft's 0-point is top-left
                                                                         // while gl's 0-point is bottom-left
        GL11.glReadPixels(pixelX, pixelY, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, pixel);

        return new Color(pixel[0], pixel[1], pixel[2]);
    }


    private void calculateHeadSize(int headSize, int pixelSize, int spaceBetweenPixels) {
        int head = headSize * pixelSize + (headSize - 1) * spaceBetweenPixels;
        headX = this.guiLeft + (this.xSize - head) / 2;
        headY = this.guiTop + (this.ySize - head) / 2;
        endHeadX = headX + head;
        endHeadY = headY + head;
    }

    private ImageButton createModeButton(int x, int y, ResourceLocation texture, Mode buttonMode) {
        ImageButton imageButton = new ImageButton(
                this.guiLeft + x, this.guiTop + y,
                20, 20,
                0, 0, 20,
                texture,
                64, 64,
                button -> {
                    mode = buttonMode;
                    modeButtons.forEach((m, b) -> b.active = true);
                    button.active = false;
                },
                (button, poseStack, mouseX, mouseY) -> {
                    this.renderTooltip(
                            poseStack,
                            Component.translatable("gui.editor.mode." + buttonMode.name().toLowerCase() + ".tooltip"),
                            mouseX, mouseY
                    );
                },
                CommonComponents.EMPTY
        );
        modeButtons.put(buttonMode, imageButton);
        return imageButton;
    }

    public enum Mode {
        BRUSH(TextureLocations.BRUSH_BUTTON, (screen, mouseX, mouseY, button) -> {
            Point point = calculatePoint(screen, mouseX, mouseY);

            if (button == 0) {
                screen.pixels.put(new Point(point.x, point.y), screen.mod.getModVariables().getFinalColor());
            } else if (button == 1) {
                screen.pixels.remove(new Point(point.x, point.y));
            }
        }),
        ERASER(TextureLocations.ERASER_BUTTON, (screen, mouseX, mouseY, button) -> {
            Point point = calculatePoint(screen, mouseX, mouseY);
            screen.pixels.remove(new Point(point.x, point.y));
        }),
        PICKER(TextureLocations.PIPETTE_BUTTON, (screen, mouseX, mouseY, button) -> {
            Color color = screen.getPixelColor(mouseX, mouseY);
            screen.mod.getModVariables().setFinalColor(color);

            screen.modeButtons.get(Mode.BRUSH).onPress();
        });

        private final ResourceLocation texture;
        private final ButtonPressCallback onButtonPress;

        Mode(final ResourceLocation texture, final ButtonPressCallback onButtonPress) {
            this.texture = texture;
            this.onButtonPress = onButtonPress;
        }

        public ResourceLocation getTexture() {
            return texture;
        }

        public void onButtonPress(EyesEditorScreen screen, double mouseX, double mouseY, int button) {
            onButtonPress.onButtonPress(screen, mouseX, mouseY, button);
        }

        private static Point calculatePoint(EyesEditorScreen screen, double mouseX, double mouseY) {
            int spaceBetweenPixels = 2;
            int pixelSize = 16;

            int x = (int) ((mouseX - screen.headX) / (pixelSize + spaceBetweenPixels));
            int y = (int) ((mouseY - screen.headY) / (pixelSize + spaceBetweenPixels));

            return new Point(x, y);
        }

        @FunctionalInterface
        public interface ButtonPressCallback {
            void onButtonPress(EyesEditorScreen screen, double mouseX, double mouseY, int button);
        }
    }
}
