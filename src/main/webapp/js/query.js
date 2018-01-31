function constructDatasetChangeQuery( datasetName )
{
  return new formQuery( datasetName );
}
function formQuery( databasename ){
  this.databasename = databasename;
}

function constructUserQuery()
{
  return new Query( "SimilaritySearch" );
}

function constructRepresentativeTrendQuery()
{
  return new Query( "RepresentativeTrends" );
}

function constructOutlierTrendQuery()
{
  return new Query( "Outlier" );
}

function Query( searchMethod ) {
  this.method = searchMethod; // fix to dynamically fetch
  this.xAxis = "tid";
  this.yAxis = "value";
  this.groupBy = "name";
  this.outlierCount = getNumResults();
  this.dataX = []; // fix to dynamically fetch
  this.dataY = []; // fix to dynamically fetch
  this.yMax = null; // fix to dynamically fetch. is this field necessary?
  this.yMin = null; // fix to dynamically fetch. is this field necessary?
  var points = []

  for(var i = 0; i < sketchpadData.length; i++){
    var xp = sketchpadData[i]["xval"];
    var yp = sketchpadData[i]["yval"];
    points.push(new Point( xp, yp ));
    this.dataX.push( xp );
    this.dataY.push( yp );
  }

  this.sketchPoints = [new SketchPoints(this.xAxis, this.yAxis, points)];
  this.distanceNormalized = false; // fix to dynamically fetch
  this.outputNormalized = false; // fix to dynamically fetch
  this.clustering = "KMeans"; // fix to dynamically fetch
  this.kMeansClusterSize = getClusterSize();
  this.distance_metric = "Euclidean"; // fix to dynamically fetch
  this.predicateOperator = "="; // fix to dynamically fetch
  this.predicateColumn = "name";
  this.predicateValue = ""; // fix to dynamically fetch
  this.xRange = getXRange();
  this.considerRange = getConsiderRange();
}

function SketchPoints(xAxisName, yAxisName, points){
  var xAxisData = globalDatasetInfo.XColumn;
  var yAxisData = globalDatasetInfo.YColumn;
  this.points = points;
  this.minX = xAxisData.min;
  this.maxX = xAxisData.max;
  this.minY = yAxisData.min;
  this.maxY = yAxisData.max;
  this.yAxis = "value";
  this.xAxis = "tid";
  this.groupBy = "name";
}


function getXRange() //when zoomed in
{
  return xrangeNew;
}

function getNumResults()
{
  return angular.element($("#left-middle-bar")).scope().numResults;
}

function getClusterSize()
{
  return angular.element($("#left-bottom-bar")).scope().clusterSize;
}

function getConsiderRange()
{
	return angular.element($("#left-middle-bar")).scope().considerRange;
}

function getScatterplotOption()
{
  return angular.element($("#left-middle-bar")).scope().showScatterplot;
}

function getSelectedDataset()
{
  return $("#dataset-form-control option:selected").val();
}
