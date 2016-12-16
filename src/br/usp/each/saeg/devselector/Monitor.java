package br.usp.each.saeg.devselector;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;

@ManagedBean(name="monitor", eager=true)
@SessionScoped
public class Monitor {
	
	private File histogram;
	private List<String[]> groupMatrix = new ArrayList<String[]>();
	private List<String[]> frequencyMatrix = new ArrayList<String[]>();
	
	//private final String WEB_PATH = System.getProperty("user.home")+"/app/apache-tomcat-7.0.70/webapps/devselector/resources/images/";
	private final String WEB_PATH = "/Users/higor/workspace/devselector/WebContent/resources/images/";
	//private final String WEB_PATH = "/var/lib/tomcat8/webapps/devselector/resources/images/";//for the server
	private final String FILE_EXT = ".png";
	private final String FILENAME = "histogram";
	private final String FONT = "TimesRoman";
	private final int GROUP_COLUMN = 12;
	
	
	public Monitor(){
		loadChart();
		load();
	}
	
	public void update(){
		loadChart();
		load();
	}
	
	public File getHistogram() {
		return histogram;
	}

	public void setHistogram(File histogram) {
		this.histogram = histogram;
	}

	public List<String[]> getGroupMatrix() {
		return groupMatrix;
	}

	public void setGroupMatrix(List<String[]> groupMatrix) {
		this.groupMatrix = groupMatrix;
	}

	public List<String[]> getFrequencyMatrix() {
		return frequencyMatrix;
	}

	public void setFrequencyMatrix(List<String[]> frequencyMatrix) {
		this.frequencyMatrix = frequencyMatrix;
	}

	private void load(){
		Experience experience = new Experience();
		frequencyMatrix = experience.getFrequencyMatrix();
		groupMatrix = experience.getGroupMatrix();
	}
	
	///image area
	
	public void loadChart(){
		createDataset();
	}
	
	public void createDataset(){
    	DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    	int numberOfGroups = 9;
    	int participantsPerGroup[] = new int[numberOfGroups];//the array index is the group number
    	//fill the array with 0s
    	for(int i = 0; i < numberOfGroups; i++){
    		participantsPerGroup[i] = 0;
    	}
    	//sum the number of participants per group
    	for(String data[] : groupMatrix){
    		int groupNumber = Integer.parseInt(data[GROUP_COLUMN]);
    		participantsPerGroup[groupNumber] += 1; 
    	}
    	//add to the dataset
    	int groupCounter = 0;
    	for(int numberOfParticipants : participantsPerGroup){
    		dataset.addValue(numberOfParticipants, "participants", String.valueOf(groupCounter));
    		groupCounter++;
    	}
    	
		createBarChart(dataset,"Histogram of Group Distribution","group","participants",FILENAME);
		
    }
    
    
    private void createBarChart(final CategoryDataset dataset,String title, String xAxisTitle, String yAxisTitle, String fileName){
		Font titleFont = new Font(FONT, Font.BOLD, 30);
		Font labelAxisFont = new Font(FONT, Font.BOLD, 30);
		Font budgetSeriesFont = new Font(FONT, Font.PLAIN, 30);
		Font effortRangeFont = new Font(FONT, Font.PLAIN, 30);
		Font legendFont = new Font(FONT, Font.PLAIN, 16);
		Font barFont = new Font(FONT, Font.PLAIN, 30);
		        
        // create the chart...
        final JFreeChart chart = ChartFactory.createBarChart(
            title,      				// chart title
            xAxisTitle,                 // x axis label
            yAxisTitle,                	// y axis label
            dataset,                  	// data
            PlotOrientation.VERTICAL,
            true,                     	// include legend
            true,                     	// tooltips
            false                     	// urls
        );
        
        chart.setBackgroundPaint(Color.white);
        
        //set the chart's font
        TextTitle chartTitle = chart.getTitle();
        chartTitle.setFont(titleFont);
                       
        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.white);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.black);
        plot.setOutlineVisible(true);
        
        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setGradientPaintTransformer(null);
        //renderer.setMinimumBarLength(.1);
        renderer.setItemMargin(0.05);
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, Color.black);
        
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        renderer.setBaseItemLabelFont(barFont);
                        
        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.STANDARD);//.createUpRotationLabelPositions(Math.PI / 6.0)
        domainAxis.setCategoryMargin(0.2);
        domainAxis.setTickLabelFont(budgetSeriesFont);
        domainAxis.setLabelFont(labelAxisFont);
        //domainAxis.setCategoryLabelPositionOffset(4);
        domainAxis.setLowerMargin(0.01);//set margin
        domainAxis.setUpperMargin(0.01);
        
        // set the range axis to display integers only...
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickLabelFont(effortRangeFont);
        rangeAxis.setLabelFont(labelAxisFont);
        
        final LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.TOP);
        legend.setItemFont(legendFont);
                 
        createImageFile(chart,fileName);
        
    }
    
	
	private void createImageFile(JFreeChart chart,String fileName){
    	
    	File imageChart = new File(WEB_PATH+fileName+FILE_EXT);
        try {
			ChartUtilities.saveChartAsPNG(imageChart, chart, 1680, 1050);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
}
