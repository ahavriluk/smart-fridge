package com.oleksandr.smartfridge.storage;

/**
 * This is an Item class. Items are stored in Bins in the fridge
 * 
 * @author oleksandr
 *
 */
public class Item {

	/**
	 * Item's ID
	 */
	private String itemUUID;

	/**
	 * Describes item's type
	 */
	private long type;

	/**
	 * Name of the item
	 */
	private String name;

	/**
	 * Indicates how much space item could take from the Bin
	 */
	private Double fillFactor;

	/**
	 * Constructor
	 * 
	 * @param type     item type
	 * @param itemUUID item ID
	 * @param name     item name
	 */
	public Item(long type, String itemUUID, String name,
			Double fillFactor) {
		super();
		this.type = type;
		this.itemUUID = itemUUID;
		this.name = name;
		this.fillFactor = fillFactor;
	}

	/**
	 * Returns item type
	 * 
	 * @return long
	 */
	public long getType() {
		return type;
	}

	/**
	 * Returns item's Name
	 * 
	 * @return string
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns item ID
	 * 
	 * @return String
	 */
	public String getUUID() {
		return itemUUID;
	}

	/**
	 * Returns the fill factor values for the item. The value is from 0 to
	 * 1.
	 * 
	 * @return Double
	 */
	public Double getFillFactor() {
		return fillFactor;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((itemUUID == null) ? 0 : itemUUID.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (itemUUID == null) {
			if (other.itemUUID != null)
				return false;
		} else if (!itemUUID.equals(other.itemUUID)) {
			return false;
		}
		return true;
	}

}
