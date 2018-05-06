package com.model2.mvc.service.cart.impl;

import java.util.HashMap;
import java.util.Map;

import com.model2.mvc.common.Search;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.dao.CartDAO;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.dao.ProductDAO;

public class CartServiceImpl implements CartService {
	
	private CartDAO cartDAO;

	public CartServiceImpl() {
		// TODO Auto-generated constructor stub
		cartDAO = new CartDAO();
	}

	@Override
	public void addCart(Cart cart) throws Exception {
		// TODO Auto-generated method stub
		cartDAO.addCart(cart);
	}

	@Override
	public Map<String, Object> getCartList(Search search, String userId) throws Exception {
		// TODO Auto-generated method stub
		return cartDAO.getCartList(search, userId);
	}

	@Override
	public Map<String, Object> getCartList2(String cartNo) throws Exception {
		// TODO Auto-generated method stub
		return cartDAO.getCartList2(cartNo);
	}

	@Override
	public void updateCart(Cart cart) throws Exception {
		// TODO Auto-generated method stub
		cartDAO.updateCart(cart);
	}

	@Override
	public void deleteCart(int cartNo) throws Exception {
		// TODO Auto-generated method stub
		cartDAO.deleteCart(cartNo);
	}

	@Override
	public Cart getCart(String userId, int prodNo) throws Exception {
		// TODO Auto-generated method stub
		return cartDAO.findCart(userId, prodNo);
	}

}
