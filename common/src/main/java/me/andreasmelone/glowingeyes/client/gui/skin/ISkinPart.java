package me.andreasmelone.glowingeyes.client.gui.skin;

import java.util.List;
import java.util.Map;

public interface ISkinPart {
    int getRow();

    boolean containsData();

    int getSizeX();

    int getSizeY();

    int getX();

    int getY();

    boolean isSlim();

    String getTranslationKey();

    static int getRowY(int row, boolean isSlim) {
        if (isSlim) return SlimSkinPart.getRowY(row);
        return ClassicSkinPart.getRowY(row);
    }

    static List<? extends ISkinPart> getRow(int row, boolean isSlim) {
        if (isSlim) return SlimSkinPart.getRow(row);
        return ClassicSkinPart.getRow(row);
    }

    static Map<Integer, List<? extends ISkinPart>> getRows(boolean isSlim) {
        if (isSlim) return SlimSkinPart.getRows();
        return ClassicSkinPart.getRows();
    }

    static ISkinPart getFromCoordinates(int x, int y, boolean isSlim) {
        if (isSlim) return SlimSkinPart.getFromCoordinates(x, y);
        return ClassicSkinPart.getFromCoordinates(x, y);
    }

    static ISkinPart getPart(ClassicSkinPart part, boolean isSlim) {
        if (!isSlim) return part;
        return SlimSkinPart.valueOf(part.name());
    }
}
