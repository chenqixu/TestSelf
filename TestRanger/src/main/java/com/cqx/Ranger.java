package com.cqx;

public interface Ranger {
	/**
	 * 获取所有有效的策略
	 * 
	 * @return
	 */
	public String getAllValidPolice();

	/**
	 * 创建策略
	 * 
	 * @param policeUser
	 *            策略对应的用户
	 * @param dbName
	 *            :数据库，多个数据库用逗号分割（英文符）
	 * @param tableName
	 *            ：表，多个用逗号分割
	 * @param permissionsType
	 *            表，所对应的权限，多个用逗号分割,eg :drop, all, select, update, create, index,
	 *            lock, alter
	 * @return
	 */
	public boolean createPolice(CreatePoliceReq createRequest);

	/**
	 * 通过策略名称获取策略
	 * 
	 * @param policyName
	 * @return
	 */
	public String getPolicyByName(String policyName);

	/**
	 * 通过policeName 删除策略
	 * 
	 * @param policeName
	 * @return
	 */
	public boolean deletePoliceByPoliceName(String policeName);

	/**
	 * 通过policeId 删除策略
	 * 
	 * @param policeId
	 * @return
	 */
	public boolean deletePoliceByPoliceId(String policeId);

	/**
	 * 修改策略
	 * 
	 * @param updatePoliceReq
	 * @return
	 */
	public boolean updatePolicyById(UpdatePoliceReq updatePoliceReq);
}
