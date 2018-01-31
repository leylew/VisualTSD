/**
 *
 */
package edu.uiuc.zenvisage.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import edu.uiuc.zenvisage.data.remotedb.MetadataLoader;
import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;
import edu.uiuc.zenvisage.data.remotedb.VisualComponent;
import edu.uiuc.zenvisage.data.remotedb.VisualComponentList;
import edu.uiuc.zenvisage.data.remotedb.WrapperType;
import edu.uiuc.zenvisage.service.cluster.Clustering;
import edu.uiuc.zenvisage.service.cluster.KMeans;
import edu.uiuc.zenvisage.service.distance.Distance;
import edu.uiuc.zenvisage.service.distance.Euclidean;
import edu.uiuc.zenvisage.model.*;
import edu.uiuc.zenvisage.service.utility.DataReformation;
import edu.uiuc.zenvisage.service.utility.LinearNormalization;
import edu.uiuc.zenvisage.service.utility.Normalization;
import edu.uiuc.zenvisage.service.utility.Original;
import edu.uiuc.zenvisage.service.utility.PiecewiseAggregation;
import edu.uiuc.zenvisage.server.UploadHandleServlet;
import edu.uiuc.zenvisage.service.Metadata;

/**
 * @author tarique
 *
 */
public class ZvMain {
	private MetadataLoader metadataloader;
	public Analysis analysis;
	public Distance distance;
	public Normalization normalization;
	public Normalization outputNormalization;
	public PiecewiseAggregation paa;
	public ArrayList<List<Double>> data;
	public String databaseName;
	public String buffer = null;

	public ZvMain() throws IOException, InterruptedException{
		System.out.println("ZVMAIN LOADED");
	}
	
//	public boolean userLogin(HttpServletRequest request) throws ServletException, IOException, InterruptedException, SQLException {
//		SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();	
//		Map<String, String[]> prm = request.getParameterMap();
//		return sqlQueryExecutor.isPass(prm.get("uname")[0],prm.get("upassword")[0]);
//	}
	
	public static void filePreHandler(String fname, String fpath, Metadata fileMetadata)throws IOException{
		String createpath = System.getProperty("user.dir");
		String filename = fname + ".csv";
		File tmpfile = new File(createpath,filename);
		if(!tmpfile.exists()){
		    try {  
		        tmpfile.createNewFile(); // 创建文件  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    }  
        }
		else{
			tmpfile.delete();
			tmpfile.createNewFile();
		}
        File orgfile = new File(fpath);
        BufferedReader reader = new BufferedReader(new FileReader(orgfile));
        int line = 1;
        String content = null;
        FileOutputStream in = new FileOutputStream(tmpfile);
        String tmpstr = "tid,value,name\n";
        in.write(tmpstr.getBytes());
        while((content = reader.readLine()) != null){
        	String[] contents = content.split(" ");
        	int timeid = 0;
        	for(int i=0; i<contents.length; i++){
        		if(!contents[i].isEmpty()){
        			timeid++;
	        		String tmp = timeid + "," + contents[i] + "," + line + "\n";
	        		in.write(tmp.getBytes());
	        		Float num = Float.parseFloat(contents[i]);
	        		if(num < fileMetadata.ymin)
	        			fileMetadata.ymin = num;
	        		if(num > fileMetadata.ymax)
	        			fileMetadata.ymax = num;
        		}
        	}
        	fileMetadata.xmax = timeid;
        	line++;
        }
        fileMetadata.numofdata = line-1;
        in.close();
        reader.close();
	}
	
	public void fileUpload(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, InterruptedException, SQLException {
		UploadHandleServlet uploadHandler = new UploadHandleServlet();
		List<String> names = uploadHandler.upload(request, response);
		uploadDatasettoDB(names,true);
	}

    public static void uploadDatasettoDB(List<String> names, boolean overwrite) throws SQLException, IOException, InterruptedException{
		Metadata fileMetadata = new Metadata();
		filePreHandler(names.get(0),names.get(1),fileMetadata);
		String datahandledpath = System.getProperty("user.dir")+"/"+names.get(0)+".csv";
		if (names.size() == 2) {
			SQLQueryExecutor sqlQueryExecutor = new SQLQueryExecutor();	
			/*create csv table*/
			if(!sqlQueryExecutor.isTableExists(names.get(0))){			
				/*insert zenvisage_metadata*/
				String metadataTupleSQL = "INSERT INTO zenvisage_metadata (database, x_max,y_min,y_max,numOfData) VALUES "+
						"('" + names.get(0) +"', '"+ fileMetadata.xmax + "','" + fileMetadata.ymin + "','" + fileMetadata.ymax+"','"+fileMetadata.numofdata+"');";
				if(sqlQueryExecutor.insert(metadataTupleSQL, "zenvisage_metadata", "database", names.get(0))){
					System.out.println("MetaData successfully inserted into Postgres");
				} else {
					System.out.println("MedataData already exists!");
				}
				/*Create database*/
				sqlQueryExecutor.createTable("CREATE TABLE "+names.get(0)+"(tid INT,value REAL,name TEXT);");
				sqlQueryExecutor.insertTable(names.get(0), datahandledpath);
				System.out.println(names.get(0) + " not exists! Created " + names.get(0) + " table from "+datahandledpath);
				System.out.println("Successful upload! "+ names.get(0) +" "+  datahandledpath);
			} else if(overwrite) {//
				String updatesql = "UPDATE zenvisage_metadata SET x_max="+fileMetadata.xmax+",y_min="+fileMetadata.ymin+",y_max="+fileMetadata.ymax+",numOfData="+fileMetadata.numofdata+" WHERE database='"+names.get(0)+"';";
				sqlQueryExecutor.executeUpdate(updatesql);
				sqlQueryExecutor.dropTable(names.get(0));
				sqlQueryExecutor.createTable("CREATE TABLE "+names.get(0)+"(tid INT,value REAL,name TEXT);");
				sqlQueryExecutor.insertTable(names.get(0), datahandledpath);
				System.out.println(names.get(0) + " exists! Overwrite and create " + names.get(0) + " from "+datahandledpath);
			}
		}
	}
public Result convertVCListtoVisualOutput(VisualComponentList vcList){
		Result finalOutput = new Result();
		//VisualComponentList -> Result. Only care about the outputcharts. this is for submitZQL
	    for(VisualComponent viz : vcList.getVisualComponentList()) {
	    	Chart outputChart = new Chart();
	    	outputChart.setzType( viz.getzAttribute() );
	    	outputChart.setxType( viz.getxAttribute() );
	    	outputChart.setyType( viz.getyAttribute() );
	    	outputChart.title = viz.getZValue().getStrValue();
	    	outputChart.setNormalizedDistance(viz.getScore());
	    	for(WrapperType xValue : viz.getPoints().getXList()) {
	    		outputChart.xData.add(xValue.toString());
	    	}
	    	for(WrapperType yValue : viz.getPoints().getYList()) {
	    		outputChart.yData.add(yValue.toString());
	    	}
	    	finalOutput.outputCharts.add(outputChart);
	    }
		return finalOutput;
	 }

	/**
	 * Given a front end sketch or drag and drop, run similarity search through the query graph backend
	 * @param zvQuery
	 * @return
	 * @throws InterruptedException
	 * @throws IOException
	 */	
	public String runDragnDropInterfaceQuerySeparated(String query, String method) throws InterruptedException, IOException, SQLException{
		 ZvQuery args = new ObjectMapper().readValue(query,ZvQuery.class);
		 System.out.println("Before SQL");
		 SQLQueryExecutor sqlQueryExecutor= new SQLQueryExecutor();
		 sqlQueryExecutor.SQLQueryEnhanced(this.databaseName);
		 System.out.println("After SQL");
		 LinkedHashMap<String, LinkedHashMap<Float, Float>> output =  sqlQueryExecutor.getVisualComponentList().toInMemoryHashmap();
		 System.out.println("After To HashMap");
		 //output = cleanUpDataWithAllZeros(output);
		 Result finalOutput = new Result();
		 finalOutput.method = method;
		 ChartOutputUtil chartOutput = new ChartOutputUtil(finalOutput, args, HashBiMap.create());
		 if (args.distance_metric.equals("Euclidean")) {
			 distance = new Euclidean();
		 }
		 if (args.distanceNormalized) {
			 normalization = new LinearNormalization();
		 }
		 else {
			 normalization = new LinearNormalization();
		 }
		 // generate the corresponding output normalization
		 outputNormalization = new Original();
		 // reformat database data
		 DataReformation dataReformatter = new DataReformation(normalization);
		 double[][] normalizedgroups;

		 System.out.println("Before Methods");
		 // generate the corresponding analysis method
		 if (method.equals("Outlier")) {
			 normalizedgroups = dataReformatter.reformatData(output);
			 Clustering cluster = new KMeans(distance, normalization, args);
			 analysis = new Outlier(chartOutput,new Euclidean(),normalization,cluster,args);
		 }
		 else if (method.equals("RepresentativeTrends")) {
			 normalizedgroups = dataReformatter.reformatData(output);
			 Clustering cluster = new KMeans(distance, normalization, args);
			 analysis = new Representative(chartOutput,new Euclidean(),normalization,cluster,args);
		 }
		 else if (method.equals("SimilaritySearch")) {
			 if(args.considerRange){
				 double[][][] overlappedDataAndQueries = dataReformatter.getOverlappedData(output, args); // O(V*P)
				 normalizedgroups = overlappedDataAndQueries[0];
				 double[][] overlappedQuery = overlappedDataAndQueries[1];
				 analysis = new Similarity(chartOutput,distance,normalization,args,dataReformatter, overlappedQuery);
			 }
			 else{
				 normalizedgroups = dataReformatter.reformatData(output);
				 double[] interpolatedQuery = dataReformatter.getInterpolatedData(args.dataX, args.dataY, args.xRange, normalizedgroups[0].length); // O(P)
				 analysis = new Similarity(chartOutput,distance,normalization,paa,args,dataReformatter, interpolatedQuery);
			 }
			 ((Similarity) analysis).setDescending(false);
		 }
		 else { 
			 normalizedgroups = dataReformatter.reformatData(output);
			 double[] interpolatedQuery = dataReformatter.getInterpolatedData(args.dataX, args.dataY, args.xRange, normalizedgroups[0].length);
			 analysis = new Similarity(chartOutput,distance,normalization,paa,args,dataReformatter, interpolatedQuery);
			 ((Similarity) analysis).setDescending(true);
		 }
		 System.out.println("After Interpolation and normalization");
		 analysis.compute(output, normalizedgroups, args);
		 System.out.println("After Distance calulations");
		 ObjectMapper mapper = new ObjectMapper();
		 System.out.println("After Interpolation and normalization");
		 String res = mapper.writeValueAsString(analysis.getChartOutput().finalOutput);
		 System.out.println("After mapping to output string");
		 return res;
	}

	public String getInterfaceFomData(String query) throws IOException, InterruptedException, SQLException{
		FormQuery fq = new ObjectMapper().readValue(query,FormQuery.class);
		this.databaseName = fq.getDatabasename();
		List<String> valueList = new SQLQueryExecutor().getMetadata(databaseName);
		this.metadataloader = new MetadataLoader(this.databaseName, valueList);
		buffer = new ObjectMapper().writeValueAsString(this.metadataloader.getFormMetdaData());
		System.out.println("inMemoryDatabase.getFormMetdaData()"+buffer);
		return buffer;
	}
}
