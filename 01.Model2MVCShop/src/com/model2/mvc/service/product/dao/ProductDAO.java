package com.model2.mvc.service.product.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;

public class ProductDAO {

	public ProductDAO() {
		// TODO Auto-generated constructor stub
	}
	
	public void insertProduct(Product product) throws Exception{
		
		Connection con = DBUtil.getConnection();
		
		String sql = "INSERT INTO product(prod_no, prod_name, prod_detail, manufacture_day, price, image_file, reg_date, prod_cnt) "
							   + "VALUES (seq_product_prod_no.NEXTVAL,?, ?, ?, ?, ?, SYSDATE, ?)";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		//stmt.setInt(1, product.getProdNo());
		stmt.setString(1, product.getProdName());
		stmt.setString(2, product.getProdDetail());
		stmt.setString(3, product.getManuDate());
		stmt.setInt(4, product.getPrice());
		stmt.setString(5, product.getFileName());
		//stmt.setDate(6, product.getRegDate());
		stmt.setInt(6, product.getProdCnt());
		
		stmt.executeUpdate();
		
		con.close();
	}
	
	public Product findProduct(int prodNo) throws Exception {	//	상품 리스트에서 상품 이름 클릭 했을 때
		Connection con = DBUtil.getConnection();
		
		String sql = "SELECT p.prod_no, p.prod_name, p.prod_detail, p.manufacture_day, p.price, p.image_file, p.reg_date, p.prod_cnt FROM product p where p.prod_no = ?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setInt(1, prodNo);
		
		ResultSet rs = stmt.executeQuery();
		
		Product product = null;
		while(rs.next()) {
			product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			product.setProdName(rs.getString("prod_name"));
			product.setProdDetail(rs.getString("prod_detail"));
			product.setManuDate(rs.getString("manufacture_day"));
			product.setPrice(rs.getInt("price"));
			product.setFileName(rs.getString("image_file"));
			product.setRegDate(rs.getDate("reg_date"));
			product.setProdCnt(rs.getInt("prod_cnt"));
		}
		
		con.close();
		
		return product;
	}
	

	public Map<String, Object> getProductIn(String prodNoList) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		
		Connection con = DBUtil.getConnection();
		
		String[] prodNo = prodNoList.split(",");
		
		String sql = "SELECT p.prod_no, p.prod_name, p.prod_detail, p.manufacture_day, p.price, p.image_file, p.reg_date, p.prod_cnt "
				+ "FROM product p WHERE p.prod_no IN (";
		
		for(int i=0; i<prodNo.length; i++) {
			if (i==0) {
				sql += "'"+prodNo[i]+"'";
			}else {
				sql += ",'"+prodNo[i]+"'";
			}
		}
		sql += ")";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		ResultSet rs = stmt.executeQuery();

		int totalCount = 0;
		List<Product> list = new ArrayList<Product>();
		while(rs.next()) {
			totalCount++;
			Product product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			product.setProdName(rs.getString("prod_name"));
			product.setProdDetail(rs.getString("prod_detail"));
			product.setManuDate(rs.getString("manufacture_day"));
			product.setPrice(rs.getInt("price"));
			product.setFileName(rs.getString("image_file"));
			product.setRegDate(rs.getDate("reg_date"));
			product.setProdCnt(rs.getInt("prod_cnt"));
			
			list.add(product);
		}
		
		map.put("totalCount", totalCount);
		map.put("list", list);
		
		rs.close();
		stmt.close();
		con.close();
		
		return map;
	}

	public Map<String, Object> getProductList(Search search) throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		Connection con = DBUtil.getConnection();
		
		String sql = "SELECT DISTINCT p.prod_no, p.prod_name, p.prod_detail, p.manufacture_day, p.price, p.image_file, p.reg_date, p.prod_cnt" + 
				"  FROM product p, (SELECT * FROM transaction WHERE tran_status_code <> '5') t" + 
				" WHERE p.prod_no = t.prod_no(+) ";
		if(search.getSearchCondition() != null) {
			if(search.getSearchCondition().equals("0") && !search.getSearchKeyword().equals("")) {
				sql += " AND p.prod_no LIKE '%"+search.getSearchKeyword()+"%'";
			}else if(search.getSearchCondition().equals("1") && !search.getSearchKeyword().equals("")) {
				sql += " AND p.prod_name LIKE '%"+search.getSearchKeyword()+"%'";
			}else if(search.getSearchCondition().equals("2") && !search.getSearchKeyword().equals("")) {
				sql += " AND p.price LIKE '%"+search.getSearchKeyword()+"%'";
			}
		}
		sql += " ORDER BY ";
		
		if(search.getSort() != null) {
			if(search.getSort().equals("asc")) {
				sql += " p.price,";
			}else if(search.getSort().equals("dsc")) {
				sql += " p.price DESC,";
			}
		}
		sql += " p.prod_no";
		
		System.out.println("ProductDAO :: Original SQL :: " + sql);
		
		int totalCount = this.getTotalCount(sql);
		System.out.println("ProductDAO :: totalCount :: "+totalCount);

		sql = makeCurrentPageSql(sql, search);
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		System.out.println(search);

		List<Product> list = new ArrayList<Product>();

		while(rs.next()) {
			Product product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			product.setProdName(rs.getString("prod_name"));
			product.setProdDetail(rs.getString("prod_detail"));
			product.setManuDate(rs.getString("manufacture_day"));
			product.setPrice(rs.getInt("price"));
			product.setFileName(rs.getString("image_file"));
			product.setRegDate(rs.getDate("reg_date"));
			product.setProdCnt(rs.getInt("prod_cnt"));
			list.add(product);
		}
		
		map.put("totalCount", new Integer(totalCount));
		map.put("list", list);
		
		rs.close();
		pStmt.close();
		con.close();
		
		return map;
	}
	
	public void updateProduct(Product product) throws Exception {
		
		Connection con = DBUtil.getConnection();

		String sql = "UPDATE product SET prod_name = ?, prod_detail = ?, manufacture_day = ?, price = ?, image_file = ?, prod_cnt = ? WHERE prod_no = ?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, product.getProdName());
		stmt.setString(2, product.getProdDetail());
		stmt.setString(3, product.getManuDate());
		stmt.setInt(4, product.getPrice());
		stmt.setString(5, product.getFileName());
		stmt.setInt(6, product.getProdCnt());
		stmt.setInt(7, product.getProdNo());
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
