package net.replaceitem.mazeworld.screen.widget;

import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.function.IntFunction;
import java.util.function.ToIntFunction;

public class MappedIntegerSliderWidget<T> extends IntegerSliderWidget {
    private final IntFunction<T> toValue;

    public MappedIntegerSliderWidget(int x, int y, int width, Component name, T value, int min, int max, ToIntFunction<T> toControl, IntFunction<T> toValue, @Nullable UpdateCallback<T> callback) {
        this.toValue = toValue;
        super(x, y, width, name, toControl.applyAsInt(value), min, max, (integerSliderWidget, value1) -> {
            if(callback != null) {
                callback.onValueChange(integerSliderWidget, toValue.apply(value1));
            }
        });
    }

    public T getMappedValue() {
        return toValue.apply(getIntegerValue());
    }

    public interface UpdateCallback<T> {
        void onValueChange(IntegerSliderWidget integerSliderWidget, T value);
    }
}
