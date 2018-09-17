package com.oleksandr.smartfridge.storage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.oleksandr.smartfridge.storage.Item;

class ItemTest {

	@Test
	@DisplayName("Constructor")
	void testItem() {
		Item item = new Item(1, "ItemUUID", "Milk", 0.3);
		assertNotNull(item);
	}

	@Test
	@DisplayName("Getters test")
	void testGetters() {
		Item item = new Item(1, "ItemUUID", "Milk", 0.3);
		assertAll("Item", () -> assertEquals("ItemUUID", item.getUUID()), () -> assertEquals(1, item.getType()),
				() -> assertEquals("Milk", item.getName()), () -> assertEquals(0.3, item.getFillFactor(), 0.001));
	}

	@Test
	@DisplayName("Testing Equal and hashCode")
	void testEqualAndHashCode() {
		Item item = new Item(1, "ItemUUID", "Milk", 0.3);
		assertTrue(item.equals(item));

		Item item2 = new Item(1, "ItemUUID", "Milk", 0.3);
		assertTrue(item.equals(item2));
		assertEquals(item.hashCode(), item2.hashCode());

		Item item3 = new Item(1, "ItemUUID3", "Milk", 0.3);
		assertFalse(item.equals(item3));
		assertNotEquals(item.hashCode(), item3.hashCode());
	}

}
