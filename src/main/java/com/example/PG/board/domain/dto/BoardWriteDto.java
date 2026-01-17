package com.example.PG.board.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class BoardWriteDto {
    
    @NotBlank(message = "제목은 필수입니다.") // 공백, null 모두 허용 안 함
    @Size(max = 100, message = "제목은 100자 이내로 작성해주세요.")
    // HTML 폼의 <input name="title">과 이름이 같아야 자동으로 값이 담깁니다.
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    // HTML 폼의 <textarea name="content">와 이름이 같아야 합니다.
    private String content;
}
