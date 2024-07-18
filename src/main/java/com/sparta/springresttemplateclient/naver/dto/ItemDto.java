package com.sparta.springresttemplateclient.naver.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Getter
@NoArgsConstructor
public class ItemDto {
  private String title;
  private String link;
  private String image;
  private int lprice;


  // org.json의존성에서 JSON 객체를 다루기 위한 클래스
  public ItemDto(JSONObject itemJson) {
    this.title = itemJson.getString("title");
    this.link = itemJson.getString("link");
    this.image = itemJson.getString("image");
    this.lprice = itemJson.getInt("lprice");
  }
}