package com.sparta.springresttemplateclient.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.json.JSONObject;

@Getter
@NoArgsConstructor
public class ItemDto {
  private String title;
  private int price;

  public ItemDto(JSONObject itemJson) {
    //  {"title":"Mac", "price":3_888_000}, {}...
    this.title = itemJson.getString("title");
    this.price = itemJson.getInt("price");
  }
}