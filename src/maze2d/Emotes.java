package maze2d;

import java.awt.FontMetrics;

public class Emotes {
    public static final String[] playerFramesLowHealth  = {"\uD83E\uDD75", "\uD83E\uDD2F"};
    public static final String[] playerFramesNearEnemy  = {"\uD83D\uDE28", "\uD83D\uDE31"};
    public static final String[] playerFramesHasKey     = {"\uD83D\uDE0E", "\uD83E\uDD11"};
    public static final String[] playerFramesDefault    = {"\uD83D\uDE10", "\uD83D\uDE11"};

    public static final String[] doomFacesLowHealth = {"\uD83D\uDE2E", "\uD83E\uDD22"};
    public static final String[] doomFacesNearEnemy = {"\uD83D\uDE21", "\uD83D\uDE2F"};
    public static final String[] doomFacesHasKey    = {"\uD83D\uDE0F", "\uD83D\uDE08"};
    public static final String[] doomFacesDefault   = {"\uD83D\uDE12", "\uD83D\uDE14"};

    public static final String[] enemyFramesIdle   = {"\uD83D\uDC80", "\uD83D\uDC7B"};
    public static final String[] enemyFramesAttack = {"\u2620",       "\uD83D\uDC80"};

    public static int measureMaxEmojiWidth(FontMetrics fm) {
        String[] all = {
            "\uD83E\uDD75","\uD83E\uDD2F","\uD83D\uDE28","\uD83D\uDE31","\uD83D\uDE0E","\uD83E\uDD11","\uD83D\uDE10","\uD83D\uDE11",
            "\uD83D\uDE2E","\uD83E\uDD22","\uD83D\uDE21","\uD83D\uDE2F","\uD83D\uDE0F","\uD83D\uDE08","\uD83D\uDE12","\uD83D\uDE14",
            "\uD83D\uDC80","\uD83D\uDC7B","\u2620",
            "\uD83E\uDD29","\uD83C\uDF89"
        };
        int maxW = 0;
        for (String s : all) {
            int w = fm.stringWidth(s);
            if (w > maxW) maxW = w;
        }
        return maxW;
    }
}