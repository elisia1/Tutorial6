package nz.ac.vuw.swen301.tuts.log4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The purpose of this class is to read and merge financial transactions, and print a summary:
 * - total amount
 * - highest/lowest amount
 * - number of transactions
 *
 * Console printing (System.out / System.err / printStackTrace) has been replaced by Log4j 2.
 * Two named loggers are used:
 *   - FILE:         file-access events (which file is read, missing files, read/close errors).
 *                   Configured in log4j2.xml to write to BOTH logs.txt and the console.
 *   - TRANSACTIONS: transaction data (each parsed purchase, the final summary).
 *                   Configured in log4j2.xml to write to the console ONLY.
 *
 * @author jens dietrich
 */
public class MergeTransactions {

	// Named loggers: matched by name in src/main/resources/log4j2.xml
	private static final Logger FILE = LogManager.getLogger("FILE");
	private static final Logger TRANSACTIONS = LogManager.getLogger("TRANSACTIONS");

	private static DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
	private static NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(Locale.getDefault());

	public static void main(String[] args) {
		List<Purchase> transactions = new ArrayList<Purchase>();

		// read data from 4 files
		readData("transactions1.csv", transactions);
		readData("transactions2.csv", transactions);
		readData("transactions3.csv", transactions);
		readData("transactions4.csv", transactions);

		// print some info for the user  ->  INFO
		TRANSACTIONS.info("" + transactions.size() + " transactions imported");
		TRANSACTIONS.info("total value: " + CURRENCY_FORMAT.format(computeTotalValue(transactions)));
		TRANSACTIONS.info("max value: " + CURRENCY_FORMAT.format(computeMaxValue(transactions)));
	}

	private static double computeTotalValue(List<Purchase> transactions) {
		double v = 0.0;
		for (Purchase p : transactions) {
			v = v + p.getAmount();
		}
		return v;
	}

	private static double computeMaxValue(List<Purchase> transactions) {
		double v = 0.0;
		for (Purchase p : transactions) {
			v = Math.max(v, p.getAmount());
		}
		return v;
	}

	// read transactions from a file, and add them to a list
	private static void readData(String fileName, List<Purchase> transactions) {

		File file = new File(fileName);
		String line = null;
		// print info for user  ->  INFO (file-access event -> FILE logger)
		FILE.info("import data from " + fileName);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				String[] values = line.split(",");
				Purchase purchase = new Purchase(
						values[0],
						Double.parseDouble(values[1]),
						DATE_FORMAT.parse(values[2])
				);
				transactions.add(purchase);
				// this is for debugging only  ->  DEBUG
				TRANSACTIONS.debug("imported transaction " + purchase);
			}
		} catch (FileNotFoundException x) {
			// print warning  ->  WARN (pass the exception so the stack trace is logged)
			FILE.warn("file " + fileName + " does not exist - skip", x);
		} catch (IOException x) {
			// print error message and details  ->  ERROR
			FILE.error("problem reading file " + fileName, x);
		}
		// happens if date parsing fails
		catch (ParseException x) {
			// print error message and details  ->  ERROR
			FILE.error("cannot parse date from string - please check whether syntax is correct: " + line, x);
		}
		// happens if double parsing fails
		catch (NumberFormatException x) {
			// print error message and details  ->  ERROR
			FILE.error("cannot parse double from string - please check whether syntax is correct: " + line, x);
		} catch (Exception x) {
			// any other exception
			// print error message and details  ->  ERROR
			FILE.error("exception reading data from file " + fileName + ", line: " + line, x);
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				// print error message and details  ->  ERROR
				FILE.error("cannot close reader used to access " + fileName, e);
			}
		}
	}

}
