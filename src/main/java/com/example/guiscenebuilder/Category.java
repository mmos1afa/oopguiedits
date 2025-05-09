package com.example.guiscenebuilder;

import java.util.Objects;

public class Category {
    static int noOfCategories;
    private String name;
    public Category(String name)
    {
        for(int i=0;i<Database.getCategories().size();i++)
        {
            if(this.equals(Database.getCategories().get(i)))
            {
                throw new IllegalArgumentException("Category already exists");
            }
        }
        this.name = name;
        noOfCategories++;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public static int getNoOfCategories() {
        return noOfCategories;
    }

    @Override
    public String toString()
    {
        return "Category name : " + name;
    }
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Category){
            return Objects.equals(((Category) obj).name, this.name);
        }
        else{
            return false;
        }
    }
}
