package org.example.models;

import lombok.Data;

@Data
public class Category {
    private String categoryId;
    private String name;
    private boolean deleted;


    @Override
    public String toString() {
        return "Category{" +
                "categoryId='" + categoryId + '\'' +
                ", name='" + name + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
