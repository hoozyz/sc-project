package com.hoozy.study.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.hoozy.study.entity.Know;
import com.hoozy.study.entity.User;
import com.hoozy.study.interfaces.KnowMapping;
import com.hoozy.study.service.KnowService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class KnowController {
	
	private final KnowService knowService;
	
	@GetMapping("/know")
	public String know(Model model, @SessionAttribute(name = "loginUser", required = false) User loginUser) {
		model.addAttribute("user", new User());
		
		if(loginUser != null) {
			model.addAttribute("user", loginUser); // 로그인 했을 경우 user 변경
		}
		
		Map<String, List<KnowMapping>> map = new HashMap<>();
		
		map = knowService.findAllByCate();
		
		model.addAttribute("map", map);
		
		return "know";
	}
	
	@PostMapping("/know/cont")
	@ResponseBody
	public Know knowCont(String name) {
		Know know = new Know();
		 
		know = knowService.findByName(name);
		
		return know;
	}
	
	@PostMapping("/know/like")
	@ResponseBody
	public void like(String name, int check, @SessionAttribute(name = "loginUser", required = false) User loginUser) {
		Know know = new Know();
		System.out.println(name);
		know = knowService.findByName(name);
		if(check == 1) { // 좋아요 추가
			know.setLikes(know.getLikes() + 1);
			if(know.getLikenick() == null) { // 첫 유저
				know.setLikenick(loginUser.getNick()); // 현재 로그인 한 유저 좋아요 닉네임 목록에 추가
			} else {
				know.setLikenick(know.getLikenick() + " " + loginUser.getNick()); // 현재 로그인 한 유저 좋아요 닉네임 목록에 추가
			}
		} else { // 좋아요 제거
			know.setLikes(know.getLikes() - 1);
			String userStr = ""; // 유저 닉네임 목록 문자열 생성
			for(String user : know.getLikenick().split(" ")) { // user 목록 가져오고 현재 로그인 한 닉네임 제거
				 if(user.equals(loginUser.getNick())) {
					 continue; // 추가 안함.
				 }
				 userStr += user;
			}
			know.setLikenick(userStr); 
		}
		knowService.save(know);
	}
}
