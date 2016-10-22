package tikape.runko.database;

import java.sql.*;
import java.util.*;

/**
 * Dao -interface
 *
 * @param <T> Palautettava tyyppi
 * @param <K> Avain
 */
public interface Dao<T, K> {

    /**
     *
     * @param key
     * @return
     * @throws SQLException
     */
    T findOne(K key) throws SQLException;

    /**
     *
     * @return @throws SQLException
     */
    List<T> findAll() throws SQLException;

    /**
     *
     * @param key
     * @throws SQLException
     */
    void delete(K key) throws SQLException;
}
