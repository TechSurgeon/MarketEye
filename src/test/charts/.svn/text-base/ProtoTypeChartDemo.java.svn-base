package test.charts;

import java.awt.Color;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYBubbleRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import com.sa.marketslayer.library.databeans.MarketDay;
import com.sa.marketslayer.library.databeans.RBMessageTrail;
import com.sa.marketslayer.library.databeans.Security;
import com.sa.marketslayer.utils.PersistDBUtil;
import com.sa.marketslayer.utils.PersistFileUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This demo shows a time series chart that has multiple range axes.
 */
public class ProtoTypeChartDemo extends ApplicationFrame {

	private static Logger logger = Logger.getLogger("ProtoTypeChartDemo");
	
    /**
     * A demonstration application showing how to create a time series chart with muliple axes.
     *
     * @param title  the frame title.
     */
    public ProtoTypeChartDemo(final String title) {

        super(title);
        
        HashMap<String, Security> secs = (HashMap<String, Security>)PersistDBUtil.loadSecurities();
        
        Security graphMe = secs.get("UCHB");
        System.out.println(graphMe);
        
        HashMap<Date, MarketDay> hist = (HashMap<Date, MarketDay>)PersistDBUtil.load_date_md_map(graphMe, graphMe.getId());
        
		//String testSym="UCHB";
		//String testFileLoc = "C:/0_My_Real_Documents/Eclipse/eclipse/workspace/MarketSlayer/data/system/OTCBB/Historical/";
		//String fileName = testSym.toUpperCase()+".his";

		//RBMessageTrail tm = new RBMessageTrail();
		//Security sec = new Security("UCHB.ob", "OTCBB", "UCHB", "UCHB");
		//XStream xstream=new XStream(new DomDriver());
		//HashMap<Date, MarketDay> hist = null;
		
		//String xml = PersistFileUtil.openFile(testFileLoc);
		
		//System.out.println("done reading xml "+xml);
/*		File tempSecurityFile = new File(testFileLoc+fileName);
		String tempSecurityFileLoc = testFileLoc+fileName;
		if (tempSecurityFile.exists()){
			try {
				InputStream closeMe = PersistFileUtil.getInputStream(tempSecurityFileLoc);
				hist = (HashMap<Date, MarketDay>) xstream.fromXML(closeMe);//closeMe);
				closeMe.close();
				logger.info("Loaded Security "+testSym+" from FS");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.info("Failed to load "+testSym+" from FS");
			}
		}*/
        
	/*	String fileName2 = testSym.toUpperCase()+".rb";
		File temp2 = new File(testFileLoc+fileName2);
		if (temp2.exists()){
			try {
				InputStream closeMe = PersistFileUtil.getInputStream(testFileLoc+fileName2);
				tm = (RBMessageTrail) xstream.fromXML(closeMe);//closeMe);
				closeMe.close();
				logger.info("Loaded RBMessageTrail for "+testSym+" from FS");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				logger.info("Failed to load "+testSym+" RBMessageTrail from FS");
			}
		}
		else{
			logger.info("No RBMessages found on FS for "+sec.symbol+"\nAttempting to download RBMessageTrail.");
			
		}*/
	
        final JFreeChart chart = createChart(graphMe, hist);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(600, 270));
        //chartPanel.setHorizontalZoom(true);
        //chartPanel.setVerticalZoom(true);
        setContentPane(chartPanel);

    }

    /**
     * Creates the demo chart.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(Security stock, HashMap<Date, MarketDay> hist) {

    	Date[] dates = new Date[hist.keySet().size()];
        dates = hist.keySet().toArray(dates);

        Arrays.sort(dates);
        Day d = new Day(dates[0]);
        final XYDataset dataset1 = createCloseChart("Open (Price per Share)", d, stock, "open", hist);
        
        System.out.println("day 1: "+d.toString());
        System.out.println("last day: "+dates[dates.length-1].toString());
        //Day d = new Day();
        final JFreeChart chart = ChartFactory.createTimeSeriesChart(
            stock.lookup+" "+stock.exchange, 
            "Day of Month", 
            "Open (Price per Share)",
            dataset1, 
            true, 
            true, 
            false
        );

        chart.setBackgroundPaint(Color.white);
        //chart.addSubtitle(new TextTitle(""));  
        final XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        
        //UnitType ut = new UnitType();
        plot.setAxisOffset(new RectangleInsets(1.0,1.0,1.0,1.0));//new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));
        
        //final StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
        // XYLineAndShapeRenderer
        final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setPaint(Color.black);
       
        // AXIS 2
        final NumberAxis axis2 = new NumberAxis("Share Trade Volume");
        axis2.setAutoRangeIncludesZero(false);
        axis2.setLabelPaint(Color.red);
        axis2.setTickLabelPaint(Color.red);
        plot.setRangeAxis(1, axis2);
        plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_LEFT);

        final IntervalXYDataset dataset2 = (IntervalXYDataset)createCloseChart("Share Trade Volume", d, stock, "volume", hist);
/*        XYBubbleRenderer renderer = new XYBubbleRenderer(0);
        renderer.setSeriesPaint(0,new Color(0,255,0,118));
        plot.setDataset(1,intervalData);
        plot.setRenderer(1,renderer);*/
        plot.setDataset(1, dataset2);
        plot.mapDatasetToRangeAxis(1, 1);
        XYBubbleRenderer renderer2 = new XYBubbleRenderer(0);
        //XYItemRenderer renderer2 = new StandardXYItemRenderer();
        renderer2.setSeriesPaint(0, Color.CYAN);
        plot.setRenderer(1, renderer2);
        
       // AXIS 3
        final NumberAxis axis3 = new NumberAxis("High of Day");
        axis3.setLabelPaint(Color.blue);
        axis3.setTickLabelPaint(Color.blue);
        plot.setRangeAxis(2, axis3);

        final XYDataset dataset3 = createCloseChart("Highest price of Day", d, stock, "high", hist);
        plot.setDataset(2, dataset3);
        plot.mapDatasetToRangeAxis(2, 2);
        XYItemRenderer renderer3 = new StandardXYItemRenderer();
        renderer3.setSeriesPaint(0, Color.blue);
        plot.setRenderer(2, renderer3);

        // AXIS 4        
        final NumberAxis axis4 = new NumberAxis("Low of Day");
        axis4.setLabelPaint(Color.green);
        axis4.setTickLabelPaint(Color.green);
        plot.setRangeAxis(2, axis4);
        
        final XYDataset dataset4 = createCloseChart("Lowest price of Day", d, stock, "low", hist);
        plot.setDataset(2, dataset4);
        plot.mapDatasetToRangeAxis(2, 2);
        
        XYItemRenderer renderer4 = new StandardXYItemRenderer();
        renderer4.setSeriesPaint(0, Color.green);        
        plot.setRenderer(2, renderer4);
        
        // AXIS 5        
        final NumberAxis axis5 = new NumberAxis("Close Price");
        axis5.setLabelPaint(Color.yellow);
        axis5.setTickLabelPaint(Color.yellow);
        plot.setRangeAxis(2, axis5);
        
        final XYDataset dataset5 = createCloseChart("Close Price", d, stock, "close", hist);
        plot.setDataset(2, dataset5);
        plot.mapDatasetToRangeAxis(2, 2);
        
        XYItemRenderer renderer5 = new StandardXYItemRenderer();
        renderer5.setSeriesPaint(0, Color.yellow);        
        plot.setRenderer(2, renderer5);
                
        return chart;
    }
    
    //"Price per Share", 100.0, new Minute(), 200
    private XYDataset createCloseChart(final String name,
    									final RegularTimePeriod start,
    									Security stock, String field, HashMap<Date, MarketDay> hist){
        final TimeSeries series = new TimeSeries(name, start.getClass());
        RegularTimePeriod period = start;
        Date[] dates = new Date[hist.keySet().size()];
        dates = hist.keySet().toArray(dates);

        Arrays.sort(dates);
        Calendar cal = Calendar.getInstance();
        cal.set(dates[0].getHours(), dates[0].getMonth(), dates[0].getDate());
        
        period = start;
        Date old = dates[0];
       // old.setDate(old.getDay());
        
        int counter =0;
        int daysDifference = 0;
        outer:for(Date d : dates){
        	//daysDifference = old.getDate()-d.getDate();
        	//if(d.getDate()<old.getDate() || daysDifference==1)
        	//	period = period.next();
        	//else{System.out.println("special jump in date: "+daysDifference);
        	//	period = period.next();
/*        		for(int k=1; k-1<((-1)*daysDifference);k++){
        			period = period.next();			
        		}*/

        	//}
            long l1 = old.getTime();
            long l2 = d.getTime();
            long difference = l2 - l1; // diff in milliseconds
            int days = (int)(difference/(24*60*60*1000));
        	
/*        	while(cal.DAY_OF_MONTH!=d.getDay()&& cal.MONTH!=d.getMonth()&& cal.YEAR!=d.getYear()){
        		period = period.next();
        		System.out.println(cal.MONTH+"/"+cal.DAY_OF_MONTH+"/"+cal.YEAR);
        		cal.roll(3, true);
        	}*/
            if(days<=1)
            	period=period.next();
            else{
                for(int f=0;f<days;f++)
                	period = period.next();       	
            }
            
        	old = d;
        	//if(counter++<50) continue outer;
        	//TimeSeriesDataItem s = new TimeSeriesDataItem();
        	MarketDay marketDay = hist.get(d);
        	if(field.equals("close")){
        		series.add(period, marketDay.close);     
        	}
        	else if(field.equals("open")){
        		series.add(period, marketDay.open);     
        	}
        	else if(field.equals("high")){
        		series.add(period, marketDay.high);     
        	}
        	else if(field.equals("low")){
        		series.add(period, marketDay.low);     
        	}
        	else if(field.equals("volume")){
        		series.add(period, marketDay.volume);     
        	}
        	
        }

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        return dataset;
    	
    }
    
    /**
     * Creates a sample dataset.
     * 
     * @param name  the dataset name.
     * @param base  the starting value.
     * @param start  the starting period.
     * @param count  the number of values to generate.
     *
     * @return The dataset.
     */
    private XYDataset createDataset(final String name, final double base, 
                                    final RegularTimePeriod start, final int count) {

        final TimeSeries series = new TimeSeries(name, start.getClass());
        RegularTimePeriod period = start;
        double value = base;
        for (int i = 0; i < count; i++) {
            series.add(period, value);    
            period = period.next();
            value = value * (1 + (Math.random() - 0.495) / 10.0);
        }

        final TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(series);

        return dataset;

    }

    /**
     * Starting point for the demonstration application.
     *
     * @param args  ignored.
     */
    public static void main(final String[] args) {

        final ProtoTypeChartDemo demo = new ProtoTypeChartDemo("Stock Chart Demo");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }

}



