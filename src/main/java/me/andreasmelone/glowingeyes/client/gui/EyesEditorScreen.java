package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.capability.eyes.GlowingEyesCapability;
import me.andreasmelone.glowingeyes.common.packets.CapabilityUpdatePacket;
import me.andreasmelone.glowingeyes.common.packets.PacketManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class EyesEditorScreen extends Screen {
    public EyesEditorScreen() {
        super(Component.empty());
    }
    private int guiLeft, guiTop;

    protected int xSize = 256;
    protected int ySize = 222;

    HashMap<Point, Color> pixels = new HashMap<>();
    List<Button> modeButtons = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        AtomicBoolean saved = new AtomicBoolean(false);
        Player player = getMinecraft().player;
        if(player != null) {
            player.getCapability(GlowingEyesCapability.INSTANCE).ifPresent(eyes -> {
                pixels = eyes.getGlowingEyesMap();
                saved.set(true);
            });
        }
        if(!saved.get()) {
            LogUtils.getLogger().error("Could not load glowing eyes map from player capability");
        }

        // the color picker button
        this.addRenderableWidget(new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 30,
                20, 20,
                0, 0, 20,
                TextureLocations.COLOR_PICKER,
                64, 64,
                button -> {
                    getMinecraft().setScreen(new ColorPickerScreen(this));
                }
        ));

        // the preset menu button
        this.addRenderableWidget(new ImageButton(
                this.guiLeft + this.xSize - 60, this.guiTop + this.ySize - 30,
                20, 20,
                0, 0, 20,
                TextureLocations.PRESET_MENU_BUTTON,
                64, 64,
                button -> {

                }
        ));

        modeButtons.add(new ImageButton(
                this.guiLeft + 8, this.guiTop + 70,
                20, 20,
                0, 0, 20,
                TextureLocations.BRUSH_BUTTON,
                64, 64,
                button -> {
                    mode = Mode.BRUSH;
                    modeButtons.forEach(b -> b.active = true);
                    button.active = false;
                }
        ));
        modeButtons.add(new ImageButton(
                this.guiLeft + 8, this.guiTop + 95,
                20, 20,
                0, 0, 20,
                TextureLocations.ERASER_BUTTON,
                64, 64,
                button -> {
                    mode = Mode.ERASER;
                    modeButtons.forEach(b -> b.active = true);
                    button.active = false;
                }
        ));

        modeButtons.get(0).onPress();
        modeButtons.forEach(this::addRenderableWidget);
    }

    int headX, headY;
    int endHeadX, endHeadY;

    boolean displaySecondLayer = false;

    /**
     * The method that renders the screen
     * @param poseStack The PoseStack, a stack of transformations to apply to the rendering
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     * @param deltaTime The time since the last frame
     */
    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float deltaTime) {
        renderBackground(poseStack);

        GuiUtil.drawBackground(poseStack,
                TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        int spaceBetweenPixels = 2;
        int pixelSize = 16;
        final int headSize = 8;

        headX = this.guiLeft + (this.xSize - (headSize * pixelSize + (headSize - 1) * spaceBetweenPixels)) / 2;
        headY = this.guiTop + (this.ySize - (headSize * pixelSize + (headSize - 1) * spaceBetweenPixels)) / 2;
        endHeadX = headX + headSize * pixelSize + (headSize - 1) * spaceBetweenPixels;
        endHeadY = headY + headSize * pixelSize + (headSize - 1) * spaceBetweenPixels;

        fill(
                poseStack,
                headX - spaceBetweenPixels, headY - spaceBetweenPixels,
                endHeadX + spaceBetweenPixels, endHeadY + spaceBetweenPixels,
                new Color(160, 160, 160, 255).getRGB()
        );

        RenderSystem.setShaderTexture(0, getMinecraft().player.getSkinTextureLocation());

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
                    fill(
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
                            pixelSize,
                            pixelSize,
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
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        Color defaultColor = new Color(255, 0, 0);

        if(mouseX >= headX && mouseX <= endHeadX && mouseY >= headY && mouseY <= endHeadY) {
            int spaceBetweenPixels = 2;
            int pixelSize = 16;

            int x = (int) ((mouseX - headX) / (pixelSize + spaceBetweenPixels));
            int y = (int) ((mouseY - headY) / (pixelSize + spaceBetweenPixels));

            if (mode == Mode.BRUSH) {
                if (mouseButton == 0) {
                    pixels.put(new Point(x, y), defaultColor);
                } else if (mouseButton == 1) {
                    pixels.remove(new Point(x, y));
                }
            }

            if (mode == Mode.ERASER && mouseButton == 0) {
                pixels.remove(new Point(x, y));
            }

        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }
    @Override
    public void onClose() {
        AtomicBoolean saved = new AtomicBoolean(false);
        Player player = getMinecraft().player;
        if(player != null) {
            player.getCapability(GlowingEyesCapability.INSTANCE).ifPresent(eyes -> {
                eyes.setGlowingEyesMap(pixels);
                saved.set(true);

                PacketManager.INSTANCE.sendToServer(new CapabilityUpdatePacket(player, eyes));
            });
        }
        if(!saved.get()) {
            LogUtils.getLogger().error("Could not save glowing eyes map to player capability");
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    Mode mode = Mode.BRUSH;
    enum Mode {
        BRUSH,
        ERASER
    }
}
