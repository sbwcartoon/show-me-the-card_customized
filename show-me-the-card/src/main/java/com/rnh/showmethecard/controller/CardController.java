package com.rnh.showmethecard.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.gson.Gson;
import com.rnh.showmethecard.common.Literal;
import com.rnh.showmethecard.model.dao.CardDao;
import com.rnh.showmethecard.model.dto.CardBasicInfo;
import com.rnh.showmethecard.model.dto.CardForInsert;
import com.rnh.showmethecard.model.dto.Member;
import com.rnh.showmethecard.model.dto.MyCardList;
import com.rnh.showmethecard.model.service.CardService;
import com.rnh.showmethecard.webscraping.HtmlParser;

@Controller
@RequestMapping(value = "/mypage")
public class CardController {
	
//	@Autowired
//	@Qualifier("cardDao")
//	private CardDao dao;
//	public void setDao(CardDao dao) {
//		this.dao = dao;
//	}
	@Autowired
	@Qualifier("cardService")
	private CardService cardService;
	
	private int cardNo;
	
	Member member;
	String mId;
	int afNo;
	
	Gson gson = new Gson();
	
	
	

	@RequestMapping(value="cardregister.action", method=RequestMethod.GET)
	public String cardRegisterForm(Model model, String fNo, HttpServletResponse response) {
		if (fNo == null) {
			return "card/cardregisterform";
		} else {
			afNo = Integer.parseInt(fNo);
			model.addAttribute("getFNo", fNo);
			
			return "card/cardregisterform";
		}
		
	}
	
	@RequestMapping(value="card.action", method=RequestMethod.GET)
	public String card() {
		return "card/card";
	}
	@RequestMapping(value="checkurl.action", method = RequestMethod.GET,  produces = "application/json;charset=utf-8")
	public String checkandshowcard(Model model, HttpSession session, String url) {
		HtmlParser h = new HtmlParser(url, Literal.ParseHtml.From.WEB);
		
		if (h.isUrlOk()) {
			cardNo = cardService.checkCardDb(h.getUrl());
			model.addAttribute("url", h.getUrl());
			model.addAttribute("title", h.getTitle());
			model.addAttribute("desc", h.getDesc());
			model.addAttribute("img", h.getImg());
			model.addAttribute("cardNo", cardNo);
			model.addAttribute("check", "fine");
			return "card/card";
		} else{
			model.addAttribute("check", "bad");
			System.out.println("잘못된주소");
		return "틀렸어!";
		}	
	}
	
	@RequestMapping(value="showmycardlist.action", method=RequestMethod.GET)
	public String ShowMYCardList(HttpSession session, HttpServletRequest req, int fNo){
		member = (Member) session.getAttribute("loginuser");
		mId = member.getmId();
		System.out.println("b"+fNo);
		List<MyCardList> myCardListList= cardService.readMyCard(fNo);
		
		int listLength = myCardListList.size();
		
//		for(int i=0;i<listLength;i++){
//			String tmp = myCardListList.get(i).getUrl();
//			HtmlParser h = new HtmlParser(tmp, Literal.ParseHtml.From.DB);
//			myCardListList.get(i).setDesc(h.getDesc());
//			myCardListList.get(i).setTitle(h.getTitle());
//		}
		
		Collections.reverse(myCardListList);
		req.setAttribute("mycardListList", myCardListList);
		return "card/mycardlist";
	}
	
	@RequestMapping(value="cardregisterfin.action", method=RequestMethod.POST)
	@ResponseBody
	public String cardRegisterfinal(HttpSession session,  @RequestBody String stringJson) {
		member = (Member) session.getAttribute("loginuser");
		mId = member.getmId();
		System.out.println(stringJson);
		CardForInsert cardForInsert;
		cardForInsert = gson.fromJson(stringJson, CardForInsert.class);
		HtmlParser h = new HtmlParser(cardForInsert.getSiteUrl(), Literal.ParseHtml.From.WEB);
		cardForInsert.setSiteUrl(h.getUrl());
		cardForInsert.setDiscoverer(mId);
		cardForInsert.setImgSrc(h.getImg());
		
		cardService.insertMyCardOrCardDb(cardForInsert);
		return "입력 성공";
	}
		
}









