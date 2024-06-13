package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.ReportCard;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.service.dto.ReportCardDTO;
import com.mycompany.myapp.service.dto.UserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ReportCard} and its DTO {@link ReportCardDTO}.
 */
@Mapper(componentModel = "spring")
public interface ReportCardMapper extends EntityMapper<ReportCardDTO, ReportCard> {
    @Mapping(target = "student", source = "student", qualifiedByName = "userLogin")
    @Mapping(target = "teacher", source = "teacher", qualifiedByName = "userLogin")
    ReportCardDTO toDto(ReportCard s);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);
}
