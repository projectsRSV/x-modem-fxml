package sample.utils;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ProgressProperty {
    private DoubleProperty doubleProperty;

    public double getDouble() {
        return getProperty().get();
    }

    public DoubleProperty getProperty() {
        if (doubleProperty == null) {
            return doubleProperty = new SimpleDoubleProperty();
        }
        return doubleProperty;
    }

    public void setDouble(double doubleProperty) {
        this.getProperty().set(doubleProperty);
    }

}
