package com.oleksandr.smartfridge;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oleksandr.smartfridge.storage.InMemoryStorage;
import com.oleksandr.smartfridge.storage.Item;
import com.oleksandr.smartfridge.storage.Storage;

/**
 * Smart fridge demo application. It uses a logger to output the results.
 *
 */
public class DemoApp {

	static HashMap<Long, String> itemTypes = new HashMap<>();

	public static void main(String[] args) {

		itemTypes.put(1L, "Dairy");
		itemTypes.put(2L, "Vegetables");
		itemTypes.put(3L, "Meat");
		itemTypes.put(4L, "Charcuterie");

		// List of items in demo
		ArrayList<Item> items = new ArrayList<>();
		items.add(new Item(1, "MLK", "Milk", 0.2));
		items.add(new Item(1, "SC", "Sour Cream", 0.4));
		items.add(new Item(1, "YGRT", "Yogurt", 0.4));

		items.add(new Item(2, "SLD", "Salad", 0.3));
		items.add(new Item(2, "TMT", "Tomatos", 0.4));
		items.add(new Item(2, "BP", "Bell Pepper", 0.3));

		items.add(new Item(3, "BEEF", "Beef", 0.3));
		items.add(new Item(3, "PORK", "Pork", 0.3));

		items.add(new Item(4, "SAL", "Salami", 0.3));

		// Create a logger
		final Logger logger = LoggerFactory.getLogger(DemoApp.class);

		// Create a Smart Fridge Manafger
		Storage storage = new InMemoryStorage();
		SmartFridgeManager fManager = new SmartFridgeManagerImpl(storage,
				logger);

		logger.info("Smart Fridge Demo");

		// Add items to the fridge
		items.forEach(item -> {
			String message = String.format("Adding %s: %s",
					itemTypes.get(item.getType()), item.getName());
			logger.info(message);
			fManager.handleItemAdded(item.getType(), item.getUUID(),
					item.getName(), item.getFillFactor());
		});

		// Print report
		logger.info("");
		printReport(logger, fManager);

		logger.info("");
		logger.info("Remove Yougurt");
		fManager.handleItemRemoved("YGRT");

		logger.info("Remove Tomatos");
		fManager.handleItemRemoved("TMT");

		// Print report
		logger.info("");
		printReport(logger, fManager);

		logger.info("");
		logger.info("Remove Beef");
		fManager.handleItemRemoved("BEEF");

		logger.info("Remove Bell Pepper");
		fManager.handleItemRemoved("BP");

		logger.info("Remove Salami");
		fManager.handleItemRemoved("SAL");

		// Print report
		logger.info("");
		printReport(logger, fManager);

		// Print low items report
		printLowItemsReport(logger, fManager, 0.4);
	}

	/**
	 * Prints items types and their fill factor.
	 * 
	 * @param logger   logger to output the results
	 * @param fManager {@link SmartFridgeManager}
	 */
	private static void printReport(Logger logger,
			SmartFridgeManager fManager) {
		logger.info("Fridge report:");
		itemTypes.forEach((k, v) -> {
			Double f1 = fManager.getFillFactor(k);
			double percent = f1.doubleValue() * 100.0;
			logger.info(String.format("%s - %3.0f%%", v, percent));
		});
	}

	/**
	 * Prints low items report. FillFactor indicates the
	 * 
	 * @param logger     logger to output the results
	 * @param fManager   {@link SmartFridgeManager}
	 * @param fillFactor indicates the low items threshold
	 */
	private static void printLowItemsReport(Logger logger,
			SmartFridgeManager fManager, Double fillFactor) {
		Object[] items = fManager.getItems(fillFactor);
		if (items != null) {
			logger.info("");
			String message = String.format(
					"We are running low (less then %2.0f%%) on:",
					fillFactor * 100.0);
			logger.info(message);
			for (int i = 0; i < items.length; i++) {
				double[] array = (double[]) items[i]; // array of two elements - [ itemType, fillFactor ]
				long t = (long) array[0];
				double p = array[1] * 100;
				String typeName = itemTypes.get(t);
				if (typeName != null) {
					message = String.format("%s - %3.0f%%", typeName, p);
					logger.info(message);
				}
			}
		}
	}
}
