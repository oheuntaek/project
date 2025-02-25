package users.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import users.bean.UsersDTO;
import users.dao.UsersDAO;

@Controller
public class UsersController {

	@Autowired
	private UsersService service;

	@RequestMapping(value = "/users/selectList.do", method = RequestMethod.POST)
	public ModelAndView selectList(HttpServletRequest request) {
		String str_pg = request.getParameter("pg");
		int pg = 1;
		if (str_pg != null) {
			if (!str_pg.trim().equals("") && str_pg.matches("^[0-9]*$")) {
				pg = Integer.parseInt(str_pg);
			}

		}
		System.out.println("pg = " + pg);
		int endNum = pg * 10;
		int startNum = endNum - 9;

		List<UsersDTO> list = service.selectList(startNum, endNum);
		System.out.println(list.size());
		int totalMember = service.getTotalMember(); // 총회원수
		int totalP = (totalMember + 4) / 5; // 총페이지수

		int startPage = (pg - 1) / 3 * 3 + 1;
		int endPage = startPage + 2;
		if (totalP < endPage)
			endPage = totalP;

		String rt = null;
		int total = list.size(); // 조회된 데이터 수
		// 조회된 데이터 수로 성공/실패 판단
		if (total > 0) {
			rt = "OK";
		} else {
			rt = "FAIL";
		}
		// json 객체 생성
		JSONObject json = new JSONObject();
		json.put("rt", rt);
		json.put("total", total);

		if (total > 0) {
			JSONArray item = new JSONArray();
			for (int i = 0; i < list.size(); i++) {
				UsersDTO usersDTO = list.get(i);
				JSONObject temp = new JSONObject();
				temp.put("user_code", usersDTO.getUser_code());
				temp.put("user_name", usersDTO.getUser_name());
				temp.put("user_photo", usersDTO.getUser_photo());
				temp.put("user_area", usersDTO.getUser_area());
				temp.put("user_tel", usersDTO.getUser_tel());
				temp.put("user_email1", usersDTO.getUser_email1());
				temp.put("user_email2", usersDTO.getUser_email2());
				temp.put("manner", usersDTO.getManner());
				temp.put("reply_percent", usersDTO.getReply_percent());
				temp.put("sales_count", usersDTO.getSales_count());
				temp.put("purchase_count", usersDTO.getPurchase_count());
				temp.put("interest_count", usersDTO.getInterest_count());
				temp.put("lat", usersDTO.getLat());
				temp.put("lng", usersDTO.getLng());
				temp.put("join_date", usersDTO.getJoin_date());

				item.add(i, temp);
				json.put("item", item);
			}
		}
		System.out.println(json);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("list", list);
		modelAndView.addObject("pg", pg);
		modelAndView.addObject("startPage", startPage);
		modelAndView.addObject("endPage", endPage);
		modelAndView.addObject("totalP", totalP);
		modelAndView.addObject("json", json);

		modelAndView.setViewName("/users/delete.jsp");
		return modelAndView;
	}

	@RequestMapping(value = "/users/list.do", method = RequestMethod.POST)
	public ModelAndView list(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		// DB
		UsersDAO dao = new UsersDAO();
		List<UsersDTO> list = null;
		
		String rt = null;
		try {
			list = service.list();
		} catch(Exception e) {
			rt = "Fail";
			e.printStackTrace();
		}

		// JSON
		
		int total = list.size(); // 조회된 데이터 수
		// 조회된 데이터 수로 성공/실패 판단
		if (total > 0) {
			rt = "OK";
		} else {
			rt = "Fail";
		}
		// json 객체 생성
		JSONObject json = new JSONObject();
		json.put("rt", rt);
		json.put("total", total);
		// json 배열 만들기
		if (total > 0) {
			JSONArray item = new JSONArray();

			for (int i = 0; i < list.size(); i++) {
				UsersDTO usersDTO = list.get(i);
				JSONObject temp = new JSONObject();

				temp.put("user_code", usersDTO.getUser_code());
				temp.put("user_name", usersDTO.getUser_name());
				temp.put("user_photo", usersDTO.getUser_photo());
				temp.put("user_area", usersDTO.getUser_area());
				temp.put("user_tel", usersDTO.getUser_tel());
				temp.put("user_email1", usersDTO.getUser_email1());
				temp.put("user_email2", usersDTO.getUser_email2());
				temp.put("manner", usersDTO.getManner());
				temp.put("reply_percent", usersDTO.getReply_percent());
				temp.put("sales_count", usersDTO.getSales_count());
				temp.put("purchase_count", usersDTO.getPurchase_count());
				temp.put("interest_count", usersDTO.getInterest_count());
				temp.put("lat", usersDTO.getLat());
				temp.put("lng", usersDTO.getLng());
				temp.put("join_date", usersDTO.getJoin_date());
				item.add(i, temp);

			}
			// json 객체에 배열 추가
			json.put("item", item);
		}

		System.out.println(json);
		modelAndView.setViewName("/users/delete.jsp");
		// modelAndView.addObject("su",su);
		modelAndView.addObject("json", json);
		// System.out.println("su = " + su);

		return modelAndView;
	}

	@RequestMapping(value = "/users/getTotalMember.do", method = RequestMethod.POST)
	public ModelAndView getTotalMember(HttpServletRequest request) {

		
		int su = 0;
		String rt = "";
		try {
			su = service.getTotalMember();
		} catch(Exception e) {
			rt = "Fail";
			e.printStackTrace();
		}
		if (su > 0) {
			rt = "OK";
		} else {
			rt = "Fail";
		}
		JSONObject json = new JSONObject();
		json.put("rt", rt);
		JSONArray item = new JSONArray();
		JSONObject temp = new JSONObject();
		temp.put("count", su);
		item.add(0, temp);
		json.put("item", item);
		System.out.println(json);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/users/delete.jsp");
		// modelAndView.addObject("su", su);
		modelAndView.addObject("json", json);

		// System.out.println("su = " + su);

		return modelAndView;
	}

	@RequestMapping(value = "/users/selectOne.do", method = RequestMethod.POST)
	public ModelAndView selectOne(HttpServletRequest request) {
		String str_user_code = request.getParameter("user_code");

		int user_code = 0;
		if (str_user_code != null) {
			if (!str_user_code.trim().equals("") && str_user_code.matches("^[0-9]*$")) {
				user_code = Integer.parseInt(str_user_code);
			}

		}
		System.out.println("user_code = " + user_code);
		UsersDTO usersDTO = null;
		
		String rt = "";
		try {
			usersDTO = service.selectOne(user_code);
		} catch(Exception e) {
			rt = "Fail";
			e.printStackTrace();
		}
		
		if (usersDTO != null) {
			rt = "OK";
		} else {
			rt = "Fail";
		}
		JSONObject json = new JSONObject();
		json.put("rt", rt);
		JSONArray item = new JSONArray();
		JSONObject temp = new JSONObject();
		temp.put("user_code", usersDTO.getUser_code());
		temp.put("user_name", usersDTO.getUser_name());
		temp.put("user_photo", usersDTO.getUser_photo());
		temp.put("user_area", usersDTO.getUser_area());
		temp.put("user_tel", usersDTO.getUser_tel());
		temp.put("user_email1", usersDTO.getUser_email1());
		temp.put("user_email2", usersDTO.getUser_email2());
		temp.put("manner", usersDTO.getManner());
		temp.put("reply_percent", usersDTO.getReply_percent());
		temp.put("sales_count", usersDTO.getSales_count());
		temp.put("purchase_count", usersDTO.getPurchase_count());
		temp.put("interest_count", usersDTO.getInterest_count());
		temp.put("lat", usersDTO.getLat());
		temp.put("lng", usersDTO.getLng());
		temp.put("join_date", usersDTO.getJoin_date());
		item.add(0, temp);
		json.put("item", item);
		System.out.println(json);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/users/delete.jsp");
		// modelAndView.addObject("su", su);
		modelAndView.addObject("json", json);

		// System.out.println("su = " + su);

		return modelAndView;
	}

	@RequestMapping(value = "/users/isExistId.do", method = RequestMethod.POST)
	public ModelAndView isExistId(HttpServletRequest request) {
		String str_user_code = request.getParameter("user_code");

		int user_code = 0;
		if (str_user_code != null) {
			if (!str_user_code.trim().equals("") && str_user_code.matches("^[0-9]*$")) {
				user_code = Integer.parseInt(str_user_code);
			}
		}

		String user_code1 = null;
		
		String rt = "";
		try {
			user_code1 = service.isExistId(user_code);
		} catch(Exception e) {
			rt = "Fail";
			e.printStackTrace();
		}
		
		if (user_code1 != null) {
			rt = "OK";
		} else {
			rt = "Fail";
		}
		JSONObject json = new JSONObject();
		json.put("rt", rt);
		JSONArray item = new JSONArray();
		JSONObject temp = new JSONObject();
		temp.put("user_code", user_code1);
		item.add(0, temp);
		json.put("item", item);
		System.out.println(json);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/users/delete.jsp");
		// modelAndView.addObject("su", su);
		modelAndView.addObject("json", json);

		// System.out.println("su = " + su);

		return modelAndView;
	}

	@RequestMapping(value = "/users/delete.do", method = RequestMethod.POST)
	public ModelAndView delete(HttpServletRequest request) {
		String str_user_code = request.getParameter("user_code");

		int user_code = 0;
		if (str_user_code != null) {
			if (!str_user_code.trim().equals("") && str_user_code.matches("^[0-9]*$")) {
				user_code = Integer.parseInt(str_user_code);
			}
		}

		int su = 0;
		String rt = "";
		try {
			su = service.delete(user_code);
		} catch(Exception e) {
			rt = "Fail";
			e.printStackTrace();
		}
		
		if (su > 0) {
			rt = "OK";
		} else {
			rt = "Fail";
		}
		JSONObject json = new JSONObject();
		json.put("rt", rt);
		System.out.println(json);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/users/delete.jsp");
		modelAndView.addObject("su", su);
		modelAndView.addObject("json", json);

		System.out.println("su = " + su);

		return modelAndView;
	}

	@RequestMapping(value = "/users/modify.do", method = RequestMethod.POST)
	public ModelAndView modify(MultipartFile img, HttpServletRequest request) {
		// 데이터 처리
		// post 방식에서의 한글 처리
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String filePath = "C:\\storage";
		System.out.println("img = " + img);
		String user_photo = "a.jpg";
		if (img != null && !img.equals("")) {
			user_photo = img.getOriginalFilename();
		}
		File file = new File(filePath, user_photo);
		System.out.println("img = " + img);
		if (user_photo != null && !user_photo.trim().equals("")) {
			user_photo = System.currentTimeMillis() + "_" + user_photo;
			file = new File(filePath, user_photo);
		}

		String user_name = request.getParameter("user_name");
		if (user_name == null || user_name.trim().equals("")) {
			user_name = "null";
		}
		// String user_photo = request.getParameter("user_photo");
		String user_area = request.getParameter("user_area");
		if (user_area == null || user_area.trim().equals("")) {
			user_area = "null";
		}
		String user_tel = request.getParameter("user_tel");
		if (user_tel == null || user_tel.trim().equals("")) {
			user_tel = "null";
		}
		String user_email1 = request.getParameter("user_email1");
		if (user_email1 == null || user_email1.trim().equals("")) {
			user_email1 = "null";
		}
		String user_email2 = request.getParameter("user_email2");
		if (user_email2 == null || user_email2.trim().equals("")) {
			user_email2 = "null";
		}

		String str_user_code = request.getParameter("user_code");
		int user_code = 0;
		if (str_user_code != null) {
			if (!str_user_code.trim().equals("") && str_user_code.matches("^[0-9]*$")) {
				user_code = Integer.parseInt(str_user_code);
			}

		}

		String str_lat = request.getParameter("lat");
		double lat = 999;
		if (str_lat != null) {
			if (!str_lat.trim().equals("") && str_lat.matches("^[+-]?\\d*(\\.?\\d*)$")) {
				lat = Double.parseDouble(str_lat);
			}

		}

		String str_lng = request.getParameter("lng");
		double lng = 999;
		if (str_lng != null) {
			if (!str_lng.trim().equals("") && str_lng.matches("^[+-]?\\d*(\\.?\\d*)$")) {
				lng = Double.parseDouble(str_lng);
			}

		}

		String str_manner = request.getParameter("manner");
		int manner = 0;
		if (str_manner != null) {
			if (!str_manner.trim().equals("") && str_manner.matches("^[0-9]*$")) {
				manner = Integer.parseInt(str_manner);
			}

		}
		// System.out.println("manner = " + manner);
		// int reply_percent = Integer.parseInt(request.getParameter("reply_percent"));
		String str_reply_percent = request.getParameter("reply_percent");
		int reply_percent = 0;
		if (str_reply_percent != null) {
			if (!str_reply_percent.trim().equals("") && str_reply_percent.matches("^[0-9]*$")) {
				reply_percent = Integer.parseInt(str_reply_percent);
			}

		}
		// System.out.println("reply_percent = " + reply_percent);
		// int sales_count = Integer.parseInt(request.getParameter("sales_count"));
		String str_sales_count = request.getParameter("sales_count");
		int sales_count = 0;
		if (str_sales_count != null) {
			if (!str_sales_count.trim().equals("") && str_sales_count.matches("^[0-9]*$")) {
				sales_count = Integer.parseInt(str_sales_count);
			}

		}
		// System.out.println("sales_count = " + sales_count);
		// int purchase_count =
		// Integer.parseInt(request.getParameter("purchase_count"));
		String str_purchase_count = request.getParameter("purchase_count");
		int purchase_count = 0;
		if (str_purchase_count != null) {
			if (!str_purchase_count.trim().equals("") && str_purchase_count.matches("^[0-9]*$")) {
				purchase_count = Integer.parseInt(str_purchase_count);
			}

		}
		// System.out.println("purchase_count = " + purchase_count);
		// int interest_count =
		// Integer.parseInt(request.getParameter("interest_count"));
		String str_interest_count = request.getParameter("interest_count");
		int interest_count = 0;
		if (str_interest_count != null) {
			if (!str_interest_count.trim().equals("") && str_interest_count.matches("^[0-9]*$")) {
				interest_count = Integer.parseInt(str_interest_count);
			}

		}
		// System.out.println("interest_count = " + interest_count);

		if (!user_photo.trim().equals("") && user_photo != null) {
			if (img != null) {
				try {
					// img.getInputStream() : 업로드한 파일 데이터를 읽어오는 InputStream을 구한다.
					FileCopyUtils.copy(img.getInputStream(), new FileOutputStream(file));
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		// DB 처리
		UsersDTO usersDTO = new UsersDTO();
		usersDTO.setUser_code(user_code);
		usersDTO.setInterest_count(interest_count);
		usersDTO.setManner(manner);
		usersDTO.setPurchase_count(purchase_count);
		usersDTO.setReply_percent(reply_percent);
		usersDTO.setSales_count(sales_count);
		usersDTO.setUser_area(user_area);
		usersDTO.setUser_name(user_name);
		usersDTO.setUser_photo(user_photo);
		usersDTO.setUser_tel(user_tel);
		usersDTO.setUser_email1(user_email1);
		usersDTO.setUser_email2(user_email2);

		usersDTO.setLat(lat);
		usersDTO.setLng(lng);

		System.out.println("name = " + usersDTO.getUser_name());
		/*
		 * System.out.println("lng = " + usersDTO.getLng()); System.out.println("lat = "
		 * + usersDTO.getLat());
		 */
		int su = 0;
		String rt = "";
		try {
			su = service.modify(usersDTO);
		} catch(Exception e) {
			rt = "Fail";
			e.printStackTrace();
		}
		if (su > 0) {
			rt = "OK";
		} else {
			rt = "Fail";
		}
		JSONObject json = new JSONObject();
		json.put("rt", rt);
		System.out.println(json);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/users/modify.jsp");
		modelAndView.addObject("su", su);
		modelAndView.addObject("json", json);
		modelAndView.addObject("usersDTO", usersDTO);
		System.out.println("su = " + su);

		return modelAndView;
	}

	@RequestMapping(value = "/users/write.do", method = RequestMethod.POST)
	public ModelAndView write(MultipartFile img, HttpServletRequest request) {
		// 데이터 처리
		// post 방식에서의 한글 처리
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String filePath = "C:\\storage";
		System.out.println("img = " + img);
		String user_photo = "a.jpg";
		if (img != null) {
			user_photo = img.getOriginalFilename();
		}
		File file = new File(filePath, user_photo);
		System.out.println("img = " + img);
		if (user_photo != null && !user_photo.trim().equals("")) {
			user_photo = System.currentTimeMillis() + "_" + user_photo;
			file = new File(filePath, user_photo);
		}
		String str_lat = request.getParameter("lat");
		double lat = 999;
		if (str_lat != null) {
			if (!str_lat.trim().equals("") && str_lat.matches("^[+-]?\\d*(\\.?\\d*)$")) {
				lat = Double.parseDouble(str_lat);
			}
			
		}

		String str_lng = request.getParameter("lng");
		double lng = 999;
		if (str_lng != null) {
			if (!str_lng.trim().equals("") && str_lng.matches("^[+-]?\\d*(\\.?\\d*)$")) {
				lng = Double.parseDouble(str_lng);
			}
		}
		String user_name = request.getParameter("user_name");
		if (user_name == null || user_name.trim().equals("")) {
			user_name = "null";
		}
		// String user_photo = request.getParameter("user_photo");
		String user_area = request.getParameter("user_area");
		if (user_area == null || user_area.trim().equals("")) {
			user_area = "null";
		}
		String user_tel = request.getParameter("user_tel");
		if (user_tel == null || user_tel.trim().equals("")) {
			user_tel = "null";
		}
		String user_email1 = request.getParameter("user_email1");
		if (user_email1 == null || user_email1.trim().equals("")) {
			user_email1 = "null";
		}
		String user_email2 = request.getParameter("user_email2");
		if (user_email2 == null || user_email2.trim().equals("")) {
			user_email2 = "null";
		}
		String str_manner = request.getParameter("manner");
		int manner = 0;
		if (str_manner != null || str_manner.trim().equals("")) {
			if (!str_manner.trim().equals("") && str_manner.matches("^[0-9]*$")) {
				manner = Integer.parseInt(str_manner);
			}
			
		}
		// System.out.println("manner = " + manner);
		// int reply_percent = Integer.parseInt(request.getParameter("reply_percent"));
		String str_reply_percent = request.getParameter("reply_percent");
		int reply_percent = 0;
		if (str_reply_percent != null) {
			if (!str_reply_percent.trim().equals("") && str_reply_percent.matches("^[0-9]*$")) {
				reply_percent = Integer.parseInt(str_reply_percent);
			}
			
		}
		// System.out.println("reply_percent = " + reply_percent);
		// int sales_count = Integer.parseInt(request.getParameter("sales_count"));
		String str_sales_count = request.getParameter("sales_count");
		int sales_count = 0;
		if (str_sales_count != null) {
			if (!str_sales_count.trim().equals("") && str_sales_count.matches("^[0-9]*$")) {
				sales_count = Integer.parseInt(str_sales_count);
			}
		}
		// System.out.println("sales_count = " + sales_count);
		// int purchase_count =
		// Integer.parseInt(request.getParameter("purchase_count"));
		String str_purchase_count = request.getParameter("purchase_count");
		int purchase_count = 0;
		if (str_purchase_count != null) {
			if (!str_purchase_count.trim().equals("") && str_purchase_count.matches("^[0-9]*$")) {
				purchase_count = Integer.parseInt(str_purchase_count);
			}
			
		}
		// System.out.println("purchase_count = " + purchase_count);
		// int interest_count =
		// Integer.parseInt(request.getParameter("interest_count"));
		String str_interest_count = request.getParameter("interest_count");
		int interest_count = 0;
		if (str_interest_count != null) {
			if (!str_interest_count.trim().equals("") && str_interest_count.matches("^[0-9]*$")) {
				interest_count = Integer.parseInt(str_interest_count);
			}
			
		}
		// System.out.println("interest_count = " + interest_count);

		if (img != null) {
			try {
				// img.getInputStream() : 업로드한 파일 데이터를 읽어오는 InputStream을 구한다.
				FileCopyUtils.copy(img.getInputStream(), new FileOutputStream(file));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// DB 처리
		UsersDTO usersDTO = new UsersDTO();
		usersDTO.setInterest_count(interest_count);
		usersDTO.setManner(manner);
		usersDTO.setPurchase_count(purchase_count);
		usersDTO.setReply_percent(reply_percent);
		usersDTO.setSales_count(sales_count);
		usersDTO.setUser_area(user_area);
		usersDTO.setUser_name(user_name);
		usersDTO.setUser_photo(user_photo);
		usersDTO.setUser_tel(user_tel);
		usersDTO.setUser_email1(user_email1);
		usersDTO.setUser_email2(user_email2);
		usersDTO.setLat(lat);
		usersDTO.setLng(lng);
		System.out.println("User_tel = " + usersDTO.getUser_tel());
		System.out.println("user_email2 = " + usersDTO.getUser_email2());
		System.out.println("user_email1 = " + usersDTO.getUser_email1());

		int su = 0;
		String rt = "";
		try {
			su = service.write(usersDTO);
		} catch(Exception e) {
			rt = "Fail";
			e.printStackTrace();
		}
		
		System.out.println("su = " + su);
		
		if (su > 0) {
			rt = "OK";
		} else {
			rt = "Fail";
		}
		JSONObject json = new JSONObject();
		json.put("rt", rt);
		System.out.println(json);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("/users/write.jsp");
		modelAndView.addObject("su", su);
		modelAndView.addObject("json", json);
		// modelAndView.addObject("usersDTO", usersDTO);
		System.out.println("su = " + su);

		return modelAndView;
	}

	@RequestMapping(value = "/users/login.do", method = RequestMethod.POST)
	public ModelAndView login(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		String str_user_code = request.getParameter("user_code");
		int user_code = 0;
		if (str_user_code != null) {
			user_code = Integer.parseInt(str_user_code);
		}
		String user_name = service.login(user_code);

		if (user_name != null && user_name.trim().equals("")) {
			HttpSession session = request.getSession();
			session.setAttribute("session_name", user_name);
			session.setAttribute("session_code", user_code);
			modelAndView.setViewName("../users/loginOk.do");
			JSONObject json = new JSONObject();
			json.put("user_name", user_name);
			json.put("user_code", user_code);
			System.out.println(json);
			modelAndView.addObject("json", json);
			modelAndView.addObject("user_name", user_name);
			modelAndView.addObject("user_code", user_code);
			// System.out.println("su = " + su);
		} else {

			// response.sendRedirect("loginFail.jsp");
			modelAndView.setViewName("../users/loginFail.do");

		}
		return modelAndView;
	}

	@RequestMapping(value = "/users/loginOk.do")
	public ModelAndView loginOk(HttpServletRequest request) {
		// 데이터 처리
		try {
			request.setCharacterEncoding("utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String name = request.getParameter("name");
		// String id = request.getParameter("id");

		String name = "";
		String id = "";
		/* 데이터 공유 1 : 쿠키 이용 */
		/*
		 * Cookie[] cookies = request.getCookies(); if(cookies != null) { for(int i=0;
		 * i<cookies.length; i++) { if(cookies[i].getName().equals("memName")) { name =
		 * URLDecoder.decode(cookies[i].getValue(), "UTF-8"); } else
		 * if(cookies[i].getName().equals("memId")) { id = cookies[i].getValue(); } } }
		 */
		/* 데이터 공유 2 : 세션 이용 */
		HttpSession session = request.getSession();
		name = (String) session.getAttribute("session_name");
		id = (String) session.getAttribute("session_code");
		System.out.println("name = " + name);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("../users/loginOk.jsp");
		modelAndView.addObject("name", name);
		modelAndView.addObject("id", id);
		return modelAndView;
	}

	@RequestMapping(value = "/users/logout.do")
	public ModelAndView logout(HttpServletRequest request) {
		// 쿠키 정보 삭제 : 브라우저에게 쿠키정보를 더이상 보내지마라 설정
		/*
		 * Cookie[] cookies = request.getCookies(); if(cookies != null) { for(int i=0;
		 * i<cookies.length; i++) { if(cookies[i].getName().equals("memName")) {
		 * cookies[i].setMaxAge(0); // 쿠키 삭제 요청 response.addCookie(cookies[i]); } else
		 * if(cookies[i].getName().equals("memId")) { cookies[i].setMaxAge(0); // 쿠키 삭제
		 * 요청 response.addCookie(cookies[i]); } } }
		 */
		// 세션 삭제
		HttpSession session = request.getSession();
		session.removeAttribute("session_name");
		session.removeAttribute("session_code");

		session.invalidate(); // 무효화, 전부 삭제
		ModelAndView modelAndView = new ModelAndView();
		// modelAndView.setViewName("../users/logout.jsp");
		return modelAndView;
	}

	@RequestMapping(value = "/users/loginFail.do")
	public ModelAndView loginFail(HttpServletRequest request) {

		ModelAndView modelAndView = new ModelAndView();
		// modelAndView.setViewName("../users/loginFail.jsp");
		return modelAndView;
	}
}
