package edu.uiuc.zenvisage.data.remotedb;

import java.util.ArrayList;

/**
 * SQL query representation object
 */
public class SQLQuery {

	private ArrayList<String> selectClause;
	private String fromClause;
	private ArrayList<String> whereClause;
	private ArrayList<String> groupClause;
	private ArrayList<String> orderClause;

	public SQLQuery() {
		selectClause = new ArrayList<String>();
		fromClause = "";
		whereClause = new ArrayList<String>();
		groupClause = new ArrayList<String>();
		orderClause = new ArrayList<String>();
		
	}
	
	// add element to select clause
	public void select(String value) {
		if (!value.equals("")) {
			this.selectClause.add(value);
		}
	}
	
	// add element to from clause
	public void from(String value) {
		if (!value.equals("")) {
			this.fromClause = value;
		}
	}
	
	// add element to where clause
	public void where(String value) {
		if (!value.equals("")) {
			this.whereClause.add(value);
		}
	}
	
	// add element to group by clause
	public void groupBy(String value) {
		if (!value.equals("")) {
			this.groupClause.add(value);
		}
	}
	
	// add element to order by clause
	public void orderBy(String value) {
		if (!value.equals("")) {
			this.orderClause.add(value);
		}
	}
	
	// Output the sql query as a string
	public String toString() {
		
		String query = "";
		
		//SELECT 
		query = query + "SELECT ";
		query = query + selectClause.get(0);
		for (int i = 1; i < selectClause.size(); i++) {
			query = query +", " + selectClause.get(i);
		}
		
		//FROM
		query = query + " FROM " + fromClause;
		
		//WHERE
		if (!whereClause.isEmpty()) {
			query = query + " WHERE ";
			query = query + whereClause.get(0);
			for (int i = 1; i < whereClause.size(); i++) {
				query = query +" OR " + whereClause.get(i);
			}
		}
		
		//GROUP BY
		if (!groupClause.isEmpty()) {
			query = query + " GROUP BY ";
			query = query + groupClause.get(0);
			for (int i = 1; i < groupClause.size(); i++) {
				query = query +", " + groupClause.get(i);
			}
		}
		
		//ORDER BY
		if (!orderClause.isEmpty()) {
			query = query + " ORDER BY ";
			query = query + orderClause.get(0);
			for (int i = 1; i < orderClause.size(); i++) {
				query = query +", " + orderClause.get(i);
			}
		}
		
		return query+";";
	}
	
}