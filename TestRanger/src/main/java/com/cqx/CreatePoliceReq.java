package com.cqx;

public class CreatePoliceReq {
	String PoliceName;
	String policeUser;
	String dbName;
	String tableName;
	String permissionsType;
	String ColPermissionsType;
	public String getPoliceName() {
		return PoliceName;
	}
	public void setPoliceName(String policeName) {
		PoliceName = policeName;
	}
	public String getPoliceUser() {
		return policeUser;
	}
	public void setPoliceUser(String policeUser) {
		this.policeUser = policeUser;
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
	public String getColPermissionsType() {
		return ColPermissionsType;
	}
	public void setColPermissionsType(String colPermissionsType) {
		ColPermissionsType = colPermissionsType;
	}
}
