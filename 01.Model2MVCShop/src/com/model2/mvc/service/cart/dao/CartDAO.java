package com.model2.mvc.service.cart.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.common.util.DBUtil;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;

public class CartDAO {
	
	public CartDAO() {
		
	}
	
	public void addCart(Cart cart) throws Exception{
		
		Connection con = DBUtil.getConnection();
		
		String sql = "INSERT INTO cart(cart_no, user_id, prod_no, cart_cnt)" + 
					 "VALUES(seq_cart_cart_no.NEXTVAL, ?, ?, ?)";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, cart.getCartUser().getUserId());
		stmt.setInt(2, cart.getCartProd().getProdNo());
		stmt.setInt(3, cart.getCartCnt());
		
		stmt.executeUpdate();
		
		con.close();
	}
	
	//장바구니
	public Map<String, Object> getCartList(Search search, String userId) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		Connection con = DBUtil.getConnection();
		
		String sql = "SELECT c.cart_no, c.user_id, c.cart_cnt, c.prod_no, p.prod_name, p.prod_detail, p.manufacture_day, p.price, p.image_file, p.reg_date, p.prod_cnt" + 
				"  FROM cart c, product p" + 
				" WHERE c.prod_no = p.prod_no" + 
				"   AND c.user_id = '" + userId + "'" + 
				" ORDER BY c.cart_no";
		
		System.out.println("CartDAO :: Original SQL :: " + sql);
		
		int totalCount = this.getTotalCount(sql);
		System.out.println("CartDAO :: totalCount :: "+totalCount);

		//sql = makeCurrentPageSql(sql, search);
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();
		
		System.out.println(search);

		List<Cart> list = new ArrayList<Cart>();

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
			
			User user = new User();
			user.setUserId(userId);
			
			Cart cart = new Cart();
			cart.setCartProd(product);
			cart.setCartUser(user);
			cart.setCartNo(Integer.parseInt(rs.getString("cart_no")));
			cart.setCartCnt(Integer.parseInt(rs.getString("cart_cnt")));
			
			list.add(cart);
		}
		
		map.put("totalCount", new Integer(totalCount));
		map.put("list", list);
		
		rs.close();
		pStmt.close();
		con.close();
		
		return map;
	}
	
	public void deleteCart(int cartNo) throws Exception{
		
		Connection con = DBUtil.getConnection();
		
		String sql = "DELETE FROM cart WHERE cart_no = ?";

		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setInt(1, cartNo);
		
		stmt.executeUpdate();
		
		con.close();
	}
	
	public void updateCart(Cart cart) throws Exception{

		Connection con = DBUtil.getConnection();
		
		String sql = "UPDATE cart SET cart_cnt = ? WHERE cart_no = ?";

		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setInt(1, cart.getCartCnt());
		stmt.setInt(2, cart.getCartNo());
		
		stmt.executeUpdate();
		
		con.close();
	}
	
	public Cart findCart(String userId, int prodNo) throws Exception{
		
		Connection con = DBUtil.getConnection();

		String sql = "SELECT cart_no, user_id, prod_no, cart_cnt FROM cart WHERE user_id = ? AND prod_no = ?";
		
		PreparedStatement stmt = con.prepareStatement(sql);
		stmt.setString(1, userId);
		stmt.setInt(2, prodNo);

		ResultSet rs = stmt.executeQuery();

		User user = null;
		Product product = null;
		Cart cart = null;
		
		while (rs.next()) {
			user = new User();
			user.setUserId(rs.getString("user_id"));
			
			product = new Product();
			product.setProdNo(rs.getInt("prod_no"));
			
			cart = new Cart();
			cart.setCartNo(rs.getInt("cart_no"));
			cart.setCartCnt(rs.getInt("cart_cnt"));
			cart.setCartProd(product);
			cart.setCartUser(user);
		}
		
		con.close();

		return cart;
	}
	
	//일괄 구매시 구매페이지에서 조회
	public Map<String, Object> getCartList2(String cartNo) throws Exception{
		Map<String, Object> map = new HashMap<String, Object>();
		
		String[] cartList = cartNo.split(",");
		
		Connection con = DBUtil.getConnection();

		String sql = "SELECT c.cart_no, c.user_id, c.cart_cnt, c.prod_no, p.prod_name, p.prod_detail, p.manufacture_day, p.price, p.image_file, p.reg_date, p.prod_cnt"+
				"  FROM cart c, product p" + 
				" WHERE c.prod_no = p.prod_no" + 
				"   AND c.cart_no IN (";
		for(int i=0; i<cartList.length; i++) {
			if(i==0) {
				sql += "'"+cartList[i]+"'";
			}else {
				sql += ",'"+cartList[i]+"'";
			}
		}
		sql += ")";
		
		System.out.println("CartDAO :: Original SQL :: " + sql);
		
		int totalCount = this.getTotalCount(sql);
		System.out.println("CartDAO :: totalCount :: "+totalCount);
		
		PreparedStatement pStmt = con.prepareStatement(sql);
		ResultSet rs = pStmt.executeQuery();

		List<Cart> list = new ArrayList<Cart>();
		
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
			
			Cart cart = new Cart();
			cart.setCartProd(product);
			cart.setCartNo(Integer.parseInt(rs.getString("cart_no")));
			cart.setCartCnt(Integer.parseInt(rs.getString("cart_cnt")));
			
			list.add(cart);
		}
		
		map.put("totalCount", new Integer(totalCount));
		map.put("list", list);
		
		rs.close();
		pStmt.close();
		con.close();

		return map;
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
