package com.model2.mvc.view.cart;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.model2.mvc.framework.Action;
import com.model2.mvc.service.cart.CartService;
import com.model2.mvc.service.cart.impl.CartServiceImpl;
import com.model2.mvc.service.domain.Cart;
import com.model2.mvc.service.domain.Product;
import com.model2.mvc.service.domain.User;
import com.model2.mvc.service.product.ProductService;
import com.model2.mvc.service.product.impl.ProductServiceImpl;

public class AddCartAction extends Action{

	public String execute(HttpServletRequest request, HttpServletResponse response) throws Exception{
		HttpSession session = request.getSession();
		User user = (User)session.getAttribute("user");
		
		Product product = new Product();
		product.setProdNo(Integer.parseInt(request.getParameter("prodNo")));
		
		Cart cart = new Cart();
		
		CartService service = new CartServiceImpl();
		cart = service.getCart(user.getUserId(), product.getProdNo());
		
		if(cart == null) {
			cart = new Cart();
			
			//cart.setCartCnt(Integer.parseInt(request.getParameter("cartCnt")));
			cart.setCartCnt(Integer.parseInt(request.getParameter("tranCnt")));
			cart.setCartProd(product);
			cart.setCartUser(user);
			
			System.out.println(cart);
			
			service.addCart(cart);
		}else {
			//cart.setCartCnt(Integer.parseInt(request.getParameter("cartCnt")));
			cart.setCartCnt(Integer.parseInt(request.getParameter("tranCnt")));
			
			System.out.println(cart);
			
			service.updateCart(cart);
		}
		
		return "forward:/listCart.do";
	}

}
