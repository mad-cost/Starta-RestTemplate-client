package com.sparta.springresttemplateclient.service;

import com.sparta.springresttemplateclient.dto.ItemDto;
import com.sparta.springresttemplateclient.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RestTemplateService {

  // 애플리케이션 내부에서 REST API에 요청하기 위해 RestTemplate의 메서드를 호출.
  private final RestTemplate restTemplate;
  
  // RestTemplate 주입 / RestTemplateBuilder.build()를 사용해서 주입
  public RestTemplateService(
          RestTemplateBuilder builder
  ) {
    this.restTemplate = builder.build();
  }

  // 클라이언트에서 서버로 [Get]방식으로 요청하는 로직
  public ItemDto getCallObject(String query) {
    // 요청할 URL 만들기 / UriComponentsBuilder를 사용하여 URI객체 만들기
    URI uri = UriComponentsBuilder
            .fromUriString("http://localhost:7070") // RestTemplateServer의 포트번호
            .path("/api/server/get-call-obj") // RestTemplateServer의 Controller URL
            // @RequestParam: /api/server/get-call-obj?query=value
            .queryParam("query", query) // queryParam으로 값을 보낸다 (name, value)
            .encode() // URL 인코딩
            .build() // 입력한 정보를 바탕으로 객체 생성
            .toUri(); // 생성한 객체를 URI 객체로 변환
    log.info("uri = " + uri);

    // restTemplate 사용하여 호출하기
    // .getForEntity(URI, 서버로 부터 다시 반환 받을 responseType): RestTemplateServer에 [Get]방식으로 요청을 한다
    // 데이터를 server에서 다시 반환 받으면 responseEntity의 body에 담긴다
    ResponseEntity<ItemDto> responseEntity = restTemplate.getForEntity(uri, ItemDto.class);

    log.info("statusCode = " + responseEntity.getStatusCode()); // 서버에서 날라온 StateCode

    return responseEntity.getBody();
  }


  // json의존성 추가, server에서 데이터를 전부 받아오기
  public List<ItemDto> getCallList() {
    // 요청 URL 만들기
    URI uri = UriComponentsBuilder
            .fromUriString("http://localhost:7070")
            .path("/api/server/get-call-list")
            .encode()
            .build()
            .toUri();
    log.info("uri = " + uri);

    ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
    /* String.class: server에서 중첩 Json형태로 데이터가 들어오기 때문에 dataType을 String으로 받아준다
    "{
      items":[
           {"title":"Mac", "price":3_888_000},
           {"title":"iPad", "price":1_230_000},
           {"title":"iPhone", "price":1_550_000},
           {"title":"Watch", "price":450_000},
           {"title":"AirPods", "price":350_000}
            ]
     }*/

    log.info("statusCode = " + responseEntity.getStatusCode());
    log.info("Body = " + responseEntity.getBody());

    // String.class로 받은 데이터를 변환
    return fromJSONtoItems(responseEntity.getBody());
  }

  // fromJSONtoItems()메서드
  public List<ItemDto> fromJSONtoItems(String responseEntity) {
    // JSONObject: String으로 받아온 items데이터를 변환해준다 / Json의존성 추가
    JSONObject jsonObject = new JSONObject(responseEntity);
    log.info("jsonObject : {}",jsonObject );

    // getJSONArray(): items 내부의 Json형태의 데이터를 JSONArray객체에 담아준다
    JSONArray items  = jsonObject.getJSONArray("items");
    log.info("JSONArray : {}", items);

    List<ItemDto> itemDtoList = new ArrayList<>();
    // items의 데이터 하나씩 뽑아주기
    for (Object item : items) {
      // 뽑아온 데이터를 ItemDto로 변환
      ItemDto itemDto = new ItemDto((JSONObject) item);
      itemDtoList.add(itemDto);
    }
    return itemDtoList;
  }


  public ItemDto postCall(String query) {
    // 요청 URL 만들기
    URI uri = UriComponentsBuilder
            .fromUriString("http://localhost:7070")
            // @PathVariable
            .path("/api/server/post-call/{query}")
            .encode()
            .build()
            // .expand(): /{query}에 들어갈 값
            .expand(query)
            .toUri();
    log.info("uri = " + uri);

    User user = new User("Jun", "1234");
    // postForEntity(URI, Post방식에서 Http Body에 넣어줄 값, server로 부터 반환 받을 responseType)
    ResponseEntity<ItemDto> responseEntity = restTemplate.postForEntity(uri, user, ItemDto.class);

    log.info("statusCode = " + responseEntity.getStatusCode());

    return responseEntity.getBody();
  }

  // exchange()사용
  public List<ItemDto> exchangeCall(String token) {
    // 요청 URL 만들기
    URI uri = UriComponentsBuilder
            .fromUriString("http://localhost:7070")
            .path("/api/server/exchange-call")
            .encode()
            .build()
            .toUri();
    log.info("uri = " + uri);

    // Body에 넣어 보낼 데이터
    User user = new User("Jun", "1234");

    // 위에서 만든 URI를 RequestEntity객체로 만들어서 RequestBody 실어 보낸다
    // Post방식은 RequestBody로 데이터를 보낼 수 있다 / Get방식은 Body가 없다
    RequestEntity<User> requestEntity = RequestEntity
            .post(uri) // body로 보내기 때문에 Post방식 사용
            // 클라이언트에서 {"Key" : "X-Authorization", "Value" : "token"}형태로 토큰을 server로 보낸다
            .header("X-Authorization", token)
            .body(user);

    // exchange(ResponseEntityType, server로 부터 반환 받을 responseType)
    ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

    return fromJSONtoItems(responseEntity.getBody());
  }

}