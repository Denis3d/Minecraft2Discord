package ml.denisd3d.mc2discord.core;

import discord4j.common.util.TokenUtil;
import discord4j.gateway.GatewayObserver;
import discord4j.rest.util.PermissionSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class M2DUtils {
    // Inspired from methods by Tomer Godinger.
    private static final int NOT_FOUND = -1;
    private static final String CODEBLOCKS_TOKEN = "```";

    public static final List<String> available_lang = Arrays.asList("en_us");

    public static boolean isTokenValid(String token) {
        try {
            TokenUtil.getSelfId(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean canHandleEvent() {
        return Mc2Discord.INSTANCE != null && Mc2Discord.INSTANCE.client != null && Mc2Discord.INSTANCE.getState() == GatewayObserver.CONNECTED && !Mc2Discord.INSTANCE.is_stopping;
    }

    private static int lastIndexOfRegex(String str, String toFind) {
        Pattern pattern = Pattern.compile(toFind);
        Matcher matcher = pattern.matcher(str);

        // Default to the NOT_FOUND constant
        int lastIndex = NOT_FOUND;

        // Search for the given pattern
        while (matcher.find()) {
            lastIndex = matcher.start();
        }

        return lastIndex;
    }

    public static int lastIndexOfRegex(String str, String toFind, int fromIndex) {
        // Limit the search by searching on a suitable substring
        return lastIndexOfRegex(str.substring(0, fromIndex), toFind);
    }

    public static List<String> breakStringToLines(String str, int maxLength, boolean withCodeBlocks) {
        List<String> result = new ArrayList<>();
        if (withCodeBlocks) maxLength -= 6;
        while (str.length() > maxLength) {
            int breakingIndex = getBreakingIndex(str, maxLength);

            // Append each prepared line to the builder
            result.add(withCodeBlocks ? CODEBLOCKS_TOKEN + str.substring(0, breakingIndex + 1) + CODEBLOCKS_TOKEN : str.substring(0, breakingIndex + 1));

            // And start the next line
            str = str.substring(breakingIndex + 1);
        }

        // Check if there are any residual characters left
        if (str.length() > 0) {
            result.add(withCodeBlocks ? CODEBLOCKS_TOKEN + str + CODEBLOCKS_TOKEN : str);
        }

        // Return the resulting string
        return result;
    }

    public static int getBreakingIndex(String str, int maxLength) {
        // Attempt to break on new line first,
        int breakingIndex = lastIndexOfRegex(str, "\n", maxLength);

        // Then on words,
        if (breakingIndex == NOT_FOUND) breakingIndex = lastIndexOfRegex(str, "\\s", maxLength);

        // Then on other non-alphanumeric characters,
        if (breakingIndex == NOT_FOUND) breakingIndex = lastIndexOfRegex(str, "[^a-zA-Z0-9]", maxLength);

        // And if all else fails, break in the middle of the word
        if (breakingIndex == NOT_FOUND) breakingIndex = maxLength;

        return breakingIndex;
    }

    public static int getSafeBreakingIndex(String str, int maxLength) {
        if (str.length() > maxLength) {
            return getBreakingIndex(str, maxLength);
        } else {
            return str.length();
        }
    }
}
