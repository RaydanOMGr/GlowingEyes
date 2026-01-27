package me.andreasmelone.glowingeyes.client.util;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Util;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.resources.Identifier;

public class TextureLocations {
    public static final WidgetSprites BRUSH_BUTTON =
            GuiUtil.createSprites(GlowingEyes.MOD_ID, "brush/brush_button", "brush/brush_button_disabled", "brush/brush_button_highlighted");
    public static final Identifier BRUSH_COLOR_OVERLAY =
            Util.id(GlowingEyes.MOD_ID, "brush/brush_color_overlay");

    public static final WidgetSprites ERASER_BUTTON =
            GuiUtil.createSprites(GlowingEyes.MOD_ID, "eraser/eraser_button", "eraser/eraser_button_disabled", "eraser/eraser_button_highlighted");

    public static final WidgetSprites PIPETTE_BUTTON =
            GuiUtil.createSprites(GlowingEyes.MOD_ID, "pipette/pipette_button", "pipette/pipette_button_disabled", "pipette/pipette_button_highlighted");
    public static final Identifier PIPETTE_COLOR_OVERLAY =
            Util.id(GlowingEyes.MOD_ID, "pipette/pipette_color_overlay");

    public static final WidgetSprites COLOR_PICKER_BUTTON =
            GuiUtil.createSprites(GlowingEyes.MOD_ID, "color_picker/color_picker_button", "color_picker/color_picker_button_disabled", "color_picker/color_picker_button_highlighted");

    public static final WidgetSprites FILL_BUCKET_BUTTON =
            GuiUtil.createSprites(GlowingEyes.MOD_ID, "fill_bucket/fill_bucket_button", "fill_bucket/fill_bucket_button_disabled", "fill_bucket/fill_bucket_button_highlighted");
    public static final Identifier FILL_BUCKET_COLOR_OVERLAY =
            Util.id(GlowingEyes.MOD_ID, "fill_bucket/fill_bucket_color_overlay");

    public static final WidgetSprites PRESET_MENU_BUTTON =
            GuiUtil.createSprites(GlowingEyes.MOD_ID, "preset_menu/preset_menu_button", "preset_menu/preset_menu_button_highlighted");

    public static final WidgetSprites SKIN_PART_PICKER_BUTTON =
            GuiUtil.createSprites(GlowingEyes.MOD_ID, "skin_part_picker/skin_part_picker_button", "skin_part_picker/skin_part_picker_button_highlighted");

    public static final WidgetSprites RESET_BUTTON =
            GuiUtil.createSprites(GlowingEyes.MOD_ID, "reset/reset_button", "reset/reset_button_highlighted");

    public static final Identifier CURSOR = Util.id(GlowingEyes.MOD_ID, "textures/gui/cursor.png");
    public static final Identifier BRIGHTNESS_CURSOR = Util.id(GlowingEyes.MOD_ID, "textures/gui/brightness_cursor.png");

    // 176x222
    public static final Identifier UI_BACKGROUND = Util.id(GlowingEyes.MOD_ID, "textures/gui/background/background.png");
    public static final int UI_BACKGROUND_WIDTH = 176;
    public static final int UI_BACKGROUND_HEIGHT = 222;

    // 221x222
    public static final Identifier UI_BACKGROUND_BIG = Util.id(GlowingEyes.MOD_ID, "textures/gui/background/background_big.png");
    public static final int UI_BACKGROUND_BIG_WIDTH = 221;
    public static final int UI_BACKGROUND_BIG_HEIGHT = 222;

    // 256x222
    public static final Identifier UI_BACKGROUND_BROAD = Util.id(GlowingEyes.MOD_ID, "textures/gui/background/background_broad.png");
    public static final int UI_BACKGROUND_BROAD_WIDTH = 256;
    public static final int UI_BACKGROUND_BROAD_HEIGHT = 222;

    // 200x143
    public static final Identifier UI_BACKGROUND_SLIM = Util.id(GlowingEyes.MOD_ID, "textures/gui/background/background_slim.png");
    public static final int UI_BACKGROUND_SLIM_WIDTH = 200;
    public static final int UI_BACKGROUND_SLIM_HEIGHT = 118;

    // 252x143
    public static final Identifier UI_BACKGROUND_SLIM_LONG = Util.id(GlowingEyes.MOD_ID, "textures/gui/background/background_slim_long.png");
    public static final int UI_BACKGROUND_SLIM_LONG_WIDTH = 252;
    public static final int UI_BACKGROUND_SLIM_LONG_HEIGHT = 143;

    // 34x45
    public static final Identifier UI_PLAYERBOX = Util.id(GlowingEyes.MOD_ID, "textures/gui/playerbox.png");
    public static final int UI_PLAYERBOX_WIDTH = 34;
    public static final int UI_PLAYERBOX_HEIGHT = 45;

    public static final Identifier UI_PRESETBOX = Util.id(GlowingEyes.MOD_ID, "textures/gui/presetbox.png");
    public static final int UI_PRESETBOX_WIDTH = 147;
    public static final int UI_PRESETBOX_HEIGHT = 163;
    public static final Identifier UI_SKINBOX = Util.id(GlowingEyes.MOD_ID, "textures/gui/skinbox.png");
    public static final int UI_SKINBOX_WIDTH = 179;
    public static final int UI_SKINBOX_HEIGHT = 179;

    // 128x29
    public static final WidgetSprites BIG_BUTTON = GuiUtil.createSprites(
            GlowingEyes.MOD_ID,
            "big/big_button",
            "big/big_button_disabled",
            "big/big_button_highlighted",
            "big/big_button_highlighted_disabled"
    );
    public static final int BIG_BUTTON_WIDTH = 128;
    public static final int BIG_BUTTON_HEIGHT = 29;
}