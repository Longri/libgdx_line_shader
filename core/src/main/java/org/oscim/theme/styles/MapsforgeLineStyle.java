package org.oscim.theme.styles;

import org.oscim.backend.canvas.Paint;

/**
 * Created by Longri on 26.04.18.
 */
public class MapsforgeLineStyle extends RenderStyle<LineStyle> {

    public MapsforgeLineStyle(String src, float dy, int symbol_width, int symbol_height, int symbol_percent, Scale scale,
                              Paint.Cap stroke_linecap, Paint.Join stroke_linejoin, int strokeColor, float stroke_width,
                              float[] strokeDasharray) {
        this.src = src;
        this.dy = dy;
        this.symbol_width = symbol_width;
        this.symbol_height = symbol_height;
        this.symbol_percent = symbol_percent;
        this.scale = scale;
        this.stroke_linecap = stroke_linecap;
        this.stroke_linejoin = stroke_linejoin;
        this.strokeColor = strokeColor;
        this.stroke_width = stroke_width;
        this.strokeDasharray = strokeDasharray;
    }

    public enum Scale {
        all, none, stroke
    }


    public final String src;
    public final int symbol_width;
    public final int symbol_height;
    public final int symbol_percent;
    public final float dy;
    public final Scale scale;
    public final Paint.Cap stroke_linecap;
    public final Paint.Join stroke_linejoin;
    public final int strokeColor;
    public final float stroke_width;
    public final float[] strokeDasharray;


    @Override
    public MapsforgeLineStyle current() {
        return (MapsforgeLineStyle) mCurrent;
    }
}
