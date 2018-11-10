package com.sept.database.access;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.ColumnBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.Database.FileFormat;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.healthmarketscience.jackcess.TableBuilder;
import com.sept.database.file.FileIOTool;
import com.sept.framework.exception.AppException;
import com.sept.framework.util.data.DataStore;

/**
 * 创建Access文件 <br>
 * 当前需求只需要生成Access文件即可，没加读取查询
 * 
 * @author 张超
 * @version 1.0
 * @date 创建时间 2017年10月19日
 */
public class Access {
	private String url;
	private Database accdb;
	private HashMap<String, Table> hmTables;

	public Access() {

	}

	public Access(String url) throws AppException {
		this(url, FileFormat.V2010);
	}

	public Access(String url, Database.FileFormat fileFormat) throws AppException {
		try {
			this.url = url;
			File file = new File(this.url);

			if (!file.exists()) {
				accdb = DatabaseBuilder.create(fileFormat, new File(url));
			} else {
				if (!file.isFile()) {
					throw new AppException("url不是有效的文件！");
				}
				openAccess(url);

			}
			hmTables = new HashMap<String, Table>();
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException(e);
		}
	}

	public Access openAccess(String url) throws AppException {
		try {
			this.url = url;
			File file = new File(this.url);
			if (!file.isFile()) {
				throw new AppException("url不是有效的文件！");
			}
			if (!file.exists()) {
				throw new AppException("DB文件不存在！");
			}
			accdb = DatabaseBuilder.open(new File(url));
			this.hmTables = new HashMap<>();
			Set<String> set = accdb.getTableNames();

			for (Iterator<String> iterator = set.iterator(); iterator.hasNext();) {
				String string = (String) iterator.next();
				this.hmTables.put(string, accdb.getTable(string));
			}

		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException(e);
		}
		return this;
	}

	/**
	 * 创建表
	 * 
	 * @author 张超
	 * @throws AppException
	 * @date 创建时间 2017年10月19日
	 * @since V1.0
	 */
	public void newTable(String tableName, ArrayList<ColumnBuilder> columnBuilders) throws AppException {
		if (hmTables.containsKey(tableName)) {
			throw new AppException("Access文件【" + url + "】已存在名为【" + tableName + "】的表！");
		}

		Table newTable;
		try {
			TableBuilder tableBuilder = new TableBuilder(tableName);
			for (int i = 0; i < columnBuilders.size(); i++) {
				tableBuilder.addColumn(columnBuilders.get(i));
			}
			newTable = tableBuilder.toTable(accdb);
			hmTables.put(tableName, newTable);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AppException(e);
		}
	}

	/**
	 * 创建表
	 * 
	 * @author 张超
	 * @throws AppException
	 * @date 创建时间 2017年10月19日
	 * @since V1.0
	 */
	public void newTable(String tableName, String columnBuilders) throws AppException {
		ArrayList<ColumnBuilder> alColumnBuilders = new ArrayList<ColumnBuilder>();

		if (!hmTables.containsKey(tableName)) {
			throw new AppException("Access文件【" + url + "】已存在名为【" + tableName + "】的表！");
		}

		String[] columnBuilderss = columnBuilders.split(",");
		for (int i = 0; i < columnBuilderss.length; i++) {
			String[] temps = columnBuilderss[i].split(":");
			String columnName = temps[0];
			String columnType = temps[1];
			int type = Types.VARCHAR;
			try {
				type = Integer.parseInt(columnType);
			} catch (Exception e) {
				throw new AppException("参数类型应该是数字！");
			}
			ColumnBuilder columnBuilder = null;
			try {
				columnBuilder = new ColumnBuilder(columnName).setSQLType(type);
			} catch (SQLException e) {
				throw new AppException(e);
			}

			alColumnBuilders.add(columnBuilder);
		}
		this.newTable(tableName, alColumnBuilders);
	}

	/**
	 * 增加一行数据
	 * 
	 * @author 张超
	 * @throws IOException
	 * @throws AppException
	 * @date 创建时间 2017年10月19日
	 * @since V1.0
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addRow(String tableName, Map map) throws IOException, AppException {
		if (!this.hmTables.containsKey(tableName)) {
			throw new AppException("不存在表【" + tableName + "】!");
		}
		this.hmTables.get(tableName).addRowFromMap(map);
	}

	/**
	 * 增加多行数据
	 * 
	 * @author 张超
	 * @date 创建时间 2017年10月19日
	 * @since V1.0
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addRows(String tableName, List list) throws IOException, AppException {
		if (!this.hmTables.containsKey(tableName)) {
			throw new AppException("不存在表【" + tableName + "】!");
		}
		this.hmTables.get(tableName).addRowsFromMaps(list);
	}

	/**
	 * 简单的单表筛选
	 * 
	 * @param tableName
	 * @param filterStr
	 * @return
	 * @throws Exception
	 */
	public DataStore select(String tableName, String filterStr) throws Exception {
		DataStore vds = new DataStore();
		Table table;
		try {
			table = accdb.getTable(tableName);
			List<? extends Column> columns = table.getColumns();
			for (Row row : table) {
				vds.addRow();
				for (int i = 0; i < columns.size(); i++) {
					String key = columns.get(i).getName();
					vds.put(vds.rowCount() - 1, key, row.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e);
		}
		return vds.filter(filterStr);
	}

	/**
	 * 获取一个表中的所有数据
	 * 
	 * @param tableName
	 * @param filterStr
	 * @return
	 * @throws AppException
	 */
	public DataStore getDataStore(String tableName) throws AppException {
		DataStore vds = new DataStore();
		Table table;
		try {
			table = accdb.getTable(tableName);
			List<? extends Column> columns = table.getColumns();
			for (Row row : table) {
				vds.addRow();
				for (int i = 0; i < columns.size(); i++) {
					String key = columns.get(i).getName();
					vds.put(vds.rowCount() - 1, key, row.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e);
		}
		return vds;
	}

	public int delete(String tableName, String filterStr) throws AppException {
		DataStore vds = new DataStore();
		Table table;
		try {
			table = accdb.getTable(tableName);
			List<? extends Column> columns = table.getColumns();
			int j = 0;
			for (Row row : table) {
				vds.addRow();
				for (int i = 0; i < columns.size(); i++) {
					String key = columns.get(i).getName();
					vds.put(vds.rowCount() - 1, key, row.get(key));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new AppException(e);
		}
		return 1;
	}

	public int update() {
		return 0;

	}

	public void save() throws AppException, IOException {
		File file = new File(this.url);
		if (file.isFile()) {
			FileIOTool.writeBytesToFile(this.getBytes(), file);
		} else {
			throw new AppException("url不是有效的文件！");
		}
	}

	/**
	 * 获取bytes
	 * 
	 * @author 张超
	 * @date 创建时间 2017年10月19日
	 * @since V1.0
	 */
	public byte[] getBytes() throws AppException, IOException {
		File file = accdb.getFile();
		byte[] accbytes = FileIOTool.getBytesFromFile(file);
		return accbytes;
	}

	public static void main(String[] args) throws AppException, AppException, IOException {
		// GlobalNames.init(GlobalVars.APP_TYPE_DESK);
		// Transaction tm = TransactionManager.getTransaction();
		// tm.begin();
		// String dbPath = "D://ADB111.mdb";
		// Access access = new Access(dbPath, FileFormat.V2003);
		// access.newTable("person", "ryid:12,shbzhm:12,xm:12");
		// Sql sql = new Sql();
		// StringBuffer sqlBF = new StringBuffer();
		// sqlBF.setLength(0);
		// sqlBF.append("select to_char(ryid) ryid, shbzhm, xm from bisv.person
		// where rownum < 1000");
		// sql.setSql(sqlBF.toString());
		// DataStore dsTemp = sql.executeQuery();
		// for (int i = 0; i < 1000; i++) {
		// DebugManager.println("正在写入第"+i+"行");
		// access.addRows("person", dsTemp);
		// }
		// File file = new File("D://ADB9_copy.mdb");
		// FileIOTool.writeBytesToFile(access.getBytes(), file);

		// Access access = AccessFactory.openAccess("D://ADB111.mdb");
		// DataStore vds = access.getDataStore("person");
		// System.out.println(vds.rowCount());
		// System.out.println(vds.filterKeep(" ryid ==
		// 17092310000010636675").rowCount());
	}
}