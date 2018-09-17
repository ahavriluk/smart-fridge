package com.oleksandr.smartfridge;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oleksandr.smartfridge.SmartFridgeManager;
import com.oleksandr.smartfridge.SmartFridgeManagerImpl;
import com.oleksandr.smartfridge.storage.InMemoryStorage;
import com.oleksandr.smartfridge.storage.Storage;

class SmartFridgeManagerImplTest {
	Logger logger = LoggerFactory.getLogger(SmartFridgeManagerImplTest.class); 
			
	@Test
	@DisplayName("Testing Constructor")
	void testSmartFridgeManagerImpl() {
		Storage storage = new InMemoryStorage();
		assertNotNull(storage);
		
		assertDoesNotThrow(() -> {		
			SmartFridgeManager fManager = new SmartFridgeManagerImpl(storage, logger);
			assertNotNull(fManager);			
		});
		
		assertThrows(NullPointerException.class, () -> {
			new SmartFridgeManagerImpl(null, logger);
		});
		
		assertThrows(NullPointerException.class, () -> {
			new SmartFridgeManagerImpl(storage, null);
		});
	}

	@Test
	@DisplayName("Testing fridge operations")
	void testFridgeOperations() {
		Storage storage = new InMemoryStorage();
		SmartFridgeManager fManager = new SmartFridgeManagerImpl(storage, logger);

		fManager.handleItemAdded(1, "itemUUID1", "Milk", 0.2);

		fManager.handleItemAdded(2, "itemUUID2", "Beef", 0.3);
		fManager.handleItemAdded(2, "itemUUID3", "Pork", 0.3);

		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
		assertEquals(0.6, fManager.getFillFactor(2), 0.001);

		// Check which items are low
		Object[] items = fManager.getItems(0.5);
		assertNotNull(items);
		assertEquals(1, items.length);
		assertNotNull(items[0]);

		double[] array = (double[]) items[0]; // array of two elements - [ itemType, fillFactor ]

		assertEquals(2, array.length);
		assertEquals(1, array[0]); // checking the item type
		assertEquals(0.2, array[1]); // checking the fillFactor

		// Now remove some of items from the fridge
		fManager.handleItemRemoved("itemUUID2");

		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
		assertEquals(0.3, fManager.getFillFactor(2), 0.001);

		// Check which items are low now
		items = fManager.getItems(0.5);
		assertNotNull(items);
		assertEquals(2, items.length); // should be 2
		assertNotNull(items[0]);

		array = (double[]) items[0]; // array of two elements - [ itemType, fillFactor ]

		assertEquals(2, array.length);
		assertEquals(1, array[0]); // checking the item type
		assertEquals(0.2, array[1]); // checking the fillFactor

		// the second item
		array = (double[]) items[1]; // array of two elements - [ itemType, fillFactor ]

		assertEquals(2, array.length);
		assertEquals(2, array[0]); // checking the item type
		assertEquals(0.3, array[1]); // checking the fillFactor

		// Now, remove second item type completely and add another item type 3
		fManager.handleItemRemoved("itemUUID3");

		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
		assertEquals(0.0, fManager.getFillFactor(2), 0.001);

		fManager.handleItemAdded(3, "itemUUID4", "Apple", 0.1);
		fManager.handleItemAdded(3, "itemUUID5", "Grapes", 0.5);

		// Check the fill factor of our items/containers
		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
		assertEquals(0.0, fManager.getFillFactor(2), 0.001);
		assertEquals(0.6, fManager.getFillFactor(3), 0.001);

		// Now check the item type we are running low on.

		items = fManager.getItems(0.5);
		assertNotNull(items);
		assertEquals(2, items.length); // should be 2, we are low on 1 and 2
		assertNotNull(items[0]);

		// check the first entry
		array = (double[]) items[0]; // array of two elements - [ itemType, fillFactor ]

		assertEquals(2, array.length);
		assertEquals(1, array[0]); // checking the item type
		assertEquals(0.2, array[1]); // checking the fillFactor

		// check the second entry
		array = (double[]) items[1]; // array of two elements - [ itemType, fillFactor ]
		assertEquals(2, array.length);
		assertEquals(2, array[0]); // checking the item type
		assertEquals(0.0, array[1]); // checking the fillFactor

		// Now forget item type 1
		fManager.forgetItem(1);

		// Check what items are low now
		items = fManager.getItems(0.5);
		assertEquals(1, items.length);
		
		array = (double[]) items[0]; // array of two elements - [ itemType, fillFactor ]
		assertEquals(2, array.length);
		assertEquals(2, array[0]); // checking the item type
		assertEquals(0.0, array[1]); // checking the fillFactor
	}

	@Test
	@DisplayName("Testing adding item")
	void testHandleItemAdd() {
		Storage storage = new InMemoryStorage();
		SmartFridgeManager fManager = new SmartFridgeManagerImpl(storage, logger);
		// Initialize the fridge		
		fManager.handleItemAdded(1, "", "name", 0.2);
		fManager.handleItemAdded(1, "id", "", 0.3);
		fManager.handleItemAdded(1, null, "name", 0.3);
		fManager.handleItemAdded(1, "id", null, 0.3);
		fManager.handleItemAdded(1, "id", "name", -0.3);

		// Male sure items with invalid parameters are not added
		Object[] items = fManager.getItems(1.0);
		assertEquals(0, items.length);

		// add duplicates
		fManager.handleItemAdded(2, "id2", "name", 0.2);
		fManager.handleItemAdded(2, "id2", "name", 0.2);

		assertEquals(0.2, fManager.getFillFactor(2), 0.001);

		// Overflow
		fManager.handleItemAdded(3, "id3", "name", 0.6);
		fManager.handleItemAdded(3, "id4", "name", 0.6);

		assertEquals(0.6, fManager.getFillFactor(3), 0.001);

	}

	@Test
	@DisplayName("Testing item removal")
	void testHandleItemRemoved() {
		Storage storage = new InMemoryStorage();
		SmartFridgeManager fManager = new SmartFridgeManagerImpl(storage, logger);
		// Initialize the fridge
		fManager.handleItemAdded(1, "itemUUID1", "Milk", 0.2);
		fManager.handleItemAdded(2, "itemUUID2", "Beef", 0.3);
		fManager.handleItemAdded(2, "itemUUID3", "Pork", 0.3);

		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
		assertEquals(0.6, fManager.getFillFactor(2), 0.001);

		// Try to remove item with wrong ID
		fManager.handleItemRemoved("itemDoesntExist");
		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
		assertEquals(0.6, fManager.getFillFactor(2), 0.001);

		// Try to remove item with black ID
		fManager.handleItemRemoved("");
		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
		assertEquals(0.6, fManager.getFillFactor(2), 0.001);

		fManager.handleItemRemoved(null);
		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
		assertEquals(0.6, fManager.getFillFactor(2), 0.001);
	}

	@Test
	@DisplayName("Testing 'GetItems'")
	void testGetItems() {
		Storage storage = new InMemoryStorage();
		SmartFridgeManager fManager = new SmartFridgeManagerImpl(storage, logger);
		// Initialize the fridge
		fManager.handleItemAdded(1, "itemUUID1", "Milk", 0.2);
		fManager.handleItemAdded(2, "itemUUID2", "Beef", 0.3);
		fManager.handleItemAdded(2, "itemUUID3", "Pork", 0.3);

		// Check which items are low
		Object[] items = fManager.getItems(0.5);
		assertNotNull(items);
		assertEquals(1, items.length);
		assertNotNull(items[0]);

		double[] array = (double[]) items[0]; // array of two elements - [ itemType, fillFactor ]

		assertEquals(2, array.length);
		assertEquals(1, array[0]); // checking the item type
		assertEquals(0.2, array[1]); // checking the fillFactor

		// Now check which items below 70%
		items = fManager.getItems(0.7);
		assertNotNull(items);
		assertEquals(2, items.length);
		assertNotNull(items[0]);

		array = (double[]) items[0]; // array of two elements - [ itemType, fillFactor ]

		assertEquals(2, array.length);
		assertEquals(1, array[0]); // checking the item type
		assertEquals(0.2, array[1]); // checking the fillFactor

		// second item type
		array = (double[]) items[1]; // array of two elements - [ itemType, fillFactor ]

		assertEquals(2, array.length);
		assertEquals(2, array[0]); // checking the item type
		assertEquals(0.6, array[1]); // checking the fillFactor

		// Now check which items below 0%
		items = fManager.getItems(0.0);
		assertEquals(0, items.length);
	}

	@Test
	@DisplayName("Testing 'GetFillFactor' method")
	void testGetFillFactor() {
		Storage storage = new InMemoryStorage();
		SmartFridgeManager fManager = new SmartFridgeManagerImpl(storage, logger);
		// Initialize the fridge
		fManager.handleItemAdded(1, "itemUUID1", "Milk", 0.2);
		fManager.handleItemAdded(2, "itemUUID2", "Beef", 0.3);
		fManager.handleItemAdded(2, "itemUUID3", "Pork", 0.3);

		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
		assertEquals(0.6, fManager.getFillFactor(2), 0.001);
		assertNull(fManager.getFillFactor(3));
	}

	@Test
	@DisplayName("Testing 'forget item' functionality")
	void testForgetItem() {
		Storage storage = new InMemoryStorage();
		SmartFridgeManager fManager = new SmartFridgeManagerImpl(storage, logger);
		// Initialize the fridge
		fManager.handleItemAdded(1, "itemUUID1", "Milk", 0.2);
		fManager.handleItemAdded(2, "itemUUID2", "Beef", 0.3);
		fManager.handleItemAdded(2, "itemUUID3", "Pork", 0.3);

		fManager.forgetItem(2);
		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
		assertNull(fManager.getFillFactor(2));

		fManager.forgetItem(3);
		assertEquals(0.2, fManager.getFillFactor(1), 0.001);
	}

}
