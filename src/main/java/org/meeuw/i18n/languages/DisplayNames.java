package org.meeuw.i18n.languages;

import java.util.stream.Stream;

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
