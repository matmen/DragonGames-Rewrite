package me.matmen.DragonGames.enums;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

class Messages {
    private static final String BUNDLE_NAME = "me.matmen.DragonGames.enums.messages";

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

    @Contract(pure = true)
    private Messages() {
    }

    @NotNull
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
}
