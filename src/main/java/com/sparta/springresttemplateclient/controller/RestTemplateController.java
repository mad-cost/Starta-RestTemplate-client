package com.sparta.springresttemplateclient.controller;

import com.sparta.springresttemplateclient.dto.ItemDto;
import com.sparta.springresttemplateclient.service.RestTemplateService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
public class RestTemplateController {

  private final RestTemplateService restTemplateService;

  // RestTemplateService 주입
  public RestTemplateController(RestTemplateService restTemplateService) {
    this.restTemplateService = restTemplateService;
  }


  // server에서 데이터 하나만 받아오기
  // [Get] http://localhost:8080/api/client/get-all-obj?query=Mac
  @GetMapping("/get-call-obj")
  public ItemDto getCallObject(
          // @RequestParam 생략 가능
          String query) {
    return restTemplateService.getCallObject(query);
  }


  // server에서 데이터 전부 가져오기
  // [Get] http://localhost:8080/api/server/get-call-list
  @GetMapping("/get-call-list")
  public List<ItemDto> getCallList() {
    return restTemplateService.getCallList();
  }

  @GetMapping("/post-call")
  public ItemDto postCall(String query) {
    return restTemplateService.postCall(query);
  }

  @GetMapping("/exchange-call")
  public List<ItemDto> exchangeCall(@RequestHeader("Authorization") String token) {
    return restTemplateService.exchangeCall(token);
  }
}