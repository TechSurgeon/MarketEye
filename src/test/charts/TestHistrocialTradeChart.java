package test.charts;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

import com.sa.gui.charts.HistoricalTradeChart;

public class TestHistrocialTradeChart extends TestCase {
	
	public void testCreateChart(){
		HistoricalTradeChart htc = new HistoricalTradeChart();
		
		JFreeChart chart = htc.createHistoricalChart();
		
		try {
			ChartUtilities.saveChartAsJPEG(new File("C:/0_My_Real_Documents/Eclipse/eclipse/workspace/crap/chart.jpg"), chart, 500, 300);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
