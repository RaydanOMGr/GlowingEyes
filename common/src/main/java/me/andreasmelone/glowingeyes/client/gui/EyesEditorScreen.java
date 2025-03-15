package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.gui.preset.PresetsScreen;
import me.andreasmelone.glowingeyes.client.gui.skin.SkinPart;
import me.andreasmelone.glowingeyes.client.gui.skin.SkinPartSelectorScreen;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.util.color.ColorType;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryUtil;

import java.util.Map;
import java.util.EnumMap;
import java.util.HashMap;

public class EyesEditorScreen extends Screen {
    private int guiLeft, guiTop;
    private int headX, headY;
    private int endHeadX, endHeadY;
    private long openedAt;
    private boolean displaySecondLayer = false;

    Mode mode = Mode.BRUSH;
    SkinPart selected = SkinPart.HEAD_FRONT;
    Map<Point, Color> pixels = new HashMap<>();
    Map<Mode, Button> modeButtons = new EnumMap<>(Mode.class);
    Map<ResourceLocation, Long> allocatedTextures = new HashMap<>();
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
        this.openedAt = System.currentTimeMillis();

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            pixels = GlowingEyesComponent.getGlowingEyesMap(player);
        } else {
            LogUtils.getLogger().error("Could not load glowing eyes map from player capability");
        }

        // the color picker button
        Button colorPickerButton;
        this.addRenderableWidget(colorPickerButton = new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 30,
                20, 20,
                0, 0, 20,
                TextureLocations.COLOR_PICKER_BUTTON,
                64, 64,
                button -> Minecraft.getInstance().setScreen(new ColorPickerScreen(mod, this)),
                (button, poseStack, x, y) ->
                        renderTooltip(poseStack, button.getMessage(), x, y),
                Component.translatable("tooltip.glowingeyes.editor.colorpicker")
        ));

        // the preset menu button
        Button presetMenuButton;
        this.addRenderableWidget(presetMenuButton = new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 30 - 25,
                20, 20,
                0, 0, 20,
                TextureLocations.PRESET_MENU_BUTTON,
                64, 64,
                button -> Minecraft.getInstance().setScreen(new PresetsScreen(this)),
                (button, poseStack, x, y) ->
                        renderTooltip(poseStack, button.getMessage(), x, y),
                Component.translatable("tooltip.glowingeyes.editor.presetsmenu")
        ));

        // the preset menu button
        // the 2nd layer toggle button
        Button skinPartPicker;
        this.addRenderableWidget(skinPartPicker = new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 30 - 25 * 2,
                20, 20,
                0, 0, 20,
                TextureLocations.SKIN_PART_PICKER_BUTTON,
                64, 64,
                button -> {
                    SkinPartSelectorScreen.create(this, minecraft.player.getSkinTextureLocation(), selected).thenAccept((part) -> {
                        if(part != null) selected = part;
                    });
                },
                (button, poseStack, x, y) ->
                        renderTooltip(poseStack, button.getMessage(), x, y),
                Component.translatable("tooltip.glowingeyes.editor.partpicker")
        ));

        Button resetButton;
        this.addRenderableWidget(resetButton = new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 30 - 25 * 3,
                20, 20,
                0, 0, 20,
                TextureLocations.RESET_BUTTON,
                64, 64,
                button -> {
                    this.minecraft.setScreen(new ConfirmResetScreen(this));
                },
                (button, poseStack, x, y) ->
                        renderTooltip(poseStack, button.getMessage(), x, y),
                Component.translatable("tooltip.glowingeyes.editor.reset")
        ));

        this.modeButtons.clear();

        this.createModeButton(8, 70, Mode.BRUSH);
        this.createModeButton(8, 95, Mode.ERASER);
        this.createModeButton(8, 120, Mode.PICKER);

        this.modeButtons.get(Mode.BRUSH).onPress();
        this.modeButtons.forEach((mode, button) -> this.addRenderableWidget(button));
    }

    /**
     * The method that renders the screen
     * @param poseStack The GuiGraphics object which contains all rendering functions and the current context
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

        fill(
                poseStack,
                headX - spaceBetweenPixels, headY - spaceBetweenPixels,
                endHeadX + spaceBetweenPixels, endHeadY + spaceBetweenPixels,
                headBackgroundColor.getRGB()
        );

        ResourceLocation playerSkin = Minecraft.getInstance().player.getSkinTextureLocation();
        for (int y = 0; y < headSize; y++) {
            for (int x = 0; x < headSize; x++) {
                Point point = new Point(x + selected.getX(), y + selected.getY());
                RenderSystem.setShaderTexture(0, playerSkin);
                blit(
                        poseStack,
                        headX + x * pixelSize + x * spaceBetweenPixels,
                        headY + y * pixelSize + y * spaceBetweenPixels,
                        pixelSize, pixelSize,
                        selected.getX() + x, selected.getY() + y,
                        1, 1,
                        64, 64
                );

                if (pixels.containsKey(point)) {
                    fill(
                            poseStack,
                            headX + x * pixelSize + x * spaceBetweenPixels - 1,
                            headY + y * pixelSize + y * spaceBetweenPixels - 1,
                            headX + x * pixelSize + x * spaceBetweenPixels + pixelSize + 1,
                            headY + y * pixelSize + y * spaceBetweenPixels + pixelSize + 1,
                            pixels.get(point).getRGB()
                    );
                }
            }
        }

        if(mode == Mode.PICKER && mouseX >= headX && mouseX <= endHeadX && mouseY >= headY && mouseY <= endHeadY) {
            Point convertedMouse = calculatePoint(mouseX, mouseY);
            Color color = this.getTexturePixelColor(playerSkin, 64, 64, convertedMouse.getX(), convertedMouse.getY());

            fill(poseStack, mouseX - 10, mouseY - 10 - 25, mouseX + 10, mouseY + 10 - 25, color.getRGB());
            drawCenteredString(poseStack, minecraft.font, ColorType.HEX.get(color),
                    mouseX, mouseY - 10, 0xFFFFFF);
        }

        super.render(poseStack, mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(System.currentTimeMillis() < openedAt + 200) return false;
        if (mouseX >= headX && mouseX <= endHeadX && mouseY >= headY && mouseY <= endHeadY) {
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

    /**
     * @deprecated Has been replaced by {@link EyesEditorScreen#getTexturePixelColor(ResourceLocation, int, int, int, int)}
     *
     * Gets the color of the pixel at a certain screen coordinate
     * @param x The x coordinate from which to read
     * @param y The y coordinate from which to read
     * @return The color of the pixel at x,y
     */
    @Deprecated
    private Color getPixelColor(double x, double y) {
        Window window = minecraft.getWindow();
        if (x < 0 || x >= window.getWidth()) {
            throw new IllegalArgumentException("x must be within the screen width: 0 to " + (window.getWidth() - 1) + ". Provided: " + x);
        }
        if (y < 0 || y >= window.getHeight()) {
            throw new IllegalArgumentException("y must be within the screen height: 0 to " + (window.getHeight() - 1) + ". Provided: " + y);
        }

        float[] pixel = new float[3];

        // Divides the actual width/height by the scaled width/height to find out by what factor it was scaled
        float scaleX = (float) window.getWidth() / window.getGuiScaledWidth();
        float scaleY = (float) window.getHeight() / window.getGuiScaledHeight();
        // Calculates the actual position of the pixel
        int pixelX = (int) (x * scaleX);
        int pixelY = (int) ((window.getGuiScaledHeight() - y) * scaleY); // The y value needs
        // to be inverted relative to the height
        // since minecraft's 0-point is top-left
        // while gl's 0-point is bottom-left
        GL11.glReadPixels(pixelX, pixelY, 1, 1, GL11.GL_RGB, GL11.GL_FLOAT, pixel);

        return new Color(pixel[0], pixel[1], pixel[2]);
    }

    private Color getTexturePixelColor(ResourceLocation texture, int texSizeX, int texSizeY, int x, int y) {
        if (x < 0 || x >= texSizeX) {
            throw new IllegalArgumentException("x must be within the screen width: 0 to " + (texSizeX - 1) + ". Provided: " + x);
        }
        if (y < 0 || y >= texSizeY) {
            throw new IllegalArgumentException("y must be within the screen height: 0 to " + (texSizeY - 1) + ". Provided: " + y);
        }

        Point point = new Point(x, y);
        if(pixels.containsKey(point)) {
            Color color = pixels.get(point);
            // "why?" You may ask; you see, opacity. It ruins everything.
            return new Color(color.getRed(), color.getGreen(), color.getBlue());
        }

        if(!allocatedTextures.containsKey(texture)) {
            int pixelSize = 3; // 3 channels: red, green, blue; just make sure the format is set to GL_RGB
            long adr = MemoryUtil.nmemCalloc((long) texSizeX * texSizeY * pixelSize, 1);
            long startTime = System.currentTimeMillis();

            minecraft.getTextureManager().getTexture(texture).bind();
            GlStateManager._getTexImage(3553, 0, GlConst.GL_RGB, GlConst.GL_UNSIGNED_BYTE, adr);
            LogUtils.getLogger().debug("Reading texture {} took {}ms", texture, System.currentTimeMillis() - startTime);

            allocatedTextures.put(texture, adr);
            return getTexturePixelColor(texture, texSizeX, texSizeY, x, y);
        } else {
            long adr = allocatedTextures.get(texture);
            int index = (y * texSizeX + x) * 3;
            return new Color(
                    MemoryUtil.memGetByte(adr + index) & 0xFF,
                    MemoryUtil.memGetByte(adr + index + 1) & 0xFF,
                    MemoryUtil.memGetByte(adr + index + 2) & 0xFF
            );
        }
    }

    private void calculateHeadSize(int headSize, int pixelSize, int spaceBetweenPixels) {
        int head = headSize * pixelSize + (headSize - 1) * spaceBetweenPixels;
        headX = this.guiLeft + (this.xSize - head) / 2;
        headY = this.guiTop + (this.ySize - head) / 2;
        endHeadX = headX + head;
        endHeadY = headY + head;
    }

    private Button createModeButton(int x, int y, Mode buttonMode) {
        Button imageButton = new ImageButton(
                this.guiLeft + x, this.guiTop + y,
                20, 20,
                0, 0, 20,
                buttonMode.getTexture(),
                64, 64,
                button -> {
                    mode = buttonMode;
                    modeButtons.forEach((m, b) -> b.active = true);
                    button.active = false;
                },
                (button, poseStack, buttonX, buttonY) ->
                        renderTooltip(poseStack, button.getMessage(), buttonX, buttonY),
                Component.translatable("tooltip.glowingeyes.editor." + buttonMode.name().toLowerCase())
        );

        modeButtons.put(buttonMode, imageButton);
        return imageButton;
    }

    private Point calculatePoint(double mouseX, double mouseY) {
        int spaceBetweenPixels = 2;
        int pixelSize = 16;

        int x = (int) ((mouseX - headX) / (pixelSize + spaceBetweenPixels));
        int y = (int) ((mouseY - headY) / (pixelSize + spaceBetweenPixels));

        return new Point(selected.getX() + x, selected.getY() + y);
    }

    public enum Mode {
        BRUSH(TextureLocations.BRUSH_BUTTON, (screen, mouseX, mouseY, button) -> {
            Point point = screen.calculatePoint(mouseX, mouseY);

            if (button == 0) {
                Color finalColor = screen.mod.getModVariables().getFinalColor();
                screen.pixels.put(new Point(point.getX(), point.getY()), new Color(finalColor.getRed(), finalColor.getGreen(), finalColor.getBlue(), 200));
            } else if (button == 1) {
                screen.pixels.remove(new Point(point.getX(), point.getY()));
            }  else if(button == 2) {
                screen.mod.getModVariables().setFinalColor(screen.getPixelColor(mouseX, mouseY));
            }
        }),
        ERASER(TextureLocations.ERASER_BUTTON, (screen, mouseX, mouseY, button) -> {
            Point point = screen.calculatePoint(mouseX, mouseY);
            screen.pixels.remove(new Point(point.getX(), point.getY()));
        }),
        PICKER(TextureLocations.PIPETTE_BUTTON, (screen, mouseX, mouseY, button) -> {
            Point point = screen.calculatePoint(mouseX, mouseY);
            Color color = screen.getTexturePixelColor(screen.minecraft.player.getSkinTextureLocation(), 64, 64, point.getX(), point.getY());
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

            return new Point(screen.selected.getX() + x, screen.selected.getY() + y);
        }

        @FunctionalInterface
        public interface ButtonPressCallback {
            void onButtonPress(EyesEditorScreen screen, double mouseX, double mouseY, int button);
        }
    }
}
