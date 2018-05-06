package com.model2.mvc.service.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.User;


public class UserDAO {
	
	public UserDAO(){
	}

	public void insertUser(User user) throws Exception {
		
		Connection con = DBUtil.getConnection();

		String sql = "insert into USERS values (?,?,?,'user',?,?,?,?,sysdate)";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, user.getUserId());
		stmt.setString(2, user.getUserName());
		stmt.setString(3, user.getPassword());
		stmt.setString(4, user.getSsn());
		stmt.setString(5, user.getPhone());
		stmt.setString(6, user.getAddr());
		stmt.setString(7, user.getEmail());
		stmt.executeUpdate();
		
		con.close();
	}

	public User findUser(String userId) throws Exception {
		
		Connection con = DBUtil.getConnection();

		String sql = "select * from USERS where USER_ID=?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, userId);

		ResultSet rs = stmt.executeQuery();

		User user = null;
		while (rs.next()) {
			user = new User();
			user.setUserId(rs.getString("USER_ID"));
			user.setUserName(rs.getString("USER_NAME"));
			user.setPassword(rs.getString("PASSWORD"));
			user.setRole(rs.getString("ROLE"));
			user.setSsn(rs.getString("SSN"));
			user.setPhone(rs.getString("CELL_PHONE"));
			user.setAddr(rs.getString("ADDR"));
			user.setEmail(rs.getString("EMAIL"));
			user.setRegDate(rs.getDate("REG_DATE"));
		}
		
		con.close();

		return user;
	}

	public Map<String,Object> getUserList(Search search) throws Exception {
		
		Map<String, Object> map = new HashMap<String, Object>();
		Connection con = DBUtil.getConnection();
		
		String sql = "select user_id, user_name, email from USERS ";
		if (search.getSearchCondition() != null) {
			if (search.getSearchCondition().equals("0") && !search.getSearchKeyword().equals("")) {
				sql += " where USER_ID LIKE '%" + search.getSearchKeyword() + "%'";
			} else if (search.getSearchCondition().equals("1") && !search.getSearchKeyword().equals("")) {
				sql += " where USER_NAME LIKE '%" + search.getSearchKeyword() + "%'";
			}
		}
		sql += " order by USER_ID";
		
		System.out.println("UserDAO :: Original SQL :: " + sql);
		
		int totalCount = this.getTotalCount(sql);
		System.out.println("UserDAO :: totalCount :: "+totalCount);
		
		sql = makeCurrentPageSql(sql, search);
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		System.out.println(search);

		List<User> list = new ArrayList<User>();
		
		while(rs.next()) {
			User user = new User();
			user.setUserId(rs.getString("user_id"));
			user.setUserName(rs.getString("user_name"));
			user.setEmail(rs.getString("email"));
			list.add(user);
		}
		
		map.put("totalCount", new Integer(totalCount));
		map.put("list", list);
		
		rs.close();
		pStmt.close();
		con.close();
			
		return map;
	}

	public void updateUser(User user) throws Exception {
		
		Connection con = DBUtil.getConnection();

		String sql = "update USERS set USER_NAME=?,CELL_PHONE=?,ADDR=?,EMAIL=? where USER_ID=?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, user.getUserName());
		stmt.setString(2, user.getPhone());
		stmt.setString(3, user.getAddr());
		stmt.setString(4, user.getEmail());
		stmt.setString(5, user.getUserId());
		stmt.executeUpdate();
		
		con.close();
	}
	
	private int getTotalCount(String sql) throws Exception{
		
		sql = "SELECT COUNT(*) FROM (" + sql + ") countTable";
		
		Connection con = DBUtil.getConnection();
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		int totalCount = 0;
		if(rs.next()) {
			totalCount = rs.getInt(1);
		}
		
		pStmt.close();
		con.close();
		rs.close();
		
		return totalCount;
	}
	
	private String makeCurrentPageSql(String sql, Search search) {
		sql = "SELECT * "+
				"FROM (	SELECT inner_table.*, ROWNUM AS row_seq " +
							" FROM ( " +sql+ " ) inner_table " +
							" WHERE ROWNUM <= "+search.getPage()*search.getpageSize()+")"+
				"WHERE row_seq BETWEEN " + ((search.getPage()-1)*search.getpageSize()+1) + " AND " + search.getPage()*search.getpageSize();
		
		System.out.println("UserDAO :: make SQL :: " + sql);
		
		return sql;
	}
}