package edu.uiuc.zenvisage.data.remotedb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.postgresql.util.PSQLException;



/**
 * PostgreSQL database connection portal for my local machine
 * need to change to in general
 *
 */
public class SQLQueryExecutor {

	/**
	 * Settings specific to local PSQL database, need to change this!!!!
	 */
	private String database = "postgres";
	private String host = "jdbc:postgresql://localhost:5432/"+database;
	private String username = "postgres";
	private String password = "zenvisage";
	Connection c = null;
	private VisualComponentList visualComponentList;

	// Initialize connection
	public SQLQueryExecutor() {

	      try {
		         Class.forName("org.postgresql.Driver");
		         c = DriverManager
		            .getConnection(host, username, password);
		      } catch (Exception e) {
		    	 System.out.println("Connection Failed! Check output console");
		         e.printStackTrace();
		         System.err.println(e.getClass().getName()+": "+e.getMessage());
		         System.exit(0);
		      }
	      System.out.println("Opened database successfully");
	      
	      try {
	    	  Statement s = c.createStatement();
	    	  s.execute("SET SESSION work_mem = '200MB'");
	      } catch (SQLException e) {
	    	  System.out.println("Cannot change work_mem!");
	    	  e.printStackTrace();
	    	  System.exit(0);
	      }
	}

	// Query database and return result
	public ResultSet query(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement();
	      ResultSet ret = stmt.executeQuery(sQLQuery);
	      stmt.close();
	      return ret;
	}

	public int createTable(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement();
	      System.out.println(sQLQuery);
	      int ret = stmt.executeUpdate(sQLQuery);
	      stmt.close();
	      return ret;
	}
	
	public int executeUpdate(String sQLQuery) throws SQLException {
	      Statement stmt = c.createStatement();
	      int ret = stmt.executeUpdate(sQLQuery);
	      stmt.close();
	      return ret;
	}

	public void dropTable(String tableName) throws SQLException {
		Statement stmt = c.createStatement();
		String sql = "DROP TABLE " + tableName;
	    stmt.executeUpdate(sql);
//	    System.out.println("Table " + tableName + " deleted in given database...");
	    stmt.close();
	}

	/*This is the main SQLExcecution query*/
	public void SQLQueryEnhanced(String databaseName) throws SQLException{
		databaseName = databaseName.toLowerCase();
		String sql = "SELECT name,tid,value FROM "+databaseName;
		this.visualComponentList = new VisualComponentList();
		this.visualComponentList.setVisualComponentList(new ArrayList<VisualComponent>());
		System.out.println("Running SQL Query :"+sql);
		executeSQL(sql, databaseName);
	}

	public void executeSQL(String sql,String databaseName) throws SQLException{
		Statement st = c.createStatement();
		System.out.println("before execute");
		ResultSet rs = st.executeQuery(sql);
		System.out.println("after execute");

		ArrayList <WrapperType> xList = null;
		ArrayList <WrapperType> yList = null;
		VisualComponent tempVisualComponent = null;

		String zType = "string", xType = "int", yType = "float";
		System.out.println("before loop");
		// Since we do not order by Z, we need a hashmap to keep track of all the visualcomponents
		// Since X is sorted though, the XList and YList are sorted correctly
		HashMap<String, List<VisualComponent>> vcMap = new HashMap<String, List<VisualComponent>>();
		sql_loop: while (rs.next())
		{
			if(rs.getString(1) == null || rs.getString(1).isEmpty()) continue;
			if(rs.getString(2) == null || rs.getString(2).isEmpty()) continue;
			
			String zStr = rs.getString(1);
			List<VisualComponent> vcList = vcMap.get(zStr);
			
			// adding new x,y points to existing visual components
			if(vcList != null) {
				if (rs.getString(3) == null || rs.getString(3).isEmpty()) {
					continue sql_loop;
				}
				VisualComponent vc = vcList.get(0);
				vc.getPoints().getXList().add(new WrapperType(rs.getString(2), xType));
				vc.getPoints().getYList().add(new WrapperType(rs.getString(3), yType));	
			}
			else {
				vcList = new ArrayList<VisualComponent>();
				if (rs.getString(3) == null || rs.getString(3).isEmpty()) {
					continue sql_loop;
				}
				// don't get individual y meta types -- let WrapperType interpret the int, float, or string
				xList = new ArrayList<WrapperType>();
				yList = new ArrayList<WrapperType>();
				xList.add(new WrapperType(rs.getString(2), xType));
				yList.add(new WrapperType(rs.getString(3),yType));
				tempVisualComponent = new VisualComponent(new WrapperType(zStr, zType), new Points(xList, yList), "tid", "value");
				vcList.add(tempVisualComponent);
				vcMap.put(zStr, vcList);
			}
		}
		// will be in some unsorted order (b/c hashmap), which is fine
		// what is important is that have all VCs for one pair of X,Y first, then another pair of X,Y, and so on
		for(List<VisualComponent> vcList: vcMap.values()) {
				this.visualComponentList.addVisualComponent(vcList.get(0));
		}		
		rs.close();
		st.close();
		System.out.println("after loop");
	}

	public List<String> getMetadata(String database) throws SQLException{
		Statement st = c.createStatement();
 		String sql = "SELECT x_max,y_min,y_max,numOfData FROM zenvisage_metadata WHERE database = '" + database + "';";
 		ResultSet rs = st.executeQuery(sql);
 		List<String> r = new ArrayList<String>();
 		while (rs.next())
 		{
 			r.add(new String(rs.getString(1)));
 			r.add(new String(rs.getString(2)));
 			r.add(new String(rs.getString(3)));
 			r.add(new String(rs.getString(4)));
 		}
 		return r;
	}
	
//	public boolean isPass(String name, String psw) throws SQLException{
//		Statement st = c.createStatement();
//		String sql = "SELECT psw FROM user_list WHERE name = '" + name +"';";
//		ResultSet rs = st.executeQuery(sql);
//		while(rs.next()) {
//			if(rs.getString(1).equals(psw))
//				return true;
//		}
//		return false;
//	}

	public boolean insert(String sql, String tablename, String tablenameVariable, String databasename) throws SQLException{
		int count = 0;
		Statement st0 = c.createStatement();
		tablename = tablename.toLowerCase();
		databasename = databasename.toLowerCase();
		String sql0 = "SELECT COUNT(*) FROM "
				+ tablename
	 			+ " WHERE " + tablenameVariable + " = '" + databasename + "'";

		ResultSet rs0 = st0.executeQuery(sql0);

		//if database already exist return false;
		while (rs0.next())
 		{
			if(Integer.parseInt(rs0.getString(1))>0) return false;
 		}

		Statement st = c.createStatement();

//		System.out.println(sql);
		count = st.executeUpdate(sql);

		return count > 0;
	}

	public boolean isTableExists(String tableName) throws SQLException{
		Statement st0 = c.createStatement();
		String sql0 = "select * from pg_tables where tablename = '" + tableName + "'";
		ResultSet rs0;
		try{
			 rs0 = st0.executeQuery(sql0);
		}
		catch(Exception PSQLException){

			return false;
		}
		while (rs0.next())
 		{
			if(rs0.getString(2).equals(tableName)) 
			{
				System.out.println(tableName +" already exists");	
				return true;
			}
			
 		}
		return false;
	}

	public void insertTable(String tableName, String fileName) throws SQLException{
		StringBuilder sql = new StringBuilder("COPY "+ tableName + "(tid,value,name) FROM '" + fileName + "' DELIMITER ',' CSV HEADER;");
	    Statement stmt = c.createStatement();
	    stmt.executeUpdate(sql.toString());
	    stmt.close();
	}
	
	public void updateMinMax(String tableName, String attribute, float min, float max) throws SQLException{
		String sql = "UPDATE zenvisage_metatable"+ 
				" SET min = " + min + ", max = " + max +
				" WHERE tablename = '" + tableName + "' AND attribute = '" + attribute+"'";
		System.out.println(sql);
		Statement stmt = c.createStatement();
		stmt.executeUpdate(sql);
	    stmt.close();
	}

	public VisualComponentList getVisualComponentList() {
		return visualComponentList;
	}

	public void setVisualComponentList(VisualComponentList visualComponentList) {
		this.visualComponentList = visualComponentList;
	}

}
