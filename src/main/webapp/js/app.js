var app = angular.module('zenvisage', []);
var globalDatasetInfo;

app.factory('datasetInfo', function() {
  var categoryData;
  var xAxisData;
  var yAxisData;
  var datasetService = {};

  datasetService.store = function( response ) {
    categoryData = response.zAxisColumns;
    xAxisData = response.xAxisColumns;
    yAxisData = response.yAxisColumns;
  };
  datasetService.getCategoryData = function()
  {
    return categoryData;
  }
  datasetService.getXAxisData = function()
  {
    return xAxisData;
  }
  datasetService.getYAxisData = function()
  {
    return yAxisData;
  }
  return datasetService;
});

app.factory('plotResults', function() {

    var plottingService = {};
    plottingService.displayUserQueryResults = function displayUserQueryResults( userQueryResults, includeSketch = true )
    {
      displayUserQueryResultsHelper( userQueryResults, includeSketch );
    }

    plottingService.displayRepresentativeResults = function displayRepresentativeResults( representativePatternResults )
    {
      displayRepresentativeResultsHelper( representativePatternResults )
    }

    plottingService.displayOutlierResults = function displayOutlierResults( outlierResults )
    {
      displayOutlierResultsHelper( outlierResults )
    }

    return plottingService;
});

app.controller('options-controller', [
  '$scope', '$rootScope', '$http', 'ChartSettings', '$compile',
  function($scope, $rootScope, $http, $compile){
    $scope.similarity = 'Euclidean';
    $scope.representative = 'kmeans';
    $scope.numResults = 5;
    $scope.clusterSize = 3;
    $scope.considerRange = true;
    $scope.$watchGroup(['similarity', 'numResults'], function( newValue, oldValue ) {
      if (newValue !== oldValue)
      {
        $scope.callGetUserQueryResults();
      }
    });

    $scope.$watch('clusterSize', function( newValue, oldValue ) {
      if (newValue !== oldValue)
      {
        $scope.callgetRepresentativeTrends();
      }
    });

    $scope.$watch('showScatterplot', function( newValue, oldValue ) {
      if (newValue !== oldValue)
      {
        $scope.callGetUserQueryResultsWithCallBack();
      }
    });
    
    $scope.$watch('considerRange', function( newValue, oldValue ) {
        if (newValue !== oldValue)
        {
          $scope.callGetUserQueryResultsWithCallBack();
        }
      });

    $scope.$watch('representative', function( newValue, oldValue ) {
      if (newValue !== oldValue)
      {
        $scope.callgetRepresentativeTrends();
      }
    });
    
    $scope.removerAndInsertRows = function( n ){
      $scope.$broadcast('removeAndInsertRows', {n} );
    }

    // TOP K
    $scope.getTopK = function()
    {
      clearUserQueryResultsTable();
      var q = constructUserQuery(); //goes to query.js
      var data = q;

      console.log("calling getTopK");
      $http.post('/zv/findbestclass', data).
      success(function(response) {
        console.log("getTopK: success");
        if (response.length == 0){console.log("empty response")}
        plotResults.displayUserQueryResults(response.outputCharts);
      }).
      error(function(response) {
        console.log("getUserQueryResults: fail");
      });

    }

    $scope.clearQuery = function() {
      $scope.removerAndInsertRows( 1 );
      $($( ".tabler" )[0]).find(".name").val("")
      $($( ".tabler" )[0]).find(".x-val").val("")
      $($( ".tabler" )[0]).find(".y-val").val("")
      $($( ".tabler" )[0]).find(".z-val").val("")
      $($( ".tabler" )[0]).find(".constraints").val("")
      $($( ".tabler" )[0]).find(".process").val("")
    }

    $scope.populateWeatherQuery1 = function() {
      $scope.removerAndInsertRows( 3 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'month'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'temperature'}")
      $($( ".tabler" )[0]).find(".z-val").val(" z1<-'location'.*")
      $($( ".tabler" )[0]).find(".constraints").val("location='Melbourne'")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1<-{'month'}")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val(" z2<-'location'.*")
      $($( ".tabler" )[1]).find(".constraints").val("")
      $($( ".tabler" )[1]).find(".process").val("v2<-argmin_{z2}[k=5]DEuclidean(f1,f2)")

      $($( ".tabler" )[2]).find(".name").val("*f3")
      $($( ".tabler" )[2]).find(".x-val").val("x1")
      $($( ".tabler" )[2]).find(".y-val").val("y1")
      $($( ".tabler" )[2]).find(".z-val").val("v2")
      $($( ".tabler" )[2]).find(".constraints").val("")
      $($( ".tabler" )[2]).find(".process").val("")
    }

    $scope.populateWeatherQuery2 = function() {
      $scope.removerAndInsertRows( 3 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'month'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'temperature'}")
      $($( ".tabler" )[0]).find(".z-val").val(" z1<-'location'.*")
      $($( ".tabler" )[0]).find(".constraints").val("location='Melbourne'")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1<-{'month'}")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val(" z2<-'location'.*")
      $($( ".tabler" )[1]).find(".constraints").val("")
      $($( ".tabler" )[1]).find(".process").val("v2<-argmax_{z2}[k=5]DEuclidean(f1,f2)")

      $($( ".tabler" )[2]).find(".name").val("*f3")
      $($( ".tabler" )[2]).find(".x-val").val("x1")
      $($( ".tabler" )[2]).find(".y-val").val("y1")
      $($( ".tabler" )[2]).find(".z-val").val("v2")
      $($( ".tabler" )[2]).find(".constraints").val("")
      $($( ".tabler" )[2]).find(".process").val("")
    }

    $scope.populateWeatherQuery3 = function() {
      $scope.removerAndInsertRows( 2 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'temperature'}")
      $($( ".tabler" )[0]).find(".z-val").val(" z1<-'location'.*")
      $($( ".tabler" )[0]).find(".constraints").val("")
      $($( ".tabler" )[0]).find(".process").val("v1<-argmax_{z1}[k=5]T(f1)")

      $($( ".tabler" )[1]).find(".name").val("*f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val("v1")
      $($( ".tabler" )[1]).find(".constraints").val("")
      $($( ".tabler" )[1]).find(".process").val("")
    }

    $scope.populateQuery1 = function() {
      $scope.removerAndInsertRows( 1 );
      $($( ".tabler" )[0]).find(".name").val("*f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
      $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.*")
      $($( ".tabler" )[1]).find(".constraints").val("state='CA'")
      $($( ".tabler" )[0]).find(".process").val("")
    }

    $scope.populateQuery2 = function() {

      $scope.removerAndInsertRows( 3 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
      $($( ".tabler" )[0]).find(".z-val").val(" z1<-'state'.*")
      $($( ".tabler" )[0]).find(".constraints").val("state='CA'")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val("z2<-'state'.*")
      $($( ".tabler" )[1]).find(".constraints").val("")
      $($( ".tabler" )[1]).find(".process").val("v2<-argmin_{z2}[k=7]DEuclidean(f1,f2)")

      $($( ".tabler" )[2]).find(".name").val("*f3")
      $($( ".tabler" )[2]).find(".x-val").val("x1")
      $($( ".tabler" )[2]).find(".y-val").val("y1")
      $($( ".tabler" )[2]).find(".z-val").val("v2")
      $($( ".tabler" )[2]).find(".constraints").val("")
      $($( ".tabler" )[2]).find(".process").val("")
    }

    $scope.populateQuery3 = function() {

      $scope.removerAndInsertRows( 3 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year','month'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice','listingprice'}")
      $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.'CA'")
      $($( ".tabler" )[0]).find(".constraints").val("")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val("z2<-'state'.'NY'")
      $($( ".tabler" )[1]).find(".constraints").val("")
      $($( ".tabler" )[1]).find(".process").val("x2,y2<-argmin_{x1,y1}[k=1]DEuclidean(f1,f2)")

      $($( ".tabler" )[2]).find(".name").val("*f3")
      $($( ".tabler" )[2]).find(".x-val").val("x2")
      $($( ".tabler" )[2]).find(".y-val").val("y2")
      $($( ".tabler" )[2]).find(".z-val").val("'state'.{'CA','NY'}")
      $($( ".tabler" )[2]).find(".constraints").val("")
      $($( ".tabler" )[2]).find(".process").val("")
    }

    $scope.populateQuery4 = function() {
      $scope.removerAndInsertRows( 3 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
      $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.*")
      $($( ".tabler" )[0]).find(".constraints").val("state='NY'")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y1")
      $($( ".tabler" )[1]).find(".z-val").val("z2<-'city'.*")
      $($( ".tabler" )[1]).find(".constraints").val("")
      $($( ".tabler" )[1]).find(".process").val("v2<-argmax_{z2}[k=3]DEuclidean(f1,f2)")

      $($( ".tabler" )[2]).find(".name").val("*f3")
      $($( ".tabler" )[2]).find(".x-val").val("x1")
      $($( ".tabler" )[2]).find(".y-val").val("y1")
      $($( ".tabler" )[2]).find(".z-val").val("v2")
      $($( ".tabler" )[2]).find(".constraints").val("")
      $($( ".tabler" )[2]).find(".process").val("")
    }

    $scope.populateQuery5 = function() {
      //Pairwise example
      $scope.removerAndInsertRows( 3 );
      $($( ".tabler" )[0]).find(".name").val("f1")
      $($( ".tabler" )[0]).find(".x-val").val("x1<-{'year'}")
      $($( ".tabler" )[0]).find(".y-val").val("y1<-{'soldprice'}")
      $($( ".tabler" )[0]).find(".z-val").val("z1<-'state'.*")
      $($( ".tabler" )[0]).find(".constraints").val("")
      $($( ".tabler" )[0]).find(".process").val("")

      $($( ".tabler" )[1]).find(".name").val("f2")
      $($( ".tabler" )[1]).find(".x-val").val("x1")
      $($( ".tabler" )[1]).find(".y-val").val("y2<-{'listingprice'}")
      $($( ".tabler" )[1]).find(".z-val").val("z1")
      $($( ".tabler" )[1]).find(".constraints").val("")
      $($( ".tabler" )[1]).find(".process").val("v1<-argmin_{z1}[k=7]DEuclidean(f1,f2)")

      $($( ".tabler" )[2]).find(".name").val("*f3")
      $($( ".tabler" )[2]).find(".x-val").val("x1")
      $($( ".tabler" )[2]).find(".y-val").val("y3<-{'soldprice','listingprice'}")
      $($( ".tabler" )[2]).find(".z-val").val("v1")
      $($( ".tabler" )[2]).find(".constraints").val("")
      $($( ".tabler" )[2]).find(".process").val("")
    }
    $scope.drawFunction = function() {
      var xval = [];
      var plotData = [];

      for(var i = 0; i < sketchpadData.length; i++){
        var xp = sketchpadData[i]["xval"];
        //var yp = sketchpadData[i]["yval"];
        xval.push( xp )
      }

      var scope = {
        x: xval,
      };

      var eq = $scope.equation.replace("^", ".^");
      var y = math.eval( eq, scope )
      if( eq.includes("x") )
      {
        for (i = 0; i < xval.length; i++) {
          plotData.push( { "xval": xval[i], "yval":y[i] } )
        }
      }
      else
      {
        for (i = 0; i < xval.length; i++) {
          plotData.push( { "xval": xval[i], "yval": y } )
        }
      }

      var zType = angular.element($("#left-top-bar")).scope().selectedCategory;
      var xType = angular.element($("#left-top-bar")).scope().selectedXAxis;
      var yType = angular.element($("#left-top-bar")).scope().selectedYAxis;

      plotSketchpadNew( plotData )
    }

    $scope.callGetUserQueryResults = function() {
      $rootScope.$emit("callGetUserQueryResults", {});
    }

    $scope.callgetRepresentativeTrends = function() {
      $rootScope.$emit("callgetRepresentativeTrends", {});
    }

    $scope.callGetUserQueryResultsWithCallBack = function() {
      $rootScope.$emit("callGetUserQueryResultsWithCallBack", {});
    }

}]);



// populates and controls the dataset attributes on the left-bar
// does not dynamically adjust to change in dataset yet
app.controller('datasetController', [
  '$scope', '$rootScope', '$http', 'datasetInfo', 'plotResults', 'ScatterService', 'ChartSettings',
  function($scope, $rootScope, $http, datasetInfo, plotResults, scatterService, ChartSettings){

    $scope.chartSettings = ChartSettings;
    function initializeSketchpadOnDataAttributeChange( xdata, ydata)
    {
      clearRepresentativeTable();
      clearOutlierTable();
      clearUserQueryResultsTable();

      switch( $scope.chartSettings.selectedChartOption ) {
          case 'Bar':
              break;
          case 'Scatter':
              scatterService.initializeScatterPlot(xdata["min"],xdata["max"],ydata["min"],ydata["max"]);
              break;
          default: // Line
              initializeSketchpadNew(
                xdata["min"],xdata["max"],ydata["min"],ydata["max"],
                "tid","value","name"
               );
              break;
      }
    }

    $scope.callLoadAxisInfo = function() {
      $rootScope.$emit("callLoadAxisInfo", {});
    }

    $scope.getUserQueryResultsWithCallBack = function getUserQueryResultsWithCallBack()
    {
      clearUserQueryResultsTable();
      var q = constructUserQuery(); //goes to query.js
      var data = q;
      console.log("calling getUserQueryResults");
      $http.post('/zv/postSimilarity', data).
      success(function(response) {
        console.log("getUserQueryResults: success");
        if (response.length == 0){console.log("empty response")}
        plotResults.displayUserQueryResults(response.outputCharts);
        $scope.getRepresentativeTrendsWithoutCallback();
      }).
      error(function(response) {
        console.log("getUserQueryResults: fail");
      });
    }

    // for all other normal queries
    $scope.getUserQueryResults = function getUserQueryResults()
    {
      clearUserQueryResultsTable();
      var q = constructUserQuery(); //goes to query.js
      var data = q;

      console.log("calling getUserQueryResults");
      $http.post('/zv/postSimilarity', data).
      success(function(response) {
        console.log("getUserQueryResults: success");
        if (response.length == 0){console.log("empty response")}
        plotResults.displayUserQueryResults(response.outputCharts);
      }).
      error(function(response) {
        console.log("getUserQueryResults: fail");
      });

    }

    $scope.getRepresentativeTrendsWithoutCallback = function getRepresentativeTrendsWithoutCallback()
    {
      getRepresentativeTrends( getOutlierTrends );
    }

    // for representative trends
    function getRepresentativeTrends( outlierCallback )
    {
      clearRepresentativeTable();

      var q = constructRepresentativeTrendQuery(); //goes to query.js
      var data = q;

      console.log("calling getRepresentativeTrends");
      $http.post('/zv/postRepresentative', data).
      success(function(response) {
        console.log("getRepresentativeTrends: success");
        if (response.length == 0){console.log("empty response")}
        plotResults.displayRepresentativeResults( response.outputCharts );
        outlierCallback();
      }).
      error(function(response) {
        console.log("getRepresentativeTrends: fail");
      });
    }

    function getOutlierTrends()
    {
      clearOutlierTable();

      var q = constructOutlierTrendQuery(); //goes to query.js
      var data = q;

      console.log("calling getOutlierTrends");
      $http.post('/zv/postOutlier', data).
      success(function(response) {
        console.log("getOutlierTrends: success");
        if (response.length == 0){console.log("empty response")}
        plotResults.displayOutlierResults( response.outputCharts );
      }).
      error(function(response) {
        console.log("getOutlierTrends: fail");
      });
    }

    var q = constructDatasetChangeQuery(getSelectedDataset());
    //var q = constructDatasetChangeQuery("seed2");

    var params = {
      "query": q,
    };
    var config = {
      params: params,
    };

   $scope.onDatasetChange = function() {

      clearRepresentativeTable();
      clearOutlierTable();
      clearUserQueryResultsTable();

      var q = constructDatasetChangeQuery(getSelectedDataset());

      var params = {
        "query": q,
      };
      var config = {
        params: params,
      };

      $http.get('/zv/getformdata', config).
        success(function(response) {
          globalDatasetInfo = response;
          angular.element($("#left-top-bar")).scope().numOfData = response.numofdata;
          angular.element($("#left-top-bar")).scope().numOfPoints = response.XColumn.max;
          initializeSketchpadOnDataAttributeChange(
                response.XColumn,
                response.YColumn
              );
          $scope.getUserQueryResultsWithCallBack();
        }).

        error(function(response) {
          alert('Request failed: /getformdata');
        });
    }

    $rootScope.$on("callGetUserQueryResultsWithCallBack", function(){
      $scope.getUserQueryResultsWithCallBack();
    });

    $rootScope.$on("callGetUserQueryResults", function(){
      $scope.getUserQueryResults();
    });

    $rootScope.$on("callgetRepresentativeTrends", function(){
      $scope.getRepresentativeTrendsWithoutCallback();
    });
}]);

app.service('ChartSettings', function () {
    return {};
})

  $('#tree-option').click(function() {
    $(this).toggleClass("active");
    $("#tree-div").toggle("active");
  });