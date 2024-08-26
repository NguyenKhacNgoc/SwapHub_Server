package com.server.ecommerce.services;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.server.ecommerce.dto.request.RoleCreationRequest;
import com.server.ecommerce.dto.response.RoleResponse;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class RoleService {
    Keycloak keycloak;
    @NonFinal
    @Value("${keycloak.realm}")
    String realm;

    List<String> getDefaultRoles() {
        return Arrays.asList("uma_authorization", "default-roles-" + realm, "offline_access");
    }

    UserResource getUserResource(String userId) {
        return keycloak.realm(realm).users().get(userId);

    }

    public void createRole(RoleCreationRequest request) {
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName(request.getName());
        roleRepresentation.setDescription(request.getDescription());
        keycloak.realm(realm).roles().create(roleRepresentation);

    }

    public void deleteRole(String roleName) {
        keycloak.realm(realm).roles().deleteRole(roleName);
    }

    public void assignRole(String userId, String roleName) {

        UserResource userResource = keycloak.realm(realm).users().get(userId);
        RolesResource rolesResource = keycloak.realm(realm).roles();
        RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(representation));

    }

    public void associateRole(String parentRole, String childRole) {
        var childRoleRepresent = keycloak.realm(realm).roles().get(childRole).toRepresentation();
        keycloak.realm(realm).roles().get(parentRole).addComposites(Collections.singletonList(childRoleRepresent));

    }

    public void unAssociateRole(String parentRole, String childRole) {
        var childRoleRepresent = keycloak.realm(realm).roles().get(childRole).toRepresentation();
        keycloak.realm(realm).roles().get(parentRole).deleteComposites(Collections.singletonList(childRoleRepresent));

    }

    public void deleteRoleFromUser(String userId, String roleName) {

        UserResource userResource = keycloak.realm(realm).users().get(userId);
        RolesResource rolesResource = keycloak.realm(realm).roles();
        RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();
        userResource.roles().realmLevel().remove(Collections.singletonList(representation));
    }

    RoleResponse mappingRoleRespone(RoleRepresentation roleRepresentation) {
        return RoleResponse.builder().name(roleRepresentation.getName())
                .description(roleRepresentation.getDescription()).build();
    }

    public List<RoleResponse> getAllRole() {
        var roles = keycloak.realm(realm).roles().list();

        return roles.stream().filter(role -> !getDefaultRoles().contains(role.getName()))
                .map(role -> mappingRoleRespone(role))
                .toList();
    }

    public List<RoleResponse> getCompositeRoles() {
        var roles = keycloak.realm(realm).roles().list();
        return roles.stream().filter(role -> !getDefaultRoles().contains(role.getName()))
                .filter(role -> role.isComposite())
                .map(role -> mappingRoleRespone(role)).toList();
    }

    public List<RoleResponse> getPermissions() {
        var roles = keycloak.realm(realm).roles().list();
        return roles.stream().filter(role -> !getDefaultRoles().contains(role.getName()))
                .filter(role -> !role.isComposite())
                .map(role -> mappingRoleRespone(role)).toList();
    }

    public List<RoleResponse> getRoleByUserId(String userId) {
        var list = keycloak.realm(realm).users().get(userId).roles().realmLevel().listAll();
        return list.stream().filter(role -> !getDefaultRoles().contains(role.getName()))
                .map(role -> mappingRoleRespone(role)).toList();

    }

    public List<RoleResponse> getPermissionByRole(String roleName) {
        return keycloak.realm(realm).roles().get(roleName).getRoleComposites().stream()
                .map(role -> mappingRoleRespone(role)).toList();
    }

}
