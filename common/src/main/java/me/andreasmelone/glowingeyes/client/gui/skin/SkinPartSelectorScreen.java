package me.andreasmelone.glowingeyes.client.gui.skin;

import me.andreasmelone.glowingeyes.client.gui.widget.CursorSpaceWidget;
import me.andreasmelone.glowingeyes.client.util.GuiUtil;
import me.andreasmelone.glowingeyes.client.util.SkinUtil;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import me.andreasmelone.glowingeyes.common.util.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SkinPartSelectorScreen extends Screen {
    private static final int TEXTURE_WIDTH = TextureLocations.UI_BACKGROUND_BIG_WIDTH;
    private static final int TEXTURE_HEIGHT = TextureLocations.UI_BACKGROUND_BIG_HEIGHT;
    private static final int BUTTON_X = TEXTURE_HEIGHT - 25;

    private static final int PADDING_X = 25;
    private static final int PADDING_Y = 25;
    private static final int BUTTON_HEIGHT = 20;

    private static final int TEXTURE_PADDING_Y = 5;

    private static final int SKINBOX_PADDING_Y = 3;
    private static final int SKINBOX_PADDING_X = 3;

    private int maxTextureWidth;
    private int maxTextureHeight;

    private float textureX;
    private float textureY;

    private int guiLeft, guiTop;
    private int middle;

    private float factorX;
    private float factorY;
    private ISkinPart selected;
    private CompletableFuture<ISkinPart> future;

    private CursorSpaceWidget cursorSpaceWidget;

    private final int rows = 7;
    private final Screen parent;
    private final ResourceLocation skinTexture;
    private final Color overlayColor = new Color(255, 255, 255, 120);

    protected SkinPartSelectorScreen(Screen parent, ResourceLocation skinTexture, @NotNull ISkinPart selected) {
        super(Component.empty());
        this.parent = parent;
        this.skinTexture = skinTexture;
        this.selected = selected;
    }

    @Override
    protected void init() {
        super.init();
        if (this.parent != null) {
            this.parent.init(this.minecraft, this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight());
            this.parent.clearFocus();
        }
        this.guiLeft = (this.width - TEXTURE_WIDTH) / 2;
        this.guiTop = (this.height - TEXTURE_HEIGHT) / 2;

        int paddedWidth = TEXTURE_WIDTH - 2 * PADDING_X;
        int paddedHeight = TEXTURE_HEIGHT - 2 * PADDING_Y;

        float scaleX = (float) paddedWidth / 64.0f;
        float scaleY = (float) paddedHeight / (ISkinPart.getRowY(this.rows, this.selected.isSlim()) + 12);
        float scaleFactor = Math.min(scaleX, scaleY);

        this.maxTextureWidth = (int) (64 * scaleFactor);
        this.maxTextureHeight = (int) ((ISkinPart.getRowY(this.rows, this.selected.isSlim()) + 12) * scaleFactor);

        this.textureX = this.guiLeft + (float) (TEXTURE_WIDTH - this.maxTextureWidth) / 2;
        this.textureY = this.guiTop + (float) (TEXTURE_HEIGHT - this.maxTextureHeight) / 2 - TEXTURE_PADDING_Y;

        this.factorX = 64.0f / this.maxTextureWidth;
        this.factorY = (float) (ISkinPart.getRowY(this.rows, this.selected.isSlim()) + 12) / this.maxTextureHeight;

        this.middle = this.guiLeft + (TEXTURE_WIDTH / 2);

        this.addRenderableWidget(this.cursorSpaceWidget
                = new CursorSpaceWidget((int) this.textureX, (int) this.textureY, this.maxTextureWidth, this.maxTextureHeight, this::mouseMoved, this::mouseClicked));

        int width = (this.middle - this.guiLeft) - 7;
        int doneX = this.guiLeft + 5;
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.done"), (button) -> {
                    if (this.selected == null) return;
                    this.future.complete(this.selected);
                    this.minecraft.setScreen(this.parent);
                }).pos(doneX, this.guiTop + BUTTON_X).size(width, BUTTON_HEIGHT).build()
        );

        int cancelX = this.middle + 2;
        this.addRenderableWidget(
                Button.builder(Component.translatable("gui.cancel"), (button) -> {
                    this.future.complete(null);
                    this.minecraft.setScreen(this.parent);
                }).pos(cancelX, this.guiTop + BUTTON_X).size(width, BUTTON_HEIGHT).build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
        if (this.parent != null) {
            ctx.pose().pushPose();
            ctx.pose().translate(0, 0, -10000);
            this.parent.render(ctx, 0, 0, partialTicks);
            ctx.pose().popPose();
            GuiUtil.drawTransparentBlack(ctx);
        } else super.renderBackground(ctx, mouseX, mouseY, partialTicks);

        GuiUtil.drawBackground(
                ctx, TextureLocations.UI_BACKGROUND_BIG,
                this.guiLeft, this.guiTop,
                TEXTURE_WIDTH, TEXTURE_HEIGHT
        );

        ctx.drawCenteredString(
                this.minecraft.font,
                Component.translatable("gui.glowingeyes.skinpartselection"),
                this.middle, this.guiTop + 7,
                Color.WHITE.getRGB()
        );

        ctx.blit(
                TextureLocations.UI_SKINBOX,
                (int) (this.textureX - SKINBOX_PADDING_X), (int) (this.textureY - SKINBOX_PADDING_Y),
                0, 0,
                TextureLocations.UI_SKINBOX_WIDTH, TextureLocations.UI_SKINBOX_HEIGHT,
                256, 256
        );

        ctx.pose().pushPose();
        ctx.pose().translate(this.textureX, this.textureY, 0);
        ctx.blit(
                this.skinTexture,
                0, 0,
                this.maxTextureWidth, this.maxTextureHeight,
                0, 0,
                64, ISkinPart.getRowY(this.rows, this.selected.isSlim()) + 12,
                64, 64
        );
        ctx.pose().popPose();

        double cursorX = (this.cursorSpaceWidget.isUsed() ? this.cursorSpaceWidget.getCursorX() : mouseX);
        double cursorY = (this.cursorSpaceWidget.isUsed() ? this.cursorSpaceWidget.getCursorY() : mouseY);

        int textureMouseX = (int) ((cursorX - this.textureX) * this.factorX);
        int textureMouseY = (int) ((cursorY - this.textureY) * this.factorY);

        if (this.selected != null) {
            ctx.pose().pushPose();
            ctx.pose().translate(this.textureX + (this.selected.getX() / this.factorX) - 0.25, this.textureY + (this.selected.getY() / this.factorY) - 0.25, 0);
            ctx.fill(
                    0,
                    0,
                    (int) ((this.selected.getSizeX()) / this.factorX),
                    (int) ((this.selected.getSizeY()) / this.factorY),
                    this.overlayColor.getRGB()
            );
            ctx.renderOutline(
                    0,
                    0,
                    (int) (this.selected.getSizeX() / this.factorY) + 1,
                    (int) (this.selected.getSizeY() / this.factorX) + 1,
                    Color.BLACK.getRGB()
            );
            ctx.pose().popPose();
        }

        if (textureMouseX >= 0 && textureMouseX <= 63 && textureMouseY >= 0 && textureMouseY <= 63) {
            ISkinPart part = ISkinPart.getFromCoordinates(textureMouseX, textureMouseY, this.selected.isSlim());
            if (part != null && part.containsData() && part.getRow() <= this.rows) {
                int x = part.getX();
                int y = part.getY();

                ctx.pose().pushPose();
                ctx.pose().translate(this.textureX, this.textureY, 0);
                ctx.pose().scale(1.0f / this.factorX, 1.0f / this.factorY, 1.0f);
                ctx.fill(
                        x,
                        y,
                        (x + part.getSizeX()),
                        (y + part.getSizeY()),
                        this.overlayColor.getRGB()
                );
                ctx.pose().popPose();

                ctx.renderTooltip(
                        this.minecraft.font,
                        Component.translatable(part.getTranslationKey()),
                        (int) cursorX, (int) cursorY
                );
            }
        }

        super.render(ctx, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int textureMouseX = (int) ((mouseX - this.textureX) * this.factorX);
        int textureMouseY = (int) ((mouseY - this.textureY) * this.factorY);
        ISkinPart part = ISkinPart.getFromCoordinates(textureMouseX, textureMouseY, this.selected.isSlim());
        if (part != null && part.containsData() && part.getRow() <= this.rows) {
            if (button == 0) this.selected = part;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics ctx, int mouseX, int mouseY, float partialTicks) {
    }

    @Override
    public void onClose() {
        super.onClose();
        if (this.parent != null) {
            Minecraft.getInstance().setScreen(this.parent);
        }
    }

    public static CompletableFuture<ISkinPart> create(Screen parent, ResourceLocation skin, ISkinPart selected) {
        CompletableFuture<ISkinPart> future = new CompletableFuture<>();
        SkinPartSelectorScreen screen = new SkinPartSelectorScreen(parent, skin, selected);
        screen.future = future;
        Minecraft.getInstance().setScreen(screen);
        return future;
    }

    public static CompletableFuture<ISkinPart> create(Screen parent, ResourceLocation skin) {
        return create(parent, skin, SkinUtil.isSlim() ? SlimSkinPart.HEAD_FRONT : ClassicSkinPart.HEAD_FRONT);
    }
}
