package io.bhex.broker.admin.controller.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PageResultDTO<T> {

    private T list;
    private boolean nextPage;

}
