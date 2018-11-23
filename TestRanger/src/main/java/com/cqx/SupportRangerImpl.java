package com.cqx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ranger.plugin.model.RangerPolicy;

public class SupportRangerImpl {
	private static String service = PropertyUtil.getProperty("service");// hive 的服务名
	
	public static RangerPolicy updateOfPolicy(String policeName, String dbName, String tableName, String operatePermissionsType,
            String policeUser, String colPermissionsType, String policeIsEnabled) {

        RangerPolicy rangerPolicy = new RangerPolicy();
        if (StringUtils.isNotBlank(policeName))
            rangerPolicy.setName(policeName);

        if (StringUtils.isBlank(policeIsEnabled) || "1".equals(policeIsEnabled))
            rangerPolicy.setIsEnabled(true);
        else if ("0".equals(policeIsEnabled))
            rangerPolicy.setIsEnabled(false);

        rangerPolicy.setService(service);
        rangerPolicy.setIsAuditEnabled(true);

        Map<String, RangerPolicy.RangerPolicyResource> resources = new HashMap<String, RangerPolicy.RangerPolicyResource>();

        RangerPolicy.RangerPolicyResource dbRangerPolicyResource = new RangerPolicy.RangerPolicyResource();
        RangerPolicy.RangerPolicyResource tablerRangerPolicyResource = new RangerPolicy.RangerPolicyResource();
        RangerPolicy.RangerPolicyResource columRangerPolicyResource = new RangerPolicy.RangerPolicyResource();

        dbRangerPolicyResource.setValue(dbName);
//        dbRangerPolicyResource.setValue(dbName + ",policeUser_test");
        dbRangerPolicyResource.setIsExcludes(false);
        dbRangerPolicyResource.setIsRecursive(false);

        tablerRangerPolicyResource.setValue(tableName);
        if (StringUtils.isBlank(colPermissionsType))
            columRangerPolicyResource.setValue("*");
        else
            columRangerPolicyResource.setValue(colPermissionsType);

        resources.put("database", dbRangerPolicyResource);
        resources.put("table", tablerRangerPolicyResource);
        resources.put("column", columRangerPolicyResource);

        List<RangerPolicy.RangerPolicyItem> policyItems = new ArrayList<RangerPolicy.RangerPolicyItem>();

        RangerPolicy.RangerPolicyItem rangerPolicyItem = new RangerPolicy.RangerPolicyItem();
        List<String> users = new ArrayList<String>();
        if(StringUtils.isNotBlank(policeUser)){
            String[] policeUserArr = policeUser.split("\\,");
            if (policeUserArr.length > 0){
                for (int i = 0; i < policeUserArr.length; i++) {
                    users.add(policeUserArr[i]);
                }
            }
            rangerPolicyItem.setUsers(users);
        }

        List<RangerPolicy.RangerPolicyItemAccess> rangerPolicyItemAccesses = new ArrayList<RangerPolicy.RangerPolicyItemAccess>();

        if(StringUtils.isNotBlank(operatePermissionsType)){
            String[] operatePermArr = operatePermissionsType.split("\\,");
            RangerPolicy.RangerPolicyItemAccess rangerPolicyItemAccess;
            if (operatePermArr.length > 0){
                for (int i = 0; i < operatePermArr.length; i++) {
                    rangerPolicyItemAccess = new RangerPolicy.RangerPolicyItemAccess();
                    rangerPolicyItemAccess.setType(operatePermArr[i]);
                    rangerPolicyItemAccess.setIsAllowed(Boolean.TRUE);
                    rangerPolicyItemAccesses.add(rangerPolicyItemAccess);
                }
            } 
        }

        rangerPolicyItem.setAccesses(rangerPolicyItemAccesses);

        policyItems.add(rangerPolicyItem);

        rangerPolicy.setPolicyItems(policyItems);
        rangerPolicy.setResources(resources);
        return rangerPolicy;
	}
	
	/**
     * 为创建策略而创建的策略对象
     * 
     * @param PoliceName
     * @param policeUser
     * @param dbName
     * @param tableName
     * @param operatePermissionsType
     * @return
     */
    public static RangerPolicy createOfPolicy(String PoliceName, String policeUser, String dbName, String tableName,
    		String colPermissionsType, String operatePermissionsType) {
        RangerPolicy rangerPolicy = new RangerPolicy();
        rangerPolicy.setService(service);
        rangerPolicy.setName(PoliceName);
        rangerPolicy.setIsAuditEnabled(true);

        Map<String, RangerPolicy.RangerPolicyResource> resources = new HashMap<String, RangerPolicy.RangerPolicyResource>();

        RangerPolicy.RangerPolicyResource dbRangerPolicyResource = new RangerPolicy.RangerPolicyResource();
        RangerPolicy.RangerPolicyResource tablerRangerPolicyResource = new RangerPolicy.RangerPolicyResource();
        RangerPolicy.RangerPolicyResource columRangerPolicyResource = new RangerPolicy.RangerPolicyResource();

//        String newPoliceUser = policeUser;
//        if (policeUser.contains(",")) {
//            newPoliceUser = policeUser.replace(",", "_");
//        }
        ArrayList<String> dbList = new ArrayList<String>();

        if (dbName.contains(",")) {
            String[] dbArr = dbName.split(",");
            for (String dbNameS : dbArr) {
                dbList.add(dbNameS);
            }
        } else {
            dbList.add(dbName);
        }

//        dbList.add(newPoliceUser + "_autoCreateDb");// 默认为每个策略添加一个唯一的库，以区分创建了权限相同的策略
        dbRangerPolicyResource.setValues(dbList);

        tablerRangerPolicyResource.setValue(tableName);        
        if (StringUtils.isBlank(colPermissionsType))
            columRangerPolicyResource.setValue("*");
        else
            columRangerPolicyResource.setValue(colPermissionsType);
                
        resources.put("database", dbRangerPolicyResource);
        resources.put("table", tablerRangerPolicyResource);
        resources.put("column", columRangerPolicyResource);

        List<RangerPolicy.RangerPolicyItem> policyItems = new ArrayList<RangerPolicy.RangerPolicyItem>();

        RangerPolicy.RangerPolicyItem rangerPolicyItem = new RangerPolicy.RangerPolicyItem();
        List<String> users = new ArrayList<String>();
        String[] policeUserArr = policeUser.split("\\,");
        if (policeUserArr.length > 0){
            for (int i = 0; i < policeUserArr.length; i++) {
                users.add(policeUserArr[i]);
            }
        }
        rangerPolicyItem.setUsers(users);

        List<RangerPolicy.RangerPolicyItemAccess> rangerPolicyItemAccesses = new ArrayList<RangerPolicy.RangerPolicyItemAccess>();

        String[] operatePermArr = operatePermissionsType.split("\\,");
        RangerPolicy.RangerPolicyItemAccess rangerPolicyItemAccess;
        if (operatePermArr.length > 0){
            for (int i = 0; i < operatePermArr.length; i++) {
                rangerPolicyItemAccess = new RangerPolicy.RangerPolicyItemAccess();
                rangerPolicyItemAccess.setType(operatePermArr[i]);
                rangerPolicyItemAccess.setIsAllowed(Boolean.TRUE);
                rangerPolicyItemAccesses.add(rangerPolicyItemAccess);
            }
        }

        rangerPolicyItem.setAccesses(rangerPolicyItemAccesses);

        policyItems.add(rangerPolicyItem);

        rangerPolicy.setPolicyItems(policyItems);
        rangerPolicy.setResources(resources);
        return rangerPolicy;
    }
}
