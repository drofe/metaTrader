package org.bergefall.trails;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class PlotEquityLineFromFile extends EquityLinePlotter {

	public PlotEquityLineFromFile() {
		// TODO Auto-generated constructor stub
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	protected BufferedReader getBufferToTraverse() {
		file = new File("/home/ola/hacks/metaTrader/strategyEngine/PortfolioValue-2017-02-13--17.38.56.443+1100.log");
		try {
			fileRead = new FileReader(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new BufferedReader(fileRead);
	}

}
