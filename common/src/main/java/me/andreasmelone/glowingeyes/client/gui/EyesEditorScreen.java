package me.andreasmelone.glowingeyes.client.gui;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import me.andreasmelone.glowingeyes.client.component.eyes.ClientGlowingEyesComponent;
import me.andreasmelone.glowingeyes.client.gui.button.TintedOverlayImageButton;
import me.andreasmelone.glowingeyes.client.gui.preset.PresetsScreen;
import me.andreasmelone.glowingeyes.client.gui.skin.ClassicSkinPart;
import me.andreasmelone.glowingeyes.client.gui.skin.ISkinPart;
import me.andreasmelone.glowingeyes.client.gui.skin.SkinPartSelectorScreen;
import me.andreasmelone.glowingeyes.client.gui.widget.CursorSpaceWidget;
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
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

public class EyesEditorScreen extends Screen {
    private static final int UI_WIDTH = TextureLocations.UI_BACKGROUND_BROAD_WIDTH;
    private static final int UI_HEIGHT = TextureLocations.UI_BACKGROUND_BROAD_HEIGHT;

    private static final int CLICK_DELAY = 200;

    private static final int MODE_BUTTON_WIDTH = 20;
    private static final int MODE_BUTTON_HEIGHT = 20;
    private static final int MODE_BUTTON_SPACING = 5;
    private static final int MODE_BUTTON_X = 8;

    private static final int BUTTON_WIDTH = 20;
    private static final int BUTTON_HEIGHT = 20;

    private static final int SPACE_BETWEEN_PIXELS = 2;
    private static final int PIXEL_SIZE = 16;

    private static final Color HEAD_BACKGROUND_COLOR = new Color(160, 160, 160, 255);
    private static final Color GRID_LINES_COLOR = new Color(120, 120, 120, 255);

    private static final int MENU_BUTTONS_X = UI_WIDTH - 30;
    private static final int MENU_BUTTONS_SPACING = 5;

    private static final int COLOR_BOX_WIDTH = 20;
    private static final int COLOR_BOX_HEIGHT = 20;
    private static final int COLOR_BOX_OFFSET_Y = -25;
    private static final int COLOR_LABEL_OFFSET_Y = 10;
    private static final int COLOR_BOX_BG_MARGIN_X = 3;
    private static final int COLOR_BOX_BG_MARGIN_Y = 10;

    private int guiLeft, guiTop;
    private int headX, headY;
    private int endHeadX, endHeadY;
    private int headSizeX, headSizeY;
    private float scale = 1f;
    private long openedAt;

    protected Mode mode = Mode.values()[0];
    protected ISkinPart skinPart = ISkinPart.getPart(ClassicSkinPart.HEAD_FRONT, SkinUtil.isSlim());
    protected Map<Point, Color> pixels = new HashMap<>();

    private CursorSpaceWidget cursorSpaceWidget;
    private final Map<Mode, Button> modeButtons = new EnumMap<>(Mode.class);
    private final Map<ResourceLocation, NativeImage> allocatedTextures = new HashMap<>();
    private final ClientModContext mod;
    public EyesEditorScreen(ClientModContext mod) {
        super(Component.empty());
        this.mod = mod;
    }

    @Override
    protected void init() {
        super.init();
        this.guiLeft = (this.width - UI_WIDTH) / 2;
        this.guiTop = (this.height - UI_HEIGHT) / 2;
        this.headSizeX = this.skinPart.getSizeX();
        this.headSizeY = this.skinPart.getSizeY();
        this.openedAt = System.currentTimeMillis();

        this.calculateHeadSize();

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            this.pixels = GlowingEyesComponent.getGlowingEyesMap(player);
        } else {
            LogUtils.getLogger().error("Could not load glowing eyes map from player capability");
        }

        this.modeButtons.clear();

        float middleY = this.height / 2f;
        int modeCount = Mode.values().length;
        float totalHeight = modeCount * MODE_BUTTON_HEIGHT + (modeCount - 1) * MODE_BUTTON_SPACING;
        float startY = middleY - totalHeight / 2f;

        for (int i = 0; i < modeCount; i++) {
            int posY = (int) (startY + i * (MODE_BUTTON_HEIGHT + MODE_BUTTON_SPACING));
            this.addRenderableWidget(this.createModeButton(this.guiLeft + MODE_BUTTON_X, posY, Mode.values()[i]));
        }

        this.modeButtons.get(Mode.BRUSH).onPress();

        this.cursorSpaceWidget = new CursorSpaceWidget(this.headX, this.headY, this.endHeadX - this.headX, this.endHeadY - this.headY, this::mouseMoved, this::mouseClicked);
        this.addRenderableWidget(this.cursorSpaceWidget);

        List<Button> menuButtons = new ArrayList<>();

        // the reset button
        Button resetButton = new ImageButton(
                this.guiLeft + MENU_BUTTONS_X, 0,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                TextureLocations.RESET_BUTTON,
                button -> this.minecraft.setScreen(new ConfirmResetScreen(this))
        );
        menuButtons.add(resetButton);
        this.addRenderableWidget(resetButton);
        resetButton.setTooltip(Tooltip.create(Component.translatable("tooltip.glowingeyes.editor.reset")));

        // the skin part picker button
        Button skinPartPicker = new ImageButton(
                this.guiLeft + MENU_BUTTONS_X, 0,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                TextureLocations.SKIN_PART_PICKER_BUTTON,
                button -> SkinPartSelectorScreen.create(this, this.minecraft.player.getSkin().texture(), this.skinPart).thenAccept((part) -> {
                    if (part != null) {
                        this.skinPart = part;
                        this.headSizeX = this.skinPart.getSizeX();
                        this.headSizeY = this.skinPart.getSizeY();
                        this.calculateHeadSize();
                    }
                })
        );
        menuButtons.add(skinPartPicker);
        this.addRenderableWidget(skinPartPicker);
        skinPartPicker.setTooltip(Tooltip.create(Component.translatable("tooltip.glowingeyes.editor.partpicker")));

        // the preset menu button
        Button presetMenuButton = new ImageButton(
                this.guiLeft + MENU_BUTTONS_X, 0,
                BUTTON_WIDTH, BUTTON_HEIGHT,
                TextureLocations.PRESET_MENU_BUTTON,
                button -> Minecraft.getInstance().setScreen(new PresetsScreen(this))
        );
        menuButtons.add(presetMenuButton);
        this.addRenderableWidget(presetMenuButton);
        presetMenuButton.setTooltip(Tooltip.create(Component.translatable("tooltip.glowingeyes.editor.presetsmenu")));

        // the color picker button
        Button colorPickerButton = new ImageButton(
                this.guiLeft + MENU_BUTTONS_X, 0, // this is going to be set later on
                BUTTON_WIDTH, BUTTON_HEIGHT,
                TextureLocations.COLOR_PICKER_BUTTON,
                button -> Minecraft.getInstance().setScreen(new ColorPickerScreen(this.mod, this))
        );
        menuButtons.add(colorPickerButton);
        this.addRenderableWidget(colorPickerButton);
        colorPickerButton.setTooltip(Tooltip.create(Component.translatable("tooltip.glowingeyes.editor.colorpicker")));

        int count = menuButtons.size();
        totalHeight = count * BUTTON_HEIGHT + (count - 1) * MENU_BUTTONS_SPACING;
        startY = middleY - totalHeight / 2f;

        for (int i = 0; i < count; i++) {
            int posY = (int) (startY + i * (BUTTON_HEIGHT + MENU_BUTTONS_SPACING));
            menuButtons.get(i).setY(posY);
        }
    }

    /**
     * The method that renders the screen
     *
     * @param ctx       The GuiGraphics object which contains all rendering functions and the current context
     * @param mouseX    The x position of the mouse
     * @param mouseY    The y position of the mouse
     * @param deltaTime The time since the last frame
     */
    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float deltaTime) {
        super.renderBackground(ctx, mouseX, mouseY, deltaTime);

        GuiUtil.drawBackground(ctx,
                TextureLocations.UI_BACKGROUND_BROAD, this.guiLeft, this.guiTop, UI_WIDTH, UI_HEIGHT);

        ctx.pose().pushPose();
        ctx.pose().translate(
                (this.width - (this.width * this.scale)) / 2f,
                (this.height - (this.height * this.scale)) / 2f,
                0.0f
        );
        ctx.pose().scale(this.scale, this.scale, 1.0f);
        ctx.fill(
                this.headX - SPACE_BETWEEN_PIXELS, this.headY - SPACE_BETWEEN_PIXELS,
                this.endHeadX + SPACE_BETWEEN_PIXELS, this.endHeadY + SPACE_BETWEEN_PIXELS,
                HEAD_BACKGROUND_COLOR.getRGB()
        );

        for (int y = 0; y < this.headSizeY + 1; y++) {
            ctx.fill(
                    this.headX - SPACE_BETWEEN_PIXELS,
                    this.headY + (PIXEL_SIZE * y) + (SPACE_BETWEEN_PIXELS * (y - 1)),
                    this.endHeadX + SPACE_BETWEEN_PIXELS,
                    this.headY + (PIXEL_SIZE * y) + (SPACE_BETWEEN_PIXELS * (y - 1)) + SPACE_BETWEEN_PIXELS,
                    GRID_LINES_COLOR.getRGB()
            );
        }
        for (int x = 0; x < this.headSizeX + 1; x++) {
            ctx.fill(
                    this.headX + (PIXEL_SIZE * x) + (SPACE_BETWEEN_PIXELS * (x - 1)),
                    this.headY - SPACE_BETWEEN_PIXELS,
                    this.headX + (PIXEL_SIZE * x) + (SPACE_BETWEEN_PIXELS * (x - 1)) + SPACE_BETWEEN_PIXELS,
                    this.endHeadY + SPACE_BETWEEN_PIXELS,
                    GRID_LINES_COLOR.getRGB()
            );
        }

        ResourceLocation playerSkin = Minecraft.getInstance().player.getSkin().texture();
        for (int y = 0; y < this.headSizeY; y++) {
            for (int x = 0; x < this.headSizeX; x++) {
                Point point = new Point(x + this.skinPart.getX(), y + this.skinPart.getY());
                ctx.blit(
                        RenderType::guiTextured,
                        playerSkin,
                        this.headX + x * PIXEL_SIZE + x * SPACE_BETWEEN_PIXELS,
                        this.headY + y * PIXEL_SIZE + y * SPACE_BETWEEN_PIXELS,
                        this.skinPart.getX() + x, this.skinPart.getY() + y,
                        PIXEL_SIZE, PIXEL_SIZE,
                        1, 1,
                        64, 64
                );

                if (this.pixels.containsKey(point)) {
                    ctx.fill(
                            this.headX + x * PIXEL_SIZE + x * SPACE_BETWEEN_PIXELS - 1,
                            this.headY + y * PIXEL_SIZE + y * SPACE_BETWEEN_PIXELS - 1,
                            this.headX + x * PIXEL_SIZE + x * SPACE_BETWEEN_PIXELS + PIXEL_SIZE + 1,
                            this.headY + y * PIXEL_SIZE + y * SPACE_BETWEEN_PIXELS + PIXEL_SIZE + 1,
                            this.pixels.get(point).getRGB()
                    );
                }
            }
        }
        ctx.pose().popPose();

        if (this.mode == Mode.PICKER && this.checkBounds(mouseX, mouseY, this.headX, this.endHeadX, this.headY, this.endHeadY)) {
            Point convertedMouse = this.calculatePoint(mouseX, mouseY);
            Color color = this.getTexturePixelColor(playerSkin, 64, 64, convertedMouse.getX(), convertedMouse.getY());
            String text = ColorType.HEX.get(color);
            int length = this.font.width(text);

            ctx.fill(
                    mouseX - (length / 2) - COLOR_BOX_BG_MARGIN_X, mouseY - COLOR_LABEL_OFFSET_Y + COLOR_BOX_BG_MARGIN_Y,
                    mouseX + (length / 2) + COLOR_BOX_BG_MARGIN_X, mouseY - COLOR_BOX_HEIGHT / 2 + COLOR_BOX_OFFSET_Y - COLOR_BOX_BG_MARGIN_Y,
                    0xAA000000
            );
            ctx.fill(
                    mouseX - COLOR_BOX_WIDTH / 2, mouseY - COLOR_BOX_HEIGHT / 2 + COLOR_BOX_OFFSET_Y,
                    mouseX + COLOR_BOX_WIDTH / 2, mouseY + COLOR_BOX_HEIGHT / 2 + COLOR_BOX_OFFSET_Y,
                    color.getRGB()
            );
            ctx.drawCenteredString(this.minecraft.font, text,
                    mouseX, mouseY - COLOR_LABEL_OFFSET_Y, Color.WHITE.getRGB());
        }
        super.render(ctx, mouseX, mouseY, deltaTime);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (System.currentTimeMillis() < this.openedAt + CLICK_DELAY) return false;
        if (this.checkBounds((float) mouseX, (float) mouseY, this.headX, this.endHeadX, this.headY, this.endHeadY)) {
            this.mode.onButtonPress(this, mouseX, mouseY, button);
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
            GlowingEyesComponent.setGlowingEyesMap(player, this.pixels);
            ClientGlowingEyesComponent.sendUpdate();
        } else {
            LogUtils.getLogger().error("Could not save glowing eyes map to player capability");
        }
        this.allocatedTextures.forEach((rl, nativeImage) -> nativeImage.close());
        this.allocatedTextures.clear();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTick) {
    }

    private Color getTexturePixelColor(ResourceLocation texture, int texSizeX, int texSizeY, int x, int y) {
        if (x < 0 || x >= texSizeX) {
            throw new IllegalArgumentException("x must be within the screen width: 0 to " + (texSizeX - 1) + ". Provided: " + x);
        }
        if (y < 0 || y >= texSizeY) {
            throw new IllegalArgumentException("y must be within the screen height: 0 to " + (texSizeY - 1) + ". Provided: " + y);
        }

        Point point = new Point(x, y);
        if (this.pixels.containsKey(point)) {
            Color color = this.pixels.get(point);
            // "why?" You may ask; you see, opacity. It ruins everything.
            return color.withAlpha(255);
        }

        if (!this.allocatedTextures.containsKey(texture)) {
            long startTime = System.currentTimeMillis();

            GpuTexture gpuTexture = this.minecraft.getTextureManager().getTexture(texture).getTexture();
            GpuBuffer gpuBuffer = RenderSystem.getDevice().createBuffer(
                    () -> "Skin copy buffer", BufferType.PIXEL_PACK, BufferUsage.STATIC_READ, texSizeX * texSizeY * gpuTexture.getFormat().pixelSize()
            );
            CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
            NativeImage nativeImage = new NativeImage(texSizeX, texSizeY, false);
            RenderSystem.getDevice().createCommandEncoder().copyTextureToBuffer(gpuTexture, gpuBuffer, 0, () -> {
                try (GpuBuffer.ReadView readView = commandEncoder.readBuffer(gpuBuffer)) {

                    for(int ty = 0; ty < texSizeY; ++ty) {
                        for(int tx = 0; tx < texSizeX; ++tx) {
                            int m = readView.data().getInt((tx + ty * texSizeX) * gpuTexture.getFormat().pixelSize());
                            nativeImage.setPixelABGR(tx, texSizeY - ty - 1, m | 0xFF000000);
                        }
                    }
                }

                gpuBuffer.close();
            }, 0);

            LogUtils.getLogger().debug("Reading texture {} took {}ms", texture, System.currentTimeMillis() - startTime);

            this.allocatedTextures.put(texture, nativeImage);
            return new Color(nativeImage.getPixel(x, y));
        } else {
            NativeImage img = this.allocatedTextures.get(texture);
            return new Color(img.getPixel(x, y));
        }
    }

    private void calculateHeadSize() {
        int sizeX = this.headSizeX * PIXEL_SIZE + (this.headSizeX - 1) * SPACE_BETWEEN_PIXELS;
        int sizeY = this.headSizeY * PIXEL_SIZE + (this.headSizeY - 1) * SPACE_BETWEEN_PIXELS;
        this.headX = this.guiLeft + (UI_WIDTH - sizeX) / 2;
        this.headY = this.guiTop + (UI_HEIGHT - sizeY) / 2;
        this.endHeadX = this.headX + sizeX;
        this.endHeadY = this.headY + sizeY;

        switch (this.skinPart.getSizeX() * 100 + this.skinPart.getSizeY()) {
            case 412:
            case 812:
            case 312:
                this.scale = 0.7f;
                break;
            case 808:
            case 404:
            default:
                this.scale = 1.0f;
                break;
        }
    }

    private Point calculatePoint(double mouseX, double mouseY) {
        Vector2f scaled = this.scalePoint(this.headX, this.headY);
        int x = (int) ((mouseX - scaled.x) / ((PIXEL_SIZE + SPACE_BETWEEN_PIXELS) * this.scale));
        int y = (int) ((mouseY - scaled.y) / ((PIXEL_SIZE + SPACE_BETWEEN_PIXELS) * this.scale));

        return new Point(this.skinPart.getX() + x, this.skinPart.getY() + y);
    }

    public boolean checkBounds(float x, float y, float minX, float maxX, float minY, float maxY) {
        Vector2f minVec = this.scalePoint(minX, minY);
        Vector2f maxVec = this.scalePoint(maxX, maxY);
        return x >= minVec.x && x <= maxVec.x && y >= minVec.y && y <= maxVec.y;
    }

    private Vector2f scalePoint(float x, float y) {
        PoseStack pose = new PoseStack();
        pose.pushPose();
        pose.translate(
                (this.width - (this.width * this.scale)) / 2f,
                (this.height - (this.height * this.scale)) / 2f,
                0.0f
        );
        pose.scale(this.scale, this.scale, 1.0f);
        Vector3f head = pose.last().pose().transformPosition(x, y, 0.0f, new Vector3f());
        pose.popPose();
        return new Vector2f(head.x, head.y);
    }

    private Button createModeButton(int x, int y, Mode buttonMode) {
        TintedOverlayImageButton imageButton = new TintedOverlayImageButton(
                x, y,
                MODE_BUTTON_WIDTH, MODE_BUTTON_HEIGHT,
                buttonMode.getSprites(),
                buttonMode.getColorOverlay(),
                (button) -> {
                    this.mode = buttonMode;
                    this.modeButtons.forEach((m, b) -> b.active = true);
                    button.active = false;
                }
        );
        imageButton.setColorSupplier(this.mod.getModVariables().getFinalColor()::getRGB);
        imageButton.setTooltip(Tooltip.create(Component.translatable("tooltip.glowingeyes.editor." + buttonMode.name().toLowerCase())));

        this.modeButtons.put(buttonMode, imageButton);
        return imageButton;
    }

    public enum Mode {
        BRUSH(TextureLocations.BRUSH_BUTTON, TextureLocations.BRUSH_COLOR_OVERLAY, (screen, mouseX, mouseY, button) -> {
            Point point = screen.calculatePoint(mouseX, mouseY);

            if (button == 0) {
                Color finalColor = screen.mod.getModVariables().getFinalColor();
                screen.pixels.put(new Point(point.getX(), point.getY()), finalColor.withAlpha(200));
            } else if (button == 1) {
                screen.pixels.remove(new Point(point.getX(), point.getY()));
            } else if (button == 2) {
                screen.mod.getModVariables().setFinalColor(
                        screen.getTexturePixelColor(screen.minecraft.player.getSkin().texture(), 64, 64, point.getX(), point.getY())
                );
                screen.openedAt = System.currentTimeMillis();
            }
        }),
        ERASER(TextureLocations.ERASER_BUTTON, null, (screen, mouseX, mouseY, button) -> {
            Point point = screen.calculatePoint(mouseX, mouseY);
            screen.pixels.remove(new Point(point.getX(), point.getY()));
        }),
        PICKER(TextureLocations.PIPETTE_BUTTON, TextureLocations.PIPETTE_COLOR_OVERLAY, (screen, mouseX, mouseY, button) -> {
            Point point = screen.calculatePoint(mouseX, mouseY);

            Color color = screen.getTexturePixelColor(screen.minecraft.player.getSkin().texture(), 64, 64, point.getX(), point.getY());
            screen.mod.getModVariables().setFinalColor(color);

            screen.modeButtons.get(Mode.BRUSH).onPress();
            screen.modeButtons.forEach((mode, b) -> b.setFocused(false));
            screen.openedAt = System.currentTimeMillis();
        }),
        FILL(TextureLocations.FILL_BUCKET_BUTTON, TextureLocations.FILL_BUCKET_COLOR_OVERLAY, (screen, mouseX, mouseY, button) -> {
            Point point = screen.calculatePoint(mouseX, mouseY);
            Color color = screen.getTexturePixelColor(screen.minecraft.player.getSkin().texture(), 64, 64, point.getX(), point.getY());
            Color finalColor = screen.mod.getModVariables().getFinalColor().withAlpha(200);

            Stack<Point> stack = new Stack<>();
            Set<Point> visitedPoints = new HashSet<>();
            stack.push(point);
            visitedPoints.add(point);

            int minX = screen.skinPart.getX();
            int minY = screen.skinPart.getY();
            int maxX = minX + screen.skinPart.getSizeX();
            int maxY = minY + screen.skinPart.getSizeY();

            while (!stack.isEmpty()) {
                Point p = stack.pop();
                int x = p.getX();
                int y = p.getY();

                if (x < minX || x >= maxX || y < minY || y >= maxY) continue;
                Color pixelColor = screen.getTexturePixelColor(screen.minecraft.player.getSkin().texture(), 64, 64, x, y);
                if (!pixelColor.equals(color)) continue;

                screen.pixels.put(new Point(p.getX(), p.getY()), finalColor);

                Point east = new Point(x + 1, y);
                if (!visitedPoints.contains(east)) {
                    stack.push(east);
                    visitedPoints.add(east);
                }
                Point west = new Point(x - 1, y);
                if (!visitedPoints.contains(west)) {
                    stack.push(west);
                    visitedPoints.add(west);
                }
                Point north = new Point(x, y + 1);
                if (!visitedPoints.contains(north)) {
                    stack.push(north);
                    visitedPoints.add(north);
                }
                Point south = new Point(x, y - 1);
                if (!visitedPoints.contains(south)) {
                    stack.push(south);
                    visitedPoints.add(south);
                }
            }
        });

        private final WidgetSprites sprites;
        private final ButtonPressCallback onButtonPress;
        private final ResourceLocation colorOverlay;

        Mode(final WidgetSprites sprites, final ResourceLocation colorOverlay, final ButtonPressCallback onButtonPress) {
            this.sprites = sprites;
            this.onButtonPress = onButtonPress;
            this.colorOverlay = colorOverlay;
        }

        public WidgetSprites getSprites() {
            return this.sprites;
        }

        public void onButtonPress(EyesEditorScreen screen, double mouseX, double mouseY, int button) {
            this.onButtonPress.onButtonPress(screen, mouseX, mouseY, button);
        }

        public ResourceLocation getColorOverlay() {
            return this.colorOverlay;
        }

        @FunctionalInterface
        public interface ButtonPressCallback {
            void onButtonPress(EyesEditorScreen screen, double mouseX, double mouseY, int button);
        }
    }
}
