package org.bergefall.dbstorage.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import org.bergefall.common.data.MarketDataCtx;
import org.bergefall.common.log.system.SystemLoggerIf;
import org.bergefall.common.log.system.SystemLoggerImpl;

public class CsvReader {

	private static String cSymb;

	private static long cFactor = 1_000_000;

	private static final DateTimeFormatter cFormatter =
			DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final String cSepIdentifier = "sep=";
	private static String cSep = ";";
	private static final String cDate = "Date";
	private static int cDateIdx;
	private static final String cBid = "Bid";
	private static int cBidIdx;
	private static final String cAsk = "Ask";
	private static int cAskIdx;
	private static final String cOpen = "Opening price";
	private static int cOpenIdx;
	private static final String cHigh = "High price";
	private static int cHighIdx;
	private static final String cClose = "Closing price";
	private static int cCloseIdx;
	private static final String cLow = "Low price";
	private static int cLowIdx;
	private static final String cAvg = "Average price";
	private static int cAvgIdx;
	private static final String cTotVol = "Total volume";
	private static int cTotVolIdx;
	private static final String cTurnover = "Turnover";
	private static int cTurnoverIdx;
	private static final String cTrades = "Trades";
	private static int cTradesIdx;
	
	private static SystemLoggerIf log = SystemLoggerImpl.get();

	private File file;
	private FileReader fileRead = null;
	private BufferedReader bufRead = null;

	private CsvReader() {
	}

	public CsvReader(String pFile, String symb) {
		this();
		file = new File(pFile);
		if ((file.exists() && !file.isDirectory()) == false) {
			throw new RuntimeException("File (" + pFile + ") did not exist");
		}
		cSymb = symb;
		log.setLogInOwnThread(true);
	}

	public Set<MarketDataCtx> getFileContents() {
		Set<MarketDataCtx> tPrices = new HashSet<MarketDataCtx>();
		if(!initReaders()) {
			System.out.println("Failed to initialize readers!");
			return tPrices;
		}

		try {
			// Get seperator, first line should contain it.
			String tFirstLine = bufRead.readLine();
			int tIdx = tFirstLine.indexOf(cSepIdentifier);
			cSep = tFirstLine.substring(tIdx + cSepIdentifier.length());
			if (cSep.length() != 1) {
				throw new RuntimeException("Failed to find separator");
			}
			//Get column indices. Second line shall contain headings
			extractIndices(bufRead.readLine());
			String line;
			while ((line = bufRead.readLine()) != null) {
				log.trace("READING LINE: " + line);
				tPrices.add(getPricesFromLine(line));
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeReaders();
		}

		return tPrices;
	}

	private MarketDataCtx getPricesFromLine(String line) {
		String[] data = line.split(cSep);
		LocalDateTime ldt = LocalDate.parse(data[cDateIdx], cFormatter).atStartOfDay();
		MarketDataCtx price = new MarketDataCtx(cSymb,
				ldt,
				convertData(data[cOpenIdx]),
				convertData(data[cCloseIdx]),
				convertData(data[cAvgIdx]),
				convertData(data[cHighIdx]),
				convertData(data[cLowIdx]),
				convertData(data[cAskIdx]),
				convertData(data[cBidIdx]),
				convertData(data[cTradesIdx]),
				convertData(data[cTotVolIdx]),
				convertData(data[cTurnoverIdx]));
		return price;
	}
	private long convertData(String data) {
		if (data.contains(",")) {
			data = data.replace(',', '.');
		}
		if (data.isEmpty()) {
			return 0L;
		}
		Double dbl = Double.valueOf(data);
		dbl *= cFactor;
		return dbl.longValue();
	}

	private void extractIndices(String readLine) {
		if (null == readLine) {
			throw new RuntimeException("No line for headers!");
		}
		String[] headers = readLine.split(cSep);
		for (int idx = 0; idx < headers.length; idx++) {
			String header = headers[idx].trim();
			switch (header) {
			case cClose :
				cCloseIdx = idx;
				break;
			case cOpen :
				cOpenIdx = idx;
				break;
			case cAsk:
				cAskIdx = idx;
				break;
			case cAvg :
				cAvgIdx = idx;
				break;
			case cBid :
				cBidIdx = idx;
				break;
			case cDate :
				cDateIdx = idx;
				break;
			case cHigh :
				cHighIdx = idx;
				break;
			case cLow :
				cLowIdx = idx;
				break;
			case cTurnover :
				cTurnoverIdx = idx;
				break;
			case cTotVol :
				cTotVolIdx = idx;
				break;
			case cTrades :
				cTradesIdx = idx;
				break;
			default:
				//By design
			}
		}
	}

	private boolean initReaders() {
		try {
			fileRead = new FileReader(file);
			bufRead = new BufferedReader(fileRead);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void closeReaders() {
		if (null != fileRead) {
			try {
				fileRead.close();
			} catch (IOException e) {}
		}

		if (null != bufRead) {
			try {
				bufRead.close();
			} catch (IOException e) {}
		}
	}
}
