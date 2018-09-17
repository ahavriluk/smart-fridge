package com.oleksandr.smartfridge.exceptions;

/**
 * DoublicaItemException is thrown when item being added to the Bin is already
 * there.
 * 
 * @author oleksandr
 *
 */
public class DoublicateItemException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1866980052147159937L;

	public DoublicateItemException(String message) {
		super(message);
	}

}
