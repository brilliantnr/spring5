package com.gms.web.mbr;

import java.util.function.Function;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.gms.web.cmm.Util;

@RestController
@RequestMapping("/member")

//세션 안쓰려면 el 안쓰면 되고,정보는 서버에 저장시키지 말고, 각각의 유저의 폰에 저장시켜라

/* session은 공유 공간!
 * jsp화면의 user도 세션을 가져다 쓴 것 
 * @을 붙여야 spring의 mvc가 객체로 인식하기 때문에 MemberDTO member 앞에 붙여야함
 * 1. 로그인하면서 addAttribute한 값이 session 공간에 저장된채로 유지된다.
*/
public class MemberCtrl {
	static final Logger logger = LoggerFactory.getLogger(MemberCtrl.class);
	
	@Autowired MemberService memberService;
	@Autowired MemberMapper mbrMapper;
	
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public String add(@ModelAttribute("member") Member member) {
		logger.info("---add() :: {}---","ENTER");
		logger.info("---add() :member: {}---",member);
		memberService.add(member);
		return "join_success";
	}
	
	@PostMapping("/login")
	public String login(Model model, @ModelAttribute Member member) {
		/*//@ModelAttribute MemberDTO member은 receiver.cmd 와 같다. request의 모든값 담겨있음
		//@ModelAttribute는 커맨드 패턴의 carrier */		
		logger.info("---login() :: {}---", "ENTER");
		// 람다식
		/*
		//Predicate<String> p = s -> !s.equals("");
		//string으로 인풋, 파라미터값이 널이 아니면 true ;
		: 이제 !로 not 쓰지 않는다. 대신 negate()
		*/
		/*
		Predicate<String> p = s -> s.equals("");
		Predicate<String> notP = p.negate();
		*/
		String view = "login_failed";
		if (Util.notNull.test(mbrMapper.exist(member.getUserid()))) {
			Function<Member, String> f =mbrMapper::login;
			/*Function<Member, String> f = (t) -> {
				return mbrMapper.login(t);
			};*/
			view = (f.apply(member).equals("1")) ? "login_success" : "login_failed";
		}
		member = (Predicate.isEqual("login_success").test(view))?
				mbrMapper.selectOne(member):
					new Member();
				Util.Log.accept(member.toString());
		return view;
		
		
		/*Predicate<String> ls =s -> s.equals("login_success");*/
		
		
		/*if(Util.notnull.test(view)) {
			member = mbrMapper.selectOne(member);
		}
		System.out.println("member 정보 :"+member);
		return view;
		*/
		/*String path ="";
		if(memberService.login(member)) { //true
			model.addAttribute("user",memberService.retrieve(member));
			path ="session:member/retrieve.tiles";
		}else{
			path ="public:member/login.tiles";
		}
		//request.getSession.setAttribute() 와 같다
		//request.getSession = model
		//return path;
		 * */
	}
	
	@RequestMapping("/retrieve")
	public void retrieve(Model model, @ModelAttribute Member member) {
		logger.info("---retrieve() :: {}---", "ENTER");
		model.addAttribute("user",memberService.retrieve(member));
	}
	
	@RequestMapping("/modify")
		public String modify(@ModelAttribute("user") Member user
				,@ModelAttribute("member") Member member) {
		/* @ModelAttribute("user") MemberDTO s 에는 로그인할때 받은 MemberDTO의 property들이 담겨있다
		 * @ModelAttribute("member") MemberDTO member 에는 modify메소드 부르기 바로전에 수행했던 정보수정페이지에서 입력한 값이 담겨있다
		*/
		logger.info("---modify() :: {}---", "ENTER");
		memberService.modify(user);
		return "redirect:/";
		
	}

	
	@RequestMapping("/list")
	public void list() {}
	@RequestMapping("/search")
	public void search() {
		
	}
	
	@RequestMapping("/count")
	public void count() {}
	
	@RequestMapping("/remove")
	public String remove(@ModelAttribute("user") Member user ) {
		logger.info("---remove() :: {}---", "ENTER");
		memberService.remove(user);
		return "redirect:/";
	}
	
	@RequestMapping("/logout")
	public String logout() {
		logger.info("---logout() :: {}---","ENTER");
		return "redirect:/";
	}
	@RequestMapping("/fileupload")
	public void fileupload() {}
	
}
