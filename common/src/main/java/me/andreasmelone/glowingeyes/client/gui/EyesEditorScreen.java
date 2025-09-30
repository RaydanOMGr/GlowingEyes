package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.platform.GlConst;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.gui.preset.PresetsScreen;
import me.andreasmelone.glowingeyes.client.gui.skin.ClassicSkinPart;
import me.andreasmelone.glowingeyes.client.gui.skin.ISkinPart;
import me.andreasmelone.glowingeyes.client.gui.skin.SkinPartSelectorScreen;
import me.andreasmelone.glowingeyes.client.mod.ClientModContext;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.SkinUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.client.util.color.ColorType;
import me.andreasmelone.glowingeyes.common.component.eyes.GlowingEyesComponent;
import me.andreasmelone.glowingeyes.common.util.Color;
import me.andreasmelone.glowingeyes.common.util.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class EyesEditorScreen extends Screen {
    private int guiLeft, guiTop;
    private int headX, headY;
    private int endHeadX, endHeadY;
    private int headSizeX, headSizeY;
    private float scale = 1f;
    private long openedAt;
    private final boolean displaySecondLayer = false;

    Mode mode = Mode.BRUSH;
    ISkinPart skinPart = ISkinPart.getPart(ClassicSkinPart.HEAD_FRONT, SkinUtil.isSlim());
    Map<Point, Color> pixels = new HashMap<>();
    Map<Mode, Button> modeButtons = new EnumMap<>(Mode.class);
    Map<ResourceLocation, Long> allocatedTextures = new HashMap<>();
    Color headBackgroundColor = new Color(160, 160, 160, 255);
    Color gridLinesColor = new Color(120, 120, 120, 255);

    private final int xSize = 256;
    private final int ySize = 222;
    private final int spaceBetweenPixels = 2;
    private final int pixelSize = 16;
    private final ClientModContext mod;

    public EyesEditorScreen(ClientModContext mod) {
        super(Component.empty());
        this.mod = mod;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.headSizeX = skinPart.getSizeX();
        this.headSizeY = skinPart.getSizeY();
        this.openedAt = System.currentTimeMillis();

        this.calculateHeadSize();

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
                TextureLocations.COLOR_PICKER_BUTTON,
                button -> Minecraft.getInstance().setScreen(new ColorPickerScreen(mod, this))
        ));
        colorPickerButton.setTooltip(Tooltip.create(Component.translatable("tooltip.glowingeyes.editor.colorpicker")));

        // the preset menu button
        Button presetMenuButton;
        this.addRenderableWidget(presetMenuButton = new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 30 - 25,
                20, 20,
                TextureLocations.PRESET_MENU_BUTTON,
                button -> Minecraft.getInstance().setScreen(new PresetsScreen(this))
        ));
        presetMenuButton.setTooltip(Tooltip.create(Component.translatable("tooltip.glowingeyes.editor.presetsmenu")));

        // the 2nd layer toggle button
        Button skinPartPicker;
        this.addRenderableWidget(skinPartPicker = new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 30 - 25 * 2,
                20, 20,
                TextureLocations.SKIN_PART_PICKER_BUTTON,
                button -> {
                    SkinPartSelectorScreen.create(this, minecraft.player.getSkin().texture(), skinPart).thenAccept((part) -> {
                        if (part != null) {
                            skinPart = part;
                            this.headSizeX = skinPart.getSizeX();
                            this.headSizeY = skinPart.getSizeY();
                            this.calculateHeadSize();
                        }
                    });
                }
        ));
        skinPartPicker.setTooltip(Tooltip.create(Component.translatable("tooltip.glowingeyes.editor.partpicker")));

        Button resetButton;
        this.addRenderableWidget(resetButton = new ImageButton(
                this.guiLeft + this.xSize - 30, this.guiTop + this.ySize - 30 - 25 * 3,
                20, 20,
                TextureLocations.RESET_BUTTON,
                button -> {
                    this.minecraft.setScreen(new ConfirmResetScreen(this));
                }
        ));
        resetButton.setTooltip(Tooltip.create(Component.translatable("tooltip.glowingeyes.editor.reset")));

        this.modeButtons.clear();

        this.createModeButton(8, 70, Mode.BRUSH);
        this.createModeButton(8, 95, Mode.ERASER);
        this.createModeButton(8, 120, Mode.PICKER);

        this.modeButtons.get(Mode.BRUSH).onPress();
        this.modeButtons.forEach((mode, button) -> this.addRenderableWidget(button));
    }

    /**
     * The method that renders the screen
     *
     * @param ctx The GuiGraphics object which contains all rendering functions and the current context
     * @param mouseX      The x position of the mouse
     * @param mouseY      The y position of the mouse
     * @param deltaTime   The time since the last frame
     */
    @Override
    public void render(GuiGraphics ctx, int mouseX, int mouseY, float deltaTime) {
        super.renderBackground(ctx, mouseX, mouseY, deltaTime);

        GuiUtil.drawBackground(ctx,
                TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, this.xSize, this.ySize);

        ctx.pose().pushPose();
        ctx.pose().translate(
                (width - (width * scale)) / 2f,
                (height - (height * scale)) / 2f,
                0.0f
        );
        ctx.pose().scale(scale, scale, 1.0f);
        ctx.fill(
                headX - spaceBetweenPixels, headY - spaceBetweenPixels,
                endHeadX + spaceBetweenPixels, endHeadY + spaceBetweenPixels,
                headBackgroundColor.getRGB()
        );

        for (int y = 0; y < headSizeY + 1; y++) {
            ctx.fill(
                    headX - spaceBetweenPixels,
                    headY + (pixelSize * y) + (spaceBetweenPixels * (y - 1)),
                    endHeadX + spaceBetweenPixels,
                    headY + (pixelSize * y) + (spaceBetweenPixels * (y - 1)) + spaceBetweenPixels,
                    gridLinesColor.getRGB()
            );
        }
        for (int x = 0; x < headSizeX + 1; x++) {
            ctx.fill(
                    headX + (pixelSize * x) + (spaceBetweenPixels * (x - 1)),
                    headY - spaceBetweenPixels,
                    headX + (pixelSize * x) + (spaceBetweenPixels * (x - 1)) + spaceBetweenPixels,
                    endHeadY + spaceBetweenPixels,
                    gridLinesColor.getRGB()
            );
        }

        ResourceLocation playerSkin = Minecraft.getInstance().player.getSkin().texture();
        for (int y = 0; y < headSizeY; y++) {
            for (int x = 0; x < headSizeX; x++) {
                Point point = new Point(x + skinPart.getX(), y + skinPart.getY());
                ctx.blit(
                        RenderType::guiTextured,
                        playerSkin,
                        headX + x * pixelSize + x * spaceBetweenPixels,
                        headY + y * pixelSize + y * spaceBetweenPixels,
                        skinPart.getX() + x, skinPart.getY() + y,
                        pixelSize, pixelSize,
                        1, 1,
                        64, 64
                );

                if (pixels.containsKey(point)) {
                    ctx.fill(
                            headX + x * pixelSize + x * spaceBetweenPixels - 1,
                            headY + y * pixelSize + y * spaceBetweenPixels - 1,
                            headX + x * pixelSize + x * spaceBetweenPixels + pixelSize + 1,
                            headY + y * pixelSize + y * spaceBetweenPixels + pixelSize + 1,
                            pixels.get(point).getRGB()
                    );
                }
            }
        }
        ctx.pose().popPose();

        if (mode == Mode.PICKER && checkBounds(mouseX, mouseY, headX, endHeadX, headY, endHeadY)) {
            Point convertedMouse = calculatePoint(mouseX, mouseY);
            Color color = this.getTexturePixelColor(playerSkin, 64, 64, convertedMouse.getX(), convertedMouse.getY());

            ctx.fill(mouseX - 10, mouseY - 10 - 25, mouseX + 10, mouseY + 10 - 25, color.getRGB());
            ctx.drawCenteredString(minecraft.font, ColorType.HEX.get(color),
                    mouseX, mouseY - 10, 0xFFFFFF);
        }
        super.render(ctx, mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(System.currentTimeMillis() < openedAt + 200) return false;
        if (checkBounds((float) mouseX, (float) mouseY, headX, endHeadX, headY, endHeadY)) {
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
        if (player != null) {
            GlowingEyesComponent.setGlowingEyesMap(player, pixels);
            ClientGlowingEyesComponent.sendUpdate();
        } else {
            LogUtils.getLogger().error("Could not save glowing eyes map to player capability");
        }
        allocatedTextures.forEach((rl, ptr) -> MemoryUtil.nmemFree(ptr));
        allocatedTextures.clear();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(GuiGraphics $$0, int $$1, int $$2, float $$3) {
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
            GlStateManager._getTexImage(GlConst.GL_TEXTURE_2D, 0, GlConst.GL_RGB, GlConst.GL_UNSIGNED_BYTE, adr);
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

    private void calculateHeadSize() {
        int sizeX = headSizeX * pixelSize + (headSizeX - 1) * spaceBetweenPixels;
        int sizeY = headSizeY * pixelSize + (headSizeY - 1) * spaceBetweenPixels;
        headX = this.guiLeft + (this.xSize - sizeX) / 2;
        headY = this.guiTop + (this.ySize - sizeY) / 2;
        endHeadX = headX + sizeX;
        endHeadY = headY + sizeY;

        switch (skinPart.getSizeX() * 100 + skinPart.getSizeY()) {
            case 412:
            case 812:
            case 312:
                scale = 0.7f;
                break;
            case 808:
            case 404:
            default:
                scale = 1.0f;
                break;
        }
    }

    private Point calculatePoint(double mouseX, double mouseY) {
        Vector2f scaled = scalePoint(headX, headY);
        int x = (int) ((mouseX - scaled.x) / ((pixelSize + spaceBetweenPixels) * scale));
        int y = (int) ((mouseY - scaled.y) / ((pixelSize + spaceBetweenPixels) * scale));

        return new Point(skinPart.getX() + x, skinPart.getY() + y);
    }

    public boolean checkBounds(float x, float y, float minX, float maxX, float minY, float maxY) {
        Vector2f minVec = scalePoint(minX, minY);
        Vector2f maxVec = scalePoint(maxX, maxY);
        return x >= minVec.x && x <= maxVec.x && y >= minVec.y && y <= maxVec.y;
    }

    private Vector2f scalePoint(float x, float y) {
        PoseStack pose = new PoseStack();
        pose.pushPose();
        pose.translate(
                (width - (width * scale)) / 2f,
                (height - (height * scale)) / 2f,
                0.0f
        );
        pose.scale(scale, scale, 1.0f);
        Vector3f head = pose.last().pose().transformPosition(x, y, 0.0f, new Vector3f());
        pose.popPose();
        return new Vector2f(head.x, head.y);
    }

    private Button createModeButton(int x, int y, Mode buttonMode) {
        Button imageButton = new ImageButton(
                this.guiLeft + x, this.guiTop + y,
                20, 20,
                buttonMode.getSprites(),
                button -> {
                    mode = buttonMode;
                    modeButtons.forEach((m, b) -> b.active = true);
                    button.active = false;
                }
        );
        imageButton.setTooltip(Tooltip.create(Component.translatable("tooltip.glowingeyes.editor." + buttonMode.name().toLowerCase())));

        modeButtons.put(buttonMode, imageButton);
        return imageButton;
    }

    public enum Mode {
        BRUSH(TextureLocations.BRUSH_BUTTON, (screen, mouseX, mouseY, button) -> {
            Point point = screen.calculatePoint(mouseX, mouseY);

            if (button == 0) {
                Color finalColor = screen.mod.getModVariables().getFinalColor();
                screen.pixels.put(new Point(point.getX(), point.getY()), new Color(finalColor.getRed(), finalColor.getGreen(), finalColor.getBlue(), 200));
            } else if (button == 1) {
                screen.pixels.remove(new Point(point.getX(), point.getY()));
            } else if(button == 2) {
                screen.mod.getModVariables().setFinalColor(
                        screen.getTexturePixelColor(screen.minecraft.player.getSkin().texture(), 64, 64, point.getX(), point.getY())
                );
            }
        }),
        ERASER(TextureLocations.ERASER_BUTTON, (screen, mouseX, mouseY, button) -> {
            Point point = screen.calculatePoint(mouseX, mouseY);
            screen.pixels.remove(new Point(point.getX(), point.getY()));
        }),
        PICKER(TextureLocations.PIPETTE_BUTTON, (screen, mouseX, mouseY, button) -> {
            Point point = screen.calculatePoint(mouseX, mouseY);

            Color color = screen.getTexturePixelColor(screen.minecraft.player.getSkin().texture(), 64, 64, point.getX(), point.getY());
            screen.mod.getModVariables().setFinalColor(color);

            screen.modeButtons.get(Mode.BRUSH).onPress();
            screen.modeButtons.forEach((mode, b) -> b.setFocused(false));
        });

        private final WidgetSprites sprites;
        private final ButtonPressCallback onButtonPress;

        Mode(final WidgetSprites sprites, final ButtonPressCallback onButtonPress) {
            this.sprites = sprites;
            this.onButtonPress = onButtonPress;
        }

        public WidgetSprites getSprites() {
            return sprites;
        }

        public void onButtonPress(EyesEditorScreen screen, double mouseX, double mouseY, int button) {
            onButtonPress.onButtonPress(screen, mouseX, mouseY, button);
        }

        @FunctionalInterface
        public interface ButtonPressCallback {
            void onButtonPress(EyesEditorScreen screen, double mouseX, double mouseY, int button);
        }
    }
}
