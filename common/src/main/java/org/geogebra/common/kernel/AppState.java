package org.geogebra.common.kernel;

/**
 * Interface for application state
 */
public interface AppState {

    /** Returns the application state in XML */
    String getXml();

    /** deletes this application state (i.e. deletes file) */
    void delete();
}
