package me.andreasmelone.glowingeyes.client.gui.skin;

import me.andreasmelone.glowingeyes.GlowingEyes;
import me.andreasmelone.glowingeyes.common.util.Point;

import java.util.*;

public enum SlimSkinPart implements ISkinPart {
    HEAD_FRESH_MOVES_1(0, true, 8, 8),
    HEAD_TOP(0, true, 8, 8),
    HEAD_BOTTOM(0, true, 8, 8),
    HEAD_FRESH_MOVES_2(0, true, 8, 8),
    HEAD_OVERLAY_FRESH_MOVES_3(0, true, 8, 8),
    HEAD_OVERLAY_TOP(0, true, 8, 8),
    HEAD_OVERLAY_BOTTOM(0, true, 8, 8),
    HEAD_OVERLAY_FRESH_MOVES_4(0, true, 8, 8),
    HEAD_LEFT(1, true, 8, 8),
    HEAD_FRONT(1, true, 8, 8),
    HEAD_BACK(1, true, 8, 8),
    HEAD_RIGHT(1, true, 8, 8),
    HEAD_OVERLAY_RIGHT(1, true, 8, 8),
    HEAD_OVERLAY_FRONT(1, true, 8, 8),
    HEAD_OVERLAY_LEFT(1, true, 8, 8),
    HEAD_OVERLAY_BACK(1, true, 8, 8),
    RIGHT_LEG_EMPTY_1(2, false, 4, 4),
    RIGHT_LEG_TOP(2, true, 4, 4),
    RIGHT_LEG_BOTTOM(2, true, 4, 4),
    RIGHT_LEG_EMPTY_2(2, false, 4, 4),
    TORSO_EMPTY_1(2, false, 4, 4),
    TORSO_TOP(2, true, 8, 4),
    TORSO_BOTTOM(2, true, 8, 4),
    TORSO_EMPTY_2(2, false, 4, 4),
    RIGHT_ARM_EMPTY_1(2, false, 4, 4),
    RIGHT_ARM_TOP(2, true, 3, 4),
    RIGHT_ARM_BOTTOM(2, true, 3, 4),
    RIGHT_LEG_RIGHT(3, true, 4, 12),
    RIGHT_LEG_FRONT(3, true, 4, 12),
    RIGHT_LEG_LEFT(3, true, 4, 12),
    RIGHT_LEG_BACK(3, true, 4, 12),
    TORSO_RIGHT(3, true, 4, 12),
    TORSO_FRONT(3, true, 8, 12),
    TORSO_LEFT(3, true, 4, 12),
    TORSO_BACK(3, true, 8, 12),
    RIGHT_ARM_RIGHT(3, true, 4, 12),
    RIGHT_ARM_FRONT(3, true, 3, 12),
    RIGHT_ARM_LEFT(3, true, 4, 12),
    RIGHT_ARM_BACK(3, true, 3, 12),
    RIGHT_LEG_OVERLAY_EMPTY_1(4, false, 4, 4),
    RIGHT_LEG_OVERLAY_TOP(4, true, 4, 4),
    RIGHT_LEG_OVERLAY_BOTTOM(4, true, 4, 4),
    RIGHT_LEG_OVERLAY_EMPTY_2(4, false, 4, 4),
    TORSO_OVERLAY_EMPTY_1(4, false, 4, 4),
    TORSO_OVERLAY_TOP(4, true, 8, 4),
    TORSO_OVERLAY_BOTTOM(4, true, 8, 4),
    TORSO_OVERLAY_EMPTY_2(4, false, 4, 4),
    RIGHT_ARM_OVERLAY_EMPTY_1(4, false, 4, 4),
    RIGHT_ARM_OVERLAY_TOP(4, true, 4, 4),
    RIGHT_ARM_OVERLAY_BOTTOM(4, true, 4, 4),
    RIGHT_LEG_OVERLAY_RIGHT(5, true, 4, 12),
    RIGHT_LEG_OVERLAY_FRONT(5, true, 4, 12),
    RIGHT_LEG_OVERLAY_LEFT(5, true, 4, 12),
    RIGHT_LEG_OVERLAY_BACK(5, true, 4, 12),
    TORSO_OVERLAY_RIGHT(5, true, 4, 12),
    TORSO_OVERLAY_FRONT(5, true, 8, 12),
    TORSO_OVERLAY_LEFT(5, true, 4, 12),
    TORSO_OVERLAY_BACK(5, true, 8, 12),
    RIGHT_ARM_OVERLAY_RIGHT(5, true, 4, 12),
    RIGHT_ARM_OVERLAY_FRONT(5, true, 4, 12),
    RIGHT_ARM_OVERLAY_LEFT(5, true, 4, 12),
    RIGHT_ARM_OVERLAY_BACK(5, true, 4, 12),
    LEFT_LEG_OVERLAY_EMPTY_1(6, false, 4, 4),
    LEFT_LEG_OVERLAY_TOP(6, true, 4, 4),
    LEFT_LEG_OVERLAY_BOTTOM(6, true, 4, 4),
    LEFT_LEG_OVERLAY_EMPTY_2(6, false, 4, 4),
    LEFT_LEG_EMPTY_1(6, false, 4, 4),
    LEFT_LEG_TOP(6, true, 4, 4),
    LEFT_LEG_BOTTOM(6, true, 4, 4),
    LEFT_LEG_EMPTY_2(6, false, 4, 4),
    LEFT_ARM_EMPTY_1(6, false, 4, 4),
    LEFT_ARM_TOP(6, true, 3, 4),
    LEFT_ARM_BOTTOM(6, true, 3, 4),
    LEFT_ARM_EMPTY_2(6, false, 4, 4),
    LEFT_ARM_EMPTY_3(6, false, 2, 4),
    LEFT_ARM_OVERLAY_EMPTY_1(6, false, 4, 4),
    LEFT_ARM_OVERLAY_TOP(6, true, 3, 4),
    LEFT_ARM_OVERLAY_BOTTOM(6, true, 3, 4),
    LEFT_ARM_OVERLAY_EMPTY_2(6, false, 4, 4),
    LEFT_LEG_OVERLAY_RIGHT(7, true, 4, 12),
    LEFT_LEG_OVERLAY_FRONT(7, true, 4, 12),
    LEFT_LEG_OVERLAY_LEFT(7, true, 4, 12),
    LEFT_LEG_OVERLAY_BACK(7, true, 4, 12),
    LEFT_LEG_RIGHT(7, true, 4, 12),
    LEFT_LEG_FRONT(7, true, 4, 12),
    LEFT_LEG_LEFT(7, true, 4, 12),
    LEFT_LEG_BACK(7, true, 4, 12),
    LEFT_ARM_RIGHT(7, true, 4, 12),
    LEFT_ARM_FRONT(7, true, 3, 12),
    LEFT_ARM_LEFT(7, true, 4, 12),
    LEFT_ARM_BACK(7, true, 3, 12),
    LEFT_ARM_EMPTY_4(7, false, 2, 12),
    LEFT_ARM_OVERLAY_RIGHT(7, true, 4, 12),
    LEFT_ARM_OVERLAY_FRONT(7, true, 3, 12),
    LEFT_ARM_OVERLAY_LEFT(7, true, 4, 12),
    LEFT_ARM_OVERLAY_BACK(7, true, 3, 12);

    private static final Map<Point, SlimSkinPart> POINT_TO_SKIN_PART_MAP = new HashMap<>();
    private static final Map<Integer, List<SlimSkinPart>> ROW_MAP = new HashMap<>();
    private static final Map<Integer, Integer> ROW_Y_MAP = new HashMap<>();

    private final int row;
    private final boolean containsData;
    private final int sizeX;
    private final int sizeY;

    private int x = -1;
    private int y = -1;

    SlimSkinPart(int row, boolean containsData, int sizeX, int sizeY) {
        this.row = row;
        this.containsData = containsData;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public boolean containsData() {
        return containsData;
    }

    @Override
    public int getX() {
        if (x == -1) {
            x = 0;
            for (SlimSkinPart part : getRow(row)) {
                if (part == this) break;
                x = x + part.getSizeX();
            }
        }
        return x;
    }

    @Override
    public int getY() {
        if (y == -1) {
            y = getRowY(this.getRow());
        }
        return y;
    }

    @Override
    public boolean isSlim() {
        return true;
    }

    @Override
    public int getSizeX() {
        return sizeX;
    }

    @Override
    public int getSizeY() {
        return sizeY;
    }

    public String getTranslationKey() {
        return "gui." + GlowingEyes.MOD_ID + "." + this.toString().toLowerCase(Locale.ROOT).replace("_", "");
    }

    public static int getRowY(int row) {
        return ROW_Y_MAP.getOrDefault(row, -1);
    }

    public static List<SlimSkinPart> getRow(int row) {
        return ROW_MAP.getOrDefault(row, Collections.emptyList());
    }

    public static Map<Integer, List<? extends ISkinPart>> getRows() {
        Map<Integer, List<? extends ISkinPart>> rows = new HashMap<>();
        ROW_MAP.forEach((key, value) -> {
            rows.put(key, new ArrayList<>(value));
        });
        return rows;
    }

    public static SlimSkinPart getFromCoordinates(int x, int y) {
        if (x < 0 || y < 0 || y > 63 || x > 63)
            return null;
        return POINT_TO_SKIN_PART_MAP.get(new Point(x, y));
    }

    static {
        for (SlimSkinPart part : values()) {
            ROW_MAP.computeIfAbsent(part.row, k -> new ArrayList<>()).add(part);
        }

        int currentY = 0;
        int previousMaxHeight = 0;
        for (int row = 0; row < 8; row++) {
            int currentX = 0;
            int maxHeightInRow = 0;

            for (SlimSkinPart part : getRow(row)) {
                for (int dx = 0; dx < part.getSizeX(); dx++) {
                    for (int dy = 0; dy < part.getSizeY(); dy++) {
                        POINT_TO_SKIN_PART_MAP.put(new Point(currentX + dx, currentY + dy), part);
                    }
                }

                currentX += part.getSizeX();
                maxHeightInRow = Math.max(maxHeightInRow, part.getSizeY());
            }
            ROW_Y_MAP.put(row, previousMaxHeight + ROW_Y_MAP.getOrDefault(row - 1, 0));

            previousMaxHeight = maxHeightInRow;
            currentY += maxHeightInRow;
        }
    }
}
