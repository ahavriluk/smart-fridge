package com.oleksandr.smartfridge.storage;

import java.util.Map;

import com.oleksandr.smartfridge.exceptions.BinOverflowException;
import com.oleksandr.smartfridge.exceptions.DoublicateItemException;
import com.oleksandr.smartfridge.exceptions.WrongBinException;

/**
 * Storage interface for Smart Fridge
 * 
 * @author oleksandr
 *
 */
public interface Storage {

	/**
	 * Adds items to storage
	 * 
	 * @param itemType   - items type
	 * @param itemUUID   - items UUID. Must be unique.
	 * @param name       - items name
	 * @param fillFactor - items fill factor
	 * @return {@link Item}
	 * @throws WrongBinException
	 * @throws BinOverflowException
	 * @throws DoublicateItemException
	 */
	Item addItem(long itemType, String itemUUID, String name,
			Double fillFactor) throws WrongBinException,
			BinOverflowException, DoublicateItemException;

	/**
	 * Removes item from the storage by item's UUID
	 * 
	 * @param itemUUID - items UUID
	 */
	Item removeItem(String itemUUID);

	/**
	 * Returns a list of items based on their fill factor. This method is
	 * used by the fridge to display items that are running low and need to
	 * be replenished.
	 * 
	 * getItems( 0.5 ) - will return any items that are 50% or less full,
	 * including items that are depleted. Unless all available containers
	 * are empty, this method should only consider the non-empty containers
	 * when calculating the overall fillFactor for a given item.
	 * 
	 * @param fillFactor
	 * @return a map of item type to its fill factor
	 */
	Map<Long, Double> getItems(Double fillFactor);

	/**
	 * Returns the fill factor of a bin for a given item type.
	 *
	 * @param itemType
	 *
	 * @return a double representing the fill factor for the item type
	 */
	Double getFillFactor(long itemType);

	/**
	 * Removed all items of the particular type from the storage.
	 * 
	 * @param itemType - indicates item type
	 */
	void forgetItem(long itemType);
}
