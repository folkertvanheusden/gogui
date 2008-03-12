// I18n.java

package net.sf.gogui.gogui;

import java.util.Locale;
import java.util.ResourceBundle;

/** Internationalization for this package. */
class I18n
{
    public static String i18n(String key)
    {
        return m_bundle.getString(key);
    }

    private static ResourceBundle m_bundle =
        ResourceBundle.getBundle("net.sf.gogui.gogui.text",
                                 Locale.getDefault());

    /** Make constructor unavailable; class is for namespace only. */
    private I18n()
    {
    }
}