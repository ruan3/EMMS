package com.esquel.epass.utils;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.joyaether.datastore.DataElement;
import com.joyaether.datastore.Store;
import com.joyaether.datastore.callback.StoreCallback;
import com.joyaether.datastore.exception.DatastoreException;
import com.joyaether.datastore.schema.Query;
import com.joyaether.datastore.widget.DataCache;

public class EPassDataCache extends DataCache {

	private Long cachedCount;
	private String schema;

	public EPassDataCache(Store store, String schema, Query query) {
		super(store, schema, query);
		this.schema = schema;
	}
	
	/**
	 * Returns the number of items represented by the {@link Query}.
	 * 
	 * @return The number of items represented by the {@link Query}
	 */
	public ListenableFuture<Long> getCount() {
		if (cachedCount != null) {
			return Futures.immediateFuture(cachedCount);
		} else {			
			if (getStore() != null && getSchema() != null) {
				final SettableFuture<Long> future = SettableFuture.create();
				getStore().performQuery(getQuery(), schema, new StoreCallback() {
					
					@Override
					public void success(DataElement element, String resource) {
						if (element != null && element.isArray()) {
							cachedCount = (long) element.asArrayElement().size();
							future.set(cachedCount);
						} else {
							future.setException(new DatastoreException("Failed to determine the size of cache."));
						}
					}
					
					@Override
					public void failure(DatastoreException ex, String resource) {
						future.setException(ex);
					}
					
				});
				return future;
			} else {
				return Futures.immediateFailedFuture(new DatastoreException("Failed to determine the size of cache. No store or schema specified"));
			}
		}
	}
	
	public void clear() {
		super.clear();
		cachedCount = null;
	}

}
