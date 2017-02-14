package org.bergefall.trails;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;

public class MovingAveragePlotter extends ZoomableLineChart {
	public static void main(String[] args) {
		launch(args);
	}

	protected List<Series<Number, Number>> getData() {
		try {
			file = new File("/home/ola/hacks/metaTrader/strategyEngine/PriceMAData2017-02-13--17.38.56.433+1100.log");
			fileRead = new FileReader(file);
			bufRead = new BufferedReader(fileRead);
			String line;
			final Series<Number, Number> price = new Series<>();
			final Series<Number, Number> slow = new Series<>();
			final Series<Number, Number> fast = new Series<>();
	        price.setName("Price");
	        slow.setName("Slow Average");
	        fast.setName("Fast Average");
	        int ctr = 0;
	        bufRead.readLine(); //Read headings.
			while ((line = bufRead.readLine()) != null) {
				String[] data = line.split(",");
				if (5 != data.length) {
					continue;
				}
				slow.getData().add(new Data<Number, Number>(ctr, Long.valueOf(data[3]) / 1000_000L));
				fast.getData().add(new Data<Number, Number>(ctr, Long.valueOf(data[4]) / 1000_000L));
				price.getData().add(new Data<Number, Number>(ctr, Long.valueOf(data[2])/ 1000_000L));
				ctr++;
			}
			List<Series<Number, Number>> result = new ArrayList<>();
			result.add(price);
			result.add(slow);
			result.add(fast);
			return result;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
