package edu.uiuc.zenvisage.data.remotedb;
public class ColumnMetadata {
  public String dataType;
  public float min = 100000;
  public float max=-1000;
  public float pAAWidth = (float) 0.1;
  public int numberOfSegments = 3000;
}
