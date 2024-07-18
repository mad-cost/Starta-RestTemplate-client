package com.sparta.springresttemplateclient.naver.service;

import com.sparta.springresttemplateclient.naver.dto.ItemDto;
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

@Slf4j(topic = "NAVER API")
@Service
public class NaverApiService {

  private final RestTemplate restTemplate;

  // RestTemplate 주입 -> RestTemplateBuilder의 builder.build()사용
  public NaverApiService(RestTemplateBuilder builder) {
    this.restTemplate = builder.build();
  }

  public List<ItemDto> searchItems(String query) {
    // 요청 URL 만들기
    URI uri = UriComponentsBuilder
            .fromUriString("https://openapi.naver.com") // RestTemplateServer의 포트번호
            .path("/v1/search/shop.json") // RestTemplateServer의 Controller URL (NaverApi의 어느 주소로 보내는지)
            .queryParam("display", 15) // 데이터를 15개씩 받아온다
            .queryParam("query", query) // 동적으로 사용
            .encode()
            .build()
            .toUri(); // 위에서 만든 데이터를 URI로
    log.info("uri = " + uri);

    // 위에서 만든 URI  RequestEntity객체로 만들어서 Get방식으로 요청
    // Get은 Body가 없으므로 Void로 선언
    RequestEntity<Void> requestEntity = RequestEntity
            .get(uri)
            .header("X-Naver-Client-Id", "TlgwWYXWL8TqHoar0OrE")
            .header("X-Naver-Client-Secret", "6ZfbtBIm6h")
            .build();

    // .exchange(): RestTemplate에서 클래스 HTTP 요청을 보낼 때 사용
    // .exchange(RequestEntity, ResponseType)
    // display에서 데이터를 15개를 받아온다고 했으므로 String(String.class)으로 받아온다
    ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);

    log.info("NAVER API Status Code : " + responseEntity.getStatusCode());

    return fromJSONtoItems(responseEntity.getBody());
  }

  // String.class로 받아온 items데이터를 ItemDto 객체로 변환하여 리스트로 반환
  public List<ItemDto> fromJSONtoItems(String responseEntity) {
    // responseEntity를 JSONObject로 변환
    JSONObject jsonObject = new JSONObject(responseEntity);
    log.info("@@ JSONObject ; {}", jsonObject);
    // JSON 객체에서 "items" 배열을 추출
    JSONArray items  = jsonObject.getJSONArray("items");
    log.info("@@ JSONArray ; {}", items);
    List<ItemDto> itemDtoList = new ArrayList<>();

    // JSONArray를 순회하면서 각 요소를 ItemDto 객체로 변환하여 itemDtoList에 추가
    for (Object item : items) {
      ItemDto itemDto = new ItemDto((JSONObject) item);
      itemDtoList.add(itemDto);
    }

    return itemDtoList;
  }
}