package com.oleksandr.smartfridge.exceptions;

/**
 * This exception is throw when the bin can't accept accept the item, because of
 * item's fill factor. In other words, when this item will be added to the bin,
 * it will be overflowed.
 * 
 * @author oleksandr
 *
 */
public class BinOverflowException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2399252251782867146L;

	/**
	 * Constructor
	 * 
	 * @param message
	 */
	public BinOverflowException(String message) {
		super(message);
	}

}
