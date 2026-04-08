package net.replaceitem.mazeworld.screen.widget;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jspecify.annotations.Nullable;

public class IntegerSliderWidget extends AbstractSliderButton {
    
    protected final int min;
    protected final int max;
    protected final Component name;
    @Nullable
    private final UpdateCallback callback;

    public IntegerSliderWidget(int x, int y, int width, Component name, int value, int min, int max, @Nullable UpdateCallback callback) {
        super(x, y, width, 20, name, value);
        this.min = min;
        this.max = max;
        this.setIntegerValue(value);
        this.name = name;
        this.updateMessage();
        this.callback = callback;
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.empty().append(name).append(": " + getIntegerValue()));
    }

    @Override
    protected void applyValue() {
        if(callback == null) return;
        callback.onValueChange(this, this.getIntegerValue());
    }

    public double getPercentageValue() {
        return this.value;
    }

    protected int sliderToValue(double slider, double min, double max) {
        return (int) Math.round(this.value * (max-min) + min);
    }

    protected double valueToSlider(int value, double min, double max) {
        return Mth.clamp(((double)value-min)/(max-min), min, max);
    }

    public int getIntegerValue() {
        return sliderToValue(this.value, min, max);
    }

    public void setIntegerValue(int value) {
        this.value = valueToSlider(value, min, max);
    }

    public interface UpdateCallback {
        void onValueChange(IntegerSliderWidget integerSliderWidget, int value);
    }
}
