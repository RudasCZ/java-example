package com.homework.morosystems.mapper;

import com.homework.morosystems.exception.ApplicationException;
import com.homework.morosystems.model.ErrorResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ErrorMapper {
    @Mapping(target = "message", source = "errorMsg")
    @Mapping(target = "status", source = "httpStatus")
    ErrorResponseDto toErrorResponse(ApplicationException exception);
}
