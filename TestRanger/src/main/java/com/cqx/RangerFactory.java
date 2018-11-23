package com.cqx;

public class RangerFactory {
	private RangerImpl rangerImpl = new RangerImpl();
	
	public RangerFactory(){
		PropertyUtil.printPath();
	}
	
	public void exec(String police, boolean isplit, String params){
		// 参数处理
		String[] paramarry = null;
		String param = params;
		if(isplit){
			paramarry = params.split(",");
		}
		// 按策略执行
		if(police.equals("getAllValidPolice")){
			// 获取所有有效的策略
			String allValidPolice = rangerImpl.getAllValidPolice();
			System.out.println("allValidPolice: " + allValidPolice);
		}else if(police.equals("getPolicyByName")){
			// 根据策略名获取
			System.out.println("getPolicyByName("+param+")");
			String response = rangerImpl.getPolicyByName(param);
			System.out.println(response);
		}else if(police.equals("deletePoliceByPoliceName")){
			// 删除策略（根据策略名称）
			System.out.println("deletePoliceByPoliceName("+param+")");
			boolean flag = rangerImpl.deletePoliceByPoliceName(param);
			System.out.println(flag);
		}else if(police.equals("deletePoliceByPoliceId")){
			// 删除策略（根据策略ID）
			System.out.println("deletePoliceByPoliceId("+param+")");
			boolean flag = rangerImpl.deletePoliceByPoliceId(param);
			System.out.println(flag);
		}else if(police.equals("createPolice") && paramarry!=null){
			// 创建策略
			CreatePoliceReq createPoliceReq = new CreatePoliceReq();
			createPoliceReq.setPoliceName(paramarry[0]);// "12tUpdate13"
			createPoliceReq.setPoliceUser(paramarry[1]);// "hive,hbase"
			createPoliceReq.setDbName(paramarry[2]);// "test1"
			createPoliceReq.setTableName(paramarry[3]);// "test2"
			createPoliceReq.setPermissionsType(paramarry[4]);// "select,update"
			createPoliceReq.setColPermissionsType(paramarry[5]);// "sum_date"
			boolean createPoliceFlag = rangerImpl.createPolice(createPoliceReq);
			System.out.println(createPoliceFlag);
		}else if(police.equals("updatePolicyById") && paramarry!=null){
			// 更新策略
			UpdatePoliceReq updatePoliceReq = new UpdatePoliceReq();
			updatePoliceReq.setPoliceName(paramarry[0]);// "12tUpdate13"
			updatePoliceReq.setPoliceId(paramarry[1]);// "36"
			updatePoliceReq.setPoliceUser(paramarry[2]);// "hive,hbase"
			updatePoliceReq.setDbName(paramarry[3]);// "test1"
			updatePoliceReq.setTableName(paramarry[4]);// "test,test2"
			updatePoliceReq.setPermissionsType(paramarry[5]);// "update"
			updatePoliceReq.setIsEnabled(paramarry[6]);// "true"
			updatePoliceReq.setColPermissionsType(paramarry[7]);// "home_code"
			boolean flag = rangerImpl.updatePolicyById(updatePoliceReq);
			System.out.println(flag);
		}else if(police.equals("updatePolicyByName") && paramarry!=null){
			// 更新策略
			UpdatePoliceReq updatePoliceReq = new UpdatePoliceReq();
			updatePoliceReq.setPoliceName(paramarry[0]);// "12tUpdate13"
			updatePoliceReq.setPoliceUser(paramarry[1]);// "hive,hbase"
			updatePoliceReq.setDbName(paramarry[2]);// "test1"
			updatePoliceReq.setTableName(paramarry[3]);// "test,test2"
			updatePoliceReq.setPermissionsType(paramarry[4]);// "update"
			updatePoliceReq.setIsEnabled(paramarry[5]);// "true"
			updatePoliceReq.setColPermissionsType(paramarry[6]);// "home_code"
			boolean flag = rangerImpl.updatePolicyByName(updatePoliceReq);
			System.out.println(flag);
		}
		// 删除用户策略
//		boolean deleteFlag = rangerImpl.deleteUserByUserName("bmsoft_test");
//		System.out.println(deleteFlag);
	}
	
	/**
	 * 参数0：策略
	 * 参数1：是否多个参数
	 * 参数2：策略名称/策略ID/其他使用逗号分割
	 * */
	public static void main(String[] args) {
		if(args.length!=3){
			System.out.println("no enough args.");
		}
		String police = args[0];
		boolean isplit = Boolean.valueOf(args[1]);
		String params = args[2];
		new RangerFactory().exec(police, isplit, params);
	}
}
