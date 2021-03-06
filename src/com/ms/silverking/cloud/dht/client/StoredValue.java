package com.ms.silverking.cloud.dht.client;

/**
 * <p>Represents data returned from a retrieval operation. Depending on the type of
 * retrieval, this object may contain an actual value and/or meta data for the stored value.
 * For EXISTENCE retrievals, a non-null object with no value and potentially no meta-data 
 * will be returned.</p>
 * 
 * <p>This class extends StoredValueBase with convenience forms of meta data methods.</p>
 * 
 * <p>This class also adds a guarantee that meta data retrieved is stored distinctly from the value.
 * Thus values may be garbage collected independently from the meta data.</p>
 * 
 * @param <V> value type
 */
public interface StoredValue<V> extends StoredValueBase<V>, MetaData {
    // FUTURE - think about the separate gc guarantee. Might need to provide an explicit method
    // for that.
    
    // FUTURE - think about multi-versioned results
    /**
     * Experimental - not yet supported 
     * @return TODO
     */
    public StoredValue<V> next();
}
