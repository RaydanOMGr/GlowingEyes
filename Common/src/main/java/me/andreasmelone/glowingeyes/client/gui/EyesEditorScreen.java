package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.gui.preset.PresetsScreen;
import me.andreasmelone.glowingeyes.client.util.ColorUtil;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class EyesEditorScreen extends Screen {
    public EyesEditorScreen() {
        super(Component.empty());
    }
    private int guiLeft, guiTop,
            headX, headY,
            endHeadX, endHeadY;
    private boolean displaySecondLayer = false;
    private int xSize = 256;
    private int ySize = 222;

    Map<Point, Color> pixels = new HashMap<>();
    Map<Mode, Button> modeButtons = new HashMap<>();
    Color headBackgroundColor = new Color(160, 160, 160, 255);
    Mode mode = Mode.BRUSH;

    @Override
    protected void init() {
        super.init();
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
                button -> Minecraft.getInstance().setScreen(new ColorPickerScreen(this))
        ));

        // the preset menu button
        this.addRenderableWidget(new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 55,
                20, 20,
                0, 0, 20,
                TextureLocations.PRESET_MENU_BUTTON,
                64, 64,
                button -> Minecraft.getInstance().setScreen(new PresetsScreen(this))
        ));

        this.modeButtons.clear();

        this.createModeButton(8, 70, TextureLocations.BRUSH_BUTTON, Mode.BRUSH);
        this.createModeButton(8, 95, TextureLocations.ERASER_BUTTON, Mode.ERASER);
        this.createModeButton(8, 120, TextureLocations.PIPETTE_BUTTON, Mode.PICKER);

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
                    Gui.fill(
                            poseStack,
                            headX + x * pixelSize + x * spaceBetweenPixels,
                            headY + y * pixelSize + y * spaceBetweenPixels,
                            headX + x * pixelSize + x * spaceBetweenPixels + pixelSize,
                            headY + y * pixelSize + y * spaceBetweenPixels + pixelSize,
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

        super.render(poseStack, mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(mouseX >= headX && mouseX <= endHeadX && mouseY >= headY && mouseY <= endHeadY) {
            int spaceBetweenPixels = 2;
            int pixelSize = 16;

            int x = (int) ((mouseX - headX) / (pixelSize + spaceBetweenPixels));
            int y = (int) ((mouseY - headY) / (pixelSize + spaceBetweenPixels));

            if (mode == Mode.BRUSH) {
                if (button == 0) {
                    pixels.put(new Point(x, y), ColorPickerScreen.getSelectedColor());
                } else if (button == 1) {
                    pixels.remove(new Point(x, y));
                }
            }

            if (mode == Mode.ERASER && button == 0) {
                pixels.remove(new Point(x, y));
            }

            if (mode == Mode.PICKER && button == 0) {
                GL.createCapabilities();
                GL11.glReadBuffer(GL11.GL_FRONT);

                float[] pixel = new float[4];

                double scaleX = (double) Minecraft.getInstance().getWindow().getWidth() / (double) Minecraft.getInstance().getWindow().getGuiScaledWidth();
                double scaleY = (double) Minecraft.getInstance().getWindow().getHeight() / (double) Minecraft.getInstance().getWindow().getGuiScaledHeight();

                GL11.glReadPixels(
                        (int)(mouseX * scaleX), (int) (mouseY * scaleY),
                        1, 1,
                        GL11.GL_RGBA, GL11.GL_FLOAT,
                        pixel
                );

                Color color = new Color(pixel[0], pixel[1], pixel[2], pixel[3]);
                System.out.println("Color: " + ColorUtil.intToHex(color.getRGB()));
                ColorPickerScreen.setSelectedColor(color);

                modeButtons.get(Mode.BRUSH).onPress();
            }
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

    public void openAsParent() {
        this.pixels.clear();
        this.pixels.putAll(GlowingEyesComponent.getGlowingEyesMap(Minecraft.getInstance().player));

        Minecraft.getInstance().setScreen(this);
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
                }
        );
        modeButtons.put(buttonMode, imageButton);
        return imageButton;
    }

    enum Mode {
        BRUSH,
        ERASER,
        PICKER;
    }
}
