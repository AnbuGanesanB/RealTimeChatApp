package com.example.RealTimeChatApplication.mapper;

import com.example.RealTimeChatApplication.model.files.OutFileDto;
import com.example.RealTimeChatApplication.model.files.SharedFile;
import org.springframework.stereotype.Component;

@Component
public class OutFileMapper {

    public OutFileDto getFileDetails(SharedFile file){
        OutFileDto outFileDto = new OutFileDto();
        outFileDto.setOriginalFileName(file.getOriginalFileName());
        outFileDto.setUniqueFileName(file.getUniqueFileName());
        return outFileDto;
    }
}
