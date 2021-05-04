package io.github.cubecreator.ui.menu

import com.google.gson.annotations.SerializedName

final class MenuItem {

    String text
    @SerializedName("tooltip")
    String toolTip
    String accelerator
    String action
    @SerializedName("icon")
    String iconUrl
    List<MenuItem> menu
    @SerializedName("enable_preprocessor")
    boolean enablePreprocessor
    String name

    MenuItem() {
        menu = new ArrayList<>()
        enablePreprocessor = false
    }

    void addItem(String text, String action) {
        addItem(text, "", action)
    }

    void addItem(String text, String toolTip, String action) {
        addItem(text, toolTip, "", action)
    }

    void addItem(String text, String toolTip, String accelerator, String action) {
        addItem(text, toolTip, accelerator, "", action)
    }

    void addItem(String text, String toolTip, String accelerator, String iconUrl, String action) {
        MenuItem item = new MenuItem()
        item.text = text
        item.toolTip = toolTip
        item.iconUrl = iconUrl
        item.accelerator = accelerator
        item.action = action
        menu.add(item)
    }

}
