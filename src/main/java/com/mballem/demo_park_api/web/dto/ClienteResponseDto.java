package com.mballem.demo_park_api.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClienteResponseDto {
    private Long id;
    private String nome;
    private String cpf;
}
