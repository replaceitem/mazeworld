package mazeworld.screen.widget;

import net.minecraft.text.Text;

public class LogarithmicIntegerSliderWidget extends IntegerSliderWidget {

    private final double base;
    private final double base_1;
    
    public LogarithmicIntegerSliderWidget(int x, int y, int width, Text text, int value, int min, int max, UpdateCallback callback) {
        super(x, y, width, text, 0, min, max, callback);
        this.base = ((double)max)/10;
        this.base_1 = this.base-1;
        this.setIntegerValue(value);
        this.updateMessage();
    }

    protected int sliderToValue(double slider, double min, double max) {
        double span = max-min;
        return (int) Math.round((Math.pow(base, slider)-1D)*span/base_1 + min);
    }

    protected double valueToSlider(int value, double min, double max) {
        double span = max-min;
        return logBase(base, ((base_1*(value-min))/span)+1D);
    }
    
    private static double logBase(double base, double val) {
        return Math.log(val)/Math.log(base);
    }
}
