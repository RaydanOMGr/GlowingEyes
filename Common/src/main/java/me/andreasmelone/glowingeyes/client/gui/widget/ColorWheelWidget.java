package me.andreasmelone.glowingeyes.client.gui.widget;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.client.util.TextureLocations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ColorWheelWidget extends AbstractWidget implements Widget, GuiEventListener {
    private static final float BRIGHTNESS = 1.0f;
    private static final int WHEEL_RADIUS = 256;
    private static final float SCALE = (WHEEL_RADIUS / 100.0f) * 2;
    private static ResourceLocation colorWheel;

    private Color selectedColor;
    private final int radius;
    private final Point circleMid;
    private final List<Consumer<ColorWheelWidget>> onChangeListeners = new ArrayList<>();

    public ColorWheelWidget(int posX, int posY, int diameter, Color color) {
        super(posX, posY, diameter, diameter, Component.empty());
        this.setSelectedColor(color);
        this.radius = diameter / 2;
        this.circleMid = new Point(
                x + (width / 2),
                y + (height / 2)
        );
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderTexture(0, getColorWheel());
        GuiComponent.blit(
                poseStack,
                x, y,
                0, 0,
                width, height,
                width, height
        );

        RenderSystem.setShaderTexture(0, TextureLocations.CURSOR);
        blit(
                poseStack,
                getCursorX(), getCursorY(),
                0, 0,
                8, 8,
                8, 8
        );
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(isInCircle(new Point((int) mouseX, (int) mouseY))) return this.mouseDragged(mouseX, mouseY, button, 0, 0);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        Point mouse = new Point((int) mouseX, (int) mouseY);
        if (isInCircle(mouse)) {
            this.setCursorX((int) mouseX);
            this.setCursorY((int) mouseY);
        } else {
            Point between = findBorderPoint(mouse);
            this.setCursorX(between.x);
            this.setCursorY(between.y);
        }
        triggerChange();

        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    public int getCursorX() {
        return getPointFromColor(selectedColor.getRGB()).x;
    }

    public void setCursorX(int cursorX) {
        this.selectedColor = getColorAt(cursorX, this.getCursorY());
    }

    public int getCursorY() {
        return getPointFromColor(selectedColor.getRGB()).y;
    }

    public void setCursorY(int cursorY) {
        this.selectedColor = getColorAt(this.getCursorX(), cursorY);
    }

    public Color getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(Color selectedColor) {
        this.selectedColor = selectedColor;
    }

    public void onChange(Consumer<ColorWheelWidget> listener) {
        this.onChangeListeners.add(listener);
    }

    private Point findBorderPoint(Point outside) {
        float distanceX = outside.x - circleMid.x;
        float distanceY = outside.y - circleMid.y;
        float angle = (float) Math.atan2(distanceY, distanceX); // it's in radians smh

        return new Point(
                (int) (circleMid.x + radius * Math.cos(angle)),
                (int) (circleMid.y + radius * Math.sin(angle))
        );
    }

    private boolean isInCircle(Point point) {
        double distanceX = point.x - circleMid.x;
        double distanceY = point.y - circleMid.y;

        return (distanceX * distanceX) + (distanceY * distanceY) <= radius * radius;
    }

    public void triggerChange() {
        this.onChangeListeners.forEach((l) -> l.accept(this));
    }

    private ResourceLocation getColorWheel() {
        if (colorWheel == null) {
            NativeImage image = getNativeImage();
            colorWheel = Minecraft.getInstance().getTextureManager().register(
                    GlowingEyes.MOD_ID + "_color_wheel",
                    new DynamicTexture(image)
            );
        }
        return colorWheel;
    }

    private void resetColorWheel() {
        Minecraft.getInstance().getTextureManager().release(colorWheel);
        colorWheel = null;
    }

    @NotNull
    private NativeImage getNativeImage() {
        NativeImage nativeImage = new NativeImage(WHEEL_RADIUS * 2, WHEEL_RADIUS * 2, true);
        for (int x = 0; x < WHEEL_RADIUS * 2; x++) {
            for (int y = 0; y < WHEEL_RADIUS * 2; y++) {
                int dx = x - WHEEL_RADIUS;
                int dy = y - WHEEL_RADIUS;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                if (distance < WHEEL_RADIUS) {
                    // use the getColorAt method to get the color at the current position
                    Color color = getColorAt(x, y);
                    nativeImage.setPixelRGBA(x, y, new Color(color.getBlue(), color.getGreen(), color.getRed()).getRGB());
                } else {
                    nativeImage.setPixelRGBA(x, y, 0x000000FF);
                }
            }
        }

        return nativeImage;
    }

    private Color getColorAt(int x, int y) {
        int dx = x - WHEEL_RADIUS;
        int dy = y - WHEEL_RADIUS;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance < WHEEL_RADIUS) {
            float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
            if (angle < 0) {
                angle += 360;
            }
            float saturation = distance / WHEEL_RADIUS;

            return new Color(Color.HSBtoRGB(angle / 360, saturation, BRIGHTNESS));
        } else {
            return null;
        }
    }

    private Point getPointFromColor(int color) {
        float[] hsv = new float[3];
        Color.RGBtoHSB(
                (color >> 16) & 0xFF,
                (color >> 8) & 0xFF,
                color & 0xFF,
                hsv
        );
        float angle = hsv[0] * 360;
        float saturation = hsv[1];
        float value = hsv[2];
        float distance = saturation * WHEEL_RADIUS;
        int x = (int) (Math.cos(Math.toRadians(angle)) * distance + WHEEL_RADIUS);
        int y = (int) (Math.sin(Math.toRadians(angle)) * distance + WHEEL_RADIUS);

        return new Point(x, y);
    }

    public static int getWheelRadius() {
        return WHEEL_RADIUS;
    }

    public static float getScale() {
        return SCALE;
    }

    @Override
    public void updateNarration(NarrationElementOutput out) {
    }
}
