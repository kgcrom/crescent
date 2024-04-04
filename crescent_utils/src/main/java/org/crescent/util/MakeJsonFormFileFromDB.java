package org.crescent.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.dbcp.BasicDataSource;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MakeJsonFormFileFromDB {

	private static final Logger logger = LoggerFactory.getLogger(MakeJsonFormFileFromDB.class);
	private ConnectionInfoList connectionInfoList = null;
	
	public static void main(String[] args) throws SQLException, IOException {
		
		if(args.length != 2) {
			throw new IllegalArgumentException("need name and jdbc_connection_info.xml location info");
		}
		
		MakeJsonFormFileFromDB makeJsonFormFileFromDB = new MakeJsonFormFileFromDB();
		makeJsonFormFileFromDB.makeFile(args[0], args[1]);
	}

	private void makeFile(String dbName, String xmlLocation) throws SQLException, IOException {
		initConnectionInfoList(xmlLocation);
		
		ConnectionInfo connectionInfo = connectionInfoList.getConnectionInfo(dbName);
		
		if(connectionInfo == null) {
			throw new IllegalStateException("no name in jdbc_connection_info.xml " + dbName);
		}

		BasicDataSource ds = new BasicDataSource();
		ds.setUrl(connectionInfo.getUrl());
		ds.setUsername(connectionInfo.getUserName());
		ds.setPassword(connectionInfo.getPassword());
		ds.setDriverClassName(connectionInfo.getJdbcDriver());
		
		Connection conn = ds.getConnection();
		String sql = connectionInfo.getSql();
		PreparedStatement psmt = conn.prepareStatement(sql);
		ResultSet rs = psmt.executeQuery();
		
		List<Field> fieldInfoList = connectionInfo.getFieldMapping().getFieldList();
		List<Map<String, String>> resultSetMapList = new ArrayList<Map<String, String>>();
		
		int limitCountEachFile = connectionInfo.getLimitRowsEachFile();
		
		int rowCount = 0;

		ObjectMapper mapper = new ObjectMapper();
		
		while(rs.next()) {
			rowCount++;
			
			Map<String, String> row = new HashMap<String, String>();
				
			for(Field f : fieldInfoList) {
				row.put(f.getTo(), rs.getString(f.getColumn()));
			}
			
			resultSetMapList.add(row);
			
			if((rowCount % limitCountEachFile) == 0 && rowCount != 0) {
				String timeText = new SimpleDateFormat("yyyymmdd-hhmmss").format(OffsetDateTime.now(ZoneId.systemDefault()));
				
				File targetFile = new File(connectionInfo.getJsonFileDir()+"/"+connectionInfo.getName()+"_"+timeText+".txt");
				
				if(!targetFile.exists()) {
					targetFile.createNewFile();
				}
				
				OutputStream os = new FileOutputStream(targetFile);
				OutputStreamWriter osw = new OutputStreamWriter(os);
				BufferedWriter bw = new BufferedWriter(osw);
				
				Map<String, Object> indexingForm = new HashMap<String, Object>();
				indexingForm.put("command", "add");
				indexingForm.put("indexingType", "bulk");
				indexingForm.put("documentList", resultSetMapList);
				
				String jsonForm = mapper.writeValueAsString(resultSetMapList);
				
				bw.write(jsonForm);
				
				bw.close();
				osw.close();
				os.close();
				
				resultSetMapList.clear();
			}
		}
		
		{
			//남은 것 처리 영역
			String timeText = new SimpleDateFormat("yyyymmdd-hhmmss").format(OffsetDateTime.now());
			File targetFile = new File(connectionInfo.getJsonFileDir()+"/"+connectionInfo.getName()+"_"+timeText+".txt");
			
			if(!targetFile.exists()) {
				targetFile.createNewFile();
			}
			
			OutputStream os = new FileOutputStream(targetFile);
			OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
			BufferedWriter bw = new BufferedWriter(osw);
			
			String jsonForm = mapper.writeValueAsString(resultSetMapList);
			bw.write(jsonForm);

			bw.close();
			osw.close();
			os.close();
		}
		
		rs.close();
		psmt.close();
		conn.close();
	}
	
	private void initConnectionInfoList(String xmlLocation) throws FileNotFoundException {
		XStream xstream = new XStream();
		xstream.processAnnotations(ConnectionInfoList.class);

		InputStream is = new FileInputStream(xmlLocation);
		ConnectionInfoList connectionInfoList = (ConnectionInfoList)xstream.fromXML(is);
		
		this.connectionInfoList = connectionInfoList;
		
		logger.info(connectionInfoList.connectionInfoList.stream().map(ConnectionInfo::getName).collect(Collectors.joining(",")));
	}
	
	@XStreamAlias("connectionInfoList")
	protected static class ConnectionInfoList {
		
		@XStreamImplicit(itemFieldName="connectionInfo")
		private List<ConnectionInfo> connectionInfoList;
		
		@XStreamOmitField
		private Map<String, ConnectionInfo> connectionInfoMapByName;

		public List<ConnectionInfo> getConnectionInfoList() {
			return connectionInfoList;
		}

		public void setConnectionInfo(List<ConnectionInfo> connectionInfoList) {
			this.connectionInfoList = connectionInfoList;
		}

		public Map<String, ConnectionInfo> getConnectionInfoMapByName() {
			return connectionInfoMapByName;
		}

		public void setConnectionInfoMapByName(
				Map<String, ConnectionInfo> connectionInfoMapByName) {
			this.connectionInfoMapByName = connectionInfoMapByName;
		}

		private ConnectionInfo getConnectionInfo(String name) {
			if(connectionInfoMapByName == null) {
				connectionInfoMapByName = new HashMap<String, ConnectionInfo>();
			
				for(ConnectionInfo connectionInfo : connectionInfoList) {
					connectionInfoMapByName.put(connectionInfo.getName(), connectionInfo);
				}
			}
			
			return connectionInfoMapByName.get(name);
		}
		
		@Override
		public String toString() {
			return "ConnectionInfoList [connectionInfoList=" + connectionInfoList
					+ ", connectionInfoMapByName=" + connectionInfoMapByName
					+ "]";
		}
		
	}
	
	@XStreamAlias("connectionInfo")
	protected static class ConnectionInfo {
		
		@XStreamAsAttribute
		private String name;
		
		@XStreamAsAttribute
		private String url;
		
		@XStreamAsAttribute
		private String userName;
		
		@XStreamAsAttribute
		private String password;
		
		@XStreamAsAttribute
		private String jdbcDriver;
		
		@XStreamAlias("fieldMapping")
		private FieldMapping fieldMapping = null;
		
		@XStreamAlias("jsonFileDir")
		private String jsonFileDir;
		
		@XStreamAlias("limitRowsEachFile")
		private int limitRowsEachFile;

		@XStreamAlias("sql")
		private String sql;
		
		
		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}

		public String getJdbcDriver() {
			return jdbcDriver;
		}

		public void setJdbcDriver(String jdbcDriver) {
			this.jdbcDriver = jdbcDriver;
		}

		public FieldMapping getFieldMapping() {
			return fieldMapping;
		}

		public void setFieldMapping(FieldMapping fieldMapping) {
			this.fieldMapping = fieldMapping;
		}

		public String getJsonFileDir() {
			return jsonFileDir;
		}

		public void setJsonFileDir(String jsonFileDir) {
			this.jsonFileDir = jsonFileDir;
		}

		public int getLimitRowsEachFile() {
			return limitRowsEachFile;
		}

		public void setLimitRowsEachFile(int limitRowsEachFile) {
			this.limitRowsEachFile = limitRowsEachFile;
		}

		@Override
		public String toString() {
			return "ConnectionInfo [name=" + name + ", url=" + url
					+ ", userName=" + userName + ", password=" + password
					+ ", jdbcDriver=" + jdbcDriver + ", fieldMapping="
					+ fieldMapping + ", jsonFileDir=" + jsonFileDir
					+ ", limitRowsEachFile=" + limitRowsEachFile + ", sql="
					+ sql + "]";
		}
	}
	
	@XStreamAlias("fieldMapping")
	protected static class FieldMapping {
		
		@XStreamImplicit(itemFieldName="field")
		private List<Field> fieldList = null;

		public List<Field> getFieldList() {
			return fieldList;
		}

		public void setFieldList(List<Field> fieldList) {
			this.fieldList = fieldList;
		}

		@Override
		public String toString() {
			return "FieldMapping [fieldList=" + fieldList + "]";
		}		
	}
	
	@XStreamAlias("Field")
	protected static class Field {
		
		@XStreamAsAttribute
		private String column;
		
		@XStreamAsAttribute
		private String to;

		public String getColumn() {
			return column;
		}

		public void setColumn(String column) {
			this.column = column;
		}

		public String getTo() {
			return to;
		}

		public void setTo(String to) {
			this.to = to;
		}

		@Override
		public String toString() {
			return "FieldMapping [column=" + column + ", to=" + to + "]";
		}
	}
}
