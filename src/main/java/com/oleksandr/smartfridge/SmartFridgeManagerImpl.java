package com.oleksandr.smartfridge;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;

import com.oleksandr.smartfridge.storage.Storage;

/**
 * Implementation class of Smart Fridge Manager interface
 * 
 * @author oleksandr
 *
 */
public class SmartFridgeManagerImpl implements SmartFridgeManager {

	/**
	 * Fridge's storage
	 */
	private Storage storage;

	/**
	 * Logger
	 */
	private Logger logger;

	/**
	 * Constructor
	 * 
	 * @param logger
	 */
	public SmartFridgeManagerImpl(Storage storage, Logger logger) {
		super();
		this.logger = Objects.requireNonNull(logger,
				"Logger reference must be not null");
		this.storage = Objects.requireNonNull(storage,
				"Storage reference must be not null");
	}

	/**
	 * @see com.oleksandr.smartfridge.SmartFridgeManager#handleItemRemoved(java.lang.
	 *      String)
	 */
	@Override
	public void handleItemRemoved(String itemUUID) {
		storage.removeItem(itemUUID);
	}

	/**
	 * 
	 * @see com.oleksandr.smartfridge.SmartFridgeManager#handleItemAdded(long,
	 *      java.lang.String, java.lang.String, java.lang.Double)
	 */
	@Override
	public void handleItemAdded(long itemType, String itemUUID, String name,
			Double fillFactor) {
		try {
			storage.addItem(itemType, itemUUID, name, fillFactor);
		} catch (Exception e) {
			logger.warn(String.format("Item wasn't added to the fridge: %s",
					e.getMessage()));
		}
	}

	/**
	 * @see com.oleksandr.smartfridge.SmartFridgeManager#getItems(java.lang.Double)
	 */
	@Override
	public Object[] getItems(Double fillFactor) {
		Map<Long, Double> items = storage.getItems(fillFactor);
		if (items == null) {
			return new Object[0];
		}

		int size = items.size();
		ArrayList<Object> result = new ArrayList<>(size);

		items.forEach((k, v) -> result
				.add(new double[] { k.longValue(), v.doubleValue() }));

		return result.toArray();
	}

	/**
	 * @see com.oleksandr.smartfridge.SmartFridgeManager#getFillFactor(long)
	 */
	@Override
	public Double getFillFactor(long itemType) {
		return storage.getFillFactor(itemType);
	}

	/**
	 * @see com.oleksandr.smartfridge.SmartFridgeManager#forgetItem(long)
	 */
	@Override
	public void forgetItem(long itemType) {
		storage.forgetItem(itemType);
	}

}
