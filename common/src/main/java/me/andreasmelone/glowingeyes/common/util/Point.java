package me.andreasmelone.glowingeyes.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.io.Serializable;
import java.util.Objects;

public class Point implements Serializable, Cloneable {
    public static final Codec<Point> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("x").forGetter(Point::getX),
            Codec.INT.fieldOf("y").forGetter(Point::getY)
    ).apply(instance, Point::new));

    public static final Codec<Point> CODEC_STRING = Codec.STRING.xmap(
            Point::deserializePointString,
            Point::serializePointString
    );

    public static final StreamCodec<ByteBuf, Point> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            Point::getX,
            ByteBufCodecs.INT,
            Point::getY,
            Point::new
    );

    private int x;
    private int y;

    public Point() {
        this(0, 0);
    }

    public Point(Point p) {
        this(p.x, p.y);
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void translate(int dx, int dy) {
        this.x += dx;
        this.y += dy;
    }

    private static Point deserializePointString(String point) {
        String[] parts = point.split(",");
        int x = Integer.parseInt(parts[0]);
        int y = Integer.parseInt(parts[1]);
        return new Point(x, y);
    }

    private static String serializePointString(Point point) {
        return point.x + "," + point.y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || this.getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return this.x == point.x && this.y == point.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x, this.y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + this.x +
                ", y=" + this.y +
                '}';
    }

    @Override
    public Point clone() {
        try {
            return (Point) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
