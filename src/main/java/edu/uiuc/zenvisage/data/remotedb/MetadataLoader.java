package edu.uiuc.zenvisage.data.remotedb;
import java.io.IOException;

import java.sql.SQLException;

import java.util.List;

import org.roaringbitmap.RoaringBitmap;

public class MetadataLoader {
	public DatabaseMetaData databaseMetaData= new DatabaseMetaData();

	public MetadataLoader(String name, List<String> metadataList) throws IOException, InterruptedException, SQLException{
		this.databaseMetaData.dataset = name;
		this.databaseMetaData.XColumn.dataType = "int";
		this.databaseMetaData.XColumn.min = 1;
		this.databaseMetaData.XColumn.max = Float.parseFloat(metadataList.get(0));
		this.databaseMetaData.YColumn.dataType = "float";
		this.databaseMetaData.YColumn.min = Float.parseFloat(metadataList.get(1));
		this.databaseMetaData.YColumn.max = Float.parseFloat(metadataList.get(2));
		this.databaseMetaData.numofdata = Integer.parseInt(metadataList.get(3));
	}
    public DatabaseMetaData getFormMetdaData(){
	  return databaseMetaData;
    }
}
