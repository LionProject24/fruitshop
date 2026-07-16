package org.example.frusitshopapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {

    private String name;
    private Long price;
    private Long stock;
    private String description;
    private String imageUrl;

}
