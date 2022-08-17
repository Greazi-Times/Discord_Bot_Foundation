package com.greazi.discordbotfoundation.handlers.modals;

//import net.dv8tion.jda.api.entities.Emoji;
//import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
//import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
//
//import java.util.List;
//
// Disabled because select menus are not compatible with modals
//public class SimpleSelect {
//
//    private final SelectMenu.Builder builder;
//
//    public SimpleSelect(String id){
//        builder = SelectMenu.create(id);
//    }
//
//    public void setDisabled(){
//        builder.setDisabled(true);
//    }
//
//    public void setPlaceholder(String placeholder){
//        builder.setPlaceholder(placeholder);
//    }
//
//    public void setMinValues(int min){
//        builder.setMinValues(min);
//    }
//
//    public void setMaxValues(int max){
//        builder.setMaxValues(max);
//    }
//
//    public void setMinMaxValues(int min, int max) {
//        builder.setRequiredRange(min, max);
//    }
//
//    public void setDefaultOptions(List<SelectOption> defaultOptions){
//        builder.setDefaultOptions(defaultOptions);
//    }
//
//    public void setDefaultValues(List<String> defaultValues){
//        builder.setDefaultValues(defaultValues);
//    }
//
//    public void addOption(String label, String value){
//        builder.addOption(label, value);
//    }
//
//    public void addOption(String label, String value, String description){
//        builder.addOption(label, value, description);
//    }
//
//    public void addOption(String label, String value, String description, Emoji emoji){
//        builder.addOption(label, value, description, emoji);
//    }
//
//    public void addOption(String label, String value, Emoji emoji){
//        builder.addOption(label, value, emoji);
//    }
//
//    public String getId(){
//        return builder.getId();
//    }
//
//    public SelectMenu build(){
//        return builder.build();
//    }
//
//}
