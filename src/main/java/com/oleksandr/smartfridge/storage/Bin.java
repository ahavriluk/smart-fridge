package com.oleksandr.smartfridge.storage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

import com.oleksandr.smartfridge.exceptions.BinOverflowException;
import com.oleksandr.smartfridge.exceptions.DoublicateItemException;
import com.oleksandr.smartfridge.exceptions.WrongBinException;

/**
 * This is a storage bin in the Fridge. It can contain multiple items and it
 * has a limited capacity. The fill factor indicates how much the Bin is
 * filled up. 0 - the Bin is empty, 1 - the Bin is full. When item is added,
 * the fill factor is increased, when items is removed, the fill factor is
 * decreased. Each item, when added, increases the Bin's fill factor by its
 * fill factor amount.
 * 
 * @author oleksandr
 *
 */
class Bin {

	/**
	 * Describes the which items type are stored in the bin. binType =
	 * itemType
	 */
	private long type;

	/**
	 * Indicates the how much bin is filled up. The value ranges is 0-1. 0 -
	 * the bin is empty. 1 - the bin is full.
	 */
	private BigDecimal fillFactor = new BigDecimal(0, mathCtx);

	/**
	 * Precision for math operations
	 */
	public static final MathContext mathCtx = new MathContext(2);

	/**
	 * This is a collection of all items in the bin. The key is the item's
	 * UUID.
	 */
	private HashMap<String, Item> items = new HashMap<>();

	/**
	 * Constructs a bin of a certain type
	 * 
	 * @param itemType defines what items could be stored in the bin
	 */
	public Bin(long itemType) {
		super();
		type = itemType;
	}

	/**
	 * Added item to the bin It check if item could be added to the bin and
	 * throws {@link WrongBinException}, if item type doesn't match the
	 * bin's type. When adding an item, the bin's capacity is exceeded, it
	 * throws {@link BinOverflowException}. When items being added has the
	 * UUID of the item already in the Bin {@link DoublicateItemException}
	 * is thrown.
	 * 
	 * @param item Item object reference. If Item is null, it is ignored, no
	 *             exception is thrown.
	 * @throws WrongBinException
	 * @throws BinOverflowException
	 * @throws DoublicateItemException
	 */
	public void addItem(Item item) throws WrongBinException,
			BinOverflowException, DoublicateItemException {
		if (item == null) {
			return;
		}

		// Check if item could be added to the Bin
		if (item.getType() != type) {
			throw new WrongBinException();
		}

		// Check if the item is already in the fridge
		if (items.containsKey(item.getUUID())) {
			throw new DoublicateItemException(item.getUUID());
		}

		BigDecimal itemFillFactor = new BigDecimal(item.getFillFactor(),
				mathCtx);

		// Calculate a new fill factor
		BigDecimal newFillFactor = fillFactor.add(itemFillFactor);
		
		// Check if Bin can accept the item. If adding an item exceed the
		// bin's capacity, it throws an exception
		if (newFillFactor.compareTo(BigDecimal.ONE) > 0) {
			throw new BinOverflowException(String.format(
					"Can't add item. Not enough room for item type %d",
					item.getType()));
		}

		// adjust the Bin's fill factor
		fillFactor = newFillFactor;

		items.put(item.getUUID(), item);
	}

	/**
	 * Removes item from the Bin. The Bin's fill factor is reduced by item's
	 * fill factor value
	 * 
	 * @param itemUUID - item's UUID
	 * @return {@link Item}
	 */
	public Item removeItem(String itemUUID) {
		// First get the item from the Bin
		Item item = items.get(itemUUID);
		if (item != null) {
			BigDecimal itemFillFactor = new BigDecimal(item.getFillFactor(),
					mathCtx);
			// adjust the fill factor
			fillFactor = fillFactor.subtract(itemFillFactor);
			items.remove(itemUUID);
		}
		return item;
	}

	/**
	 * Returns items from the Bin
	 * 
	 * @param itemUUID item's UUID
	 * @return {@link Item}
	 */
	public Item getItem(String itemUUID) {
		return items.get(itemUUID);
	}

	/**
	 * Returns fill factor value
	 * 
	 * @return {@link Double}
	 */
	public Double getFillFactor() {
		return fillFactor.doubleValue();
	}

	/**
	 * Returns Bins type
	 * 
	 * @return long
	 */
	public long getType() {
		return type;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (type ^ (type >>> 32));
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Bin other = (Bin) obj;
		return type == other.type;
	}

}
