package tikape.runko.domain;

import java.util.ArrayList;
import java.util.List;

public class Category {

    private final int categoryId;
    private final String name;
    private List<SubCategory> subCategories;

    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
        subCategories = new ArrayList<>();
    }

    public Category setSubCategories(List<SubCategory> list) {
        subCategories = list;
        return this;
    }

    public String getName() {
        return name;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

}