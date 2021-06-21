package com.example.delicioso;

public class HomeRecipeListItem {
    private String name;
    private String pathToPhoto;

    HomeRecipeListItem(String name, String path) {
        this.name = name;
        this.pathToPhoto = path;
    }

    public String getName() {
        return name;
    }

    public String getPathToPhoto() {
        return pathToPhoto;
    }
}
