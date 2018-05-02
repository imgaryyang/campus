package com.gzzm.platform.commons.callback;

import net.cyan.commons.util.Provider;
import net.cyan.nest.annotation.Inject;

/**
 * @author camel
 * @date 2015/3/6
 */
public final class Callbacks
{
    @Inject
    private static Provider<CallbackService> callbackServiceProvider;

    private Callbacks()
    {
    }

    public static CallbackService getService()
    {
        return callbackServiceProvider.get();
    }

    public static void call(String js, String method, Callback callback, Object... args)
    {
        getService().call(js, method, callback, args);
    }

    public static String call(String js, String method, Object... args)
    {
        return getService().call(js, method, args);
    }

    public static void prompt(String message, String defaultValue, Callback callback)
    {
        call(null, "Cyan.prompt", callback, message, defaultValue);
    }

    public static String prompt(String message, String defaultValue)
    {
        return call(null, "Cyan.prompt", message, defaultValue);
    }

    public static void confirm(String message, Callback callback)
    {
        call(null, "Cyan.confirm", callback, message);
    }

    public static String confirm(String message)
    {
        return call("Cyan.confirm", message);
    }

    public static void message(String message, Callback callback)
    {
        call(null, "Cyan.message", callback, message);
    }

    public static void message(String message)
    {
        call(null, "Cyan.message", message);
    }

    public static void opinion(String text, Callback callback)
    {
        call("/platform/opinion/opinion.js", "System.Opinion.edit", callback, text);
    }

    public static String opinion(String text)
    {
        return call("/platform/opinion/opinion.js", "System.Opinion.edit", text);
    }

    public static void opinion_simple(String text, Callback callback)
    {
        call("/platform/opinion/opinion.js", "System.Opinion.edit_simple", callback, text);
    }

    public static String opinion_simple(String text)
    {
        return call("/platform/opinion/opinion.js", "System.Opinion.edit_simple", text);
    }
}
