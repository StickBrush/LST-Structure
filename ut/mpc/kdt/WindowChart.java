package ut.mpc.kdt;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.swtchart.Chart;
import org.swtchart.ILineSeries;
import org.swtchart.LineStyle;
import org.swtchart.ISeries.SeriesType;

import ut.mpc.setup.Init;

//set normalize to true to normalize plot to highest value of data points
public class WindowChart {
	public String name;
	public boolean normalize = Init.CoverageWindow.NORMALIZE_PLOT;
	public double maxVal = 0.0;
	public ArrayList<double[]> points = new ArrayList<double[]>();
	public ArrayList<double[]> values = new ArrayList<double[]>();
	
	public WindowChart(String name){
		this.name = name;
	}
	
	public void addData(double[] point, double[] value){
		this.points.add(point);
		this.values.add(value);
		if(value[0] > this.maxVal)
			this.maxVal = value[0];
	}
	
	public void plot(){
        Display display = new Display();
        Shell shell = new Shell(display);
        shell.setText("coverage");
        shell.setSize(500, 400);
        shell.setLayout(new FillLayout());

        createChart(shell);

        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
        display.dispose();
	}
	

    /**
     * create the chart.
     * 
     * @param parent
     *            The parent composite
     * @return The created chart
     */
    public Chart createChart(Composite parent) {

        // create a chart
        Chart chart = new Chart(parent, SWT.NONE);

        // set titles
        chart.getTitle().setText("Window Coverage");
        chart.getAxisSet().getXAxis(0).getTitle().setText("X Axis");
        chart.getAxisSet().getYAxis(0).getTitle().setText("Y Axis");

        // create scatter series
        ILineSeries scatterSeries = (ILineSeries) chart.getSeriesSet()
                .createSeries(SeriesType.LINE, "scatter series");
        scatterSeries.setLineStyle(LineStyle.NONE);
        double[] xSeries = new double[this.points.size()];
        double[] ySeries = new double[this.points.size()];
        Color[] colors = new Color[this.points.size()];
        for(int i = 0; i < this.points.size(); i++){
        	xSeries[i] = this.points.get(i)[0];
        	ySeries[i] = this.points.get(i)[1];
        	colors[i] = determineColor(this.values.get(i)[0]);
        }
        
        scatterSeries.setSymbolColors(colors);
        
        scatterSeries.setXSeries(xSeries);
        scatterSeries.setYSeries(ySeries);

        // adjust the axis range
        chart.getAxisSet().adjustRange();
        return chart;
    }
    
    //
    public Color determineColor(double value){
    	int red,green,blue;
    	
    	if(this.normalize){
	    	red = (int) ((value / this.maxVal) * 255);
	    	if(red > 255)
	    		red = 255;
	    	green = 255 - red;
    	} else {
	    	red = (int) ((value / 100) * 255);
	    	if(red > 255)
	    		red = 255;
	    	green = 255 - red;
    	}
    	Color color = new Color(Display.getDefault(), red, green, 0);
    	//Color color = new Color(Display.getDefault(), green, green, green); //red, green, 0
    	return color;
    }

}
