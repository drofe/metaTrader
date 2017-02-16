package org.bergefall.trails;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public abstract class EquityLinePlotter extends ZoomableLineChart {

	protected List<Series<Number, Number>> getData() {
		try {
			bufRead = getBufferToTraverse();
			String line;
			final Series<Number, Number> tot = new Series<>();
			final Series<Number, Number> cash = new Series<>();
			final Series<Number, Number> hm = new Series<>();
	        tot.setName("Total");
	        cash.setName("Cash");
	        hm.setName("HM");
	        int ctr = 0;
			while ((line = bufRead.readLine()) != null) {
				String[] data = line.split(";");
				if (2 == data.length) {
					String[] cashData = data[1].split(",");
					cash.getData().add(new Data<Number, Number>(ctr, Long.valueOf(cashData[3]) / 1000_000L));
					tot.getData().add(new Data<Number, Number>(ctr, Long.valueOf(cashData[3]) / 1000_000L));
				} else if (3 == data.length){
					String[] cashData = data[2].split(",");
					String[] hmData = data[1].split(",");
					cash.getData().add(new Data<Number, Number>(ctr, Long.valueOf(cashData[3]) / 1000_000L));
					hm.getData().add(new Data<Number, Number>(ctr, Long.valueOf(hmData[3]) / 1000_000L));
					tot.getData().add(new Data<Number, Number>(ctr, (Long.valueOf(hmData[3]) + 
							Long.valueOf(cashData[3]))/ 1000_000L));
				}
				ctr++;
			}
			List<Series<Number, Number>> result = new ArrayList<>();
			result.add(tot);
			result.add(cash);
			result.add(hm);
			return result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	protected abstract BufferedReader getBufferToTraverse();
	
}
