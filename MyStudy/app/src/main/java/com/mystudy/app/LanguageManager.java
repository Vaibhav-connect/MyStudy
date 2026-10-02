package com.mystudy.app;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

public class LanguageManager {

    private static final String PREF_NAME = "MyStudySettings";
    private static final String LANGUAGE_KEY = "appLanguage";

    public static void saveLanguage(
            Context context,
            String language
    ) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        preferences.edit()
                .putString(LANGUAGE_KEY, language)
                .apply();

        applyLanguage(language);
    }

    public static String getLanguage(Context context) {

        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );

        return preferences.getString(
                LANGUAGE_KEY,
                "English"
        );
    }

    public static void applySavedLanguage(Context context) {

        applyLanguage(
                getLanguage(context)
        );
    }

    private static void applyLanguage(
            String language
    ) {

        String languageTag;

        if ("Marathi".equalsIgnoreCase(language)) {

            languageTag = "mr";

        } else if ("Hindi".equalsIgnoreCase(language)) {

            languageTag = "hi";

        } else {

            languageTag = "en";
        }

        LocaleListCompat appLocale =
                LocaleListCompat.forLanguageTags(
                        languageTag
                );

        AppCompatDelegate.setApplicationLocales(
                appLocale
        );
    }
}
