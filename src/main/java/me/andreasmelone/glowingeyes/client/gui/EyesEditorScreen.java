package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.gui.preset.PresetsScreen;
import me.andreasmelone.glowingeyes.client.util.ColorUtil;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.server.capability.eyes.GlowingEyesCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

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
        Player player = Minecraft.getInstance().player;
        if(player != null) {
            pixels = GlowingEyesCapability.getGlowingEyesMap(player);
            saved.set(true);
        }
        if(!saved.get()) {
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
                button -> {
                    Minecraft.getInstance().setScreen(new PresetsScreen(this));
                }
        ));

        modeButtons.clear();
        modeButtons.add(new ImageButton(
                this.guiLeft + 8, this.guiTop + 70,
                20, 20,
                0, 0, 20,
                TextureLocations.BRUSH_BUTTON,
                64, 64,
                button -> {
                    mode = Mode.BRUSH;
                    modeButtons.forEach(b -> b.active = true);
                    if(mode == Mode.BRUSH) button.active = false;
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
                    if(mode == Mode.ERASER) button.active = false;
                }
        ));
//        modeButtons.add(new ImageButton(
//                this.guiLeft + 8, this.guiTop + 120,
//                20, 20,
//                0, 0, 20,
//                TextureLocations.COLOR_PICKER,
//                64, 64,
//                button -> {
//                    mode = Mode.PICKER;
//                    modeButtons.forEach(b -> b.active = true);
//                    if(mode == Mode.PICKER) button.active = false;
//                }
//        ));

        modeButtons.get(0).onPress();
        modeButtons.forEach(this::addRenderableWidget);
    }

    int headX, headY;
    int endHeadX, endHeadY;

    boolean displaySecondLayer = false;

    /**
     * The method that renders the screen
     * @param guiGraphics The guiGraphics object that handles rendering
     * @param mouseX The x position of the mouse
     * @param mouseY The y position of the mouse
     * @param deltaTime The time since the last frame
     */
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float deltaTime) {
        renderBackground(guiGraphics);

        GuiUtil.drawBackground(guiGraphics,
                TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        int spaceBetweenPixels = 2;
        int pixelSize = 16;
        final int headSize = 8;

        headX = this.guiLeft + (this.xSize - (headSize * pixelSize + (headSize - 1) * spaceBetweenPixels)) / 2;
        headY = this.guiTop + (this.ySize - (headSize * pixelSize + (headSize - 1) * spaceBetweenPixels)) / 2;
        endHeadX = headX + headSize * pixelSize + (headSize - 1) * spaceBetweenPixels;
        endHeadY = headY + headSize * pixelSize + (headSize - 1) * spaceBetweenPixels;

        guiGraphics.fill(
                headX - spaceBetweenPixels, headY - spaceBetweenPixels,
                endHeadX + spaceBetweenPixels, endHeadY + spaceBetweenPixels,
                new Color(160, 160, 160, 255).getRGB()
        );

        for (int y = 0; y < headSize; y++) {
            for (int x = 0; x < headSize; x++) {
                Point point = new Point(x, y);
                guiGraphics.blit(
                        Minecraft.getInstance().player.getSkinTextureLocation(),
                        headX + x * pixelSize + x * spaceBetweenPixels,
                        headY + y * pixelSize + y * spaceBetweenPixels,
                        pixelSize,
                        pixelSize,
                        8f + x, 8f + y,
                        1, 1,
                        64, 64
                );

                if(pixels.containsKey(point)) {
                    guiGraphics.fill(
                            headX + x * pixelSize + x * spaceBetweenPixels,
                            headY + y * pixelSize + y * spaceBetweenPixels,
                            headX + x * pixelSize + x * spaceBetweenPixels + pixelSize,
                            headY + y * pixelSize + y * spaceBetweenPixels + pixelSize,
                            pixels.get(point).getRGB()
                    );
                }

                if(displaySecondLayer) {
                    guiGraphics.blit(
                            Minecraft.getInstance().player.getSkinTextureLocation(),
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

        super.render(guiGraphics, mouseX, mouseY, deltaTime);
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

                modeButtons.get(0).onPress();
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
        AtomicBoolean saved = new AtomicBoolean(false);
        Player player = Minecraft.getInstance().player;
        if(player != null) {
            GlowingEyesCapability.setGlowingEyesMap(player, pixels);
            saved.set(true);

            GlowingEyesCapability.sendUpdate();
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

    public void openAsParent() {
        this.pixels.clear();
        this.pixels.putAll(GlowingEyesCapability.getGlowingEyesMap(Minecraft.getInstance().player));

        Minecraft.getInstance().setScreen(this);
    }

    Mode mode = Mode.BRUSH;
    enum Mode {
        BRUSH,
        ERASER,
        PICKER;
    }
}
