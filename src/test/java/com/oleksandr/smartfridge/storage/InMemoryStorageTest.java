package com.oleksandr.smartfridge.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.oleksandr.smartfridge.exceptions.BinOverflowException;
import com.oleksandr.smartfridge.exceptions.DoublicateItemException;
import com.oleksandr.smartfridge.exceptions.WrongBinException;
import com.oleksandr.smartfridge.storage.InMemoryStorage;
import com.oleksandr.smartfridge.storage.Item;

class InMemoryStorageTest {

	@Test
	@DisplayName("Testing adding items with invalid parameters")
	void testAddItemWithInvalidParameters() {
		InMemoryStorage storage = new InMemoryStorage();

		assertThrows(IllegalArgumentException.class, () -> {
			storage.addItem(1, null, "Milk", 0.3);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			storage.addItem(2, "UUIID", null, 0.3);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			storage.addItem(3, "UUID", "Milk", -0.3);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			storage.addItem(4, "", "Milk", 0.3);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			storage.addItem(5, "UUID", "", 0.3);
		});

		assertThrows(IllegalArgumentException.class, () -> {
			storage.addItem(6, "UUID", "name", 3.0);
		});
	}

	@Test
	@DisplayName("Testing bin overflow")
	void testBinOverflow() {
		InMemoryStorage storage = new InMemoryStorage();

		assertThrows(BinOverflowException.class, () -> {
			storage.addItem(1, "id1", "Milk1", 0.3);
			storage.addItem(1, "id2", "Milk2", 0.8);
		});
	}

	@Test
	@DisplayName("Testing adding a new item")
	void testAddItem() {
		InMemoryStorage storage = new InMemoryStorage();

		try {
			final Item item1 = storage.addItem(1, "ItemUUID1", "Milk", 0.3);
			assertAll("Item", () -> assertEquals("ItemUUID1", item1.getUUID()), () -> assertEquals(1, item1.getType()),
					() -> assertEquals("Milk", item1.getName()), () -> assertEquals(0.3, item1.getFillFactor(), 0.001));

			final Item item2 = storage.addItem(2, "ItemUUID2", "Ground Beef", 0.4);
			assertAll("Item", () -> assertEquals("ItemUUID2", item2.getUUID()), () -> assertEquals(2, item2.getType()),
					() -> assertEquals("Ground Beef", item2.getName()),
					() -> assertEquals(0.4, item2.getFillFactor(), 0.001));
		} catch (WrongBinException | BinOverflowException | DoublicateItemException e) {
			fail("Should not throw an exception");
		}
	}

	@Test
	@DisplayName("Testing adding two items with the same id")
	void testAddNotUniqueItem() {
		InMemoryStorage storage = new InMemoryStorage();

		assertDoesNotThrow(() -> {
			storage.addItem(1, "ItemUUID1", "Milk", 0.3);
		});

		assertThrows(DoublicateItemException.class, () -> {
			storage.addItem(2, "ItemUUID1", "Milk", 0.3);
		});
	}

	@Test
	@DisplayName("Testing removing")
	void testRemoveItem() {
		InMemoryStorage storage = new InMemoryStorage();

		try {
			Item item1 = storage.addItem(1, "ItemUUID1", "Milk", 0.3);
			assertAll("Item", () -> assertEquals("ItemUUID1", item1.getUUID()), () -> assertEquals(1, item1.getType()),
					() -> assertEquals("Milk", item1.getName()), () -> assertEquals(0.3, item1.getFillFactor(), 0.001));

			Item item2 = storage.addItem(2, "ItemUUID2", "Ground Beef", 0.4);
			assertAll("Item", () -> assertEquals("ItemUUID2", item2.getUUID()), () -> assertEquals(2, item2.getType()),
					() -> assertEquals("Ground Beef", item2.getName()),
					() -> assertEquals(0.4, item2.getFillFactor(), 0.001));

			Item item3 = storage.addItem(2, "ItemUUID3", "Ham", 0.4);
			assertAll("Item", () -> assertEquals("ItemUUID3", item3.getUUID()), () -> assertEquals(2, item3.getType()),
					() -> assertEquals("Ham", item3.getName()), () -> assertEquals(0.4, item3.getFillFactor(), 0.001));

			Item removedItem1 = storage.removeItem("ItemUUID1");
			assertAll("Item", () -> assertEquals("ItemUUID1", removedItem1.getUUID()),
					() -> assertEquals(1, removedItem1.getType()), () -> assertEquals("Milk", removedItem1.getName()),
					() -> assertEquals(0.3, removedItem1.getFillFactor(), 0.001));

			Item removedItem2 = storage.removeItem("ItemUUID2");
			assertAll("Item", () -> assertEquals("ItemUUID2", removedItem2.getUUID()),
					() -> assertEquals(2, removedItem2.getType()),
					() -> assertEquals("Ground Beef", removedItem2.getName()),
					() -> assertEquals(0.4, removedItem2.getFillFactor(), 0.001));

			Item removedItem3 = storage.removeItem("ItemUUID3");
			assertAll("Item", () -> assertEquals("ItemUUID3", removedItem3.getUUID()),
					() -> assertEquals(2, removedItem3.getType()), () -> assertEquals("Ham", removedItem3.getName()),
					() -> assertEquals(0.4, removedItem3.getFillFactor(), 0.001));

			Item removedItem = storage.removeItem("ItemUUID2");
			assertNull(removedItem);

		} catch (WrongBinException | BinOverflowException | DoublicateItemException e) {
			fail("Should not throw an exception");
		}
	}

	@Test
	@DisplayName("Testing removing item which doesn't exist")
	void testRemoveItemWichDoesntExit() {
		InMemoryStorage storage = new InMemoryStorage();

		assertDoesNotThrow(() -> {
			storage.addItem(1, "ItemUUID1", "Milk", 0.3);
			storage.addItem(2, "ItemUUID2", "Milk", 0.3);
		});

		Item result = storage.removeItem("ItemUUID3");
		assertNull(result);
	}

	@Test
	@DisplayName("Testing forget item")
	void testForgetItem() {
		InMemoryStorage storage = new InMemoryStorage();

		assertDoesNotThrow(() -> {
			storage.addItem(1, "ItemUUID1", "Milk", 0.3);
			storage.addItem(2, "ItemUUID2", "Milk", 0.3);
		});

		storage.forgetItem(2);

		assertNull(storage.getFillFactor(2));

		// Check items
		Map<Long, Double> items = storage.getItems(1.0);
		assertEquals(1, items.size());
		assertEquals(0.3, items.get(1L), 0.001);

		// forget item type which doesn't exist
		storage.forgetItem(3);

		// Check items
		items = storage.getItems(1.0);
		assertEquals(1, items.size());
		assertEquals(0.3, items.get(1L), 0.001);
	}

	@Test
	@DisplayName("getItem method should return bins which are running low on items")
	void testGetItems() {
		InMemoryStorage storage = new InMemoryStorage();

		assertDoesNotThrow(() -> {
			storage.addItem(1, "ItemUUID1", "Milk", 0.3);
			storage.addItem(2, "ItemUUID2", "Beef", 0.3);
			storage.addItem(2, "ItemUUID3", "Pork", 0.3);

			storage.removeItem("ItemUUID1");
			storage.removeItem("ItemUUID2");
			storage.removeItem("ItemUUID3");
		});

		Map<Long, Double> items = storage.getItems(0.5);
		assertNotNull(items);
		assertEquals(2, items.size());
		assertEquals(0.0, items.get(1L), 0.001);
		assertEquals(0.0, items.get(2L), 0.001);

		assertDoesNotThrow(() -> {
			storage.addItem(1, "ItemUUID1", "Milk", 0.3);
			storage.addItem(2, "ItemUUID2", "Beef", 0.3);
			storage.addItem(2, "ItemUUID3", "Pork", 0.3);
			storage.addItem(3, "ItemUUID4", "Tomatos", 0.2);
			storage.addItem(3, "ItemUUID5", "Salat", 0.2);
			storage.addItem(4, "ItemUUID6", "Cheese", 0.2);
			storage.removeItem("ItemUUID6");
		});

		items = storage.getItems(0.5);

		assertEquals(3, items.size());
		assertEquals(0.3, items.get(1L), 0.001);
		assertEquals(0.4, items.get(3L), 0.001);
		assertEquals(0.0, items.get(4L), 0.001);
	}

	@Test
	@DisplayName("Testing getFillFactor")
	void testGetFillFactor() {
		InMemoryStorage storage = new InMemoryStorage();

		assertDoesNotThrow(() -> {
			storage.addItem(1, "ItemUUID1", "Milk", 0.3);
			storage.addItem(2, "ItemUUID2", "Beef", 0.3);
			storage.addItem(2, "ItemUUID3", "Pork", 0.3);
		});

		assertEquals(0.3, storage.getFillFactor(1), 0.001);
		assertEquals(0.6, storage.getFillFactor(2), 0.001);
		assertNull(storage.getFillFactor(3));
	}

}
