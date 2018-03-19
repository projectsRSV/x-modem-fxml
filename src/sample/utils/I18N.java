package sample.utils;


import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {
    private static final ObjectProperty<Locale> localeProperty;

    static {
        localeProperty = new SimpleObjectProperty<>(getDefaultLocale());
        localeProperty.addListener((observable, oldValue, newValue) -> Locale.setDefault(newValue));
    }

    public static Locale getLocaleProperty() {
        return localeProperty.get();
    }

    public static ObjectProperty<Locale> localePropertyProperty() {
        return localeProperty;
    }

    public static StringBinding createStringBinding(final String key, Object... args) {
        return Bindings.createStringBinding(() -> get(key, args), localeProperty);
    }

    public static void setLocale(Locale locale) {
        localePropertyProperty().set(locale);
        Locale.setDefault(locale);
    }

    public static List<Locale> getSupportedLocales() {
        return Arrays.asList(Locale.ENGLISH, new Locale("ru", "RU"));
    }

    public static String get(final String key, final Object... args) {
        ResourceBundle bundle = ResourceBundle.getBundle("message", getLocaleProperty(), CsControl.Cp1251);
//        ResourceBundle bundle = ResourceBundle.getBundle("message", getLocale());
        return MessageFormat.format(bundle.getString(key), args);
    }


    private static Locale getDefaultLocale() {
        Locale systemLocale = Locale.getDefault();
        return getSupportedLocales().contains(systemLocale) ? systemLocale : Locale.ENGLISH;
    }
}
