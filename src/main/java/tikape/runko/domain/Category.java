package tikape.runko.domain;

import java.util.ArrayList;
import java.util.List;

public class Category {

    public int categoryId;
    public String name;
    public List<SubCategory> subCategories;

    /**
     * Yläkategoria -luokka
     *
     * @param categoryId Yläkategorian ID
     * @param name Yläkategorian nimi
     */
    public Category(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
        subCategories = new ArrayList<>();
    }

    public Category(String name) {
        this(-1, name);
    }

    /**
     * Asettaa yläkategoriaan alakategoriat
     *
     * @param list Alakategorioiden lista
     * @return Category -ilmentymä
     */
    public Category setSubCategories(List<SubCategory> list) {
        subCategories = list;
        return this;
    }

    /**
     * Palauttaa yläkategorian nimen
     *
     * @return Yläkategorian nimi
     */
    public String getName() {
        return name;
    }

    /**
     * Palauttaa yläkategorian ID:n
     *
     * @return Yläkategorian ID
     */
    public int getCategoryId() {
        return categoryId;
    }

    /**
     * Palauttaa yläkategorialle määritetyt alakategoriat
     *
     * @return Lista alakategorioista
     */
    public List<SubCategory> getSubCategories() {
        return subCategories;
    }

    /**
     * Palauttaa yläkategorian nimen
     *
     * @return Yläkategorian nimi
     */
    @Override
    public String toString() {
        return name;
    }

}
