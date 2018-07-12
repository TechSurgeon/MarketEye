package com.sa.gui.charts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickMarkPosition;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import com.sa.marketslayer.library.databeans.MarketDay;
import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.utils.PersistDBUtil;

/**
 * @author C
 */

public class MarketEyeHighLowChart extends ApplicationFrame implements ActionListener{

	//private static SecurityManager sm = new SecurityManager();
	//private static HistoricalDataManager hm = new HistoricalDataManager();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel controlPanel;
	private JPanel viewPanel;
	private JTextField symbolController;
	//private static DateFormat format = DateFormat.getDateInstance();
	
    /**
     * A demonstration application showing a high-low-open-close chart.
     *
     * @param title  the frame title.
     */
    public MarketEyeHighLowChart(String title) {

        super(title);
        
        //HashMap<String, Security> secs = (HashMap<String, Security>)PersistDBUtil.loadSecurities();
        
        Security graphMe = PersistDBUtil.loadSecurity("UCHB.ob");
        HashMap<Date, MarketDay> hist = (HashMap<Date, MarketDay>)PersistDBUtil.load_date_md_map(graphMe, graphMe.getId());

        //Security graphMe = secs.get("UCHB.ob");
        System.out.println(graphMe);
        controlPanel = new JPanel(new GridLayout(2,1));
        symbolController = new JTextField();
        symbolController.setSize(100,50);
        symbolController.addActionListener(this);
        JPanel inputPanel = new JPanel(new GridLayout(1,2));

        
        JButton updateButton = new JButton("Update");
        updateButton.setSize(100,50);
        updateButton.addActionListener(this);
        
        
        inputPanel.add(updateButton);
        inputPanel.add(symbolController);
        inputPanel.setSize(200, 50);
        inputPanel.setPreferredSize(new Dimension(200,50));
        controlPanel.add(inputPanel, 0);
        setContentPane(controlPanel);
        updateView(graphMe, hist);

    }
    
    public void updateView(Security graphMe, HashMap<Date, MarketDay> hist){

        viewPanel = openViewer(graphMe, hist);
        viewPanel.setSize(500,600);
        //controlPanel.add(viewPanel, 1);
        //this.remove(oldPanel);

        //this.repaint();
        JFrame showMe = new JFrame();
        showMe.setPreferredSize(new Dimension(500,600));
        showMe.setContentPane(viewPanel);
        viewPanel.setPreferredSize(new Dimension(500,600));
        viewPanel.setSize(500,600);
        showMe.pack();
        showMe.setVisible(true);
        
    }
    
    public void actionPerformed(ActionEvent e){
    	//if(e.getActionCommand().equals("Update")){
    		String sym = symbolController.getText();
    		Security viewMe = PersistDBUtil.loadSecurity(sym);
    		if(viewMe==null) {
    			System.out.println("Symbol not found " + sym);
    			return;
    		}
    		else{
    			System.out.println(viewMe);
    	        HashMap<Date, MarketDay> hist = (HashMap<Date, MarketDay>)PersistDBUtil.load_date_md_map(viewMe, viewMe.getId());
    	        //viewPanel = openViewer(viewMe, hist);
    	        updateView(viewMe,hist);
    		}
    		
    	//}
    }
    
    private static JPanel openViewer(Security viewMe, HashMap<Date, MarketDay> hist){

        
        OHLCDataset dataset = createPriceDataset(viewMe, hist);
        IntervalXYDataset volumeDataset= createVolumeDataSet(hist);
        IntervalXYDataset dollarvolumeDataset= createDollarVolumeDataSet(hist);
        
        JFreeChart chart = /*createOHLCChart*/createOHLCChart(dataset, volumeDataset, dollarvolumeDataset, viewMe);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        
        JFreeChart dollarVolchart = createDollarVolumeChart(dollarvolumeDataset);
        ChartPanel dollarVolchartPanel = new ChartPanel(dollarVolchart);
        dollarVolchartPanel.setPreferredSize(new java.awt.Dimension(500, 120));
        
        //chartPanel.add(dollarVolchart);
        JPanel wrapper = new JPanel(new GridLayout(2,1));
        wrapper.add(chartPanel);
        wrapper.add(dollarVolchartPanel);
        
        return wrapper;
    }
    
    private static JFreeChart createOHLCChart(OHLCDataset dataset, IntervalXYDataset volumeDataSet,IntervalXYDataset dollarvolumeDataSet, Security graphMe) {
        JFreeChart chart = ChartFactory.createHighLowChart(
                graphMe.getName()+" ("+graphMe.getLookup()+")", "Date", "Open-High-Low-Close", dataset, true);
        XYPlot plot = (XYPlot) chart.getPlot();
        //plot.setSeriesPaint(0, Color.blue);
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

        NumberAxis rangeAxis2 = new NumberAxis("Volume");
        rangeAxis2.setUpperMargin(1.00);  // to leave room for price line
        plot.setRangeAxis(1, rangeAxis2);
        plot.setDataset(1, volumeDataSet);
        plot.setRangeAxis(1, rangeAxis2);
        plot.mapDatasetToRangeAxis(1, 1);
        XYBarRenderer renderer2 = new XYBarRenderer(0.20);
        renderer2.setToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")
            )
        );
        plot.setRenderer(1, renderer2);
        
       /* NumberAxis rangeAxis3 = new NumberAxis("Dollar Volume");
        rangeAxis3.setUpperMargin(1.00);  // to leave room for price line
        plot.setRangeAxis(2, rangeAxis3);
        plot.setDataset(2, dollarvolumeDataSet);
        plot.setRangeAxis(2, rangeAxis3);
        plot.mapDatasetToRangeAxis(2, 1);
        XYBarRenderer renderer3 = new XYBarRenderer(0.20);
        renderer3.setToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")
            )
        );
        plot.setRenderer(2, renderer3);*/
        
        return chart;
    }
    
    
 /*   private static JFreeChart createOHLCChartWithDollarVolume(OHLCDataset dataset, IntervalXYDataset volumeDataSet, IntervalXYDataset dollarvolumeDataSet, Security graphMe) {
        JFreeChart chart = ChartFactory.createHighLowChart(
                graphMe.getName()+" ("+graphMe.getLookup()+")", "Date", "Open-High-Low-Close", dataset, true);
        XYPlot plot = (XYPlot) chart.getPlot();
        //plot.setSeriesPaint(0, Color.blue);
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);

        NumberAxis rangeAxis2 = new NumberAxis("Volume");
        rangeAxis2.setUpperMargin(1.00);  // to leave room for price line
        plot.setRangeAxis(1, rangeAxis2);
        plot.setDataset(1, volumeDataSet);
      //  plot.setDataset(2, dollarvolumeDataSet);
        plot.setRangeAxis(1, rangeAxis2);
        //plot.setRangeAxis(2, rangeAxis2);
        plot.mapDatasetToRangeAxis(1, 1);
        XYBarRenderer renderer2 = new XYBarRenderer(0.20);
        renderer2.setToolTipGenerator(
            new StandardXYToolTipGenerator(
                StandardXYToolTipGenerator.DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0,000.00")
            )
        );
        plot.setRenderer(1, renderer2);
       // plot.setRenderer(2, renderer2);
        
        ////
        JFreeChart chart2 = ChartFactory.createXYBarChart(
                "Dollar Volume",
                "Day",
                true,
                "USD",
                dollarvolumeDataSet,
                PlotOrientation.VERTICAL,
                true,
                false,
                false
            );

            // then customise it a little...
            chart.setBackgroundPaint(Color.white);
            
            XYPlot plot2 = (XYPlot) chart.getPlot(); 
            XYItemRenderer renderer = plot2.getRenderer();
            StandardXYToolTipGenerator generator = new StandardXYToolTipGenerator(
                "{1} = {2}", new SimpleDateFormat("yyyy"), new DecimalFormat("0"));
            renderer.setToolTipGenerator(generator);
            
            plot2.setBackgroundPaint(Color.lightGray);
            plot2.setRangeGridlinePaint(Color.white);
            DateAxis axis2 = (DateAxis) plot2.getDomainAxis();
            axis2.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
            axis2.setLowerMargin(0.01);
            axis2.setUpperMargin(0.01);
        ////
        // trying to add dollarvolume synched with X-axis
        CategoryPlot subplot1 = new CategoryPlot((CategoryDataset)dataset, null, axis, 
                (CategoryItemRenderer)renderer2);
        subplot1.setDomainGridlinesVisible(true);
        
        CategoryPlot subplot2 = new CategoryPlot((CategoryDataset)dollarvolumeDataSet, null, axis, 
        		(CategoryItemRenderer)renderer2);
        subplot2.setDomainGridlinesVisible(true);
        
        return chart;
    }*/
    
    private static JFreeChart createDollarVolumeChart(IntervalXYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYBarChart(
            "Dollar Volume",
            "Day",
            true,
            "USD",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            false,
            false
        );

        // then customise it a little...
        chart.setBackgroundPaint(Color.white);
        
        XYPlot plot = (XYPlot) chart.getPlot(); 
        XYItemRenderer renderer = plot.getRenderer();
        StandardXYToolTipGenerator generator = new StandardXYToolTipGenerator(
            "{1} = {2}", new SimpleDateFormat("d-MMM-yyyy"), new DecimalFormat("0"));
        renderer.setToolTipGenerator(generator);
        
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setTickMarkPosition(DateTickMarkPosition.MIDDLE);
        axis.setLowerMargin(0.01);
        axis.setUpperMargin(0.01);
        return chart;
    }
    
    /**
     * Creates a sample high low dataset.
     *
     * @return a sample high low dataset.
     */
    public static OHLCDataset createPriceDataset(Security sec, HashMap<Date, MarketDay> marketdays) {

    	Date[] dates = new Date[marketdays.keySet().size()];
    	dates = marketdays.keySet().toArray(dates);
        Arrays.sort(dates);
        System.out.println("Range["+dates.length+"]: "+dates[0]+" to "+dates[dates.length-1]);
      
        Date first = dates[0];
        Date last = dates[dates.length-1];
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2= Calendar.getInstance();
        calendar1.setTime(last);//new Calendar();
        calendar2.setTime(first);
        
        long millisInADay = 24*60*60*1000;//milli seconds in a day
        long start = calendar2.getTimeInMillis();
        long end = calendar1.getTimeInMillis();
        
        long diff = end - start;
        int sizeNeeded = (int)(diff/millisInADay);
        
        if(diff%millisInADay!=0){
        	//throw new RuntimeException("diff%millisInADay="+diff%millisInADay+ " millisecs in a day: "+millisInADay+" difference: "+(double)(((double)millisInADay-(diff%millisInADay))/1000)+" secs.");
        	System.out.println("Validation of date difference FAILED: "+"diff%millisInADay="+diff%millisInADay+ " millisecs in a day: "+millisInADay+" difference: "+(double)(((double)millisInADay-(diff%millisInADay))/1000)+" secs.");
        }
        else{
        	System.out.println("Validated date difference. days: "+sizeNeeded);
        }
        
        Date[] date = new Date[sizeNeeded];
        double[] high = new double[sizeNeeded];
        double[] low = new double[sizeNeeded];
        double[] open = new double[sizeNeeded];
        double[] close = new double[sizeNeeded];
        double[] volume = new double[sizeNeeded];
        int dataIndex =0;
       // int errors = 0;
        
        Date crtDate = first;///new Date(year-1900, month-1, day, 16 ,0 ,0 );//keys
        //crtDate.setHours(12);
        System.out.println("Starting analysis at "+crtDate);	
        
        for(int index = 0; index<sizeNeeded/*marketdays.keySet().size()*/;index++){
        	int yearNum = crtDate.getYear();
        	int monthNum = crtDate.getMonth();
        	int dayNum = crtDate.getDay();
        	//System.out.println("Current Date("+format.format(crtDate)+") being processed: D:"+dayNum+" M:"+monthNum+" Y:"+yearNum);
        	
        	Date dailyDate = dates[dataIndex];
        	MarketDay crt = marketdays.get(dailyDate);
        	if(dailyDate==null){//fatal, data has no integrity
        		throw new RuntimeException("null Date found for index+"+dataIndex+" D:"+dayNum+" M:"+monthNum+" Y:"+yearNum);
        	}

        	int yearNumMD = dailyDate.getYear();
        	int monthNumMD = dailyDate.getMonth();
        	int dayNumMD = dailyDate.getDay();
        	//System.out.println("Current MarketDay Date("+format.format(dailyDate)+") being processed: D:"+dayNumMD+" M:"+monthNumMD+" Y:"+yearNumMD);
        	//System.out.println("Match? "+yearNum+"?"+yearNumMD+" and "+monthNum+"?"+monthNumMD+" and "+dayNum+"?"+dayNumMD);
        	
        	if(yearNum==yearNumMD&&monthNum==monthNumMD&&dayNum==dayNumMD){// MATCH!

		        	//System.out.println("db date: "+d);
		        	//System.out.println("gen date vars: "+d.getYear()+" "+d.getMonth()+" "+d.getDay());
		        	//yyyy,m,d,h,m
			        date[index] = crtDate;//DateUtilities.createDate(1900+crtDate.getYear(), crtDate.getMonth()+1, crtDate.getDay(), 12, 0);
			        //System.out.println("+++Record+++ Adding record for: "+date[index]);
			        high[index] = (double)crt.high;
			        low[index] = (double)crt.low;
			        open[index] = (double)crt.open;
			        close[index] = (double)crt.close;
			        volume[index] = (double)crt.volume;

			        ++dataIndex;//increment to next marketDay
	        	}else{// add empty date
	            	if(dailyDate.before(crtDate)){// we are too late for adding this record! FATAL
	               		throw new RuntimeException("FATAL: Record's date:"+dailyDate+" but it's already:"+crtDate);
	            	}
			        date[index] = crtDate;//DateUtilities.createDate(1900+crtDate.getYear(), crtDate.getMonth()+1, crtDate.getDay(), 12, 0);
			       // System.out.println("+++++ Adding record for: "+date[index]);
			        high[index] = (double)0;
			        low[index] = (double)0;
			        open[index] = (double)0;
			        close[index] = (double)0;
			        volume[index] = (double)0;
	        }
        	calendar1.setTime(crtDate);
        	
        	crtDate = new Date(calendar1.getTimeInMillis()+millisInADay);
        	crtDate.setHours(12);
        	//System.out.println();
        	//System.out.println();
        }
        
        if(dataIndex<dates.length-2){
        	throw new RuntimeException("Not all Records read in!!! fill index: "+dataIndex+" length of time axis: "+(dates.length-1));
        }

        return new DefaultHighLowDataset(sec.name, date, high, low, open, close, volume);

    }

    /**
     * Creates a sample high low dataset.
     *
     * @return a sample high low dataset.
     */
    public static OHLCDataset createPriceDayDataset(Security sec, HashMap<Date, MarketDay> marketdays) {

    	Date[] dates = new Date[marketdays.keySet().size()];
    	dates = marketdays.keySet().toArray(dates);
        Arrays.sort(dates);
        System.out.println("Range["+dates.length+"]: "+dates[0]+" to "+dates[dates.length-1]);
      
        Date first = dates[0];
        Date last = dates[dates.length-1];
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2= Calendar.getInstance();
        calendar1.setTime(last);//new Calendar();
        calendar2.setTime(first);
        
        long millisInADay = 24*60*60*1000;//milli seconds in a day
        long start = calendar2.getTimeInMillis();
        long end = calendar1.getTimeInMillis();
        
        long diff = end - start;
        int sizeNeeded = (int)(diff/millisInADay);
        
        if(diff%millisInADay!=0){
        	//throw new RuntimeException("diff%millisInADay="+diff%millisInADay+ " millisecs in a day: "+millisInADay+" difference: "+(double)(((double)millisInADay-(diff%millisInADay))/1000)+" secs.");
        	System.out.println("Validation of date difference FAILED: "+"diff%millisInADay="+diff%millisInADay+ " millisecs in a day: "+millisInADay+" difference: "+(double)(((double)millisInADay-(diff%millisInADay))/1000)+" secs.");
        }
        else{
        	System.out.println("Validated date difference. days: "+sizeNeeded);
        }
        
        //Date[] day = new Date[sizeNeeded];
        Day[] day = new Day[sizeNeeded];
        double[] high = new double[sizeNeeded];
        double[] low = new double[sizeNeeded];
        double[] open = new double[sizeNeeded];
        double[] close = new double[sizeNeeded];
        double[] volume = new double[sizeNeeded];
        int dataIndex =0;
       // int errors = 0;
        Date crtDate = first;///new Date(year-1900, month-1, day, 16 ,0 ,0 );//keys
        //crtDate.setHours(12);
        System.out.println("Starting analysis at "+crtDate);	
        
        for(int index = 0; index<sizeNeeded/*marketdays.keySet().size()*/;index++){
	        crtDate = dates[index];
	        MarketDay now = marketdays.get(crtDate);
        	//day[index] = new Day(crtDate);//DateUtilities.createDate(1900+crtDate.getYear(), crtDate.getMonth()+1, crtDate.getDay(), 12, 0);
	        System.out.println("+++++ Adding record for: "+day[index]);
	        high[index] = now.high;
	        low[index] = now.low;
	        open[index] = now.open;
	        close[index] = now.close;
	        volume[index] = now.volume;

/*	        }
        	calendar1.setTime(crtDate);
        	
        	crtDate = new Date(calendar1.getTimeInMillis()+millisInADay);
        	crtDate.setHours(12);
        	System.out.println();
        	System.out.println();*/
        }
        
        if(dataIndex<dates.length-2){
        	throw new RuntimeException("Not all Records read in!!! fill index: "+dataIndex+" length of time axis: "+(dates.length-1));
        }
/*        Date crtDate = first;///new Date(year-1900, month-1, day, 16 ,0 ,0 );//keys
        //crtDate.setHours(12);
        System.out.println("Starting analysis at "+crtDate);	
        
        for(int index = 0; index<sizeNeededmarketdays.keySet().size();index++){
        	int yearNum = crtDate.getYear();
        	int monthNum = crtDate.getMonth();
        	int dayNum = crtDate.getDay();
        	System.out.println("Current Date("+format.format(crtDate)+") being processed: D:"+dayNum+" M:"+monthNum+" Y:"+yearNum);
        	
        	Date dailyDate = dates[dataIndex];
        	MarketDay crt = marketdays.get(dailyDate);
        	if(dailyDate==null){//fatal, data has no integrity
        		throw new RuntimeException("null Date found for index+"+dataIndex+" D:"+dayNum+" M:"+monthNum+" Y:"+yearNum);
        	}

        	int yearNumMD = dailyDate.getYear();
        	int monthNumMD = dailyDate.getMonth();
        	int dayNumMD = dailyDate.getDay();
        	System.out.println("Current MarketDay Date("+format.format(dailyDate)+") being processed: D:"+dayNumMD+" M:"+monthNumMD+" Y:"+yearNumMD);
        	System.out.println("Match? "+yearNum+"?"+yearNumMD+" and "+monthNum+"?"+monthNumMD+" and "+dayNum+"?"+dayNumMD);
        	
        	if(yearNum==yearNumMD&&monthNum==monthNumMD&&dayNum==dayNumMD){// MATCH!

		        	//System.out.println("db date: "+d);
		        	//System.out.println("gen date vars: "+d.getYear()+" "+d.getMonth()+" "+d.getDay());
		        	//yyyy,m,d,h,m
			        date[index] = crtDate;//DateUtilities.createDate(1900+crtDate.getYear(), crtDate.getMonth()+1, crtDate.getDay(), 12, 0);
			        System.out.println("+++Record+++ Adding record for: "+date[index]);
			        high[index] = (double)crt.high;
			        low[index] = (double)crt.low;
			        open[index] = (double)crt.open;
			        close[index] = (double)crt.close;
			        volume[index] = (double)crt.volume;

			        ++dataIndex;//increment to next marketDay
	        	}else{// add empty date
	            	if(dailyDate.before(crtDate)){// we are too late for adding this record! FATAL
	               		throw new RuntimeException("FATAL: Record's date:"+dailyDate+" but it's already:"+crtDate);
	            	}
			        date[index] = crtDate;//DateUtilities.createDate(1900+crtDate.getYear(), crtDate.getMonth()+1, crtDate.getDay(), 12, 0);
			        System.out.println("+++++ Adding record for: "+date[index]);
			        high[index] = (double)0;
			        low[index] = (double)0;
			        open[index] = (double)0;
			        close[index] = (double)0;
			        volume[index] = (double)0;
	        }
        	calendar1.setTime(crtDate);
        	
        	crtDate = new Date(calendar1.getTimeInMillis()+millisInADay);
        	crtDate.setHours(12);
        	System.out.println();
        	System.out.println();
        }
        
        if(dataIndex<dates.length-2){
        	throw new RuntimeException("Not all Records read in!!! fill index: "+dataIndex+" length of time axis: "+(dates.length-1));
        }*/

        return new DefaultHighLowDataset(sec.name, dates, high, low, open, close, volume);

    }
    
    public static IntervalXYDataset createVolumeDataSet(HashMap<Date, MarketDay> hist){
    	TimeSeries series1 = new TimeSeries("Volume", Day.class);
    	
    	
    	for(Date d : hist.keySet() ){
    		series1.add(new Day(d), hist.get(d).volume);
    	}
    		
    	return new TimeSeriesCollection(series1);
    }
    
    public static IntervalXYDataset createDollarVolumeDataSet(HashMap<Date, MarketDay> hist){
    	TimeSeries series1 = new TimeSeries("Dollar Volume [(open+close)/2*volume]", Day.class);
    	
    	
    	for(Date d : hist.keySet() ){
    		series1.add(new Day(d), ((hist.get(d).open+hist.get(d).close)/2)*hist.get(d).volume);
    	}
    		
    	return new TimeSeriesCollection(series1);
    }
    /**
     * Creates a panel for the demo (used by SuperDemo.java).
     * 
     * @return A panel.
     */
    public static JPanel createDemoPanel() {
        Security graphMe = PersistDBUtil.loadSecurity("UCHB.ob");
        //Security graphMe = secs.get("UCHB.ob");
        System.out.println(graphMe);
        
        HashMap<Date, MarketDay> hist = (HashMap<Date, MarketDay>)PersistDBUtil.load_date_md_map(graphMe, graphMe.getId());

        OHLCDataset dataset = createPriceDataset(graphMe, hist);
        IntervalXYDataset volumeDataSet = createVolumeDataSet(hist);
        IntervalXYDataset dollarvolumeDataSet = createDollarVolumeDataSet(hist);
        
        JFreeChart chart = createOHLCChart(dataset, volumeDataSet,dollarvolumeDataSet,graphMe);
        return new ChartPanel(chart);
    }
    
    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(String[] args) {

    	MarketEyeHighLowChart demo = new MarketEyeHighLowChart("High-Low-Open-Close");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}
