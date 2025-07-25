package com.huiminpay.user.convert;

import com.huiminpay.user.api.dto.authorization.PrivilegeDTO;
import com.huiminpay.user.entity.AuthorizationPrivilege;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-26T20:27:55+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_131 (Oracle Corporation)"
)
public class AuthorizationPrivilegeConvertImpl implements AuthorizationPrivilegeConvert {

    @Override
    public PrivilegeDTO entity2dto(AuthorizationPrivilege entity) {
        if ( entity == null ) {
            return null;
        }

        PrivilegeDTO privilegeDTO = new PrivilegeDTO();

        privilegeDTO.setId( entity.getId() );
        privilegeDTO.setName( entity.getName() );
        privilegeDTO.setCode( entity.getCode() );
        privilegeDTO.setPrivilegeGroupId( entity.getPrivilegeGroupId() );

        return privilegeDTO;
    }

    @Override
    public AuthorizationPrivilege dto2entity(PrivilegeDTO dto) {
        if ( dto == null ) {
            return null;
        }

        AuthorizationPrivilege authorizationPrivilege = new AuthorizationPrivilege();

        authorizationPrivilege.setId( dto.getId() );
        authorizationPrivilege.setName( dto.getName() );
        authorizationPrivilege.setCode( dto.getCode() );
        authorizationPrivilege.setPrivilegeGroupId( dto.getPrivilegeGroupId() );

        return authorizationPrivilege;
    }

    @Override
    public List<PrivilegeDTO> entitylist2dto(List<AuthorizationPrivilege> authorizationRole) {
        if ( authorizationRole == null ) {
            return null;
        }

        List<PrivilegeDTO> list = new ArrayList<PrivilegeDTO>( authorizationRole.size() );
        for ( AuthorizationPrivilege authorizationPrivilege : authorizationRole ) {
            list.add( entity2dto( authorizationPrivilege ) );
        }

        return list;
    }
}
