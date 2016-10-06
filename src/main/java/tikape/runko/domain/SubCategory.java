package tikape.runko.domain;

public class SubCategory {

    public int subCategoryId;
    public int categoryId;
    public String name;
    public String description;

    /**
     * Alakategoria -luokka
     * @param categoryId Yläkategorian ID
     * @param subCategoryId Alakategorian ID
     * @param name Alakategorian nimi
     */
    public SubCategory(int categoryId, int subCategoryId, String name) {
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId;
        this.name = name;
    }

    /**
     * Palauttaa alakategorian nimen
     *
     * @return Alakategorian nimi
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
     * Palauttaa alakategorian ID:n
     *
     * @return Alakategorian ID
     */
    public int getSubCategoryId() {
        return subCategoryId;
    }

    /**
     * Asettaa alakategorian kuvauksen
     *
     * @param description Alakategorian kuvaus
     * @return SubCategory -ilmentymä
     */
    public SubCategory setDescription(String description) {
        this.description = description;
        return this;
    }

    /**
     * Palauttaa alakategorian kuvauksen
     *
     * @return Alakategorian kuvaus
     */
    public String getDescription() {
        return description;
    }

    /**
     * Palauttaa alakategorian ID:n, nimen ja kuvauksen
     *
     * @return Alakategorian kuvaus muodossa ID: nimi (kuvaus)
     */
    @Override
    public String toString() {
        return subCategoryId + ": " + name + " (" + description + ")";
    }

}
