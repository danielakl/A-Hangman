package no.daniel.hangman.data.dao;

import java.util.List;

import no.daniel.hangman.data.entity.Entity;

public interface DAO<Data extends Entity> {
    /**
     * Get a single entity identified by the given identifier.
     * @param identifier for identifying the entity.
     * @return the entity found.
     */
    Data get(Object identifier);

    /**
     * Get all entities or entities passing the filter.
     * @param filter used to filter returned entities.
     * @return an array of entities, returns all entities if filter is null.
     */
    List<Data> get(String filter);

    /**
     * Save a new entity to the data source.
     * @param entity the entity to store.
     * @return the newly saved entity.
     */
    Data create(Data entity);

    /**
     * Updates the stored entity with new values from the updated entity.
     * @param updated an entity with new properties.
     * @return the updated entity.
     */
    Data update(Data updated);

    /**
     * Deletes all entities.
     * @return the number of deleted entities.
     */
    long delete();

    /**
     * Deletes the given entity on the data source.
     * @param entity the entity to delete.
     * @return true if the entity was successfully deleted or already gone.
     */
    boolean delete(Data entity);
}
