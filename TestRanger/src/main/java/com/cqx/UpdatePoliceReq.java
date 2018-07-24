package com.cqx;

public class UpdatePoliceReq {
	String policeId;
	String policeName;
	String dbName;
	String tableName;
	String permissionsType;
	String policeUser;
	String colPermissionsType;
	String isEnabled;
	public String getPoliceId() {
		return policeId;
	}
	public void setPoliceId(String policeId) {
		this.policeId = policeId;
	}
	public String getPoliceName() {
		return policeName;
	}
	public void setPoliceName(String policeName) {
		this.policeName = policeName;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getPermissionsType() {
		return permissionsType;
	}
	public void setPermissionsType(String permissionsType) {
		this.permissionsType = permissionsType;
	}
	public String getPoliceUser() {
		return policeUser;
	}
	public void setPoliceUser(String policeUser) {
		this.policeUser = policeUser;
	}
	public String getColPermissionsType() {
		return colPermissionsType;
	}
	public void setColPermissionsType(String colPermissionsType) {
		this.colPermissionsType = colPermissionsType;
	}
	public String getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}
}
