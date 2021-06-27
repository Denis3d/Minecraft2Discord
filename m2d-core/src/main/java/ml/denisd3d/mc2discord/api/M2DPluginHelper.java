package ml.denisd3d.mc2discord.api;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class M2DPluginHelper {
    public static final List<IM2DPlugin> plugins = new ArrayList<>();

    public static void register(IM2DPlugin plugin) {
        plugins.add(plugin);
    }

    public static boolean execute(Predicate<? super IM2DPlugin> predicate) {
        if (plugins.isEmpty())
            return false;
        else
            return plugins.stream().anyMatch(predicate);
    }
}