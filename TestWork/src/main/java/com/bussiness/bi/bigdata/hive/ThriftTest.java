package com.bussiness.bi.bigdata.hive;

import java.util.*;

import org.apache.hive.service.auth.*;
import org.apache.hive.service.cli.thrift.*;
import org.apache.thrift.protocol.*;
import org.apache.thrift.transport.*;

public class ThriftTest {
    private String HOST = "10.1.8.75";
    private int PORT = 9083;
    private String USER = "hive";
    private String PASSWORD = "hive";
    private TCLIService.Client client;
    private TOperationState tOperationState = null;    
    
    public void init() throws Exception {
		TTransport transport = HiveAuthFactory.getSocketTransport(HOST, PORT, 99999);
		transport = PlainSaslHelper.getPlainTransport(USER, PASSWORD, transport);
		client = new TCLIService.Client(new TBinaryProtocol(transport));
		transport.open();
    }
    
    public TOpenSessionResp openSession(TCLIService.Client client) throws Exception {    
        TOpenSessionReq openSessionReq = new TOpenSessionReq();  
        return client.OpenSession(openSessionReq);  
   } 
    
    public TOperationHandle submitQuery(String command) throws Exception {
		TOperationHandle tOperationHandle;
		TExecuteStatementResp resp = null;
		TSessionHandle sessHandle = openSession(client).getSessionHandle();
		TExecuteStatementReq execReq = new TExecuteStatementReq(sessHandle, command);
		// 异步运行
		execReq.setRunAsync(true);
		// 执行sql
		resp = client.ExecuteStatement(execReq);// 执行语句
		tOperationHandle = resp.getOperationHandle();// 获取执行的handle		
		if (tOperationHandle == null) {
           //语句执行异常时，会把异常信息放在resp.getStatus()中。								
			throw new Exception(resp.getStatus().getErrorMessage());
		}
		return tOperationHandle;
	}
    
    public String getQueryLog(TOperationHandle tOperationHandle)
			throws Exception {
		String log = "";
		return log;
	}
    
	public TOperationState getQueryHandleStatus(
			TOperationHandle tOperationHandle) throws Exception {
		if (tOperationHandle != null) {
			TGetOperationStatusReq statusReq = new TGetOperationStatusReq(
					tOperationHandle);
			TGetOperationStatusResp statusResp = client.GetOperationStatus(statusReq);			
			tOperationState = statusResp.getOperationState();			
		}
		return tOperationState;
	}

	public List<String> getColumns(TOperationHandle tOperationHandle)
			throws Exception {
		TGetResultSetMetadataResp metadataResp;
		TGetResultSetMetadataReq metadataReq;
		TTableSchema tableSchema;
		metadataReq = new TGetResultSetMetadataReq(tOperationHandle);
		metadataResp = client.GetResultSetMetadata(metadataReq);
		List<TColumnDesc> columnDescs;
		List<String> columns = null;
		tableSchema = metadataResp.getSchema();
		if (tableSchema != null) {
			columnDescs = tableSchema.getColumns();
			columns = new ArrayList<String>();
			for (TColumnDesc tColumnDesc : columnDescs) {
				columns.add(tColumnDesc.getColumnName());
			}
		}
		return columns;
	}

	/**
	 * 获取执行结果 select语句
	 */
	public List<Object> getResults(TOperationHandle tOperationHandle) throws Exception {
		TFetchResultsReq fetchReq = new TFetchResultsReq();
		fetchReq.setOperationHandle(tOperationHandle);
		fetchReq.setMaxRows(1000);
		TFetchResultsResp  re=client.FetchResults(fetchReq);
		List<TColumn> list = re.getResults().getColumns();
		List<Object> list_row = new ArrayList<Object>();
		for(TColumn field:list){			
			if (field.isSetStringVal()) {
				list_row.add(field.getStringVal().getValues());
			} else if (field.isSetDoubleVal()) {
				list_row.add(field.getDoubleVal().getValues());
			} else if (field.isSetI16Val()) {
				list_row.add(field.getI16Val().getValues());
			} else if (field.isSetI32Val()) {
				list_row.add(field.getI32Val().getValues());
			} else if (field.isSetI64Val()) {
				list_row.add(field.getI64Val().getValues());
			} else if (field.isSetBoolVal()) {
				list_row.add(field.getBoolVal().getValues());
			} else if (field.isSetByteVal()) {
				list_row.add(field.getByteVal().getValues());
			}
		}		
		for(Object obj:list_row){
			System.out.println(obj);
		}
		return list_row;
	}

	public void cancelQuery(TOperationHandle tOperationHandle) throws Exception {
		if (tOperationState != TOperationState.FINISHED_STATE) {
			TCancelOperationReq cancelOperationReq = new TCancelOperationReq();
			cancelOperationReq.setOperationHandle(tOperationHandle);
			client.CancelOperation(cancelOperationReq);
		}
	}
    
	public static void main(String[] args) throws Exception {
		ThriftTest tt = new ThriftTest();
		tt.init();
		TOperationHandle handle = tt.submitQuery("show databases");
		tt.getResults(handle);
	}
}
