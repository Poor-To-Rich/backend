package com.poortorich.auth.oauth2.domain.model;

public interface OAuth2Response {

    String getProvider();

    String getProviderId();

    String getProfileImage();

    String getEmail();

    String getName();
}
