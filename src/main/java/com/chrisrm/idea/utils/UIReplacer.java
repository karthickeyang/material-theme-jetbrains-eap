package com.chrisrm.idea.utils;

import com.intellij.util.ui.UIUtil;

import javax.swing.*;
import java.lang.reflect.Field;

public class UIReplacer {
    public static void patchUI() {
        try {
            Patcher.patchTables();
            Patcher.patchStatusBar();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class Patcher {
        static void patchTables() throws Exception {
            StaticPatcher.setFinalStatic(UIUtil.class, "DECORATED_ROW_BG_COLOR", UIManager.get("Table.stripedBackground"));
        }

        static void patchStatusBar() throws Exception {
            //        StaticPatcher.setFinalStatic(StatusBarUI.);
            Class<?> clazz = Class.forName("com.intellij.openapi.wm.impl.status.StatusBarUI$BackgroundPainter");
            Field border_top_color = clazz.getDeclaredField("BORDER_TOP_COLOR");
            Field border2_top_color = clazz.getDeclaredField("BORDER2_TOP_COLOR");
            Field border_bottom_color = clazz.getDeclaredField("BORDER_BOTTOM_COLOR");

            StaticPatcher.setFinalStatic(border_top_color, UIManager.getColor("StatusBar.topColor").brighter().brighter());
            StaticPatcher.setFinalStatic(border2_top_color, UIManager.getColor("StatusBar.topColor2"));
            StaticPatcher.setFinalStatic(border_bottom_color, UIManager.getColor("StatusBar.bottomColor"));
        }
    }


}
