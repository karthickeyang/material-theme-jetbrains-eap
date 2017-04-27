package com.chrisrm.idea.utils;

import com.chrisrm.idea.MTLaf;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.ColorUtil;
import com.intellij.util.ui.JBDimension;
import com.intellij.util.ui.JBInsets;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.IconUIResource;
import java.awt.*;

/**
 * This is because Jetbrains is greedy and make such methods private :(
 */
public class PropertiesParser {
  public static final Object SYSTEM = new Object();

  public static Insets parseInsets(String value) {
    final java.util.List<String> numbers = StringUtil.split(value, ",");
    return new JBInsets(Integer.parseInt(numbers.get(0)),
        Integer.parseInt(numbers.get(1)),
        Integer.parseInt(numbers.get(2)),
        Integer.parseInt(numbers.get(3))).asUIResource();
  }

  @SuppressWarnings("UseJBColor")
  public static Color parseColor(String value) {
    if (value != null && value.length() == 8) {
      final Color color = ColorUtil.fromHex(value.substring(0, 6));
      if (color != null) {
        try {
          int alpha = Integer.parseInt(value.substring(6, 8), 16);
          return new ColorUIResource(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
        }
        catch (Exception ignore) {
        }
      }
      return null;
    }
    return ColorUtil.fromHex(value, null);
  }

  public static Integer getInteger(String value) {
    try {
      return Integer.parseInt(value);
    }
    catch (NumberFormatException e) {
      return null;
    }
  }

  public static Dimension parseSize(String value) {
    final java.util.List<String> numbers = StringUtil.split(value, ",");
    return new JBDimension(Integer.parseInt(numbers.get(0)), Integer.parseInt(numbers.get(1))).asUIResource();
  }

  public static Object parseValue(String key, @NotNull String value) {
    if ("null".equals(value)) {
      return null;
    }

    if ("system".equals(value)) {
      return SYSTEM;
    }

    if (key.endsWith("Insets")) {
      return parseInsets(value);
    } else if (key.endsWith("Font")) {
      try {
        return parseFont(value);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    } else if (key.endsWith("Border") || key.endsWith("border")) {

      try {
        if (StringUtil.split(value, ",").size() == 4) {
          return new BorderUIResource.EmptyBorderUIResource(parseInsets(value));
        } else {
          return Class.forName(value).newInstance();
        }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    } else if (key.endsWith("Size")) {
      return parseSize(value);
    } else {
      final Color color = parseColor(value);
      final Integer invVal = getInteger(value);
      final Boolean boolVal = "true".equals(value) ? Boolean.TRUE : "false".equals(value) ? Boolean.FALSE : null;
      Icon icon = value.startsWith("AllIcons.") ? IconLoader.getIcon(value) : null;
      if (icon == null && value.endsWith(".png")) {
        icon = IconLoader.findIcon(value, MTLaf.class, true);
      }
      if (color != null) {
        return new ColorUIResource(color);
      } else if (invVal != null) {
        return invVal;
      } else if (icon != null) {
        return new IconUIResource(icon);
      } else if (boolVal != null) {
        return boolVal;
      }
    }
    return value;
  }

  @NotNull
  private static Font parseFont(String value) {
    java.util.List<String> split = StringUtil.split(value, ",");
    if (split.size() == 3) {
      return new FontUIResource(split.get(0), Integer.parseInt(split.get(1)), Integer.parseInt(split.get(2)));
    } else if (split.size() == 2) {
      return new FontUIResource("Dialog", Integer.parseInt(split.get(0)), Integer.parseInt(split.get(1)));
    } else if (split.size() == 1) {
      return new FontUIResource("Dialog", 1, Integer.parseInt(split.get(0)));
    } else {
      return null;
    }
  }
}
