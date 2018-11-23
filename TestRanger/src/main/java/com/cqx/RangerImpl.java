package com.cqx;

import org.apache.log4j.Logger;
import org.apache.ranger.admin.client.datatype.RESTResponse;
import org.apache.ranger.plugin.model.RangerPolicy;
import org.apache.ranger.plugin.util.RangerRESTUtils;
import org.apache.ranger.plugin.util.ServicePolicies;

import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;

public class RangerImpl implements Ranger {
	private static Logger log = Logger.getLogger(RangerImpl.class);
//	private static final String EXPECTED_MIME_TYPE = PropertyUtil
//			.getProperty("expected_mime_type");
	private static String rangerBaseUrl = PropertyUtil
			.getProperty("rangerBaseUrl");
	private static String service = PropertyUtil.getProperty("service"); // hive的服务名
	private static String adminUser = PropertyUtil.getProperty("adminUser");
	private static String adminPwd = PropertyUtil.getProperty("adminPwd"); // ranger自己的登录密码（不是通过单点登录的密码）

	public String getAllValidPolice() {
		String url = rangerBaseUrl + "/service/plugins/policies/download/"
				+ service;
		log.info("getAllValidPolice, reqUrl=" + url);
		ClientResponse response = null;
		Client client = null;
		String allPolice = null;
		try {
			client = Client.create();
			WebResource webResource = client.resource(url).queryParam(
					RangerRESTUtils.REST_PARAM_LAST_KNOWN_POLICY_VERSION,
					Long.toString(68));
			response = webResource.accept(RangerRESTUtils.REST_MIME_TYPE_JSON)
					.get(ClientResponse.class);
			if (response != null && response.getStatus() == 200) {
				ServicePolicies ret = response.getEntity(ServicePolicies.class);
				Gson gson = new Gson();
				allPolice = gson.toJson(ret);
				log.info("getAllValidPolice is success , the resp="
						+ gson.toJson(ret));
			} else {
				RESTResponse resp = RESTResponse.fromClientResponse(response);
				log.warn("getAllValidPolice is fail," + resp.toString());
			}
		} catch (Exception e) {
			log.error("getAllValidPolice is fail, errMassge=" + e.getMessage());
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.destroy();
			}
		}
		return allPolice;
	}

	public String getPolicyByName(String policyName) {
		String url = rangerBaseUrl + "/service/public/v2/api/service/"
				+ service + "/policy/" + policyName;
		log.info("getPolicyByName, reqUrl=" + url);
		Client client = null;
		ClientResponse response = null;
		String jsonString = null;
		try {
			client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(adminUser, adminPwd));
			WebResource webResource = client.resource(url);
			response = webResource.accept(RangerRESTUtils.REST_MIME_TYPE_JSON)
//			response = webResource.accept(EXPECTED_MIME_TYPE).get(
					.get(ClientResponse.class);
			if (response.getStatus() == 200) {
				jsonString = response.getEntity(String.class);
				log.info("getPolicyByName is success, the response message is :"
						+ jsonString);
			} else {
				RESTResponse resp = RESTResponse.fromClientResponse(response);
				jsonString = resp.toJson();
				log.warn("getPolicyByName is fail, the response message is :"
						+ resp.toString());
			}
		} catch (Exception e) {
			RESTResponse resp = RESTResponse.fromClientResponse(response);
			jsonString = resp.toJson();
			log.error("getPolicyByName is fail, the error message is :"
					+ e.getMessage() + " and the response message is : "
					+ jsonString);
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.destroy();
			}
		}
		return jsonString;
	}

	public boolean createPolice(CreatePoliceReq req) {
		boolean flag = false;
		String url = rangerBaseUrl + "/service/public/v2/api/policy";
		log.info("CreatePolice of reqUrl=" + url);
		// 添加多个用户时将分割符逗号替换成下划线，用来生成新的策略名称
//		String newPoliceUser = req.getPoliceUser();
//		if (req.getPoliceUser().contains(",")) {
//			newPoliceUser = req.getPoliceUser().replace(",", "_");
//		}
//		String PoliceName = newPoliceUser + "_police";
		ClientResponse response = null;
		Client client = null;
		try {
			client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(adminUser, adminPwd));
			WebResource webResource = client.resource(url);
			Gson gson = new Gson();
			RangerPolicy createOfPolicy = SupportRangerImpl.createOfPolicy(
					req.getPoliceName(), req.getPoliceUser(), req.getDbName(),
					req.getTableName(), req.getColPermissionsType(), req.getPermissionsType());
//			log.info("json:"+gson.toJson(createOfPolicy));
			response = webResource
					.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
					.type(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
					.post(ClientResponse.class, gson.toJson(createOfPolicy));
			if (response != null && response.getStatus() == 200) {
				RangerPolicy rangerPolicy = response
						.getEntity(RangerPolicy.class);
				log.info("Create Police is success, the police message is="
						+ rangerPolicy);
				flag = true;
			} else {
				log.warn("Create Police is fail, the warn message is="
						+ response.toString());
			}
		} catch (Exception e) {
			log.error("Create Police is fail, the error message is="
					+ e.getMessage());
			flag = false;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.destroy();
			}
		}
		return flag;
	}

	public boolean deletePoliceByPoliceName(String policeName) {
		boolean flag = false;
		String url = rangerBaseUrl
				+ "/service/public/v2/api/policy?servicename=" + service
				+ "&policyname=" + policeName;
		log.info("DeletePoliceByPoliceName of requrl " + url);
		ClientResponse response = null;
		Client client = null;
		try {
			client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(adminUser, adminPwd));
			WebResource webResource = client.resource(url);
			webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
					.delete();
			flag = true;
			log.info("DeletePoliceByPoliceName is success.");
		} catch (Exception e) {
			log.error("DeletePoliceByPoliceName is fail. the errMassage is "
					+ e.getMessage());
			flag = false;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.destroy();
			}
		}
		return flag;
	}

	public boolean updatePolicyById(UpdatePoliceReq req) {
		boolean flag = false;
		String url = rangerBaseUrl + "/service/public/v2/api/policy/"
				+ req.getPoliceId();
		log.info("UpdatePolicyById of reqUrl=" + url);
		RangerPolicy rangerPolicy = SupportRangerImpl.updateOfPolicy(
				req.getPoliceName(), req.getDbName(), req.getTableName(),
				req.getPermissionsType(), req.getPoliceUser(),
				req.getColPermissionsType(), req.getIsEnabled());
		ClientResponse response = null;
		Client client = null;
		try {
			client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(adminUser, adminPwd));
			WebResource webResource = client.resource(url);
			Gson gson = new Gson();
			response = webResource
					.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
					.type(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
					.put(ClientResponse.class, gson.toJson(rangerPolicy));
			if (response != null && response.getStatus() == 200) {
				RangerPolicy policy = response.getEntity(RangerPolicy.class);
				flag = true;
				log.info("UpdatePolicyById is success, the police message is="
						+ policy);
			} else {
				log.warn("UpdatePolicyById is fail, the fail message is="
						+ response.toString());
			}
		} catch (Exception e) {
			log.error("UpdatePolicyById is fail, the error message is="
					+ e.getMessage());
			flag = false;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.destroy();
			}
		}
		return flag;
	}
	
	public boolean updatePolicyByName(UpdatePoliceReq req) {
		boolean flag = false;
		String url = rangerBaseUrl + "/service/public/v2/api/service/"
				+ service + "/policy/" + req.getPoliceName();
		log.info("updatePolicyByName of reqUrl=" + url);
		RangerPolicy rangerPolicy = SupportRangerImpl.updateOfPolicy(
				req.getPoliceName(), req.getDbName(), req.getTableName(),
				req.getPermissionsType(), req.getPoliceUser(),
				req.getColPermissionsType(), req.getIsEnabled());
		ClientResponse response = null;
		Client client = null;
		try {
			client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(adminUser, adminPwd));
			WebResource webResource = client.resource(url);
			Gson gson = new Gson();
//			log.info("json:"+gson.toJson(rangerPolicy));
			response = webResource
					.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
					.type(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
					.put(ClientResponse.class, gson.toJson(rangerPolicy));
			if (response != null && response.getStatus() == 200) {
				RangerPolicy policy = response.getEntity(RangerPolicy.class);
				flag = true;
				log.info("updatePolicyByName is success, the police message is="
						+ policy);
			} else {
				log.warn("updatePolicyByName is fail, the fail message is="
						+ response.toString());
			}
		} catch (Exception e) {
			log.error("updatePolicyByName is fail, the error message is="
					+ e.getMessage());
			flag = false;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.destroy();
			}
		}
		return flag;
	}

	public boolean deletePoliceByPoliceId(String policeId) {
		boolean flag = false;
		String url = rangerBaseUrl + "/service/public/v2/api/policy/"
				+ policeId;
		log.info("DeletePoliceByPoliceId of reqUrl=" + url);
		ClientResponse response = null;
		Client client = null;
		try {
			client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(adminUser, adminPwd));
			WebResource webResource = client.resource(url);
			webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
					.delete();
			flag = true;
		} catch (Exception e) {
			log.error("DeletePoliceByPoliceId is fail, the error Massage is="
					+ e.getMessage());
			flag = false;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.destroy();
			}
		}
		return flag;
	}

	/**
	 * 这里的删除只是把用户设为不可见，不可见之后在配置策略时，这个用户就变成不可选，但是原先这个用户所拥有的策略还是存在的。真正删除这个用户后，
	 * 其所拥有的策略才不存在。
	 * 
	 * @param UserName
	 * @return
	 */
	public boolean deleteUserByUserName(String UserName) {
		boolean flag = false;
		String url = rangerBaseUrl + "/service/xusers/users/userName/"
				+ UserName;
		// service/xusers/secure/users/delete?forceDelete=true&
		log.info("deleteUserByUserName of reqUrl=" + url);
		ClientResponse response = null;
		Client client = null;
		try {
			client = Client.create();
			client.addFilter(new HTTPBasicAuthFilter(adminUser, adminPwd));
			WebResource webResource = client.resource(url);
			webResource.accept(RangerRESTUtils.REST_EXPECTED_MIME_TYPE)
					.delete();
			flag = true;
		} catch (Exception e) {
			log.error("DeletePoliceByPoliceId is fail, the error Massage is="
					+ e.getMessage());
			flag = false;
		} finally {
			if (response != null) {
				response.close();
			}
			if (client != null) {
				client.destroy();
			}
		}
		return flag;
	}
}
