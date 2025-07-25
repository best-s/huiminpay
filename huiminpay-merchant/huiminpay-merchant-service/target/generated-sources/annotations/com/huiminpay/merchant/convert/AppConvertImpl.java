package com.huiminpay.merchant.convert;

import com.huiminpay.merchant.dto.AppDTO;
import com.huiminpay.merchant.entity.App;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-12-26T20:27:17+0800",
    comments = "version: 1.3.0.Final, compiler: javac, environment: Java 1.8.0_131 (Oracle Corporation)"
)
public class AppConvertImpl implements AppConvert {

    @Override
    public App dto2entity(AppDTO dto) {
        if ( dto == null ) {
            return null;
        }

        App app = new App();

        app.setId( dto.getId() );
        app.setAppId( dto.getAppId() );
        app.setAppName( dto.getAppName() );
        app.setMerchantId( dto.getMerchantId() );
        app.setPublicKey( dto.getPublicKey() );
        app.setNotifyUrl( dto.getNotifyUrl() );

        return app;
    }

    @Override
    public AppDTO entity2dto(App entity) {
        if ( entity == null ) {
            return null;
        }

        AppDTO appDTO = new AppDTO();

        appDTO.setId( entity.getId() );
        appDTO.setAppId( entity.getAppId() );
        appDTO.setAppName( entity.getAppName() );
        appDTO.setMerchantId( entity.getMerchantId() );
        appDTO.setPublicKey( entity.getPublicKey() );
        appDTO.setNotifyUrl( entity.getNotifyUrl() );

        return appDTO;
    }

    @Override
    public List<App> dtoList2entityList(List<AppDTO> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<App> list = new ArrayList<App>( dtoList.size() );
        for ( AppDTO appDTO : dtoList ) {
            list.add( dto2entity( appDTO ) );
        }

        return list;
    }

    @Override
    public List<AppDTO> entityList2dtoList(List<App> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<AppDTO> list = new ArrayList<AppDTO>( entityList.size() );
        for ( App app : entityList ) {
            list.add( entity2dto( app ) );
        }

        return list;
    }
}
