/**
 * 
 */
package com.oleksandr.smartfridge.storage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.oleksandr.smartfridge.exceptions.BinOverflowException;
import com.oleksandr.smartfridge.exceptions.DoublicateItemException;
import com.oleksandr.smartfridge.exceptions.WrongBinException;
import com.oleksandr.smartfridge.storage.Bin;
import com.oleksandr.smartfridge.storage.Item;

/**
 * @author oleksandr
 *
 */
class BinTest {

	final double delta = 0.001;

	/**
	 * Test method for {@link com.oleksandr.smartfridge.storage.Bin#Bin(long)}.
	 */
	@Test
	@DisplayName("Constructor")
	void testBin() {
		Bin bin = new Bin(1);
		assertNotNull(bin);
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#addItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Adding an item to the Bin")
	void testAddItem() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item = new Item(itemType, "ItemUUID1", "Milk", 0.1);
		try {
			bin.addItem(item);

			assertEquals(0.1, bin.getFillFactor().doubleValue(), delta);

		} catch (WrongBinException | BinOverflowException | DoublicateItemException e) {
			fail("Should not throw an exception");
		}
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#addItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Adding a null item to the Bin shouldn't fail")
	void testAddNullItem() {
		long binType = 1;

		Bin bin = new Bin(binType);

		try {
			bin.addItem(null);

			assertEquals(0.0, bin.getFillFactor().doubleValue(), delta);
		} catch (WrongBinException | BinOverflowException | DoublicateItemException e) {
			fail("Should not throw an exception");
		}
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#addItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Adding two items to the Bin")
	void testAddTwoItems() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item1 = new Item(itemType, "ItemUUID1", "Milk", 0.1);
		Item item2 = new Item(itemType, "ItemUUID2", "Milk", 0.1);
		try {
			bin.addItem(item1);
			bin.addItem(item2);

			assertEquals(0.2, bin.getFillFactor().doubleValue(), delta);

		} catch (WrongBinException | BinOverflowException | DoublicateItemException e) {
			fail("Should not throw an exception");
		}
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#addItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Adding two items to the Bin which makes it full")
	void testAddItemsAndFillUpBin() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item1 = new Item(itemType, "ItemUUID1", "Milk", 0.5);
		Item item2 = new Item(itemType, "ItemUUID2", "Milk", 0.5);
		try {
			bin.addItem(item1);
			bin.addItem(item2);

			assertEquals(1, bin.getFillFactor().doubleValue(), delta);

		} catch (WrongBinException | BinOverflowException | DoublicateItemException e) {
			fail("Should not throw an exception");
		}
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#addItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Adding an item of a wrong type to the Bin should throw an exception")
	void testAddItemOfWrongType() {
		long binType = 1;
		long itemType = 2;
		Bin bin = new Bin(binType);
		Item item = new Item(itemType, "ItemUUID1", "Milk", 0.1);

		assertThrows(WrongBinException.class, () -> {
			bin.addItem(item);
		});

		assertEquals(0, bin.getFillFactor().doubleValue(), delta);
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#addItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Adding an item which exceeds Bin's capacity should throw an exception")
	void testAddItemOverflow() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item1 = new Item(itemType, "ItemUUID1", "Milk", 0.7);
		Item item2 = new Item(itemType, "ItemUUID2", "Milk", 0.7);

		assertThrows(BinOverflowException.class, () -> {
			bin.addItem(item1);
			bin.addItem(item2);
		});

		assertEquals(0.7, bin.getFillFactor().doubleValue(), delta);
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#addItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Adding two items with the same UUID should throw an exception")
	void testAddDoublicateItem() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item1 = new Item(itemType, "ItemUUID1", "Milk", 0.1);
		Item item2 = new Item(itemType, "ItemUUID1", "Milk", 0.1);

		assertThrows(DoublicateItemException.class, () -> {
			bin.addItem(item1);
			bin.addItem(item2);
		});

		assertEquals(0.1, bin.getFillFactor().doubleValue(), delta);
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#addItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Adding two items with the blank UUID should throw an exception")
	void testAddBlankUUIDItems() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item1 = new Item(itemType, "", "Milk", 0.1);
		Item item2 = new Item(itemType, "", "Milk", 0.1);

		assertThrows(DoublicateItemException.class, () -> {
			bin.addItem(item1);
			bin.addItem(item2);
		});

		assertEquals(0.1, bin.getFillFactor().doubleValue(), delta);
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#removeItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Removing item should reduce the fill factor of the Bin")
	void testRemoveItem() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item1 = new Item(itemType, "ItemUUID1", "Milk", 0.1);
		Item item2 = new Item(itemType, "ItemUUID2", "Sour Cream", 0.2);

		assertDoesNotThrow(() -> {
			bin.addItem(item1);
			bin.addItem(item2);
		});

		Item removedItem = bin.removeItem(item2.getUUID());
		assertEquals(item2, removedItem);

		assertEquals(0.1, bin.getFillFactor().doubleValue(), delta);
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#removeItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Removing null item shouldn't throw an exception or reduce the fill factor of the Bin")
	void testRemoveNullItem() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item1 = new Item(itemType, "ItemUUID1", "Milk", 0.1);
		Item item2 = new Item(itemType, "ItemUUID2", "Sour Cream", 0.2);

		assertDoesNotThrow(() -> {
			bin.addItem(item1);
			bin.addItem(item2);
		});

		Item removedItem = bin.removeItem(null);

		assertNull(removedItem);
		assertEquals(0.3, bin.getFillFactor().doubleValue(), delta);
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#removeItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Removing item with blank UUID shouldn't throw an exception or reduce the fill factor of the Bin")
	void testRemoveItemWithBlankUUID() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item1 = new Item(itemType, "ItemUUID1", "Milk", 0.1);
		Item item2 = new Item(itemType, "ItemUUID2", "Sour Cream", 0.2);

		assertDoesNotThrow(() -> {
			bin.addItem(item1);
			bin.addItem(item2);
		});

		Item removedItem = bin.removeItem("");

		assertNull(removedItem);
		assertEquals(0.3, bin.getFillFactor().doubleValue(), delta);
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#addItem(com.oleksandr.smartfridge.storage.Item)}.
	 * {@link com.oleksandr.smartfridge.storage.Bin#removeItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Adding and Removing item should increace/reduce the fill factor of the Bin")
	void testAddRemoveItems() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item1 = new Item(itemType, "ItemUUID1", "Milk", 0.1);
		Item item2 = new Item(itemType, "ItemUUID2", "Sour Cream", 0.25);
		Item item3 = new Item(itemType, "ItemUUID3", "Yogurt", 0.3);
		Item item4 = new Item(itemType, "ItemUUID4", "Buttermilk", 0.2);
		Item item5 = new Item(itemType, "ItemUUID5", "Heavy Cream", 0.45);

		assertDoesNotThrow(() -> {
			bin.addItem(item1); // 0.1
			bin.addItem(item2); // 0.35
			bin.addItem(item3); // 0.65
			bin.addItem(item4); // 0.85

			bin.removeItem(item1.getUUID()); // 0.75
			bin.removeItem(item3.getUUID()); // 0.45

			bin.addItem(item5); // 0.9
			bin.addItem(item1); // 1.0
		});

		assertEquals(1.0, bin.getFillFactor().doubleValue());
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#addItem(com.oleksandr.smartfridge.storage.Item)}.
	 * {@link com.oleksandr.smartfridge.storage.Bin#removeItem(com.oleksandr.smartfridge.storage.Item)}.
	 */
	@Test
	@DisplayName("Add and remove 5 items. The Bin should have fill factor 0")
	void testAdd5Remove5Items() {
		long binType = 1;
		long itemType = 1;
		Bin bin = new Bin(binType);
		Item item1 = new Item(itemType, "ItemUUID1", "Milk", 0.1);
		Item item2 = new Item(itemType, "ItemUUID2", "Sour Cream", 0.25);
		Item item3 = new Item(itemType, "ItemUUID3", "Yogurt", 0.3);
		Item item4 = new Item(itemType, "ItemUUID4", "Buttermilk", 0.2);
		Item item5 = new Item(itemType, "ItemUUID5", "Heavy Cream", 0.15);

		assertDoesNotThrow(() -> {
			bin.addItem(item1); // 0.1
			bin.addItem(item2); // 0.35
			bin.addItem(item3); // 0.65
			bin.addItem(item4); // 0.85
			bin.addItem(item5); // 1.0
		});

		assertEquals(1, bin.getFillFactor().doubleValue());

		assertDoesNotThrow(() -> {
			bin.removeItem(item1.getUUID());
			bin.removeItem(item2.getUUID());
			bin.removeItem(item3.getUUID());
			bin.removeItem(item4.getUUID());
			bin.removeItem(item5.getUUID());
		});

		assertEquals(0, bin.getFillFactor().doubleValue());
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#getItem(java.lang.String)}.
	 */
	@Test
	@DisplayName("Return item")
	void testGetItem() {
		long binType = 1;
		long itemType = 1;
		String itemUUID = "ItemUUID1";

		Bin bin = new Bin(binType);
		Item item = new Item(itemType, itemUUID, "Milk", 0.1);

		try {
			bin.addItem(item);

			Item result = bin.getItem(itemUUID);

			assertEquals(item, result);
			assertEquals(itemUUID, result.getUUID());

		} catch (WrongBinException | BinOverflowException | DoublicateItemException e) {
			fail("Should not throw an exception");
		}
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#getFillFactor()}.
	 */
	@Test
	@DisplayName("Return fill factor")
	void testGetFillFactor() {
		Bin bin = new Bin(1);
		assertEquals(0.0, bin.getFillFactor().doubleValue(), delta);

		assertDoesNotThrow(() -> {
			bin.addItem(new Item(1, "ItemUUID1", "Milk", 0.1));
		});

		assertEquals(0.1, bin.getFillFactor().doubleValue());

		assertDoesNotThrow(() -> {
			bin.addItem(new Item(1, "ItemUUID2", "Sour cream", 0.3));
		});

		assertEquals(0.4, bin.getFillFactor().doubleValue(), delta);
	}

	/**
	 * Test method for {@link com.oleksandr.smartfridge.storage.Bin#getType()}.
	 */
	@Test
	@DisplayName("Return type")
	void testGetType() {
		long binType = 1;
		Bin bin = new Bin(binType);
		assertEquals(binType, bin.getType());
	}

	/**
	 * Test method for
	 * {@link com.oleksandr.smartfridge.storage.Bin#equals(Object obj)} and
	 * {@link com.oleksandr.smartfridge.storage.Bin#hashCode()}.
	 */
	@Test
	@DisplayName("Testing equal and hashCode")
	void testEqualandHashCode() {
		Bin bin1 = new Bin(1);
		Bin bin2 = new Bin(1);
		assertTrue(bin1.equals(bin1));
		assertTrue(bin1.equals(bin2));
		assertEquals(bin1.hashCode(), bin2.hashCode());

		Bin bin3 = new Bin(3);
		assertFalse(bin1.equals(bin3));
		assertNotEquals(bin1.hashCode(), bin3.hashCode());
	}
}
