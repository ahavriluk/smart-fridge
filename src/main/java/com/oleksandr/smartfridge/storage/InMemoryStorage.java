/**
 * 
 */
package com.oleksandr.smartfridge.storage;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.oleksandr.smartfridge.exceptions.BinOverflowException;
import com.oleksandr.smartfridge.exceptions.DoublicateItemException;
import com.oleksandr.smartfridge.exceptions.WrongBinException;

/**
 * Implements in memory storage of items for Smart Fridge
 * 
 * @author oleksandr
 *
 */
public class InMemoryStorage implements Storage {

	/**
	 * Collection of bins in the fridge. Maps item type to its bin.
	 */
	private HashMap<Long, Bin> binMap = new HashMap<>();

	/**
	 * Maps items UUID to Bin.
	 */
	private HashMap<String, Bin> uuidMap = new HashMap<>();

	/**
	 * @see com.oleksandr.smartfridge.storage.Storage#addItem(long,
	 *      java.lang.String, java.lang.String, java.lang.Double)
	 */
	@Override
	public Item addItem(long itemType, String itemUUID, String name,
			Double fillFactor) throws WrongBinException,
			BinOverflowException, DoublicateItemException {

		if (itemUUID == null) {
			throw new IllegalArgumentException("Item UUID can't be null");
		}

		if (itemUUID == "") {
			throw new IllegalArgumentException("Item UUID can't be blank");
		}

		if (name == null) {
			throw new IllegalArgumentException("Item name can't be null");
		}

		if (name == "") {
			throw new IllegalArgumentException("Item name can't be blank");
		}

		BigDecimal ff = new BigDecimal(fillFactor, Bin.mathCtx);
		if (ff.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException(
					"Fill factor must be greater then 0");
		}

		if (ff.compareTo(BigDecimal.ONE) >= 0) {
			throw new IllegalArgumentException(
					"Fill factor must be not greater then 1");
		}

		// check if item UUID is unique
		if (uuidMap.containsKey(itemUUID)) {
			throw new DoublicateItemException(
					String.format("Item UUID %s is not unique", itemUUID));
		}

		Bin bin = binMap.computeIfAbsent(itemType, k -> new Bin(itemType));

		// add item to existing bin first
		Item item = new Item(itemType, itemUUID, name, fillFactor);
		bin.addItem(item);

		// then add bin to uuidMap if addItem doesn't fail.
		// we have to do it here, after we added an item to the bin
		// successfully,
		// otherwise we end up with a map record for UUID which wasn't added
		// to the bin

		uuidMap.put(itemUUID, bin);

		return item;
	}

	/**
	 * @see com.oleksandr.smartfridge.storage.Storage#removeItem(java.lang.String)
	 */
	@Override
	public Item removeItem(String itemUUID) {
		// find a bin by UUID
		Bin bin = uuidMap.get(itemUUID);
		if (bin != null) {
			uuidMap.remove(itemUUID);
			return bin.removeItem(itemUUID);
		}
		return null;
	}

	/**
	 * @see com.oleksandr.smartfridge.storage.Storage#getFillFactor(long)
	 */
	@Override
	public Double getFillFactor(long itemType) {
		Bin bin = binMap.get(itemType);
		if (bin != null) {
			return bin.getFillFactor();
		}
		return null;
	}

	/**
	 * 
	 * @see com.discoverorg.smartfridge.Storage#getItems(java.lang.Double)
	 */
	@Override
	public Map<Long, Double> getItems(Double fillFactor) {
		HashMap<Long, Double> returnMap = new HashMap<>();

		// check all bins if their fill factor is less then passed value
		binMap.values().stream().filter(b -> {
			BigDecimal binFillFactor = new BigDecimal(b.getFillFactor(),
					Bin.mathCtx);
			BigDecimal inFillFactor = new BigDecimal(fillFactor,
					Bin.mathCtx);

			return binFillFactor.compareTo(inFillFactor) < 0;
		}).forEach(b -> returnMap.put(b.getType(), b.getFillFactor()));

		return returnMap;
	}

	/**
	 * @see com.oleksandr.smartfridge.storage.Storage#forgetItem(long)
	 */
	@Override
	public void forgetItem(long itemType) {
		Bin bin = binMap.remove(itemType);
		if (bin != null) {
			// process uiidMap to remove all references to bin if it matches
			// item's type
			uuidMap.values().removeIf(b -> b.getType() == itemType);
		}
	}

}
