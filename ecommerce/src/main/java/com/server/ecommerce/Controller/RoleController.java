package com.server.ecommerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.server.ecommerce.dto.request.RoleCreationRequest;
import com.server.ecommerce.dto.response.ApiResponse;
import com.server.ecommerce.services.RoleService;

@RestController
@RequestMapping("api")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping("role/create")
    public ResponseEntity<?> createNewRole(@RequestBody RoleCreationRequest request) {
        roleService.createRole(request);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PostMapping("role/delete/{roleName}")
    public ResponseEntity<?> deleteRole(@PathVariable String roleName) {
        roleService.deleteRole(roleName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("role/assign/user/{userId}")
    public ResponseEntity<?> assignRole(@PathVariable String userId, @RequestParam("roleName") String roleName) {
        roleService.assignRole(userId, roleName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }

    @DeleteMapping("role/remove/user/{userId}")
    public ResponseEntity<?> removeRoleFromRole(@PathVariable String userId,
            @RequestParam("roleName") String roleName) {
        roleService.deleteRoleFromUser(userId, roleName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("roles")
    public ApiResponse<?> getAllRole() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(roleService.getAllRole());
        return apiResponse;
    }

    @PutMapping("role/associate/{roleName}")
    public ResponseEntity<?> associateRole(@PathVariable String roleName,
            @RequestParam("permission") String permission) {
        roleService.associateRole(roleName, permission);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("role/associate/remove/{roleName}")
    public ResponseEntity<?> unAssociateRole(@PathVariable String roleName,
            @RequestParam("permission") String permission) {
        roleService.unAssociateRole(roleName, permission);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("role/composite/true")
    public ApiResponse<?> getCompositeRole() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(roleService.getCompositeRoles());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("role/composite/false")
    public ApiResponse<?> getPermission() {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(roleService.getPermissions());
        return apiResponse;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("role/{roleName}")
    public ApiResponse<?> getPermissionByRole(@PathVariable String roleName) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(roleService.getPermissionByRole(roleName));
        return apiResponse;

    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @GetMapping("roles/{userId}")
    public ApiResponse<?> getRoleByUserId(@PathVariable String userId) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setResult(roleService.getRoleByUserId(userId));
        return apiResponse;
    }

}
