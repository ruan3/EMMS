package com.esquel.epass.lib.flipview;

/**
 * 
 * @author hung
 * 
 */
public class OverFlipperFactory {

    static OverFlipper create(FlipView v, OverFlipMode mode) {
        switch (mode) {
        case GLOW:
            return new GlowOverFlipper(v);
        case RUBBER_BAND:
            return new RubberBandOverFlipper();
        }
        return null;
    }

}
