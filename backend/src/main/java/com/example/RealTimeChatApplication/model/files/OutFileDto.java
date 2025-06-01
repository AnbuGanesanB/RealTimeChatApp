package com.example.RealTimeChatApplication.model.files;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OutFileDto {

    private String originalFileName;
    private String uniqueFileName;
}
