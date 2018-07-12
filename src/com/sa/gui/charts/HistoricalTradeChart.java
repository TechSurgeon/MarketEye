package com.sa.gui.charts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class HistoricalTradeChart /*extends JFreeChart*/ {

	public HistoricalTradeChart(){
		
	}
	
	public JFreeChart createHistoricalChart(){
		XYSeries series = new XYSeries("Average Size");
		series.add(20.0, 10.0);
		series.add(40.0, 20.0);
		series.add(70.0, 50.0);
		
		DatasetGroup dsg = new DatasetGroup();
		
		XYDataset xyDataset = new XYSeriesCollection();
		xyDataset.setGroup(dsg);
		
		JFreeChart chart = ChartFactory.createXYLineChart
							("Sample XY Chart",  // Title
		                      "Height",           // X-Axis label
		                      "Weight",           // Y-Axis label
		                      xyDataset,          // Dataset
		                      PlotOrientation.VERTICAL,
		                      true ,               // Show legend
		                      true,
		                      true
		                      );
		return chart;
	}
}
