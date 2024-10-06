package org.meeuw.i18n.languages;

import java.util.stream.Stream;

/**
 * All {@link ISO_639_Code#refName()} as a {@link java.util.ResourceBundle}.
 * The default tab-files of ISO-636 provide the names of all language in english.
 * This is the fallback for if a {@link java.util.PropertyResourceBundle} for given {@link java.util.Locale} is missing.
 * @since 3.5
 */
public class DisplayNames extends java.util.ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return Stream.concat(
            Stream.of(ISO_639_1_Code.values()),
            ISO_639_3_Code.stream())
            .map(i -> new Object[] {i.code(), i.refName()})
            .toArray(i -> new Object[i][2]);

    }
}
