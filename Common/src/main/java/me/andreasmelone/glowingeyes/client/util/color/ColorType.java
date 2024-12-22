package me.andreasmelone.glowingeyes.client.util.color;

import java.awt.*;
import java.util.function.BiFunction;
import java.util.function.Function;

public enum ColorType {
    RED(
            color -> String.valueOf(color.getRed()),
            (color, value) -> new Color(Math.max(0, Math.min(255, value)), color.getGreen(), color.getBlue()),
            string -> {
                int val = Integer.parseInt(string);
                return Math.max(0, Math.min(255, val)); // Clamp between 0-255
            }
    ),
    GREEN(
            color -> String.valueOf(color.getGreen()),
            (color, value) -> new Color(color.getRed(), Math.max(0, Math.min(255, value)), color.getBlue()),
            string -> {
                int val = Integer.parseInt(string);
                return Math.max(0, Math.min(255, val));
            }
    ),
    BLUE(
            color -> String.valueOf(color.getBlue()),
            (color, value) -> new Color(color.getRed(), color.getGreen(), Math.max(0, Math.min(255, value))),
            string -> {
                int val = Integer.parseInt(string);
                return Math.max(0, Math.min(255, val));
            }
    ),
    HEX(
            color -> String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()),
            (color, hexValue) -> {
                int r = (hexValue >> 16) & 0xFF;
                int g = (hexValue >> 8) & 0xFF;
                int b = hexValue & 0xFF;
                return new Color(r, g, b);
            },
            string -> {
                // Handle HEX input
                if(string.startsWith("#")) string = string.substring(1);
                if (string.length() == 6) {
                    return Integer.parseInt(string, 16); // Parsing directly to int
                } else {
                    throw new NumberFormatException("Invalid HEX format");
                }
            }
    );

    private final Function<Color, String> getter;
    private final BiFunction<Color, Integer, Color> updater;
    private final Function<String, Integer> parser; // New parser function

    ColorType(Function<Color, String> getter, BiFunction<Color, Integer, Color> updater, Function<String, Integer> parser) {
        this.getter = getter;
        this.updater = updater;
        this.parser = parser;
    }

    public String get(Color color) {
        return getter.apply(color);
    }

    public Color parseAndUpdate(Color color, String input) {
        int parsedValue;
        try {
            parsedValue = parse(input);
            return update(color, parsedValue);
        } catch (NumberFormatException e) {
            return color;
        }
    }

    public Color update(Color color, int value) {
        return updater.apply(color, value);
    }

    public int parse(String input) throws NumberFormatException {
        return parser.apply(input);
    }
}