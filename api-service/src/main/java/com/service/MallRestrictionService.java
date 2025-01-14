package com.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.logging.Log;
import io.quarkus.security.identity.SecurityIdentity;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.eclipse.microprofile.jwt.JsonWebToken;

@ApplicationScoped
@RequiredArgsConstructor
public class MallRestrictionService {

    private static final String RESTRICTED_MALLS = "restricted_malls";

    private final JsonWebToken token;

    private final SecurityIdentity securityIdentity;

    private final ObjectMapper mapper;

    public List<Integer> getRestrictedMalls() {
        try {
            Object restrictedMalls = token.getClaim(RESTRICTED_MALLS);

            if (restrictedMalls != null) {
                return mapper.readValue(restrictedMalls.toString(), new TypeReference<>() {});
            }
        } catch (JsonProcessingException e) {
            Log.warn("Failed to parse restricted malls");
        }

        return List.of();
    }

    public boolean isRestrictedMall(Integer mallId) {
        return getRestrictedMalls().contains(mallId);
    }

    public String getRequestAuthor() {
        return securityIdentity.getPrincipal().getName();
    }
}
