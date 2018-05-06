package com.model2.mvc.view.cart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.Cart;

public class UpdateCartAction extends Action{

	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		int cartNo = Integer.parseInt(request.getParameter("cartNo"));
		int cartCnt = Integer.parseInt(request.getParameter("cartCnt"));
		
		Cart cart = new Cart();
		cart.setCartNo(cartNo);
		cart.setCartCnt(cartCnt);
		
		CartService service = new CartServiceImpl();
		service.updateCart(cart);
		
		return "forward:/listCart.do";
	}

}
