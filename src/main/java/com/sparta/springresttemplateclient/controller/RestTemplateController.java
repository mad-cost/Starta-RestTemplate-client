package com.sparta.springresttemplateclient.controller;

import com.sparta.springresttemplateclient.dto.ItemDto;
import com.sparta.springresttemplateclient.service.RestTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/client")
public class RestTemplateController {

  private final RestTemplateService restTemplateService;

  // RestTemplateService 주입
  public RestTemplateController(RestTemplateService restTemplateService) {
    this.restTemplateService = restTemplateService;
  }

  // [ @RequestParam ] 방식
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


  // [ @PathVariable ] 방식
  @GetMapping("/post-call")
  public ItemDto postCall(
          /* /post-call?query=Mac으로 client에서는 시작하지만,
             postCall()에서 server로 보낼때는 @PathVariable방식으로 보낸다 */
          // @RequestParam 생략 가능
          String query) {
    return restTemplateService.postCall(query);
  }


  @GetMapping("/exchange-call")
  public List<ItemDto> exchangeCall(
          // 클라이언트에서 {"Key" : "Authorization", "Value" : "token"}형태로 토큰을 받는다
          // 받은 토큰을 service로직을 통해 다시 서버로 보내줄 예정
          @RequestHeader("Authorization")
          String token
  ) {
    return restTemplateService.exchangeCall(token);
  }

}