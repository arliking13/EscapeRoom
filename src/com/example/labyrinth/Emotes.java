package com.example.labyrinth;

import java.awt.FontMetrics;

/**
 * A central place for storing all the emoji frames used by the player and enemies,
 * plus utility methods for measuring widths.
 */
public class Emotes {

    // Player frames
    public static final String[] playerFramesLowHealth  = {"\uD83E\uDD75", "\uD83E\uDD2F"}; // 🥵, 🤯
    public static final String[] playerFramesNearEnemy  = {"\uD83D\uDE28", "\uD83D\uDE31"}; // 😨, 😱
    public static final String[] playerFramesHasKey     = {"\uD83D\uDE0E", "\uD83E\uDD11"}; // 🫡, 🤑
    public static final String[] playerFramesDefault    = {"\uD83D\uDE10", "\uD83D\uDE11"}; // 😐, 😑

    // Additional doom faces
    public static final String[] doomFacesLowHealth = {"\uD83D\uDE2E", "\uD83E\uDD22"}; // 😮, 🤢
    public static final String[] doomFacesNearEnemy = {"\uD83D\uDE21", "\uD83D\uDE2F"}; // 😡, 😯
    public static final String[] doomFacesHasKey    = {"\uD83D\uDE0F", "\uD83D\uDE08"}; // 😏, 😈
    public static final String[] doomFacesDefault   = {"\uD83D\uDE12", "\uD83D\uDE14"}; // 😒, 😔

    // Enemies
    public static final String[] enemyFramesIdle   = {"\uD83D\uDC80", "\uD83D\uDC7B"}; // 💀, 👾
    public static final String[] enemyFramesAttack = {"\u2620",       "\uD83D\uDC80"}; // ☠, 💀

    /**
     * Measure the widest possible emoji among all frames, to avoid clipping.
     */
    public static int measureMaxEmojiWidth(FontMetrics fm) {
        String[] all = {
            // player frames
            "\uD83E\uDD75","\uD83E\uDD2F","\uD83D\uDE28","\uD83D\uDE31","\uD83D\uDE0E","\uD83E\uDD11","\uD83D\uDE10","\uD83D\uDE11",
            // doom faces
            "\uD83D\uDE2E","\uD83E\uDD22","\uD83D\uDE21","\uD83D\uDE2F","\uD83D\uDE0F","\uD83D\uDE08","\uD83D\uDE12","\uD83D\uDE14",
            // enemies
            "\uD83D\uDC80","\uD83D\uDC7B","\u2620",
            // celebration frames
            "\uD83E\uDD29","\uD83C\uDF89" // 🤩, 🎉
        };
        int maxW = 0;
        for (String s : all) {
            int w = fm.stringWidth(s);
            if (w > maxW) maxW = w;
        }
        return maxW;
    }
}
