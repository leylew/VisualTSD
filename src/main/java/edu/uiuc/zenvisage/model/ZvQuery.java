package edu.uiuc.zenvisage.model;

public class ZvQuery {
	public String method;
	public String xAxis;
	public String yAxis;
	public String groupBy;
	public String aggrFunc;
	public String aggrVar;
	public int outlierCount;
	public float[] dataX;
	public float[] dataY;
	public double yMax;
	public double yMin;
	public Sketch[] sketchPoints;
	public double minX;
	public double maxX;
	public boolean outputNormalized = false;
	public boolean distanceNormalized=false;
	public String clustering="DBSCAN";
	public String distance_metric="Euclidean";
	public String predicateColumn;
	public String predicateOperator;
	public String predicateValue;
	public float[] xRange;
	public boolean considerRange;
	public int kMeansClusterSize;

	
	public String getDistance_metric() {
		return distance_metric;
	}
	public void setDistance_metric(String distance_metric) {
		this.distance_metric = distance_metric;
	}

	public String getClustering() {
		return clustering;
	}
	public void setClustering(String clustering) {
		this.clustering = clustering;
	}
	public boolean isOutputNormalized() {
		return outputNormalized;
	}
	public void setOutputNormalized(boolean outputNormalized) {
		this.outputNormalized = outputNormalized;
	}
	
	public boolean isDistanceNormalized() {
		return distanceNormalized;
	}
	
	public void setDistanceNormalized(boolean distanceNormalized) {
		this.distanceNormalized = distanceNormalized;
	}
	// constructor for args
	public ZvQuery (String method, String Yaxis, String Xaxis, String groupBy, String aggrFunc, String aggrVar, int outlierCount, boolean outputNormalized) {
		this.method = method;
		this.yAxis = Yaxis;
		this.xAxis = Xaxis;
		this.groupBy = groupBy;
		this.aggrFunc = aggrFunc;
		this.aggrVar = aggrVar;
		this.outlierCount = outlierCount;
		this.outputNormalized = outputNormalized;
	}
	// dummy constructor
	public ZvQuery () {
		
	}
	
	public Sketch[] getSketchPoints() {
		return sketchPoints;
	}
	public void setSketchPoints(Sketch[] sketchPoints) {
		this.sketchPoints = sketchPoints;
	}

	public String getxAxis() {
		return xAxis;
	}
	public void setxAxis(String xAxis) {
		this.xAxis = xAxis;
	}
	public String getyAxis() {
		return yAxis;
	}
	public void setyAxis(String yAxis) {
		this.yAxis = yAxis;
	}

	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getYaxis() {
		return yAxis;
	}
	public void setYaxis(String yaxis) {
		yAxis = yaxis;
	}
	public String getXaxis() {
		return xAxis;
	}
	public void setXaxis(String xaxis) {
		xAxis = xaxis;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public String getAggrFunc() {
		return aggrFunc;
	}
	public void setAggrFunc(String aggrFunc) {
		this.aggrFunc = aggrFunc;
	}
	public String getAggrVar() {
		return aggrVar;
	}
	public void setAggrVar(String aggrVar) {
		this.aggrVar = aggrVar;
	}
	public int getOutlierCount() {
		return outlierCount;
	}
	public void setOutlierCount(int outlierCount) {
		this.outlierCount = outlierCount;
	}
	public float[] getDataX() {
		return dataX;
	}
	public void setDataX(float[] dataX) {
		this.dataX = dataX;
	}
	public float[] getDataY() {
		return dataY;
	}
	public void setDataY(float[] dataY) {
		this.dataY = dataY;
	}
	public int getKMeansClusterSize() {
		return this.kMeansClusterSize;
	}
	public void setKMeansClusterSize(int k) {
		this.kMeansClusterSize = k;
	}
	
}
