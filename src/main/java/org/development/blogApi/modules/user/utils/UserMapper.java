package org.development.blogApi.modules.user.utils;

import org.development.blogApi.modules.auth.dto.response.ViewMeDto;
import org.development.blogApi.modules.auth.dto.response.ViewUserDto;
import org.development.blogApi.modules.user.entity.UserEntity;

public class UserMapper {
    public static ViewUserDto toView(UserEntity user) {
//        const banInfo = BanMapper.toBanExtendedInfoView(
//                    user.banInfo.isBanned,
//                    user.banInfo.banReason,
//                    user.banInfo.banDate,
//                    );

        return new ViewUserDto(
                user.getId().toString(),
                user.getLogin(),
                user.getEmail(),
                user.getCreatedAt()
                // banInfo,
        );
    }

    public static ViewMeDto toViewMe(UserEntity user){
        return new ViewMeDto(user.getId().toString(), user.getLogin(), user.getEmail());
    }
}
